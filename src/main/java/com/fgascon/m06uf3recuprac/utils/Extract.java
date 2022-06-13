package com.fgascon.m06uf3recuprac.utils;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class Extract {
    public static String fileExtension(File file) {
        if (!file.getName().contains(".")) return "";

        return file.getName().substring(file.getName().lastIndexOf(".") + 1);
    }

    public static String fileFolder(File file) throws IOException {
        String path = file.getCanonicalPath();

        if(file.isDirectory()){
            return path;
        }

        // corta por el ultimo separador
        return path.substring(0, path.lastIndexOf(OS.getSeparator()));

    }

    public static LocalDateTime dateOfLastModification(File file) {
        long epochTime = file.lastModified();

        return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochTime),
                TimeZone.getDefault().toZoneId());
    }

    public static String md5AsString(File file) throws IOException, NoSuchAlgorithmException {
        byte[] b = getMd5(file);
        StringBuilder md5 = new StringBuilder();

        for (byte unByte : b) {
            md5.append(Integer.toString((unByte & 0xff) + 0x100, 16).substring(1));
        }
        return md5.toString();
    }

    private static byte[] getMd5(File file) throws IOException, NoSuchAlgorithmException {
        InputStream fis = new FileInputStream(file);

        byte[] buffer = new byte[1024];
        MessageDigest complete = MessageDigest.getInstance("MD5");
        int numRead;
        do {
            numRead = fis.read(buffer);
            if (numRead > 0) {
                complete.update(buffer, 0, numRead);
            }
        } while (numRead != -1);

        fis.close();

        return complete.digest();
    }

    public static String textFromFile(File file) throws FileNotFoundException {
        Scanner read = new Scanner(file);
        StringBuilder text = new StringBuilder();

        while(read.hasNextLine()){
            text.append(read.nextLine()).append(System.lineSeparator());
        }

        return text.toString();
    }

    public static List<String> linesFromText(String text) {
        List<String> lines;

        lines = Arrays.stream(text.split("\n")).collect(Collectors.toList());

        return lines;
    }

    public static String nameFromRemotePath(String remotePath) {
        int lastSeparator = remotePath.lastIndexOf(Convert.GET_URL_SEPARATOR);
        return remotePath.substring(lastSeparator + 1);
    }

    public static String folderFromRemotePath(String remotePath) {
        int lastSeparator = remotePath.lastIndexOf(Convert.GET_URL_SEPARATOR);
        return remotePath.substring(0, lastSeparator);
    }
}
