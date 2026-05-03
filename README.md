# GuicedEE Swagger UI

[![Build](https://github.com/GuicedEE/SwaggerUI/actions/workflows/build.yaml/badge.svg)](https://github.com/GuicedEE/SwaggerUI/actions/workflows/build.yaml)
[![Maven Central](https://img.shields.io/maven-central/v/com.guicedee/guiced-swagger-ui)](https://central.sonatype.com/artifact/com.guicedee/guiced-swagger-ui)
[![Snapshots](https://img.shields.io/badge/Snapshots-GitHub%20Packages-orange)](https://github.com/orgs/GuicedEE/packages)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue)](https://www.apache.org/licenses/LICENSE-2.0)

![Java 25+](https://img.shields.io/badge/Java-25%2B-green)
![Guice 7](https://img.shields.io/badge/Guice-7%2B-green)
![Vert.x 5](https://img.shields.io/badge/Vert.x-5%2B-green)

**Browsersable Swagger UI** for the [GuicedEE](https://github.com/GuicedEE) / Vert.x stack.
Add the dependency and a fully functional Swagger UI is mounted at `/swagger/` automatically  it reads from your `/openapi.json` endpoint out of the box, with zero code required.

Built on [Swagger UI](https://swagger.io/tools/swagger-ui/)   [Vert.x](https://vertx.io/)   [Google Guice](https://github.com/google/guice)   JPMS module `com.guicedee.swaggerui`   Java 25+

## Installation

```xml
<dependency>
  <groupId>com.guicedee</groupId>
  <artifactId>guiced-swagger-ui</artifactId>
</dependency>
```

<details>
<summary>Gradle (Kotlin DSL)</summary>

```kotlin
implementation("com.guicedee:guiced-swagger-ui:2.0.1")
```
</details>

> **OpenAPI spec generation**  pair this module with the `openapi` module for automatic spec generation:
>
> ```xml
> <dependency>
>   <groupId>com.guicedee</groupId>
>   <artifactId>openapi</artifactId>
> </dependency>
> ```

## Features

- **Zero-configuration UI mounting**  `SwaggerUIRegistration` registers a Vert.x `StaticHandler` on the router at startup; Swagger UI is live immediately
- **Pre-bundled assets**  all Swagger UI static resources (HTML, JS, CSS, favicons) are included in the JAR under `swagger/`
- **Points at `/openapi.json` by default**  the bundled `swagger-initializer.js` is pre-configured to load the spec from `/openapi.json`
- **Fully configurable**  route path, resource root, caching, directory listing, and index page are all controllable via system properties or environment variables
- **Can be disabled**  set `guicedee.swaggerui.enabled=false` to skip registration entirely (e.g. in production)
- **JPMS + ServiceLoader friendly**  fully modular with `module-info.java` and `META-INF/services` descriptors

## Quick Start

**Step 1**  Add both dependencies (see [Installation](#installation)).

**Step 2**  Bootstrap GuicedEE:

```java
IGuiceContext.instance().inject();
```

**Step 3**  Open your browser:

```
http://localhost:8080/swagger/
```

That's it. The Swagger UI loads and reads from `/openapi.json` automatically. If the `openapi` module is on the classpath your Jakarta REST resources appear in the UI immediately.

## Architecture

```
Startup
  IGuiCeContext.instance()
    +-- %% SwaggerUIRegistration    (Vert.x RouterConfigurator, order MAX_VALUE-100)
        +-- %% Reads configuration from system properties / environment
            +-- Creates StaticHandler for classpath "swagger" directory
                +-- Mounts route on Router at /swagger/* (configurable)
```

### Request lifecycle

```
HTTP GET /swagger/index.html
  +-- !Vert.x Router
      !StaticHandler
        +-- Serves index.html from classpath:swagger/
        +-- Browser loads swagger-ui-bundle.js, CSS, etc.
        +-- swagger-initializer.js fetches(/openapi.json)
        +-- Swagger UI renders the API documentation
```

## Bundled assets

The following Swagger UI files are bundled in the JAR at `src/main/resources/swagger/`:

| File | Purpose |
|---|---|
| `index.html` | Entry point HTML page |
| `swagger-initializer.js` | Configures `SwaggerUIBundle` (points at `/openapi.json`) |
| `swagger-ui-bundle.js` | Core Swagger UI JavaScript |
| `swagger-ui-standalone-preset.js` | Standalone layout preset |
| `swagger-ui.css` / `index.css` | Stylesheets |
| `favicon-16x16.png` / `favicon-32x32.png` | Favicons |
| `oauth2-redirect.html` | OAauth 2 redirect handler |

## Configuration

All configuration is driven by system properties with automatic environment variable fallbacks (via `Environment.getSystemPropertyOrEnvironment`).

