package com.serg.service;

import com.serg.service.fakeurlconnection.HttpUrlStreamHandler;
import org.apache.commons.io.IOUtils;
import org.jsoup.HttpStatusException;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.util.Set;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class HtmlParserTest {

    private final HttpUrlStreamHandler httpUrlStreamHandler = HttpUrlStreamHandler.getInstants();
    private static final String TEST_URL = "https://my.test.com/index/index.html";
    private static final String RESOURCE_NOT_FOUND = "https://my.test.com/index/index-not-found.html";

    @BeforeEach
    void setUp() throws Exception {
        httpUrlStreamHandler.resetConnections();
        httpUrlStreamHandler.addFakeResponse(TEST_URL, "index/index.html");
        httpUrlStreamHandler.addFakeResponse(RESOURCE_NOT_FOUND, "index/index.html");
    }

    @Test
    void shouldThrowHttpStatusExceptionWhenParsingHtmlIfResourceNotFound() throws Exception {
        HttpStatusException thrown = assertThrows(HttpStatusException.class, () ->
                HtmlParser.parse(RESOURCE_NOT_FOUND)
        );

        int statusNotFound = 404;
        assertEquals(statusNotFound, thrown.getStatusCode());
    }

    @Test
    void shouldGetCombinedHtmlTextWithNormalizedAndTrimmedSpaces() throws Exception {
        String indexHtml = HtmlParser.parse(TEST_URL).getContent();

        String expectedHtmlText = resource("index/text_index.txt");
        assertEquals(expectedHtmlText, indexHtml);
    }

    @Test
    void shouldGetHtmlTitleSuccessfully() throws Exception {
        String indexHtml = HtmlParser.parse(TEST_URL).getTitle();

        String expectedTitle = "Web Content";
        assertEquals(expectedTitle, indexHtml);
    }

    @Test
    void shouldExtractOnlyChildLinksWithAbsoluteURLsFromHTML() throws Exception {
        Set<String> indexHtml = HtmlParser.parse(TEST_URL).getChildUrls();

        Set<String> expectedHtmlInternalLinks = Set.of("https://my.test.com/test-link");
        assertEquals(expectedHtmlInternalLinks, indexHtml);
    }

    public static String resource(String name) throws IOException {
        return IOUtils.resourceToString(name, UTF_8, Thread.currentThread().getContextClassLoader());
    }
}