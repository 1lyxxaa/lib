package ru.example.libraryserver.auth;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestHeader;

import java.lang.annotation.*;
import java.lang.reflect.Method;

/**
 * Аспект для проверки роли пользователя перед выполнением методов, помеченных аннотацией {@link RequireRole}.
 * Используется для обеспечения безопасности и контроля доступа к методам.
 */
@Aspect
@Component
public class AuthAspect {
    @Autowired
    private AuthService authService;

    /**
     * Проверяет роль пользователя перед выполнением метода.
     * @param joinPoint точка соединения, представляющая метод, который будет выполнен
     * @return результат выполнения метода, если проверка прошла успешно, иначе возвращает ResponseEntity с ошибкой
     * @throws Throwable если произошла ошибка при выполнении метода
     */
    @Around("@annotation(ru.example.libraryserver.auth.RequireRole)")
    public Object checkRole(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RequireRole requireRole = method.getAnnotation(RequireRole.class);
        String required = requireRole.value();
        // Найти токен среди аргументов
        String token = null;
        Object[] args = joinPoint.getArgs();
        Annotation[][] paramAnns = method.getParameterAnnotations();
        for (int i = 0; i < paramAnns.length; i++) {
            for (Annotation ann : paramAnns[i]) {
                if (ann instanceof RequestHeader) {
                    RequestHeader rh = (RequestHeader) ann;
                    if ("X-Auth-Token".equals(rh.value())) {
                        token = (String) args[i];
                    }
                }
            }
        }
        if (token == null) {
            return ResponseEntity.status(401).body("Missing token");
        }
        User user = authService.getUserByToken(token);
        if (user == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        if (!user.getRole().equals(required) && !user.getRole().equals("ADMIN")) {
            return ResponseEntity.status(403).body("Forbidden");
        }
        return joinPoint.proceed();
    }
}

