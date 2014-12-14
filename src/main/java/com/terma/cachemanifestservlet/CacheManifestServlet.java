package com.terma.cachemanifestservlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class CacheManifestServlet extends HttpServlet {

    public static final String CONTENT_TYPE = "text/cache-manifest";
    public static final String RESOURCES_PARAMETER = "resources";

    private static String content;

    @Override
    public void init(final ServletConfig config) throws ServletException {
        super.init(config);

        final List<Resource> resources = parseResources(config);

        final String sha;
        try {
            sha = calcResourcesSha(resources);
        } catch (final Exception ex) {
            throw new ServletException("Can't calculate digest for " + resources, ex);
        }

        final StringBuilder contentBuffer = new StringBuilder();
        contentBuffer.append("CACHE MANIFEST").append("\n");
        contentBuffer.append("# SHA ").append(sha).append("\n");
        contentBuffer.append("\n");

        contentBuffer.append("CACHE:").append("\n");
        for (final Resource resource : resources) {
            contentBuffer.append(resource.alias).append("\n");
        }
        contentBuffer.append("\n");

        contentBuffer.append("NETWORK:").append("\n");
        contentBuffer.append("*").append("\n");

        content = contentBuffer.toString();
    }

    @Override
    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("utf8");
        response.setContentType(CONTENT_TYPE);
        response.getWriter().append(content);
    }

    private List<Resource> parseResources(final ServletConfig config) throws ServletException {
        final List<Resource> resources = new ArrayList<Resource>();

        final String resourcesValue = config.getInitParameter(RESOURCES_PARAMETER);
        if (resourcesValue != null) {
            for (final String resourceString : resourcesValue.split(",")) {
                final int aliasIndex = resourceString.indexOf('=');
                final Resource resource;
                if (aliasIndex > -1) {
                    final String alias = resourceString.substring(0, aliasIndex).trim();
                    final String name = resourceString.substring(aliasIndex + 1).trim();
                    if (alias.isEmpty()) throw new ServletException(
                            "Empty alias provided if that's what you want just remove =!");
                    resource = new Resource(name, alias);
                } else {
                    resource = new Resource(
                            resourceString.trim(),
                            resourceString.trim());
                }
                resources.add(resource);
            }
        }

        return resources;
    }

    private String calcResourcesSha(final List<Resource> resources) throws Exception {
        final ResourceDigest digest = new ResourceDigest();
        for (final Resource resource : resources) {
            final InputStream stream = getServletContext().getResourceAsStream(resource.name);
            if (stream == null) throw new IOException("Can't load resource " + resource.name);
            digest.update(resource.alias + resource.name, stream);
        }
        return digest.digest();
    }

    private class Resource {

        final String name;
        final String alias;

        private Resource(String name, String alias) {
            this.name = name;
            this.alias = alias;
        }

        @Override
        public String toString() {
            return name;
        }

    }

}
