cache-manifest-servlet
======================

[![Build Status](https://travis-ci.org/terma/cache-manifest-servlet.svg)](https://travis-ci.org/terma/cache-manifest-servlet)

## Intro

HTML5 provides awesome feature [Application Cache](http://www.html5rocks.com/en/tutorials/appcache/beginner/)

This feature bases on ```cache.manifest``` file which browser use to determine which resources should be accessible in offline mode. Each time when you open page browser use cached version of page and resources however in background check if ```cache.manifest``` was changed on server if yes it reload resources.

Browser updates cached resources **only if content** of ```cache.manifest``` was changed. So if you change some script file and forget to modify manifest file you will still see old script in browser!

That's mean you need to keep in mind *update cache.manifest* after any resource update

## Easy way

This servlet can generate ```cache.manifest``` file for you and ensure that of it will be updated if you made any changes in your scripts

## How it works

Each time when webapp started servlet calculates SHA of all resources in ```cache.manifest``` and provide it like comment in manifest file
```
CACHE MANIFEST
# SHA 49a36366bbd85cfc44e8e28afe525e38b12865da66bfcdaa7abf6d87355b14
...
```
as result browser will find updated version of manifest if any resources were changed

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

### Virtual resource

Sometime you resource name in your webapp has different name than public name (in manifest file as well) for example page ```/WEB-INF/jsp/page.jsp``` could be mapped to ```/``` to achive that

```xml
<param-value>
  /=/WEB-INF/jsp/page.jsp
</param-value>
```
