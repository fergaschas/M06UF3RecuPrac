package com.fgascon.m06uf3recuprac.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.fgascon.m06uf3recuprac.utils.Regex.pathSeparator;

public class Convert {

    public static final String GET_URL_SEPARATOR = "_";
    public static final char GET_URL_SEPARATOR_CHAR = '_';

    public static String toRemotePath(String localPath) {
        String remotePath;

        remotePath = localPath
                .replaceFirst(OS.getDiskDrive(), "")
                .replaceAll(pathSeparator(), GET_URL_SEPARATOR);

        return remotePath;
    }

    public static String toLocalPath(String remotePath) {
        StringBuilder localPath = new StringBuilder();

        localPath.append(OS.getDiskDrive())
                .append(remotePath.replaceAll(GET_URL_SEPARATOR, OS.getSeparator()));

        return localPath.toString();
    }

    public static LocalDateTime ToLocalDateTime(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(date, formatter);
    }

    public static String toString(LocalDateTime lastModified) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return lastModified.format(formatter);
    }
    public static String getRemotePath(String remoteName, String remoteFolder) {
        return remoteFolder + GET_URL_SEPARATOR + remoteName;
    }
}
