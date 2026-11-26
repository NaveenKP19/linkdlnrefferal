package com.personalproject.linkdlnrefferal.security;

import com.personalproject.linkdlnrefferal.exception.BadRequestException;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class LinkedInOAuthRateLimiter {

    private static final int MAX_ATTEMPTS = 20;
    private static final Duration WINDOW = Duration.ofMinutes(5);

    private final ConcurrentMap<String, Deque<Instant>> attempts = new ConcurrentHashMap<>();

    public void check(String action, String clientIp) {
        String key = action + ":" + (clientIp == null ? "unknown" : clientIp);
        Instant now = Instant.now();
        Instant threshold = now.minus(WINDOW);

        Deque<Instant> timestamps = attempts.computeIfAbsent(key, ignored -> new ArrayDeque<>());
        synchronized (timestamps) {
            while (!timestamps.isEmpty() && timestamps.peek().isBefore(threshold)) {
                timestamps.poll();
            }
            if (timestamps.size() >= MAX_ATTEMPTS) {
                throw new BadRequestException("Too many LinkedIn authentication attempts. Please try again later.");
            }
            timestamps.add(now);
        }
    }
}
