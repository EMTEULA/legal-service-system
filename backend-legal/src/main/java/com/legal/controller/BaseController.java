package com.legal.controller;

import com.legal.exception.BusinessException;

import javax.servlet.http.HttpServletRequest;

abstract class BaseController {
    protected String role(HttpServletRequest request) {
        return (String) request.getAttribute("role");
    }

    protected Integer refId(HttpServletRequest request) {
        return (Integer) request.getAttribute("refId");
    }

    protected Integer userId(HttpServletRequest request) {
        return (Integer) request.getAttribute("userId");
    }

    protected void requireRole(HttpServletRequest request, String... allowed) {
        String current = role(request);
        for (String role : allowed) if (role.equals(current)) return;
        throw new BusinessException("无权限操作");
    }
}
