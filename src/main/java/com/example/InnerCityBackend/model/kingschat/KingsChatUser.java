package com.example.InnerCityBackend.model.kingschat;

@lombok.Data
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
public class KingsChatUser {
    private String name;
    private String user_id;
    private String superuser;
    private String avatar_url;
    private String username;
    private boolean is_blogger;
    private String user_bio;
    private boolean private_account;
    private int posts_count;

    public String getUserId() { return user_id; }
    public String getAvatarUrl() { return avatar_url; }
    public String getUserBio() { return user_bio; }
}
