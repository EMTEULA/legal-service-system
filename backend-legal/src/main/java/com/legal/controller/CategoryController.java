package com.legal.controller;

import com.legal.common.PageResult;
import com.legal.common.Result;
import com.legal.service.LegalService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/category")
public class CategoryController extends BaseController {
    private final LegalService service;
    public CategoryController(LegalService service) { this.service = service; }

    @GetMapping("/list")
    public Result<PageResult<Map<String, Object>>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer status) {
        return Result.success(service.categoryList(pageNum, pageSize, name, status));
    }

    @GetMapping("/all")
    public Result<List<Map<String, Object>>> all() {
        return Result.success(service.categoryList(1, 1000, null, 1).getList());
    }

    @PostMapping("/add")
    public Result<Void> add(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        requireRole(request, "admin");
        service.saveCategory(body);
        return Result.success("新增成功", null);
    }

    @PutMapping("/update")
    public Result<Void> update(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        requireRole(request, "admin");
        service.saveCategory(body);
        return Result.success("修改成功", null);
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Integer id, HttpServletRequest request) {
        requireRole(request, "admin");
        service.deleteCategory(id);
        return Result.success("删除成功", null);
    }
}
