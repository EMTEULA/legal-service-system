package com.legal.controller;

import com.legal.common.PageResult;
import com.legal.common.Result;
import com.legal.service.LegalService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/lawyer")
public class LawyerController extends BaseController {
    private final LegalService service;
    public LawyerController(LegalService service) { this.service = service; }

    @GetMapping("/list")
    public Result<PageResult<Map<String, Object>>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) Integer status) {
        return Result.success(service.lawyerList(pageNum, pageSize, name, categoryId, status));
    }

    @GetMapping("/available")
    public Result<List<Map<String, Object>>> available(@RequestParam Integer categoryId,
                                                        @RequestParam String startTime,
                                                        @RequestParam String endTime,
                                                        HttpServletRequest request) {
        requireRole(request, "customer");
        return Result.success(service.availableLawyers(categoryId, startTime, endTime));
    }

    @GetMapping("/profile")
    public Result<Map<String, Object>> profile(HttpServletRequest request) {
        requireRole(request, "lawyer");
        return Result.success(service.lawyerProfile(refId(request)));
    }

    @PostMapping("/add")
    public Result<Void> add(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        requireRole(request, "admin");
        service.addLawyer(body);
        return Result.success("律师及账号创建成功", null);
    }

    @PutMapping("/update")
    public Result<Void> update(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        requireRole(request, "admin");
        service.updateLawyer(body);
        return Result.success("修改成功", null);
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Integer id, HttpServletRequest request) {
        requireRole(request, "admin");
        service.deleteLawyer(id);
        return Result.success("删除成功", null);
    }
}
