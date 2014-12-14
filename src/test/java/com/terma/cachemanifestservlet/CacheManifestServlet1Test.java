package com.terma.cachemanifestservlet;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.mockito.Mockito.*;

public class CacheManifestServlet1Test {

    private HttpServletRequest request = null; //Mockito.mock(HttpServletRequest.class);

    private ServletContext servletContext = mock(ServletContext.class);

    private ServletConfig servletConfig = mock(ServletConfig.class);

    private StringWriter stringWriter = new StringWriter();
    private PrintWriter printWriter = new PrintWriter(stringWriter);
    private HttpServletResponse response = mock(HttpServletResponse.class);

    private CacheManifestServlet servlet = new CacheManifestServlet();

    @Before
    public void setup() throws Exception {
        Mockito.when(response.getWriter()).thenReturn(printWriter);
        Mockito.when(servletConfig.getServletContext()).thenReturn(servletContext);
        Mockito.when(servletContext.getResourceAsStream("/index.html")).thenReturn(new ByteArrayInputStream("TEST MESSAGE".getBytes()));
        Mockito.when(servletContext.getResourceAsStream("/moma/noam.jsp")).thenReturn(new ByteArrayInputStream("M".getBytes()));
    }

    @Test
    public void shouldReturnCacheManifestContentType() throws Exception {
        // given
        servlet.init(servletConfig);
        // when
        servlet.doGet(request, response);
        // then
        verify(response).setContentType(CacheManifestServlet.CONTENT_TYPE);
    }

    @Test
    public void givenNoResourcesShouldProvideShaAndAllFromNetwork() throws Exception {
        // given
        servlet.init(servletConfig);
        // when
        servlet.doGet(request, response);
        // then
        Assert.assertEquals(
                "" +
                        "CACHE MANIFEST\n" +
                        "# SHA e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855\n" +
                        "\n" +
                        "CACHE:\n" +
                        "\n" +
                        "NETWORK:\n" +
                        "*\n",
                stringWriter.toString());
    }

    @Test
    public void givenOneResourceShouldAddItToCached() throws Exception {
        // given
        when(servletConfig.getInitParameter("resources")).thenReturn("/index.html");
        servlet.init(servletConfig);
        // when
        servlet.doGet(request, response);
        // then
        Assert.assertEquals(
                "" +
                        "CACHE MANIFEST\n" +
                        "# SHA 2abf4ca96fb2b2ae884d43bf23a2e6a92ca22672721dffa35a5af21d9b811ee\n" +
                        "\n" +
                        "CACHE:\n" +
                        "/index.html\n" +
                        "\n" +
                        "NETWORK:\n" +
                        "*\n",
                stringWriter.toString());
    }

    @Test
    public void givenMultiResourcesByCommaShouldAddThemToCached() throws Exception {
        // given
        when(servletConfig.getInitParameter("resources")).thenReturn("/index.html,/moma/noam.jsp");
        servlet.init(servletConfig);
        // when
        servlet.doGet(request, response);
        // then
        Assert.assertEquals(
                "" +
                        "CACHE MANIFEST\n" +
                        "# SHA 31b7d82f91d8124de396a4c895434db3f063a58fec7215388bbf7bf78b2132f8\n" +
                        "\n" +
                        "CACHE:\n" +
                        "/index.html\n" +
                        "/moma/noam.jsp\n" +
                        "\n" +
                        "NETWORK:\n" +
                        "*\n",
                stringWriter.toString());
    }

    @Test
    public void shouldTrimResourcesBeforeUsage() throws Exception {
        // given
        when(servletConfig.getInitParameter("resources")).thenReturn(" /a.html   , /b.html");
        servlet.init(servletConfig);
        // when
        servlet.doGet(request, response);
        // then
        Assert.assertEquals(
                "" +
                        "CACHE MANIFEST\n" +
                        "# SHA 4a94b5dbf54698f0abbe25901e4153a0fad8d8a8b9f1485f2bac65f38afde0ca\n" +
                        "\n" +
                        "CACHE:\n" +
                        "/a.html\n" +
                        "/b.html\n" +
                        "\n" +
                        "NETWORK:\n" +
                        "*\n",
                stringWriter.toString());
    }

    @Test
    public void shouldAllowDefineDifferentNameForMappingInManifest() throws Exception {
        // given
        when(servletConfig.getInitParameter("resources")).thenReturn("/a.html=/b.html");
        servlet.init(servletConfig);
        // when
        servlet.doGet(request, response);
        // then
        Assert.assertEquals(
                "" +
                        "CACHE MANIFEST\n" +
                        "# SHA 1016979378e94f92aa94e8f26962bd3ee57ff3c2d628689f1c02d96b07f4e80\n" +
                        "\n" +
                        "CACHE:\n" +
                        "/b.html\n" +
                        "\n" +
                        "NETWORK:\n" +
                        "*\n",
                stringWriter.toString());
    }

}
