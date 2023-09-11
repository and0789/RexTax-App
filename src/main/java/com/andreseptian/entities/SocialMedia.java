package com.andreseptian.entities;

public record SocialMedia(String id, String name) {

    @Override
    public String toString() {
        return name;
    }

}
