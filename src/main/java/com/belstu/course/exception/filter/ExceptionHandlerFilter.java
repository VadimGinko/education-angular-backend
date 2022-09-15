package com.belstu.course.exception.filter;

import com.belstu.course.exception.JwtAuthenticationException;
import com.belstu.course.exception.UserInActiveException;
import com.belstu.course.mapper.JsonError;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ExceptionHandlerFilter extends OncePerRequestFilter {
    @SneakyThrows
    @Override
    public void doFilterInternal(HttpServletRequest servletRequest,
                                 HttpServletResponse servletResponse,
                                 FilterChain filterChain) {
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } catch (UserInActiveException | UsernameNotFoundException | JwtAuthenticationException e) {
            Cookie[] cookies = servletRequest.getCookies();
            if (cookies != null) {
                for (Cookie c : cookies) {
                    String cookieName = "access";
                    if (cookieName.equals(c.getName())) {
                        c.setMaxAge(0);
                        servletResponse.addCookie(c);
                        servletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());

                        servletResponse.getWriter().println(
                                new JsonError().getError("Authorization", e.getMessage()));
                        servletResponse.getWriter().flush();
                    }
                }
            } else {
                servletResponse.sendError(
                        HttpServletResponse.SC_BAD_GATEWAY,
                        e.getMessage());
            }
        }
    }
}
