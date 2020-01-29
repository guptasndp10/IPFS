package com.fai.ipfs.model;

import java.io.Serializable;
import java.util.Arrays;
import org.springframework.data.annotation.Id;


public class Document implements Serializable {

    @Id
    private String id;
    private String filHash;
    private Long fileSize;
    private String fileName;
    private byte[] fileData;
    private String message;

    public Document(String id, String filHash, Long fileSize, String fileName, byte[] fileData, String message) {
        this.id = id;
        this.filHash = filHash;
        this.fileSize = fileSize;
        this.fileName = fileName;
        this.fileData = fileData;
        this.message = message;
    }

    public Document() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFilHash() {
        return filHash;
    }

    public void setFilHash(String filHash) {
        this.filHash = filHash;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public byte[] getFileData() {
        return fileData;
    }

    public void setFileData(byte[] fileData) {
        this.fileData = fileData;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Document{" +
                "id='" + id + '\'' +
                ", filHash='" + filHash + '\'' +
                ", fileSize=" + fileSize +
                ", fileName='" + fileName + '\'' +
                ", fileData=" + Arrays.toString(fileData) +
                ", message='" + message + '\'' +
                '}';
    }
}
