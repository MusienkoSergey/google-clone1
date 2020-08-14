package com.serg.util;

import org.apache.commons.lang3.StringUtils;

import java.net.URI;

public class UrlComparer {
    public static boolean urlHostsMatch(String url1, String url2) {
        try {
            return StringUtils.equals(URI.create(url1).getHost(), URI.create(url2).getHost());
        } catch (Exception e) {
            return false;
        }
    }
}
