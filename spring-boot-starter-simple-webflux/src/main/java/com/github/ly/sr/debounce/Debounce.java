package com.github.ly.sr.debounce;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Inherited
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Debounce {
    long expire() default 1;

    TimeUnit unit() default TimeUnit.SECONDS;

}
