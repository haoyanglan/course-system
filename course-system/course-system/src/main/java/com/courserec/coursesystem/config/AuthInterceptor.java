package com.courserec.coursesystem.config;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.courserec.coursesystem.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) return true;
        String path = request.getRequestURI();
        if (path.equals("/auth/login") || path.equals("/auth/register") || path.equals("/auth/resetPassword") || path.equals("/error")) return true;

        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) token = token.substring(7);
        if (token == null || token.isBlank()) return reject(response, 401, "请先登录");

        try {
            DecodedJWT jwt = JwtUtils.verifyToken(token);
            String username = jwt.getClaim("username").asString();
            String role = jwt.getClaim("role").asString();
            if (username == null || role == null) return reject(response, 401, "登录已失效");
            request.setAttribute("authUsername", username);
            request.setAttribute("authRole", role);
            if (requiresAdmin(path) && !"ADMIN".equals(role)) return reject(response, 403, "无权执行此操作");
            if (requiresTeacher(path) && !"TEACHER".equals(role)) return reject(response, 403, "仅教师可操作");
            if (requiresStudent(path) && !"STUDENT".equals(role)) return reject(response, 403, "仅学生可操作");
            if ((path.startsWith("/chat/") || path.equals("/classroom/reserve") || path.equals("/classroom/myReservations"))
                    && !"STUDENT".equals(role) && !"TEACHER".equals(role)) return reject(response, 403, "仅师生可操作");
            return true;
        } catch (JWTVerificationException exception) {
            return reject(response, 401, "登录已失效，请重新登录");
        }
    }

    private boolean requiresAdmin(String path) {
        return path.startsWith("/admin/") || path.equals("/banner/save") || path.equals("/banner/delete")
                || path.equals("/classroom/add") || path.equals("/classroom/delete")
                || path.equals("/forum/delete") || path.equals("/course/delete");
    }

    private boolean requiresTeacher(String path) {
        return path.equals("/enroll/updateGrade") || path.equals("/enroll/submitAttendance")
                || path.equals("/enroll/evalStats") || path.equals("/enroll/students") || path.equals("/course/complete")
                || path.equals("/course/teacherList");
    }

    private boolean requiresStudent(String path) {
        return path.equals("/enroll/my") || path.equals("/enroll/submit") || path.equals("/enroll/drop")
                || path.equals("/enroll/myGrades") || path.equals("/enroll/evaluate")
                || path.equals("/enroll/myAttendance") || path.equals("/course/recommend")
                || path.equals("/ai/chat");
    }

    private boolean reject(HttpServletResponse response, int status, String message) throws Exception {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":" + status + ",\"message\":\"" + message + "\"}");
        return false;
    }
}
