package com.belstu.course.jwt;

import com.belstu.course.exception.JwtAuthenticationException;
import com.belstu.course.exception.UserInActiveException;
import com.belstu.course.model.enums.UserStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtTokenFilter extends GenericFilterBean {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String token = jwtTokenProvider.resolveToken((HttpServletRequest) servletRequest);

        try {
            if (token != null && jwtTokenProvider.validateToken(token)) {
                Authentication authentication = jwtTokenProvider.getAuthentication(token);
                if (authentication == null) {
                    return;
                }

                JwtUser user = (JwtUser) authentication.getPrincipal();
                if (user.getStatus().equals(UserStatus.IN_ACTIVE)) {
                    throw new UserInActiveException("Аккаунт заблокирован");
                }
                if (user.getStatus().equals(UserStatus.EMAIL_NOT_CONFIRMED)) {
                    throw new UserInActiveException("Почта не подтверждена");
                }
                SecurityContextHolder.getContext().setAuthentication(authentication);

            }
        } catch (JwtAuthenticationException | UserInActiveException e) {
            Cookie[] cookies = ((HttpServletRequest) servletRequest).getCookies();
            if (cookies != null) {
                for (Cookie c : cookies) {
                    String cookieName = "access";
                    if (cookieName.equals(c.getName())) {
                        c.setMaxAge(0);
                    }
                }
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
