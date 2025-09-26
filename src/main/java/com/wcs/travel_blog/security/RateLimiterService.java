package com.wcs.travel_blog.security;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimiterService {

    private final Map<String, Deque<Instant>> requestsByKey = new ConcurrentHashMap<>();

    public boolean tryConsume(String key, int limit, Duration window) {
        Instant now = Instant.now();
        Deque<Instant> deque = requestsByKey.computeIfAbsent(key, ignored -> new ArrayDeque<>());
        synchronized (deque) {
            while (!deque.isEmpty() && Duration.between(deque.peekFirst(), now).compareTo(window) > 0) {
                deque.removeFirst();
            }
            if (deque.size() >= limit) {
                return false;
            }
            deque.addLast(now);
            return true;
        }
    }
}
