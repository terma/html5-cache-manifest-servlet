package com.terma.cachemanifestservlet;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
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
        Mockito.when(servletContext.getResourceAsStream("/img/background.png")).thenReturn(new ByteArrayInputStream("M".getBytes()));
    }

    @Test
    public void shouldReturnCacheManifestContentType() throws Exception {
        // given
        when(servletConfig.getInitParameter("resources")).thenReturn("");
        servlet.init(servletConfig);
        // when
        servlet.doGet(request, response);
        // then
        verify(response).setContentType(CacheManifestServlet.CONTENT_TYPE);
    }

    @Test
    public void givenNoResourcesShouldProvideShaAndAllFromNetwork() throws Exception {
        // given
        when(servletConfig.getInitParameter("resources")).thenReturn(" ");
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
        when(servletConfig.getInitParameter("resources")).thenReturn("/index.html,/img/background.png");
        servlet.init(servletConfig);
        // when
        servlet.doGet(request, response);
        // then
        Assert.assertEquals(
                "" +
                        "CACHE MANIFEST\n" +
                        "# SHA 90c9869d9145c76d9743c35a19ecab5edf6f963f03071ed7871d9141d95c22\n" +
                        "\n" +
                        "CACHE:\n" +
                        "/index.html\n" +
                        "/img/background.png\n" +
                        "\n" +
                        "NETWORK:\n" +
                        "*\n",
                stringWriter.toString());
    }

    @Test
    public void shouldTrimResourcesBeforeUsage() throws Exception {
        // given
        when(servletConfig.getInitParameter("resources")).thenReturn(" /index.html   , /img/background.png ");
        servlet.init(servletConfig);
        // when
        servlet.doGet(request, response);
        // then
        Assert.assertEquals(
                "" +
                        "CACHE MANIFEST\n" +
                        "# SHA 90c9869d9145c76d9743c35a19ecab5edf6f963f03071ed7871d9141d95c22\n" +
                        "\n" +
                        "CACHE:\n" +
                        "/index.html\n" +
                        "/img/background.png\n" +
                        "\n" +
                        "NETWORK:\n" +
                        "*\n",
                stringWriter.toString());
    }

    @Test
    public void shouldAllowDefineDifferentNameForMappingInManifest() throws Exception {
        // given
        when(servletConfig.getInitParameter("resources")).thenReturn("/b.html=/index.html");
        servlet.init(servletConfig);
        // when
        servlet.doGet(request, response);
        // then
        Assert.assertEquals(
                "" +
                        "CACHE MANIFEST\n" +
                        "# SHA 49a36366bbd85cfc44e8e28afe525e38b12865da66bfcdaa7abf6d87355b14\n" +
                        "\n" +
                        "CACHE:\n" +
                        "/b.html\n" +
                        "\n" +
                        "NETWORK:\n" +
                        "*\n",
                stringWriter.toString());
    }

    @Test(expected = ServletException.class)
    public void shouldThrowExceptionIfCantLoadResource() throws Exception {
        // given
        when(servletConfig.getInitParameter("resources")).thenReturn("/no_found.html");
        // when
        servlet.init(servletConfig);
    }

    @Test(expected = ServletException.class)
    public void shouldThrowExceptionIfEmptyAlias() throws Exception {
        // given
        when(servletConfig.getInitParameter("resources")).thenReturn("/index.html=");
        // when
        servlet.init(servletConfig);
    }

    @Test(expected = ServletException.class)
    public void shouldThrowExceptionIfNoResourcesInitParameterProvided() throws Exception {
        // given
        when(servletConfig.getInitParameter("resources")).thenReturn(null);
        // when
        servlet.init(servletConfig);
    }

}
