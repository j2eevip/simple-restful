package com.github.ly.sr.encryption.global;

import com.github.ly.enums.EncryptMode;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

@Slf4j
public class SrEncryptionFilter implements Filter {
    private final Set<String> skipPaths;
    private final EncryptMode encryptMode;
    private final String privateKey;

    public SrEncryptionFilter(final Set<String> skipPaths, final EncryptMode encryptMode, final String privateKey) {
        this.skipPaths = Collections.unmodifiableSet(skipPaths);
        this.encryptMode = encryptMode;
        this.privateKey = privateKey;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequestWrapper requestWrapper = null;
        if (servletRequest instanceof HttpServletRequest request) {
            String servletPath = request.getServletPath();
            boolean gotoProceed = true;
            for (String skipPath : skipPaths) {
                if (servletPath.contains(skipPath)) {
                    gotoProceed = false;
                    break;
                }
            }
            if (gotoProceed) {
                try {
                    if (request.getMethod().equalsIgnoreCase("GET")) {
                        requestWrapper = new SrParameterRequestWrapper(request, encryptMode, privateKey);
                    } else if (!request.getMethod().equalsIgnoreCase("options")
                            && MediaType.APPLICATION_JSON_VALUE.equals(request.getContentType())) {
                        requestWrapper = new SrHttpRequestWrapper(request, encryptMode, privateKey);
                    }
                } catch (Exception e) {
                    request.getRequestDispatcher("404.html").forward(request, servletResponse);
                }
            }
        }

        if (Objects.nonNull(requestWrapper)) {
            filterChain.doFilter(requestWrapper, servletResponse);
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }
}
