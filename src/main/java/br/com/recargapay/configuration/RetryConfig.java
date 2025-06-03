package br.com.recargapay.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;

@Slf4j
@Configuration
public class RetryConfig {

    private static final int MAX_ATTEMPTS = 5;
    private static final long MIN_BACKOFF = 100L;
    private static final long MAX_JITTER = 200L;

    @Bean
    public RetryTemplate retryTemplate() {
        RetryTemplate template = new RetryTemplate();

        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        backOffPolicy.backOffPeriodSupplier(createJitterSupplier());

        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy(
                MAX_ATTEMPTS,
                Map.of(
                        TransientDataAccessException.class, true,
                        ObjectOptimisticLockingFailureException.class, true
                )
        );

        template.setRetryPolicy(retryPolicy);
        template.setBackOffPolicy(backOffPolicy);
        template.registerListener(new LoggingRetryListener());

        return template;
    }

    private Supplier<Long> createJitterSupplier() {
        Random random = new Random();
        return () -> MIN_BACKOFF + random.nextInt((int) MAX_JITTER);
    }

    static class LoggingRetryListener implements RetryListener {

        @Override
        public <T, E extends Throwable> boolean open(RetryContext context, RetryCallback<T, E> callback) {
            log.debug("🔄 Starting retry process...");
            return true; // Sempre permitir o retry
        }

        @Override
        public <T, E extends Throwable> void close(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
            if (throwable != null) {
                log.error("❌ Retry failed after {} attempts. Last exception: {}", context.getRetryCount(), throwable.getMessage());
            } else {
                log.info("✅ Retry succeeded after {} attempts.", context.getRetryCount());
            }
        }

        @Override
        public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
            log.warn("⚠️ Attempt #{} failed. Retrying... Cause: {}", context.getRetryCount(), throwable.getMessage());
        }
    }
}