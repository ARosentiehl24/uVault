package com.arrg.app.uvault.model;

/*
 * Created by albert on 16/01/2016.
 */
public class UFolder {

    private String path;
    private String folderName;
    private Integer numberOfFiles;
    private String pathOfImage;

    public UFolder() {

    }

    public UFolder(String path, String folderName, Integer numberOfFiles, String pathOfImage) {
        this.path = path;
        this.folderName = folderName;
        this.numberOfFiles = numberOfFiles;
        this.pathOfImage = pathOfImage;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public Integer getNumberOfFiles() {
        return numberOfFiles;
    }

    public void setNumberOfFiles(Integer numberOfFiles) {
        this.numberOfFiles = numberOfFiles;
    }

    public String getPathOfImage() {
        return pathOfImage;
    }

    public void setPathOfImage(String pathOfImage) {
        this.pathOfImage = pathOfImage;
    }
}
