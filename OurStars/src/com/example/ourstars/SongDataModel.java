package com.example.ourstars;

import java.io.Serializable;

public class SongDataModel implements Serializable {
	private String id;
	private String name;
    private int viewcount;
    private int release;
    private String duration;
    private String url;
    private int rank;
    private String language;
    private String country;

    public SongDataModel(String id, String name, int viewcount, int release, String duration, String url,int rank, String language, String country) {
        this.id = id;
    	this.name = name;
        this.viewcount=viewcount;
        this.release = release;
        this.duration=duration;
        this.url = url;
        this.rank=rank;
        this.language=language;
        this.country=country;
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
    
    public int getViewcount() {
        return viewcount;
    }
 
    public void setViewcount(int viewcount) {
        this.viewcount = viewcount;
    }
    
    public int getRelease() {
        return release;
    }
 
    public void setRelease(int release) {
        this.release = release;
    }
    
    public String getDuration() {
        return duration;
    }
 
    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getUrl() {
        return url;
    }
 
    public void setUrl(String url) {
        this.url = url;
    }

    public int getRank() {
        return rank;
    }
 
    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getLanguage() {
        return language;
    }
 
    public void setLanguage(String language) {
        this.language=language;
    }

    public String getCountry() {
        return country;
    }
 
    public void setCountry(String country) {
        this.country=country;
    }
    
}