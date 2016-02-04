package com.example.ourstars;

import java.io.Serializable;

public class ArtistDataModel implements Serializable {
	private int id;
	private String name;
    private int all;
    private int recent;
    private String url;
    private int rank;

    public ArtistDataModel(int id, String name, int all, int recent, String url,int rank) {
        this.id = id;
    	this.name = name;
        this.all = all;
        this.recent = recent;
        this.url = url;
        this.rank=rank;
    }
 
    public int getId() {
        return id;
    }
 
    public void setId(int id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
 
    public void setName(String name) {
        this.name = name;
    }
    
    public int getAll() {
        return all;
    }
 
    public void setAll(int all) {
        this.all = all;
    }
    
    public int getRecent() {
        return recent;
    }
 
    public void setRecent(int recent) {
        this.recent = recent;
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
    
}