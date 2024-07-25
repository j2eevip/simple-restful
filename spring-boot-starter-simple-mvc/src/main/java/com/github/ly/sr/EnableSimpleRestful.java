package com.github.ly.sr;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Inherited
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(SrAutoConfiguration.class)
public @interface EnableSimpleRestful {
}
