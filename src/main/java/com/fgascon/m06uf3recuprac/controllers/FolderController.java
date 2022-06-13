package com.fgascon.m06uf3recuprac.controllers;

import com.fgascon.m06uf3recuprac.Main;
import com.fgascon.m06uf3recuprac.connections.MongoDBConnection;
import com.fgascon.m06uf3recuprac.services.FolderService;
import com.fgascon.m06uf3recuprac.utils.Convert;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class FolderController {


    public static List<String> getFolderNames() {
        List<String> folders = Collections.emptyList();

        MongoDBConnection connection = MongoDBConnection.getInstance();

        folders = FolderService.getFolders(connection);

        return folders;
    }

    public static List<String> getFolderContent(String folder) {
        List<String> folderItems = Collections.emptyList();

        MongoDBConnection connection = MongoDBConnection.getInstance();

        folderItems = FolderService.getFolderItems(folder, connection);

        return folderItems;
    }

    public static String addFolderToRemoteRepository(File localDirectory) throws DomainException {
        MongoDBConnection connection = Main.connection;
        StringBuilder errorFiles = new StringBuilder();
        File[] directoryFiles = localDirectory.listFiles();

        if (directoryFiles == null) throw new DomainException("The directory is empty");

        for (File file : directoryFiles) {
            if (file.isDirectory()) {
                addFolderToRemoteRepository(file);
            } else {
                try {
                    FileController.addFileToRemoteRepository(file);
                } catch (DomainException e) {
                    errorFiles.append(e.getMessage()).append(System.lineSeparator());
                }
            }
        }

       return errorFiles.toString();
    }

    public static void deleteFolderFromRepository(String remoteDirectory) {
        MongoDBConnection connection = Main.connection;

        FolderService.deleteFilesFromFolder(remoteDirectory, connection);

    }

    public static String downloadRemoteFolder(String remoteFolder) throws DomainException {
        MongoDBConnection connection = Main.connection;
        StringBuilder errorFiles = new StringBuilder();

        File localFolder = new File(Convert.toLocalPath(remoteFolder));
        if(!localFolder.exists())
            throw new DomainException("The folder " + remoteFolder + " does not exist in local");

        File[] directoryFiles = localFolder.listFiles();

        if (directoryFiles == null)
            throw new DomainException("The directory is empty");

        for (File file : directoryFiles) {
            if (file.isDirectory()) {
                try {
                    String remoteInnerFolder = Convert.toRemotePath(file.getCanonicalPath());
                    FolderController.downloadRemoteFolder(remoteInnerFolder);
                } catch (IOException e) {
                    throw new DomainException("Error extracting path from folder" + file.getName());
                }
            } else {
                try {
                    FileController.addFileToRemoteRepository(file);
                } catch (DomainException e) {
                    errorFiles.append(e.getMessage()).append(System.lineSeparator());
                }
            }
        }

        return errorFiles.toString();
    }

    public static List<String> getRecursiveFolderContent(String remoteFolder) {
        List<String> folderItems = Collections.emptyList();

        MongoDBConnection connection = MongoDBConnection.getInstance();

        folderItems = FolderService.getRecursiveFolderItems(remoteFolder, connection);

        return folderItems;
    }
}
