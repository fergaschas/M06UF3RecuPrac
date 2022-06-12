package com.fgascon.m06uf3recuprac.models;

import com.fgascon.m06uf3recuprac.utils.Convert;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;

public class RemoteFile {

    String name;
    String extension;
    String folder;
    String md5;
    LocalDateTime lastModified;
    ObjectId fileId;
    String text;

    public RemoteFile() {

    }

    public static RemoteFile DocumentToRemoteFile(Document doc) {
        RemoteFile file = new RemoteFile();

        file.setName(doc.getString("name"));
        file.setExtension(doc.getString("extension"));
        file.setFolder(doc.getString("folder"));
        file.setMd5(doc.getString("md5"));
        file.setLastModified(Convert.ToLocalDateTime(doc.getString("lastModified")));
        file.setFileId(doc.getObjectId("fileId"));
        file.setText(doc.getString("text"));

        return file;
    }

    public Document mapToDocument() {
        Document document = new Document()
                .append("name", name)
                .append("extension", extension)
                .append("folder", folder)
                .append("md5", md5)
                .append("lastModified", Convert.toString(lastModified))
                .append("fileId", fileId)
                .append("text", text);

        return document;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    public void setLastModified(LocalDateTime lastModified) {
        this.lastModified = lastModified;
    }

    public ObjectId getFileId() {
        return fileId;
    }

    public void setFileId(ObjectId fileId) {
        this.fileId = fileId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
