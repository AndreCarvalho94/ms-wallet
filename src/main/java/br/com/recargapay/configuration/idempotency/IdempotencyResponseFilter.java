package br.com.recargapay.configuration.idempotency;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.time.Duration;

@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
public class IdempotencyResponseFilter extends OncePerRequestFilter {

    private static final String IDEMPOTENCY_HEADER = "Idempotency-Key";

    private final RedisTemplate<String, IdempotencyValue> redisTemplate;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String key = request.getHeader(IDEMPOTENCY_HEADER);
        if (!"POST".equalsIgnoreCase(request.getMethod()) || key == null || key.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
        filterChain.doFilter(request, responseWrapper);

        String body = new String(responseWrapper.getContentAsByteArray(), responseWrapper.getCharacterEncoding());
        IdempotencyValue value = new IdempotencyValue(
                responseWrapper.getStatus(),
                body,
                responseWrapper.getContentType()
        );

        redisTemplate.opsForValue().set(key, value, Duration.ofHours(24));
        responseWrapper.copyBodyToResponse();
    }
}
