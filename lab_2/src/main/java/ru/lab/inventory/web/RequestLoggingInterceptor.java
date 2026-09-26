package ru.lab.inventory.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/** Логирует метод, URI, статус и время обработки каждого запроса. */
@Component
public class RequestLoggingInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(RequestLoggingInterceptor.class);
    private static final String ATTR_START = "requestStartTime";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        request.setAttribute(ATTR_START, System.nanoTime());
        log.info("--> {} {}", request.getMethod(), request.getRequestURI());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        Object start = request.getAttribute(ATTR_START);
        long ms = start == null ? -1 : (System.nanoTime() - (Long) start) / 1_000_000;
        log.info("<-- {} {} -> {} ({} ms)",
                request.getMethod(), request.getRequestURI(), response.getStatus(), ms);
    }
}