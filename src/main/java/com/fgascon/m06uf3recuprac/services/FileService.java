package com.fgascon.m06uf3recuprac.services;

import com.fgascon.m06uf3recuprac.connections.MongoDBConnection;
import com.fgascon.m06uf3recuprac.models.RemoteFile;
import com.fgascon.m06uf3recuprac.utils.Convert;
import com.fgascon.m06uf3recuprac.utils.Extract;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import org.bson.Document;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class FileService {

    /**
     * Anade un fichero remoto a la base de datos.
     * @param localFile
     * @param connection
     * @throws DataException
     */
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

    /**
     * Elimina un fichero remoto de la base de datos. Elimina todas sus versiones.
     * @param remoteFile
     * @param remoteFolder
     * @param connection
     * @throws DataException
     */
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

    /**
     * Obtiene el MD5 de un fichero remoto.
     * @param doc
     * @return
     */
    public static String getMd5(Document doc) {
        return doc.getString("md5");
    }

    /**
     * Busca un Document (MongoDB) de un fichero remoto en la base de datos y lo devuelve.
     * @param name
     * @param folder
     * @param connection
     * @return
     */
    public static Document findRemoteFile(String name, String folder, MongoDBConnection connection) {

        Document file = connection.getCollection()
                .find(Filters.and(Filters.eq("name", name),
                        Filters.eq("folder", folder)))
                .sort(Sorts.descending("lastModified")).first();

        return file;
    }

    /**
     * Devuelve la fecha de la ultima modificacion de un fichero remoto
     * @param doc
     * @return
     */
    public static LocalDateTime getLastModified(Document doc) {
        String date = doc.getString("lastModified");
        return Convert.ToLocalDateTime(date);
    }

    /**
     * Descarga un fichero remoto y crea un archivo local con la informacion extraida.
     * @param remoteName
     * @param remoteFolder
     * @param localFile
     * @param connection
     * @throws DataException
     */
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

    /**
     * Escribe un texto en un archivo local.
     * @param localFile
     * @param text
     * @throws DataException
     */
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

    /**
     * Clona un archivo remoto y todas las carpetas necesarias.
     * @param remote
     * @throws DataException
     */
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

    /**
     * Obtiene el texto de un fichero remoto.
     * @param doc
     * @return
     */
    private static String getTextFromRemote(Document doc) {
        return doc.getString("text");
    }

    /**
     * Obtiene el texto de un fichero local pero lo trocea en lineas.
     * @param localFile
     * @return
     * @throws DataException
     */
    public static List<String> getLocalTextLines(File localFile) throws DataException {
        List<String> localLines = Collections.emptyList();
        try {
            String localText = Extract.textFromFile(localFile);
            localLines = Extract.linesFromText(localText);
        } catch (FileNotFoundException e) {
            throw new DataException("Error reading the file");
        }

        return localLines;
    }

    /**
     * Obtiene el texto de un fichero remoto pero lo trocea en lineas.
     * @param remoteName
     * @param remoteFolder
     * @param connection
     * @return
     */
    public static List<String> getRemoteTextLines(String remoteName, String remoteFolder, MongoDBConnection connection) {
        Document remoteDoc = findRemoteFile(remoteName, remoteFolder, connection);
        String remoteText = getTextFromRemote(remoteDoc);

        return Extract.linesFromText(remoteText);
    }

}
