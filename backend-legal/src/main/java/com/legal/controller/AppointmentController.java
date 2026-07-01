package com.legal.controller;

import com.legal.common.PageResult;
import com.legal.common.Result;
import com.legal.service.LegalService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/appointment")
public class AppointmentController extends BaseController {
    private final LegalService service;
    public AppointmentController(LegalService service) { this.service = service; }

    @PostMapping("/add")
    public Result<Void> add(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        requireRole(request, "customer");
        service.addAppointment(refId(request), body);
        return Result.success("预约提交成功，等待管理员审核", null);
    }

    @GetMapping("/list")
    public Result<PageResult<Map<String, Object>>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword,
            HttpServletRequest request) {
        return Result.success(service.appointmentList(pageNum, pageSize, role(request), refId(request), status, keyword));
    }

    @PutMapping("/audit/{id}")
    public Result<Void> audit(@PathVariable Integer id, @RequestBody Map<String, Object> body,
                              HttpServletRequest request) {
        requireRole(request, "admin");
        service.auditAppointment(id, integer(body.get("status")), string(body.get("auditRemark")), userId(request));
        return Result.success("审核完成", null);
    }

    @PutMapping("/cancel/{id}")
    public Result<Void> cancel(@PathVariable Integer id, HttpServletRequest request) {
        requireRole(request, "customer");
        service.changeAppointment(id, 4, role(request), refId(request));
        return Result.success("预约已取消", null);
    }

    @PutMapping("/complete/{id}")
    public Result<Void> complete(@PathVariable Integer id, HttpServletRequest request) {
        requireRole(request, "lawyer");
        service.changeAppointment(id, 3, role(request), refId(request));
        return Result.success("咨询已完成", null);
    }

    private static Integer integer(Object value) {
        return value == null ? null : Integer.valueOf(value.toString());
    }
    private static String string(Object value) { return value == null ? null : value.toString(); }
}
