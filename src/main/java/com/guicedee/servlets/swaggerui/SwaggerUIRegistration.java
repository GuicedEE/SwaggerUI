package com.guicedee.servlets.swaggerui;

import com.google.common.base.Strings;
import com.guicedee.vertx.web.spi.VertxRouterConfigurator;
import io.vertx.core.http.MimeMapping;
import io.vertx.ext.web.Router;

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
     * <p>
     * Uses direct classpath resource loading via the module's {@link ClassLoader}
     * to serve files, which is compatible with JPMS where the Vert.x
     * static handler file resolver cannot access module resources.
     * </p>
     *
     * @param router the router to configure
     * @return the same router instance after registration
     */
    @Override
    public Router builder(Router router) {
        if (!readBoolean(PROPERTY_ENABLED, DEFAULT_ENABLED)) {
            return router;
        }
        String routePrefix = readString(PROPERTY_ROUTE, DEFAULT_ROUTE);
        // Strip trailing /* for path prefix extraction
        String pathPrefix = routePrefix.endsWith("/*")
                ? routePrefix.substring(0, routePrefix.length() - 1)
                : routePrefix.endsWith("*")
                ? routePrefix.substring(0, routePrefix.length() - 1)
                : routePrefix;
        if (!pathPrefix.startsWith("/")) {
            pathPrefix = "/" + pathPrefix;
        }
        String route = normalizeRoute(routePrefix);
        String resourceRoot = readString(PROPERTY_RESOURCE_ROOT, DEFAULT_RESOURCE_ROOT);
        String indexPage = readString(PROPERTY_INDEX_PAGE, DEFAULT_INDEX_PAGE);
        long maxAge = readLong(PROPERTY_CACHE_MAX_AGE_SECONDS, DEFAULT_CACHE_MAX_AGE_SECONDS);
        boolean cacheEnabled = readBoolean(PROPERTY_CACHE_ENABLED, DEFAULT_CACHE_ENABLED);

        // Use Module.getResourceAsStream() — in JPMS, non-package resources
        // (like swagger/) are encapsulated and not accessible via ClassLoader
        // from other modules. Module.getResourceAsStream() works because it
        // resolves resources within the module's own namespace.
        Module swaggerModule = SwaggerUIRegistration.class.getModule();

        System.out.println("[SwaggerUI] Registering route: " + route + " with prefix: " + pathPrefix + " resourceRoot: " + resourceRoot);
        System.out.println("[SwaggerUI] Module: " + swaggerModule.getName() + ", isNamed: " + swaggerModule.isNamed());
        // Quick probe of the resource
        try {
            var probe = swaggerModule.getResourceAsStream(resourceRoot + "/" + indexPage);
            System.out.println("[SwaggerUI] Probe " + resourceRoot + "/" + indexPage + " => " + probe);
            if (probe != null) probe.close();
        } catch (Exception e) {
            System.out.println("[SwaggerUI] Probe failed: " + e);
        }

        final String prefix = pathPrefix;
        router.get(route).handler(ctx -> {
            System.out.println("[SwaggerUI] Handler invoked for: " + ctx.normalizedPath());
            String normalisedPath = ctx.normalizedPath();
            String path = normalisedPath.length() > prefix.length()
                    ? normalisedPath.substring(prefix.length())
                    : "";
            // Strip leading slash from path
            if (path.startsWith("/")) {
                path = path.substring(1);
            }
            if (path.isEmpty()) {
                path = indexPage;
            }

            String resource = resourceRoot + "/" + path;

            try (var in = swaggerModule.getResourceAsStream(resource)) {
                if (in == null) {
                    ctx.response().setStatusCode(404).end();
                    return;
                }
                var response = ctx.response()
                        .putHeader("content-type", MimeMapping.mimeTypeForFilename(path));
                if (cacheEnabled) {
                    response.putHeader("cache-control", "public, max-age=" + maxAge);
                }
                response.end(io.vertx.core.buffer.Buffer.buffer(in.readAllBytes()));
            } catch (Exception e) {
                ctx.fail(e);
            }
        });


        return router;
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
        return Strings.isNullOrEmpty(value) ? defaultValue : value.trim();
    }

    private static boolean readBoolean(String key, boolean defaultValue) {
        String value = getConfigValue(key);
        return Strings.isNullOrEmpty(value)? defaultValue : Boolean.parseBoolean(value.trim());
    }

    private static long readLong(String key, long defaultValue) {
        String value = getConfigValue(key);
        if (Strings.isNullOrEmpty(value)) {
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
