package com.terma.cachemanifestservlet;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

class ResourceDigest {

    private final MessageDigest digest;

    public ResourceDigest() throws NoSuchAlgorithmException {
        digest = MessageDigest.getInstance("SHA-256");
    }

    public void update(final String resourceName, final InputStream resourceContent) throws IOException {
        if (resourceName != null) digest.update(resourceName.getBytes());
        if (resourceContent != null) updateShaByResource(resourceContent);
    }

    public String digest() throws Exception {
        final byte[] mdbytes = digest.digest();

        //convert the byte to hex format method 2
        StringBuilder hexString = new StringBuilder();
        for (byte mdbyte : mdbytes) {
            hexString.append(Integer.toHexString(0xFF & mdbyte));
        }

        return hexString.toString();
    }

    private void updateShaByResource(final InputStream resourceContent) throws IOException {
        final byte[] dataBytes = new byte[1024];
        int nread;
        while ((nread = resourceContent.read(dataBytes)) != -1) {
            digest.update(dataBytes, 0, nread);
        }
    }

}
