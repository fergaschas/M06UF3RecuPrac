package com.fgascon.m06uf3recuprac.services;

import com.fgascon.m06uf3recuprac.connections.MongoDBConnection;
import com.fgascon.m06uf3recuprac.models.RemoteFile;
import com.fgascon.m06uf3recuprac.utils.Convert;
import com.fgascon.m06uf3recuprac.utils.Extract;
import com.fgascon.m06uf3recuprac.utils.OS;
import com.mongodb.client.DistinctIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.BasicBSONList;

import java.io.File;
import java.util.*;

import static com.fgascon.m06uf3recuprac.utils.Convert.GET_URL_SEPARATOR;
import static com.fgascon.m06uf3recuprac.utils.Convert.GET_URL_SEPARATOR_CHAR;

public class FolderService {
    public static List<String> getFolders(MongoDBConnection connection) {
        List<String> folderNames = new ArrayList<>();

        MongoCursor<String> cursorFolders = connection.getCollection().distinct("folder", String.class).iterator();

        while (cursorFolders.hasNext()) {
            folderNames.add(cursorFolders.next());
        }

        cursorFolders.close();

        folderNames.sort((o1, o2) -> {
            int o1separators = (int) o1.chars().filter(ch -> ch == GET_URL_SEPARATOR_CHAR).count();
            int o2separators = (int) o2.chars().filter(ch -> ch == GET_URL_SEPARATOR_CHAR).count();
            return o1separators - o2separators;
        });
        return folderNames;
    }

    /**
     * Finds all file names from a remote folder. The list doesn't have repeated names, only shows the last file
     * added.
     *
     * @param remoteFolder path of the remote folder
     * @param connection   instance of a MongoDBConnection
     * @return a list of file names
     */
    public static List<String> getFolderItems(String remoteFolder, MongoDBConnection connection) {
        List<String> fileNames = new ArrayList<>();

        MongoCursor<String> cursorFolderItems = connection.getCollection()
                .distinct("name", String.class)
                .filter(Filters.eq("folder", remoteFolder))
                .iterator();

        while (cursorFolderItems.hasNext()) {
            fileNames.add(cursorFolderItems.next());
        }

        cursorFolderItems.close();

        return fileNames;
    }

    public static void deleteFilesFromFolder(String remoteDirectory, MongoDBConnection connection) {
        MongoCursor<Document> cursorFolderItems = connection.getCollection()
                .find(Filters.eq("folder", remoteDirectory))
                .iterator();

        while (cursorFolderItems.hasNext()) {
            connection.getCollection().deleteOne(cursorFolderItems.next());
        }

        cursorFolderItems.close();
    }

    public static List<String> getRecursiveFolderItems(String remoteFolder, MongoDBConnection connection) {
        List<String> fileNames = new ArrayList<>();

        MongoCursor<Document> cursorFolderItems = connection.getCollection()
                .find(Filters.regex("folder",remoteFolder))
                .sort(Sorts.ascending("folder")).iterator();

        while (cursorFolderItems.hasNext()) {
            Document file = cursorFolderItems.next();
            RemoteFile remoteFile = RemoteFile.DocumentToRemoteFile(file);

            if(!remoteFolder.equalsIgnoreCase(remoteFile.getFolder())){
                fileNames.addAll(getRecursiveFolderItems(remoteFile.getFolder(), connection));
            }else{
                String remotePath = Convert.getRemotePath(remoteFile.getName(), remoteFile.getFolder());
                fileNames.add(remotePath);
            }
        }
        cursorFolderItems.close();

        // aixo es fa per eliminar els duplicats(repetits amb diferents dates)
        fileNames = new ArrayList<>(new LinkedHashSet<>(fileNames));

        return fileNames;
    }
}
