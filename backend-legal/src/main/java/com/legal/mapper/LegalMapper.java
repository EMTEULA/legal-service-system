package com.legal.mapper;

import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface LegalMapper {
    Map<String, Object> findUserByUsername(String username);
    Map<String, Object> findLawyerById(Integer id);
    Map<String, Object> findCustomerById(Integer id);
    int insertCustomer(Map<String, Object> row);
    int insertUser(Map<String, Object> row);

    List<Map<String, Object>> categoryList(@Param("name") String name, @Param("status") Integer status);
    List<Map<String, Object>> categoryAll();
    Map<String, Object> categoryById(Integer id);
    int insertCategory(Map<String, Object> row);
    int updateCategory(Map<String, Object> row);
    int deleteCategory(Integer id);
    int countCategoryUsed(Integer id);

    List<Map<String, Object>> lawyerList(@Param("name") String name, @Param("categoryId") Integer categoryId,
                                         @Param("status") Integer status);
    List<Map<String, Object>> availableLawyers(@Param("categoryId") Integer categoryId,
                                                @Param("startTime") Date startTime,
                                                @Param("endTime") Date endTime);
    int insertLawyer(Map<String, Object> row);
    int updateLawyer(Map<String, Object> row);
    int applyLawyerProfile(Map<String, Object> row);
    int deleteLawyer(Integer id);
    int deleteLawyerSchedules(Integer lawyerId);
    int deleteUserByRoleRef(@Param("role") String role, @Param("refId") Integer refId);
    int deleteLawyerCategories(Integer lawyerId);
    int insertLawyerCategory(@Param("lawyerId") Integer lawyerId, @Param("categoryId") Integer categoryId);
    int countLawyerBusiness(Integer lawyerId);

    List<Map<String, Object>> scheduleList(@Param("lawyerId") Integer lawyerId,
                                           @Param("fromTime") Date fromTime);
    Map<String, Object> scheduleById(Integer id);
    int insertSchedule(Map<String, Object> row);
    int updateSchedule(Map<String, Object> row);
    int deleteSchedule(Integer id);
    int countScheduleConflict(@Param("lawyerId") Integer lawyerId, @Param("startTime") Date startTime,
                              @Param("endTime") Date endTime, @Param("excludeId") Integer excludeId);

    int countAppointmentConflict(@Param("lawyerId") Integer lawyerId, @Param("startTime") Date startTime,
                                 @Param("endTime") Date endTime, @Param("excludeId") Integer excludeId);
    int countLawyerCategory(@Param("lawyerId") Integer lawyerId, @Param("categoryId") Integer categoryId);
    int countAvailableSchedule(@Param("lawyerId") Integer lawyerId, @Param("startTime") Date startTime,
                               @Param("endTime") Date endTime);
    int insertAppointment(Map<String, Object> row);
    Map<String, Object> appointmentById(Integer id);
    List<Map<String, Object>> appointmentList(@Param("role") String role, @Param("refId") Integer refId,
                                               @Param("status") Integer status,
                                               @Param("keyword") String keyword);
    int auditAppointment(@Param("id") Integer id, @Param("status") Integer status,
                         @Param("adminId") Integer adminId, @Param("remark") String remark);
    int updateAppointmentStatus(@Param("id") Integer id, @Param("status") Integer status);

    Map<String, Object> orderById(Integer id);
    int countValidOrder(Integer appointmentId);
    int insertOrder(Map<String, Object> row);
    List<Map<String, Object>> orderList(@Param("role") String role, @Param("refId") Integer refId,
                                        @Param("status") Integer status, @Param("keyword") String keyword);
    int auditOrder(@Param("id") Integer id, @Param("status") Integer status,
                   @Param("adminId") Integer adminId, @Param("remark") String remark);

    int countPendingApply(Integer lawyerId);
    int insertApply(Map<String, Object> row);
    Map<String, Object> applyById(Integer id);
    List<Map<String, Object>> applyList(@Param("role") String role, @Param("refId") Integer refId,
                                        @Param("status") Integer status);
    int auditApply(@Param("id") Integer id, @Param("status") Integer status,
                   @Param("adminId") Integer adminId, @Param("remark") String remark);

    Map<String, Object> statsSummary(@Param("startDate") String startDate, @Param("endDate") String endDate);
    List<Map<String, Object>> statsLawyer(@Param("startDate") String startDate, @Param("endDate") String endDate);
    List<Map<String, Object>> statsCategory(@Param("startDate") String startDate, @Param("endDate") String endDate);
    List<Map<String, Object>> statsTrend(@Param("startDate") String startDate, @Param("endDate") String endDate);
}
