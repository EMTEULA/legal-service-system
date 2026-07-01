package com.legal.controller;

import com.legal.common.PageResult;
import com.legal.common.Result;
import com.legal.service.LegalService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/lawyerApply")
public class LawyerApplyController extends BaseController {
    private final LegalService service;
    public LawyerApplyController(LegalService service) { this.service = service; }

    @PostMapping("/submit")
    public Result<Void> submit(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        requireRole(request, "lawyer");
        service.submitApply(refId(request), body);
        return Result.success("修改申请已提交", null);
    }

    @GetMapping("/list")
    public Result<PageResult<Map<String, Object>>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Integer status,
            HttpServletRequest request) {
        requireRole(request, "admin", "lawyer");
        return Result.success(service.applyList(pageNum, pageSize, role(request), refId(request), status));
    }

    @PutMapping("/audit/{id}")
    public Result<Void> audit(@PathVariable Integer id, @RequestBody Map<String, Object> body,
                              HttpServletRequest request) {
        requireRole(request, "admin");
        Integer status = body.get("status") == null ? null : Integer.valueOf(body.get("status").toString());
        String remark = body.get("auditRemark") == null ? null : body.get("auditRemark").toString();
        service.auditApply(id, status, remark, userId(request));
        return Result.success("申请审核完成", null);
    }
}
