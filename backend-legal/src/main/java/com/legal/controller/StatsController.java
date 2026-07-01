package com.legal.controller;

import com.legal.common.Result;
import com.legal.service.LegalService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/stats")
public class StatsController extends BaseController {
    private final LegalService service;
    public StatsController(LegalService service) { this.service = service; }

    @GetMapping({"/summary", "/all"})
    public Result<Map<String, Object>> all(@RequestParam(required = false) String startDate,
                                           @RequestParam(required = false) String endDate,
                                           HttpServletRequest request) {
        requireRole(request, "admin");
        return Result.success(service.statistics(startDate, endDate));
    }
}
