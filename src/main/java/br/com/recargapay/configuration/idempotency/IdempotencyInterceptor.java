package br.com.recargapay.configuration.idempotency;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class IdempotencyInterceptor implements HandlerInterceptor {

    private static final String IDEMPOTENCY_HEADER = "Idempotency-Key";

    private final RedisTemplate<String, IdempotencyValue> redisTemplate;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String key = request.getHeader(IDEMPOTENCY_HEADER);
        if (key == null || key.isBlank()) {
            return true;
        }

        IdempotencyValue cached = redisTemplate.opsForValue().get(key);
        if (cached != null) {
            response.setStatus(cached.getHttpStatus());
            response.setContentType(cached.getContentType());
            response.getWriter().write(cached.getBody());
            return false;
        }

        return true;
    }
}

