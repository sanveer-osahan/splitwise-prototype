package com.prototype.splitwise.config;

import com.prototype.splitwise.exception.ClientException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;

import java.util.Optional;

/**
 * Holds the current authenticated user id
 */
public class AuthContext {

    public static final String USER_ID = "x-user-id";

    private static final ThreadLocal<String> currentUserThreadLocal =
            new InheritableThreadLocal<>();

    private AuthContext() {}

    public static void load(String currentUser) {
        currentUserThreadLocal.set(currentUser);
    }

    public static void unload() {
        currentUserThreadLocal.remove();
    }

    public static String getCurrentUserOrElseThrow() {
        final String currentUser = currentUserThreadLocal.get();
        if (StringUtils.isBlank(currentUser)) {
            throw new ClientException(HttpStatus.UNAUTHORIZED);
        }
        return currentUser;
    }

    public static Optional<String> getCurrentUser() {
        return Optional.ofNullable(currentUserThreadLocal.get())
                .filter(StringUtils::isNotBlank);
    }

    public static String getCurrentUserOrElse(String defaultValue) {
        return getCurrentUser().orElse(defaultValue);
    }
}
