package com.fgascon.m06uf3recuprac.controllers;

import com.fgascon.m06uf3recuprac.connections.MongoDBConnection;
import com.fgascon.m06uf3recuprac.services.FolderService;

import java.util.Collections;
import java.util.List;

public class FolderController {


    public static List<String> getFolderNames() {
        List<String> folders = Collections.emptyList();

        MongoDBConnection connection = MongoDBConnection.getInstance();

        folders = FolderService.getFolders(connection);

        return folders;
    }
}
