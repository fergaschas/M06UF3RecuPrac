package com.fgascon.m06uf3recuprac.utils;

import com.fgascon.m06uf3recuprac.connections.MongoDBConnection;

import java.io.File;

public class OS {
    /**
     * Comprueba si el Sistema Operativo es Windows
     * @return
     */
    public static boolean isWindows() {
        return System.getProperty("os.name").startsWith("Windows");
    }

    /**
     * Obtiene el separador de urls de cada sistema operativo.
     * @return
     */
    public static String getSeparator() {
        if (isWindows()) return "\\";
        return "/";
    }

    /**
     * Obtiene el inicio de url de los diferentes sistemas operativos.
     * @return
     */
    public static String getDiskDrive() {
        if (isWindows()) return "c:\\";

        return "/";
    }

    /**
     * obtiene el directorio home.
     * @return
     */
    public static File homeDirectory() {
        return new File(System.getProperty("user.home"));
    }

    /**
     * Obtiene el repositorio local a partir del repositorio remoto
     * @param connection
     * @return
     */
    public static File localRepository(MongoDBConnection connection) {
        String remoteRepository = connection.getCollectionName();
        String localPath = Convert.toLocalPath(remoteRepository);
        return new File(localPath);
    }

}
