package com.fgascon.m06uf3recuprac.utils;

import com.fgascon.m06uf3recuprac.connections.MongoDBConnection;

import java.io.File;

public class OS {

    public static boolean isWindows() {
        return System.getProperty("os.name").startsWith("Windows");
    }

    public static String getSeparator() {
        if (isWindows()) return "\\";
        return "/";
    }

    public static String getDiskDrive() {
        if (isWindows()) return "c:\\";

        return "/";
    }

    public static File homeDirectory() {
        return new File(System.getProperty("user.home"));
    }

    public static File localRepository(MongoDBConnection connection) {
        String remoteRepository = connection.getCollectionName();
        String localPath = Convert.toLocalPath(remoteRepository);
        return new File(localPath);
    }

}
