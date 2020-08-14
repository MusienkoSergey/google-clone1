package com.serg.service;

import com.serg.model.HtmlInfo;
import com.serg.util.UrlComparer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

public class HtmlParser {

    public static HtmlInfo parse(String url) throws IOException {
        Document document = Jsoup.connect(url).get();
        return composeHtmlInfo(document);
    }

    private static HtmlInfo composeHtmlInfo(Document document) {
        HtmlInfo htmlInfo = new HtmlInfo();
        htmlInfo.setUrl(document.baseUri());
        htmlInfo.setTitle(document.title());
        htmlInfo.setContent(document.text());
        htmlInfo.setChildUrls(getLinks(document));
        return htmlInfo;
    }

    private static Set<String> getLinks(Document document) {
        return document.select("a").stream()
                .map(link -> link.absUrl("href"))
                .filter(link -> UrlComparer.urlHostsMatch(document.baseUri(), link))
                .collect(toSet());
    }
}
