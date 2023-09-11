package com.andreseptian.entities;

public class ArtifactCategory {

    private String id;

    private String name;

    private final SocialMedia socialMedia;

    public ArtifactCategory(String id, String name, SocialMedia socialMedia) {
        this.id = id;
        this.name = name;
        this.socialMedia = socialMedia;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SocialMedia getSocialMedia() {
        return socialMedia;
    }

    @Override
    public String toString() {
        return name;
    }

}
