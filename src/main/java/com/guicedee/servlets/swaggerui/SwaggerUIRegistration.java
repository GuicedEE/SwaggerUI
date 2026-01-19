package com.guicedee.servlets.swaggerui;

import com.guicedee.vertx.web.spi.VertxRouterConfigurator;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;

/**
 * Registers the Swagger UI static content with a Vert.x {@link Router}.
 * <p>
 * The handler serves resources from the {@code swagger} classpath directory
 * under the {@code /swagger/*} path by default. Configuration is driven by
 * system properties (with optional environment variable fallbacks) so the
 * endpoint can be made production ready without code changes.
 * </p>
 * <p>
 * Supported configuration keys:
 * </p>
 * <ul>
 *     <li>{@value #PROPERTY_ENABLED} (default {@value #DEFAULT_ENABLED})</li>
 *     <li>{@value #PROPERTY_ROUTE} (default {@value #DEFAULT_ROUTE})</li>
 *     <li>{@value #PROPERTY_RESOURCE_ROOT} (default {@value #DEFAULT_RESOURCE_ROOT})</li>
 *     <li>{@value #PROPERTY_FILES_READ_ONLY} (default {@value #DEFAULT_FILES_READ_ONLY})</li>
 *     <li>{@value #PROPERTY_CACHE_ENABLED} (default {@value #DEFAULT_CACHE_ENABLED})</li>
 *     <li>{@value #PROPERTY_CACHE_MAX_AGE_SECONDS} (default {@value #DEFAULT_CACHE_MAX_AGE_SECONDS})</li>
 *     <li>{@value #PROPERTY_DIRECTORY_LISTING} (default {@value #DEFAULT_DIRECTORY_LISTING})</li>
 *     <li>{@value #PROPERTY_INDEX_PAGE} (default {@value #DEFAULT_INDEX_PAGE})</li>
 * </ul>
 */
public class SwaggerUIRegistration implements VertxRouterConfigurator<SwaggerUIRegistration> {
    @Override
    public Integer sortOrder()
    {
        return Integer.MAX_VALUE - 100;
    }

    public static final String PROPERTY_ENABLED = "guicedee.swaggerui.enabled";
    public static final String PROPERTY_ROUTE = "guicedee.swaggerui.route";
    public static final String PROPERTY_RESOURCE_ROOT = "guicedee.swaggerui.resourceRoot";
    public static final String PROPERTY_FILES_READ_ONLY = "guicedee.swaggerui.filesReadOnly";
    public static final String PROPERTY_CACHE_ENABLED = "guicedee.swaggerui.cache.enabled";
    public static final String PROPERTY_CACHE_MAX_AGE_SECONDS = "guicedee.swaggerui.cache.maxAgeSeconds";
    public static final String PROPERTY_DIRECTORY_LISTING = "guicedee.swaggerui.directoryListing";
    public static final String PROPERTY_INDEX_PAGE = "guicedee.swaggerui.indexPage";

    public static final boolean DEFAULT_ENABLED = true;
    public static final String DEFAULT_ROUTE = "/swagger/*";
    public static final String DEFAULT_RESOURCE_ROOT = "swagger";
    public static final boolean DEFAULT_FILES_READ_ONLY = true;
    public static final boolean DEFAULT_CACHE_ENABLED = true;
    public static final long DEFAULT_CACHE_MAX_AGE_SECONDS = 86400L;
    public static final boolean DEFAULT_DIRECTORY_LISTING = false;
    public static final String DEFAULT_INDEX_PAGE = "index.html";

    /**
     * Adds the Swagger UI static handler to the supplied router.
     *
     * @param builder the router to configure
     * @return the same router instance after registration
     */
    @Override
    public Router builder(Router builder) {
        if (!readBoolean(PROPERTY_ENABLED, DEFAULT_ENABLED)) {
            return builder;
        }
        String route = normalizeRoute(readString(PROPERTY_ROUTE, DEFAULT_ROUTE));
        String resourceRoot = readString(PROPERTY_RESOURCE_ROOT, DEFAULT_RESOURCE_ROOT);
        StaticHandler handler = StaticHandler.create(resourceRoot)
                .setFilesReadOnly(readBoolean(PROPERTY_FILES_READ_ONLY, DEFAULT_FILES_READ_ONLY))
                .setCachingEnabled(readBoolean(PROPERTY_CACHE_ENABLED, DEFAULT_CACHE_ENABLED))
                .setMaxAgeSeconds(readLong(PROPERTY_CACHE_MAX_AGE_SECONDS, DEFAULT_CACHE_MAX_AGE_SECONDS))
                .setDirectoryListing(readBoolean(PROPERTY_DIRECTORY_LISTING, DEFAULT_DIRECTORY_LISTING))
                .setIndexPage(readString(PROPERTY_INDEX_PAGE, DEFAULT_INDEX_PAGE));
        builder.route(route).handler(handler);
        return builder;
    }

    private static String normalizeRoute(String route) {
        String normalized = route == null ? "" : route.trim();
        if (normalized.isEmpty()) {
            normalized = DEFAULT_ROUTE;
        }
        if (!normalized.startsWith("/")) {
            normalized = "/" + normalized;
        }
        if (normalized.endsWith("/")) {
            return normalized + "*";
        }
        if (normalized.endsWith("/*")) {
            return normalized;
        }
        return normalized + "/*";
    }

    private static String readString(String key, String defaultValue) {
        String value = getConfigValue(key);
        return value == null || value.isBlank() ? defaultValue : value.trim();
    }

    private static boolean readBoolean(String key, boolean defaultValue) {
        String value = getConfigValue(key);
        return value == null ? defaultValue : Boolean.parseBoolean(value.trim());
    }

    private static long readLong(String key, long defaultValue) {
        String value = getConfigValue(key);
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException ignored) {
            return defaultValue;
        }
    }

    private static String getConfigValue(String key) {
        return com.guicedee.client.Environment.getSystemPropertyOrEnvironment(key, null);
    }
}
