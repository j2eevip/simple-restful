package com.github.ly.exception;

@FunctionalInterface
public interface ValidationFunction {
    void validate() throws Exception;
}
