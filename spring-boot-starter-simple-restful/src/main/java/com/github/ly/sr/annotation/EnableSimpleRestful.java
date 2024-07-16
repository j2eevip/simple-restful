package com.github.ly.sr.annotation;

import com.github.ly.sr.SrAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Inherited
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(SrAutoConfiguration.class)
public @interface EnableSimpleRestful {
}
