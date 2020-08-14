package com.serg.service.fakeurlconnection;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class FakeResourceHttpURLConnection extends HttpURLConnection {

    private final String resourceFileName;

    protected FakeResourceHttpURLConnection(URL url, String resourceFileName) {
        super(url);
        this.resourceFileName = resourceFileName;
    }

    @Override
    public void disconnect() {}

    @Override
    public boolean usingProxy() {
        return false;
    }

    @Override
    public void connect() {}

    @Override
    public InputStream getInputStream() {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceFileName);
    }

    @Override
    public int getResponseCode() {
        if (url.toString().contains("not-found")) {
            return 404;
        }
        return 200;
    }
}
