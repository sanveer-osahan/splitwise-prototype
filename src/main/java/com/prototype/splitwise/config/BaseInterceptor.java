package com.prototype.splitwise.config;

import com.prototype.splitwise.entity.EntityRepository;
import com.prototype.splitwise.exception.ClientException;
import com.prototype.splitwise.user.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class BaseInterceptor implements HandlerInterceptor {

    @Autowired EntityRepository<User> userRepository;

    @Override
    public boolean preHandle(
            HttpServletRequest request, HttpServletResponse response, Object handler) {
        final String currentUser = request.getHeader(AuthContext.USER_ID);
        if (StringUtils.isBlank(currentUser)) {
            throw new ClientException(HttpStatus.UNAUTHORIZED, "Auth Header required");
        }
        if (!userRepository.existsById(currentUser)) {
            throw new ClientException(HttpStatus.UNAUTHORIZED, "Invalid user : " + currentUser);
        }
        AuthContext.load(currentUser);
        return true;
    }
}
