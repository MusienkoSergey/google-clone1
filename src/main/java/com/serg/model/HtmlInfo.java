package com.serg.model;

import java.util.Set;

public class HtmlInfo {

    private String url;

    private String title;

    private String content;

    private Set<String> childUrls;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Set<String> getChildUrls() {
        return childUrls;
    }

    public void setChildUrls(Set<String> childUrls) {
        this.childUrls = childUrls;
    }
}
