package com.fgascon.m06uf3recuprac.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.fgascon.m06uf3recuprac.utils.Regex.pathSeparator;

public class Convert {

    private static final String GET_URL_SEPARATOR = "_";

    public static String toRemotePath(String localPath){
        String remotePath;

        remotePath = localPath
                .replaceFirst(OS.getDiskDrive(), "")
                .replaceAll(pathSeparator(), GET_URL_SEPARATOR);

        return remotePath;
    }

    public static LocalDateTime ToLocalDateTime(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(date, formatter);
    }

    public static String toString(LocalDateTime lastModified) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return lastModified.format(formatter);
    }
}
