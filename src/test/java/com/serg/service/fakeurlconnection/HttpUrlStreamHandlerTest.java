package com.serg.service.fakeurlconnection;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URL;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpUrlStreamHandlerTest {
    private final HttpUrlStreamHandler httpUrlStreamHandler = HttpUrlStreamHandler.getInstants();

    @BeforeEach
    void setUp() {
        httpUrlStreamHandler.resetConnections();
    }

    @Test
    void shouldReturnFakeContentAtTheSpecifiedUrl() throws Exception {
        URL url = new URL("https://some.url.that.com/want/to/mock.html");
        httpUrlStreamHandler.addFakeResponse(url, "fake.html");

        String actualResourceContent = IOUtils.toString(url, UTF_8);
        String expectedResourceContent = "test content";
        assertEquals(expectedResourceContent, actualResourceContent);
    }
}
