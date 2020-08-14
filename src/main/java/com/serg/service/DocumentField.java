package com.serg.service;

public enum DocumentField {
    URL("url"),
    TITLE("title"),
    CONTENTS("contents");

    private final String name;

    DocumentField(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
