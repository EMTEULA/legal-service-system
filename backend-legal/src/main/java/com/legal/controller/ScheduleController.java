package com.legal.controller;

import com.legal.common.Result;
import com.legal.service.LegalService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/schedule")
public class ScheduleController extends BaseController {
    private final LegalService service;
    public ScheduleController(LegalService service) { this.service = service; }

    @GetMapping("/list")
    public Result<List<Map<String, Object>>> list(@RequestParam(required = false) Integer lawyerId,
                                                  @RequestParam(required = false) String fromTime,
                                                  HttpServletRequest request) {
        requireRole(request, "admin", "lawyer");
        return Result.success(service.scheduleList(role(request), refId(request), lawyerId, fromTime));
    }

    @PostMapping("/add")
    public Result<Void> add(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        requireRole(request, "admin", "lawyer");
        service.saveSchedule(role(request), refId(request), body);
        return Result.success("排班已添加", null);
    }

    @PutMapping("/update")
    public Result<Void> update(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        requireRole(request, "admin", "lawyer");
        service.saveSchedule(role(request), refId(request), body);
        return Result.success("排班已修改", null);
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Integer id, HttpServletRequest request) {
        requireRole(request, "admin", "lawyer");
        service.deleteSchedule(role(request), refId(request), id);
        return Result.success("删除成功", null);
    }
}
