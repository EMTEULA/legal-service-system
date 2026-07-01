package com.legal.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.legal.common.PageResult;
import com.legal.exception.BusinessException;
import com.legal.mapper.LegalMapper;
import com.legal.util.JwtUtil;
import com.legal.util.NoUtil;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class LegalService {
    private final LegalMapper mapper;
    private final JwtUtil jwtUtil;

    public LegalService(LegalMapper mapper, JwtUtil jwtUtil) {
        this.mapper = mapper;
        this.jwtUtil = jwtUtil;
    }

    public Map<String, Object> login(Map<String, Object> body) {
        String username = text(body, "username");
        String password = text(body, "password");
        if (username.isEmpty() || password.isEmpty()) throw new BusinessException("请输入用户名和密码");
        Map<String, Object> user = mapper.findUserByUsername(username);
        if (user == null || !password.equals(stringValue(user, "password"))) {
            throw new BusinessException("用户名或密码错误");
        }
        String role = stringValue(user, "role");
        int userId = intValue(user, "id");
        int refId = intValue(user, "refId");
        String realName = username;
        if ("lawyer".equals(role)) {
            Map<String, Object> lawyer = mapper.findLawyerById(refId);
            if (lawyer != null) realName = stringValue(lawyer, "name");
        } else if ("customer".equals(role)) {
            Map<String, Object> customer = mapper.findCustomerById(refId);
            if (customer != null) realName = stringValue(customer, "name");
        } else {
            realName = "系统管理员";
        }
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("refId", refId);
        claims.put("role", role);
        claims.put("username", username);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("token", jwtUtil.create(claims));
        result.put("userId", userId);
        result.put("refId", refId);
        result.put("role", role);
        result.put("username", username);
        result.put("realName", realName);
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    public void register(Map<String, Object> body) {
        require(body, "name", "姓名不能为空");
        require(body, "phone", "联系电话不能为空");
        require(body, "username", "用户名不能为空");
        require(body, "password", "密码不能为空");
        if (mapper.findUserByUsername(text(body, "username")) != null) {
            throw new BusinessException("用户名已存在");
        }
        try {
            mapper.insertCustomer(body);
            body.put("role", "customer");
            body.put("refId", body.get("id"));
            mapper.insertUser(body);
        } catch (DuplicateKeyException e) {
            throw new BusinessException("用户名已存在");
        }
    }

    public PageResult<Map<String, Object>> categoryList(int pageNum, int pageSize, String name, Integer status) {
        PageHelper.startPage(pageNum, pageSize);
        List<Map<String, Object>> list = mapper.categoryList(name, status);
        return page(list);
    }

    public void saveCategory(Map<String, Object> body) {
        require(body, "name", "类别名称不能为空");
        if (body.get("basePrice") == null) body.put("basePrice", 0);
        if (body.get("status") == null) body.put("status", 1);
        try {
            if (body.get("id") == null) mapper.insertCategory(body);
            else mapper.updateCategory(body);
        } catch (DuplicateKeyException e) {
            throw new BusinessException("类别名称已存在");
        }
    }

    public void deleteCategory(Integer id) {
        if (mapper.countCategoryUsed(id) > 0) throw new BusinessException("该类别已被使用，不能删除");
        mapper.deleteCategory(id);
    }

    public PageResult<Map<String, Object>> lawyerList(int pageNum, int pageSize, String name,
                                                       Integer categoryId, Integer status) {
        PageHelper.startPage(pageNum, pageSize);
        return page(mapper.lawyerList(name, categoryId, status));
    }

    @Transactional(rollbackFor = Exception.class)
    public void addLawyer(Map<String, Object> body) {
        validateLawyer(body, true);
        if (mapper.findUserByUsername(text(body, "username")) != null) throw new BusinessException("登录用户名已存在");
        if (body.get("status") == null) body.put("status", 1);
        try {
            mapper.insertLawyer(body);
            Integer lawyerId = intObject(body.get("id"));
            saveCategories(lawyerId, list(body.get("categoryIds")));
            Map<String, Object> user = new HashMap<>();
            user.put("username", body.get("username"));
            user.put("password", body.get("password"));
            user.put("role", "lawyer");
            user.put("refId", lawyerId);
            mapper.insertUser(user);
        } catch (DuplicateKeyException e) {
            throw new BusinessException("执业证号或登录用户名已存在");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateLawyer(Map<String, Object> body) {
        validateLawyer(body, false);
        try {
            mapper.updateLawyer(body);
            Integer id = intObject(body.get("id"));
            saveCategories(id, list(body.get("categoryIds")));
        } catch (DuplicateKeyException e) {
            throw new BusinessException("执业证号已存在");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteLawyer(Integer id) {
        if (mapper.countLawyerBusiness(id) > 0) throw new BusinessException("该律师存在未完成业务，不能删除");
        mapper.deleteLawyerSchedules(id);
        mapper.deleteLawyerCategories(id);
        mapper.deleteUserByRoleRef("lawyer", id);
        mapper.deleteLawyer(id);
    }

    public List<Map<String, Object>> availableLawyers(Integer categoryId, String start, String end) {
        Date startTime = date(start);
        Date endTime = date(end);
        validateRange(startTime, endTime);
        return mapper.availableLawyers(categoryId, startTime, endTime);
    }

    public List<Map<String, Object>> scheduleList(String role, Integer refId, Integer lawyerId, String fromTime) {
        if ("lawyer".equals(role)) lawyerId = refId;
        return mapper.scheduleList(lawyerId, optionalDate(fromTime));
    }

    public void saveSchedule(String role, Integer refId, Map<String, Object> body) {
        Integer lawyerId = intObject(body.get("lawyerId"));
        if ("lawyer".equals(role)) lawyerId = refId;
        if (lawyerId == null) throw new BusinessException("请选择律师");
        Date start = date(text(body, "startTime"));
        Date end = date(text(body, "endTime"));
        validateRange(start, end);
        Integer id = intObject(body.get("id"));
        if (mapper.countScheduleConflict(lawyerId, start, end, id) > 0) {
            throw new BusinessException("该时间段与现有排班冲突");
        }
        body.put("lawyerId", lawyerId);
        body.put("startTime", start);
        body.put("endTime", end);
        if (body.get("status") == null) body.put("status", 0);
        if (id == null) mapper.insertSchedule(body); else mapper.updateSchedule(body);
    }

    public void deleteSchedule(String role, Integer refId, Integer id) {
        Map<String, Object> row = mapper.scheduleById(id);
        if (row == null) throw new BusinessException("排班不存在");
        if ("lawyer".equals(role) && intValue(row, "lawyerId") != refId) throw new BusinessException("无权操作");
        if (intValue(row, "status") == 1) throw new BusinessException("已预约的排班不能删除");
        mapper.deleteSchedule(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public void addAppointment(Integer customerId, Map<String, Object> body) {
        Integer lawyerId = intObject(body.get("lawyerId"));
        Integer categoryId = intObject(body.get("categoryId"));
        if (lawyerId == null || categoryId == null) throw new BusinessException("请选择咨询类别和律师");
        Date start = date(text(body, "startTime"));
        Date end = date(text(body, "endTime"));
        validateRange(start, end);
        if (mapper.countLawyerCategory(lawyerId, categoryId) == 0) throw new BusinessException("该律师不提供此类咨询");
        if (mapper.countAvailableSchedule(lawyerId, start, end) == 0) throw new BusinessException("所选时间不在律师空闲时段内");
        if (mapper.countAppointmentConflict(lawyerId, start, end, null) > 0) throw new BusinessException("该律师当前时间段已有预约");
        body.put("appointmentNo", NoUtil.next("AP"));
        body.put("customerId", customerId);
        body.put("startTime", start);
        body.put("endTime", end);
        mapper.insertAppointment(body);
    }

    public PageResult<Map<String, Object>> appointmentList(int pageNum, int pageSize, String role,
                                                            Integer refId, Integer status, String keyword) {
        PageHelper.startPage(pageNum, pageSize);
        return page(mapper.appointmentList(role, refId, status, keyword));
    }

    public void auditAppointment(Integer id, Integer status, String remark, Integer adminId) {
        Map<String, Object> row = mapper.appointmentById(id);
        if (row == null) throw new BusinessException("预约不存在");
        if (intValue(row, "status") != 0) throw new BusinessException("该预约已审核，不能重复审核");
        if (status == null || (status != 1 && status != 2)) throw new BusinessException("审核状态无效");
        mapper.auditAppointment(id, status, adminId, remark);
    }

    public void changeAppointment(Integer id, Integer status, String role, Integer refId) {
        Map<String, Object> row = mapper.appointmentById(id);
        if (row == null) throw new BusinessException("预约不存在");
        int current = intValue(row, "status");
        if (status == 4) {
            if (!"customer".equals(role) || intValue(row, "customerId") != refId) throw new BusinessException("无权取消该预约");
            if (current != 0 && current != 1) throw new BusinessException("当前状态不能取消");
        } else if (status == 3) {
            if (!"lawyer".equals(role) || intValue(row, "lawyerId") != refId) throw new BusinessException("无权完成该预约");
            if (current != 1) throw new BusinessException("只有审核通过的预约可以完成");
        } else throw new BusinessException("状态变更无效");
        mapper.updateAppointmentStatus(id, status);
    }

    @Transactional(rollbackFor = Exception.class)
    public void addOrder(Integer lawyerId, Map<String, Object> body) {
        Integer appointmentId = intObject(body.get("appointmentId"));
        Map<String, Object> appointment = mapper.appointmentById(appointmentId);
        if (appointment == null) throw new BusinessException("预约不存在");
        if (intValue(appointment, "lawyerId") != lawyerId) throw new BusinessException("该预约不属于当前律师");
        int appointmentStatus = intValue(appointment, "status");
        if (appointmentStatus != 1 && appointmentStatus != 3) throw new BusinessException("当前预约状态不能创建收费订单");
        if (mapper.countValidOrder(appointmentId) > 0) throw new BusinessException("该预约已存在收费订单");
        int duration = intObject(body.get("durationMinutes")) == null ? 0 : intObject(body.get("durationMinutes"));
        BigDecimal unitPrice = decimal(body.get("unitPrice"));
        if (duration <= 0 || unitPrice.compareTo(BigDecimal.ZERO) < 0) throw new BusinessException("时长和单价必须有效");
        BigDecimal hours = new BigDecimal(duration).divide(new BigDecimal("60"), 2, RoundingMode.HALF_UP);
        body.put("orderNo", NoUtil.next("OD"));
        body.put("customerId", value(appointment, "customerId"));
        body.put("lawyerId", lawyerId);
        body.put("categoryId", value(appointment, "categoryId"));
        body.put("totalAmount", unitPrice.multiply(hours).setScale(2, RoundingMode.HALF_UP));
        mapper.insertOrder(body);
        if (appointmentStatus == 1) mapper.updateAppointmentStatus(appointmentId, 3);
    }

    public PageResult<Map<String, Object>> orderList(int pageNum, int pageSize, String role,
                                                      Integer refId, Integer status, String keyword) {
        PageHelper.startPage(pageNum, pageSize);
        return page(mapper.orderList(role, refId, status, keyword));
    }

    public void auditOrder(Integer id, Integer status, String remark, Integer adminId) {
        Map<String, Object> row = mapper.orderById(id);
        if (row == null) throw new BusinessException("订单不存在");
        int current = intValue(row, "status");
        if (status == 3 && current == 1) {
            mapper.auditOrder(id, status, adminId, remark);
            return;
        }
        if (current != 0) throw new BusinessException("该订单已审核，不能重复审核");
        if (status == null || (status != 1 && status != 2)) throw new BusinessException("审核状态无效");
        mapper.auditOrder(id, status, adminId, remark);
    }

    public Map<String, Object> lawyerProfile(Integer lawyerId) {
        Map<String, Object> row = mapper.findLawyerById(lawyerId);
        if (row == null) throw new BusinessException("律师不存在");
        return row;
    }

    public void submitApply(Integer lawyerId, Map<String, Object> body) {
        if (mapper.countPendingApply(lawyerId) > 0) throw new BusinessException("已有待审核申请，请勿重复提交");
        require(body, "applyName", "姓名不能为空");
        require(body, "applyPhone", "联系电话不能为空");
        body.put("lawyerId", lawyerId);
        List<Integer> ids = list(body.get("categoryIds"));
        body.put("applyCategoryIds", join(ids));
        mapper.insertApply(body);
    }

    public PageResult<Map<String, Object>> applyList(int pageNum, int pageSize, String role,
                                                      Integer refId, Integer status) {
        PageHelper.startPage(pageNum, pageSize);
        return page(mapper.applyList(role, refId, status));
    }

    @Transactional(rollbackFor = Exception.class)
    public void auditApply(Integer id, Integer status, String remark, Integer adminId) {
        Map<String, Object> row = mapper.applyById(id);
        if (row == null) throw new BusinessException("申请不存在");
        if (intValue(row, "status") != 0) throw new BusinessException("该申请已审核");
        if (status == null || (status != 1 && status != 2)) throw new BusinessException("审核状态无效");
        if (status == 1) {
            Integer lawyerId = intObject(value(row, "lawyerId"));
            Map<String, Object> profile = new HashMap<>();
            profile.put("lawyerId", lawyerId);
            profile.put("applyName", value(row, "applyName"));
            profile.put("applyPhone", value(row, "applyPhone"));
            profile.put("applyIntroduction", value(row, "applyIntroduction"));
            mapper.applyLawyerProfile(profile);
            saveCategories(lawyerId, parseIds(stringValue(row, "applyCategoryIds")));
        }
        mapper.auditApply(id, status, adminId, remark);
    }

    public Map<String, Object> statistics(String startDate, String endDate) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("summary", mapper.statsSummary(startDate, endDate));
        result.put("lawyers", mapper.statsLawyer(startDate, endDate));
        result.put("categories", mapper.statsCategory(startDate, endDate));
        result.put("trend", mapper.statsTrend(startDate, endDate));
        return result;
    }

    private void validateLawyer(Map<String, Object> body, boolean insert) {
        require(body, "name", "律师姓名不能为空");
        require(body, "phone", "联系电话不能为空");
        require(body, "licenseNo", "执业证号不能为空");
        if (insert) {
            require(body, "username", "登录用户名不能为空");
            require(body, "password", "登录密码不能为空");
        }
        if (body.get("experienceYears") == null) body.put("experienceYears", 0);
        if (body.get("status") == null) body.put("status", 1);
    }

    private void saveCategories(Integer lawyerId, List<Integer> ids) {
        mapper.deleteLawyerCategories(lawyerId);
        for (Integer id : ids) mapper.insertLawyerCategory(lawyerId, id);
    }

    private static Date date(String text) {
        if (text == null || text.trim().isEmpty()) throw new BusinessException("时间不能为空");
        String value = text.replace("T", " ");
        if (value.length() == 16) value += ":00";
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(value);
        } catch (ParseException e) {
            throw new BusinessException("时间格式不正确");
        }
    }

    private static Date optionalDate(String text) {
        return text == null || text.trim().isEmpty() ? null : date(text);
    }

    private static void validateRange(Date start, Date end) {
        if (!start.before(end)) throw new BusinessException("结束时间必须晚于开始时间");
    }

    private static void require(Map<String, Object> body, String key, String message) {
        if (body.get(key) == null || body.get(key).toString().trim().isEmpty()) throw new BusinessException(message);
    }

    private static String text(Map<String, Object> body, String key) {
        Object value = body.get(key);
        return value == null ? "" : value.toString().trim();
    }

    private static Object value(Map<String, Object> map, String key) {
        if (map.containsKey(key)) return map.get(key);
        String underscore = key.replaceAll("([A-Z])", "_$1").toLowerCase();
        if (map.containsKey(underscore)) return map.get(underscore);
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(key) || entry.getKey().equalsIgnoreCase(underscore)) return entry.getValue();
        }
        return null;
    }

    private static String stringValue(Map<String, Object> map, String key) {
        Object value = value(map, key);
        return value == null ? "" : value.toString();
    }

    private static int intValue(Map<String, Object> map, String key) {
        Integer value = intObject(value(map, key));
        return value == null ? 0 : value;
    }

    private static Integer intObject(Object value) {
        if (value == null || value.toString().trim().isEmpty()) return null;
        return value instanceof Number ? ((Number) value).intValue() : Integer.parseInt(value.toString());
    }

    private static BigDecimal decimal(Object value) {
        if (value == null || value.toString().trim().isEmpty()) return BigDecimal.ZERO;
        return value instanceof BigDecimal ? (BigDecimal) value : new BigDecimal(value.toString());
    }

    @SuppressWarnings("unchecked")
    private static List<Integer> list(Object value) {
        if (value == null) return Collections.emptyList();
        if (value instanceof List) {
            List<Integer> result = new ArrayList<>();
            for (Object item : (List<Object>) value) result.add(intObject(item));
            return result;
        }
        return parseIds(value.toString());
    }

    private static List<Integer> parseIds(String text) {
        if (text == null || text.trim().isEmpty()) return Collections.emptyList();
        List<Integer> result = new ArrayList<>();
        for (String item : text.split(",")) result.add(Integer.parseInt(item.trim()));
        return result;
    }

    private static String join(List<Integer> ids) {
        StringBuilder out = new StringBuilder();
        for (Integer id : ids) {
            if (out.length() > 0) out.append(',');
            out.append(id);
        }
        return out.toString();
    }

    private static <T> PageResult<T> page(List<T> list) {
        PageInfo<T> info = new PageInfo<>(list);
        return new PageResult<>(info.getTotal(), list);
    }
}
