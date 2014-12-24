package com.att.hackaroomie.model;

import com.facebook.model.GraphUser;

/**
 * Created by Rohith Ravindranath on 12/20/14.
 * Represents the base user for the app
 */


public class User {

    private String id;
    private String mSocialId;

    private String mDisplayName = "";
    private String mPublicProfileUrl = "";
    private String mPublicProfilePhoto= "";
    private String mCurrentLocation = "";
    private String mProfileType = "";
    private String mEmail = "";
    private String mName = "";
    private String mPublicProfile = "";
    private String mCoverPhoto = "";
    private String mBirthDate = "";

    private GraphUser mGraphUser;


    public User(){

    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() { return this.id; }

    public void setSocialId(String id) { this.mSocialId = id; }

    public void setDisplayName(String displayName) {
        this.mDisplayName = displayName;
    }

    public String getDisplayName() { return this.mDisplayName; }

    public void setLocation(String location) {
        this.mCurrentLocation = location;
    }

    public void setPhotoUrl(String photoUrl) {
        this.mPublicProfilePhoto = photoUrl;
    }

    public void setPublicProfile(String profile) {
        this.mPublicProfile = profile;
    }

    public void setEmail(String email) {
        this.mEmail = email;
    }

    public String getCoverPhoto() {
        return mCoverPhoto;
    }

    public void setCoverPhoto(String coverPhoto) {
        this.mCoverPhoto = coverPhoto;
    }

    public void setFacebookUser(GraphUser graphUser){
        mGraphUser = graphUser;
    }

    public GraphUser getFacebookUser(){
        return mGraphUser;
    }

    public void setBirthDate(String birthDate){
        mBirthDate = birthDate;
    }

    public String getBirthDate(){
        return mBirthDate;
    }

}
