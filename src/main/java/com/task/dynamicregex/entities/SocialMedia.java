package com.task.dynamicregex.entities;

public record SocialMedia(String id, String name) {

    @Override
    public String toString() {
        return name;
    }

}
