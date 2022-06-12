package com.fgascon.m06uf3recuprac.services;

import com.fgascon.m06uf3recuprac.connections.MongoDBConnection;
import com.fgascon.m06uf3recuprac.models.RemoteFile;
import com.fgascon.m06uf3recuprac.utils.Convert;
import com.fgascon.m06uf3recuprac.utils.Extract;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import org.bson.Document;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

public class FileService {

    public static void addFileToRemoteRepository(File localFile, MongoDBConnection connection) throws DataException {
        RemoteFile remoteFile = new RemoteFile();

        remoteFile.setName(localFile.getName());
        remoteFile.setExtension(Extract.fileExtension(localFile));
        remoteFile.setLastModified(Extract.dateOfLastModification(localFile));
        try {
            String localFolder = Extract.fileFolder(localFile);
            remoteFile.setFolder(Convert.toRemotePath(localFolder));
        } catch (IOException e) {
            throw new DataException("Error extracting folder from file");
        }
        try {
            remoteFile.setMd5(Extract.md5AsString(localFile));
        } catch (Exception e) {
            throw new DataException("Error getting the md5 of the file");
        }
        try {
            remoteFile.setText(Extract.textFromFile(localFile));
        } catch (IOException e) {
            throw new DataException("Error reading file");
        }

        Document doc = remoteFile.mapToDocument();

        connection.getCollection().insertOne(doc);

    }

    public static void deleteFileFromRepository(String remoteFile, String remoteFolder, MongoDBConnection connection) throws DataException {

        MongoCursor<Document> files = connection.getCollection().find(Filters.and(
                        Filters.eq("name", remoteFile),
                        Filters.eq("folder", remoteFolder)))
                .iterator();

        if (!files.hasNext()) throw new DataException("Can't find the selected file");

        while (files.hasNext()) {
            connection.getCollection().deleteOne(files.next());
        }

        files.close();
    }

    public static String getMd5(Document doc) {
        return doc.getString("md5");
    }

    public static Document findRemoteFile(String name, String folder, MongoDBConnection connection) {

        Document file = connection.getCollection()
                .find(Filters.and(Filters.eq("name", name),
                        Filters.eq("folder", folder)))
                .sort(Sorts.descending("lastModified")).first();

        return file;
    }

    public static LocalDateTime getLastModified(Document doc) {
        String date = doc.getString("lastModified");
        return Convert.ToLocalDateTime(date);
    }

    public static void downloadRemoteFile(String remoteName, String remoteFolder, File localFile, MongoDBConnection connection) throws DataException {
        Document remoteDoc = findRemoteFile(remoteName, remoteFolder, connection);
        String text = getTextFromRemote(remoteDoc);

        if (!localFile.exists()) {
            try {
                localFile.createNewFile();
            } catch (IOException e) {
                throw new DataException("Can't create the local file");
            }
        }

        writeDataIntoFile(localFile, text);
    }

    private static void writeDataIntoFile(File localFile, String text) throws DataException {
        try {
            FileWriter fw = new FileWriter(localFile);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(text);
            bw.close();
        } catch (IOException e) {
            throw new DataException("Error writing the remote text into the local file");
        }
    }

    public static void cloneRemoteFile(RemoteFile remote) throws DataException {
        String remotePath = remote.getFolder() + Convert.GET_URL_SEPARATOR + remote.getName();
        String sLocalFolder = Convert.toLocalPath(remote.getFolder());
        Path localFolder = Paths.get(sLocalFolder);
        File newLocalFile = new File(Convert.toLocalPath(remotePath));

        try {
            Files.createDirectories(localFolder);
            newLocalFile.delete();
            newLocalFile.createNewFile();
            writeDataIntoFile(newLocalFile, remote.getText());
        } catch (IOException e) {
            throw new DataException("Error creating parent directories");
        }


    }
    private static String getTextFromRemote(Document doc) {
        return doc.getString("text");
    }
}
