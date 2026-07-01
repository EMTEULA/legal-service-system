package com.legal.controller;

import com.legal.common.PageResult;
import com.legal.common.Result;
import com.legal.service.LegalService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/order")
public class OrderController extends BaseController {
    private final LegalService service;
    public OrderController(LegalService service) { this.service = service; }

    @PostMapping("/add")
    public Result<Void> add(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        requireRole(request, "lawyer");
        service.addOrder(refId(request), body);
        return Result.success("收费订单已提交审核", null);
    }

    @GetMapping("/list")
    public Result<PageResult<Map<String, Object>>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword,
            HttpServletRequest request) {
        return Result.success(service.orderList(pageNum, pageSize, role(request), refId(request), status, keyword));
    }

    @PutMapping("/audit/{id}")
    public Result<Void> audit(@PathVariable Integer id, @RequestBody Map<String, Object> body,
                              HttpServletRequest request) {
        requireRole(request, "admin");
        Integer status = body.get("status") == null ? null : Integer.valueOf(body.get("status").toString());
        String remark = body.get("auditRemark") == null ? null : body.get("auditRemark").toString();
        service.auditOrder(id, status, remark, userId(request));
        return Result.success("订单状态已更新", null);
    }
}
