package com.fgascon.m06uf3recuprac.services;

import com.fgascon.m06uf3recuprac.connections.MongoDBConnection;
import com.fgascon.m06uf3recuprac.models.RemoteFile;
import com.mongodb.client.DistinctIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoIterable;

import java.util.ArrayList;
import java.util.List;

public class FolderService {
    public static List<String> getFolders(MongoDBConnection connection) {
        List<String> folderNames = new ArrayList<>();

        MongoCursor<String> cursorFolders = connection.getCollection().distinct("folder", String.class).iterator();

        while(cursorFolders.hasNext()){
            folderNames.add(cursorFolders.next());
        }

        cursorFolders.close();

        return folderNames;
    }
}
