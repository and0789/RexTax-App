package com.task.dynamicregex.entities;

public record ArtifactCategory(String id, String name, SocialMedia socialMedia) {

    @Override
    public String toString() {
        return name;
    }

}
