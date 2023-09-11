package com.andreseptian.entities;

import javafx.scene.control.CheckBox;

public class SocmedRegex {

    private final String id;

    private final String field;

    private final String regex;

    private final ArtifactCategory artifactCategory;

    private CheckBox mark;

    public SocmedRegex(String id, String field, String regex, ArtifactCategory artifactCategory, CheckBox mark) {
        this.id = id;
        this.field = field;
        this.regex = regex;
        this.artifactCategory = artifactCategory;
        this.mark = mark;
    }

    public SocmedRegex(String id, String field, String regex, ArtifactCategory artifactCategory) {
        this.id = id;
        this.field = field;
        this.regex = regex;
        this.artifactCategory = artifactCategory;
    }

    public String getId() {
        return id;
    }

    public String getField() {
        return field;
    }

    public String getRegex() {
        return regex;
    }

    public ArtifactCategory getArtifactCategory() {
        return artifactCategory;
    }

    public CheckBox getMark() {
        return mark;
    }

}
