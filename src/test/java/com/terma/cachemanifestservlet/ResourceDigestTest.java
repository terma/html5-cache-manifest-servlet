package com.terma.cachemanifestservlet;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.security.NoSuchAlgorithmException;

public class ResourceDigestTest {

    private ResourceDigest digest;

    @Before
    public void setup() throws NoSuchAlgorithmException {
        digest = new ResourceDigest();
    }

    @Test
    public void shouldGetDigestIfNoResourcesAdded() throws Exception {
        Assert.assertNotNull(digest.digest());
    }

    @Test
    public void shouldGetDiffDigestForOneResourceCompareNoResources() throws Exception {
        String noResourcesDigest = digest.digest();

        digest.update("a", new ByteArrayInputStream("".getBytes()));
        String oneResourceDigest  = digest.digest();

        Assert.assertNotEquals(noResourcesDigest, oneResourceDigest);
    }

    @Test
    public void shouldGetDiffDigestForMultiResourceCompareOneResource() throws Exception {
        digest.update("a", new ByteArrayInputStream("".getBytes()));
        String noResourcesDigest = digest.digest();

        digest.update("a", new ByteArrayInputStream("1".getBytes()));
        digest.update("b", new ByteArrayInputStream("2".getBytes()));
        String oneResourceDigest  = digest.digest();

        Assert.assertNotEquals(noResourcesDigest, oneResourceDigest);
    }

    @Test
    public void shouldGetDiffDigestForSameContentButDiffName() throws Exception {
        digest.update("a", new ByteArrayInputStream("".getBytes()));
        String d1 = digest.digest();

        digest.update("b", new ByteArrayInputStream("".getBytes()));
        String d2  = digest.digest();

        Assert.assertNotEquals(d1, d2);
    }

    @Test
    public void shouldGetDiffDigestForSameNameButDiffContent() throws Exception {
        digest.update("", new ByteArrayInputStream("1".getBytes()));
        String d1 = digest.digest();

        digest.update("", new ByteArrayInputStream("2".getBytes()));
        String d2  = digest.digest();

        Assert.assertNotEquals(d1, d2);
    }

    @Test
    public void shouldGetDigestForNullName() throws Exception {
        digest.update(null, new ByteArrayInputStream("1".getBytes()));
        String d1 = digest.digest();

        Assert.assertNotNull(d1);
    }

    @Test
    public void shouldGetDigestForNullContent() throws Exception {
        digest.update("", null);
        String d1 = digest.digest();

        Assert.assertNotNull(d1);
    }

}
