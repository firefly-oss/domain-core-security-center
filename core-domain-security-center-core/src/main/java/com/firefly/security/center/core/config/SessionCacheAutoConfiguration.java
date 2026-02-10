/*
 * Copyright 2025 Firefly Software Solutions Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.firefly.security.center.core.config;

import org.fireflyframework.cache.config.CacheAutoConfiguration;
import org.fireflyframework.cache.core.CacheType;
import org.fireflyframework.cache.factory.CacheManagerFactory;
import org.fireflyframework.cache.manager.FireflyCacheManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

import java.time.Duration;

/**
 * Auto-configuration for session management cache.
 * <p>
 * Creates a dedicated cache manager for user session data with appropriate TTL and configuration.
 * <p>
 * The session cache uses:
 * <ul>
 *   <li>Key prefix: {@code firefly:security:sessions}</li>
 *   <li>TTL: 30 minutes (configurable per session)</li>
 *   <li>Preferred type: REDIS (for distributed session management)</li>
 *   <li>Fallback: Caffeine (for single-instance deployments)</li>
 * </ul>
 */
@AutoConfiguration
@AutoConfigureAfter(CacheAutoConfiguration.class)
@ConditionalOnClass({FireflyCacheManager.class, CacheManagerFactory.class})
@Slf4j
public class SessionCacheAutoConfiguration {

    private static final String SESSION_CACHE_KEY_PREFIX = "firefly:security:sessions";
    private static final Duration SESSION_CACHE_TTL = Duration.ofMinutes(30);

    public SessionCacheAutoConfiguration() {
        log.info("SessionCacheAutoConfiguration loaded");
    }

    /**
     * Creates a dedicated cache manager for user session management.
     * <p>
     * This cache manager is independent from other application caches,
     * providing isolation for session data.
     *
     * @param factory the cache manager factory
     * @return a dedicated cache manager for sessions
     */
    @Bean("sessionCacheManager")
    @ConditionalOnMissingBean(name = "sessionCacheManager")
    public FireflyCacheManager sessionCacheManager(CacheManagerFactory factory) {
        String description = String.format(
                "User Session Cache - Stores session contexts and metadata (TTL: %d minutes)",
                SESSION_CACHE_TTL.toMinutes()
        );

        // Prefer Redis for distributed session management across multiple instances
        return factory.createCacheManager(
                "user-sessions",
                CacheType.AUTO,
                SESSION_CACHE_KEY_PREFIX,
                SESSION_CACHE_TTL,
                description,
                "core-domain-security-center-core.SessionCacheAutoConfiguration"
        );
    }
}
