package com.example.fetchrewardslist;

import java.io.Serializable;

public class ListData implements Serializable {

    private String id;
    private String listId;
    private String name;

    public ListData(String id, String listId, String name){
        this.id = id;
        this.listId = listId;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getListId() {
        return listId;
    }

    public void setListId(String listId) {
        this.listId = listId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
