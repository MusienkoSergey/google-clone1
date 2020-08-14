package com.serg.service.fakeurlconnection;

import org.mockito.Mockito;

import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;

public class HttpUrlStreamHandler extends URLStreamHandler {
    private final Map<URL, URLConnection> connections = new HashMap<>();

    @Override
    protected URLConnection openConnection(URL url) {
        return connections.get(url);
    }

    public void resetConnections() {
        connections.clear();
    }

    public HttpUrlStreamHandler addFakeResponse(URL url, String resource) {
        connections.put(url, new FakeResourceHttpURLConnection(url, resource));
        return this;
    }

    public HttpUrlStreamHandler addFakeResponse(String urlSpec, String resource) throws IOException {
        addFakeResponse(new URL(urlSpec), resource);
        return this;
    }

    public static HttpUrlStreamHandler getInstants() {
        return HttpUrlStreamHandlerInner.INSTANCE;
    }

    private static class HttpUrlStreamHandlerInner {
        private static final URLStreamHandlerFactory URL_STREAM_HANDLER_FACTORY = Mockito.mock(URLStreamHandlerFactory.class);
        private static final HttpUrlStreamHandler INSTANCE = new HttpUrlStreamHandler();

        static {
            URL.setURLStreamHandlerFactory(URL_STREAM_HANDLER_FACTORY);
            when(URL_STREAM_HANDLER_FACTORY.createURLStreamHandler("https")).thenReturn(INSTANCE);
            when(URL_STREAM_HANDLER_FACTORY.createURLStreamHandler("http")).thenReturn(INSTANCE);
        }
    }
}
