package com.github.ly.sr.exception;

@FunctionalInterface
public interface ValidationFunction {
    void validate() throws Exception;
}
