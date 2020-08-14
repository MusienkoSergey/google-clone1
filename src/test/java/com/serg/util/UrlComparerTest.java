package com.serg.util;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class UrlComparerTest {

    @Test
    public void shouldCompareTwoUrlsSuccessfully() {
        assertThat( "key a case different", UrlComparer.urlHostsMatch("https://WWW.TEST.COM?A=1&b=2", "https://www.test.com?b=2&a=1"), is(false));
        assertThat( "null", UrlComparer.urlHostsMatch("/test", null), is(false));
        assertThat( "null", UrlComparer.urlHostsMatch(null, "/test"), is(false));
        assertThat( "both null", UrlComparer.urlHostsMatch(null, null), is(false));
        assertThat( "protocol different", UrlComparer.urlHostsMatch("http://WWW.TEST.COM:2121", "https://www.test.com:2121"), is(false));
        assertThat( "protocol different", UrlComparer.urlHostsMatch("http://WWW.TEST.COM?A=a&b=2", "https://www.test.com?b=2&A=a"), is(false));
        assertThat( "host and scheme different case", UrlComparer.urlHostsMatch("HTTPS://WWW.TEST.COM", "https://www.test.com"), is(false));
        assertThat( "host different case", UrlComparer.urlHostsMatch("https://WWW.TEST.COM:443", "https://www.test.com"), is(false));
        assertThat( "port different", UrlComparer.urlHostsMatch("//test.com:22?A=a&B=b", "//test.com:443?A=a&B=b"), is(true));
        assertThat( "port different", UrlComparer.urlHostsMatch("https://www.test.com:8443", "https://www.test.com"), is(true));
        assertThat( "key a value different", UrlComparer.urlHostsMatch("/test?a=2&A=A", "/test?a=A&a=2"), is(true));
        assertThat( "identical urls", UrlComparer.urlHostsMatch("//test.com:443?A=a&B=b", "//test.com:443?A=a&B=b"), is(true));
        assertThat( "identical urls", UrlComparer.urlHostsMatch("/test?a=A&a=2", "/test?a=A&a=2"), is(true));
        assertThat( "identical urls", UrlComparer.urlHostsMatch("https://www.test.com", "https://www.test.com"), is(true));
        assertThat( "parameter order changed", UrlComparer.urlHostsMatch("https://www.test.com?a=1&b=2&c=522%2fMe", "https://www.test.com?c=522%2fMe&b=2&a=1"), is(true));
        assertThat( "parmeter order changed", UrlComparer.urlHostsMatch("https://www.test.com?a=1&b=2", "https://www.test.com?b=2&a=1"), is(true));
    }
}