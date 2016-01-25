package com.arrg.app.uvault.model;

import java.io.Serializable;

/*
 * Created by albert on 14/01/2016.
 */
public class UFile implements Serializable {

    private Boolean isSelected;
    private String path;

    public UFile(String path) {
        this.isSelected = false;
        this.path = path;
    }

    public Boolean getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(Boolean isSelected) {
        this.isSelected = isSelected;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
