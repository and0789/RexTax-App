package com.task.dynamicregex.entities;

import javafx.scene.control.CheckBox;

public class SocmedRegex {

    private String id;

    private String field;

    private String regex;

    private SocialMedia socialMedia;

    private CheckBox mark;

    public SocmedRegex(String id, String field, String regex, SocialMedia socialMedia, CheckBox mark) {
        this.id = id;
        this.field = field;
        this.regex = regex;
        this.socialMedia = socialMedia;
        this.mark = mark;
    }

    public SocmedRegex(String id, String field, String regex, SocialMedia socialMedia) {
        this.id = id;
        this.field = field;
        this.regex = regex;
        this.socialMedia = socialMedia;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public SocialMedia getSocialMedia() {
        return socialMedia;
    }

    public void setSocialMedia(SocialMedia socialMedia) {
        this.socialMedia = socialMedia;
    }

    public CheckBox getMark() {
        return mark;
    }

    public void setMark(CheckBox mark) {
        this.mark = mark;
    }

}
