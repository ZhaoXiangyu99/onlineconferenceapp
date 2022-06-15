package com.example.database.entity;

public class FileInfo {
    private String file_name;
    public int file_size;

    public void setFile_size(int file_size) {
        this.file_size = file_size;
    }

    public int getFile_size() {
        return file_size;
    }

    public String getFile_name() {
        return file_name;
    }


    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }
}