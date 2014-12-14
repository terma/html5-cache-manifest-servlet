cache-manifest-servlet
======================

[![Build Status](https://travis-ci.org/terma/cache-manifest-servlet.svg)](https://travis-ci.org/terma/cache-manifest-servlet)

## How to use

In your ```web.xml``` add mapping on servlet:

```xml
<servlet-mapping>
  <servlet-name>CacheManifestServlet</servlet-name>
  <url-pattern>/cache.manifest</url-pattern>
</servlet-mapping>
```

Now add servlet configuration:

```xml
<servlet>
  <servlet-name>CacheManifestServlet</servlet-name>
  <servlet-class>com.terma.cachemanifestservlet.CacheManifestServlet</servlet-class>
  <init-param>
    <param-name>resources</param-name>
    <param-value>
      /script.js
    </param-value>
  </init-param>
</servlet>
```

As result if you run your webapp and goto ```http://localhost/cache.manifest``` you will find smt like:

```
CACHE MANIFEST
# SHA f9a3d9d23bafa3698d4e94ee79721252eb8ce31747e7c9d3379b5f0dfcc4584

CACHE:
/script.js

NETWORK:
*
```

### Multifile configuration

```xml
<param-value>
  /script.js,
  /index.html,
  /img/background.png
</param-value>
```
