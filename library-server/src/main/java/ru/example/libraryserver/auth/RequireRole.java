package ru.example.libraryserver.auth;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация для указания требуемой роли пользователя для выполнения метода.
 * Используется в сочетании с аспектом {@link AuthAspect} для контроля доступа.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RequireRole {
    String[] value();
}
