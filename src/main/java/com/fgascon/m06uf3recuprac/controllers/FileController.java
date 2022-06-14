package com.fgascon.m06uf3recuprac.controllers;

import com.fgascon.m06uf3recuprac.Main;
import com.fgascon.m06uf3recuprac.connections.MongoDBConnection;
import com.fgascon.m06uf3recuprac.services.DataException;
import com.fgascon.m06uf3recuprac.services.FileService;
import com.fgascon.m06uf3recuprac.utils.Convert;
import com.fgascon.m06uf3recuprac.utils.Extract;
import org.bson.Document;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class FileController {

    /**
     * Envia un archivo a la capa de datos para que lo procese si cumple los requisitos necesarios.
     * @param localFile
     * @throws DomainException
     */
    public static void addFileToRemoteRepository(File localFile) throws DomainException {
        MongoDBConnection connection = Main.connection;

        if (!localFile.isFile())
            throw new DomainException("This is not a file!");
        if (!hasRequiredExtension(localFile))
            throw new DomainException("The file doesn't have the required extension");

        try {
            String folder = Convert.toRemotePath(Extract.fileFolder(localFile));
            Document remoteFile = FileService.findRemoteFile(localFile.getName(), folder, connection);
            if (remoteFile != null) {
                if (hasTheSameChecksum(localFile, remoteFile))
                    throw new DomainException("The md5 is the same");
                if (isOlderThanRemote(localFile, remoteFile))
                    throw new DomainException("The last modification is not after the remote file");
            }
        } catch (IOException e) {
            throw new DomainException("Error parsing paths from local to remote");

        }


        try {
            FileService.addFileToRemoteRepository(localFile, connection);
        } catch (DataException e) {
            throw new DomainException(e.getMessage());
        }
    }

    /**
     * Comprueba si la fecha de modificacion del archivo local es mayor o igual a la del archivo remoto. Si es mas antiguo
     * devuelve true.
     * @param localFile
     * @param remoteFile
     * @return
     */
    private static boolean isOlderThanRemote(File localFile, Document remoteFile) {
        LocalDateTime localFileDate = Extract.dateOfLastModification(localFile);
        LocalDateTime remoteFileDate = FileService.getLastModified(remoteFile);

        // devuelve true si es igual o anterior al remoto
        return !remoteFileDate.isBefore(localFileDate);
    }

    /**
     * Compara el valor del MD5 de un archivo local y uno remoto. Devuelve true si son iguales.
     * @param localFile
     * @param remoteFile
     * @return
     */
    private static boolean hasTheSameChecksum(File localFile, Document remoteFile) {
        try {
            String localMd5 = Extract.md5AsString(localFile);

            String remoteMd5 = FileService.getMd5(remoteFile);

            return remoteMd5.equals(localMd5);

        } catch (IOException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Comprueba que la extension del archivo sea una de las que requiere el dominio.
     * @param localFile
     * @return
     */
    private static boolean hasRequiredExtension(File localFile) {
        String[] availableExtensions = {"java", "txt", "xml", "html"};

        String extension = Extract.fileExtension(localFile);

        for (String available : availableExtensions) {
            if (extension.equalsIgnoreCase(available))
                return true;
        }

        return false;
    }

    /**
     * Envia un nombre de archivo y de carpeta de un archivo remoto para que la capa de datos lo elimine.
     * @param remoteFile
     * @param remoteFolder
     * @throws DomainException
     */
    public static void deleteFileFromRepository(String remoteFile, String remoteFolder) throws DomainException {
        MongoDBConnection connection = Main.connection;

        try {
            FileService.deleteFileFromRepository(remoteFile, remoteFolder, connection);
        } catch (DataException e) {
            throw new DomainException(e.getMessage());
        }
    }

    /**
     * Descarga un fichero remoto a partir del nombre y el nombre de archivo y carpeta remotos.
     * @param remoteName
     * @param remoteFolder
     * @throws DomainException
     */
    public static void downloadRemoteFile(String remoteName, String remoteFolder) throws DomainException {
        MongoDBConnection connection = MongoDBConnection.getInstance();

        String localFolderPath = Convert.toLocalPath(remoteFolder);
        File localFolder = new File(localFolderPath);

        if (!localFolder.exists()) throw new DomainException("The folder does not exist in local");

        String remotePath = remoteFolder + Convert.GET_URL_SEPARATOR + remoteName;
        String localPath = Convert.toLocalPath(remotePath);
        File localFile = new File(localPath);

        if (localFile.exists() && localFile.isFile()) {
            Document remoteFile = FileService.findRemoteFile(remoteName, remoteFolder, connection);
            if (remoteFile != null) {
                if (hasTheSameChecksum(localFile, remoteFile))
                    throw new DomainException("The md5 is the same");
                if (!isOlderThanRemote(localFile, remoteFile))
                    throw new DomainException("The last modification is not after the remote file");
            }
        }

        try {
            FileService.downloadRemoteFile(remoteName, remoteFolder, localFile, connection);
        } catch (DataException e) {
            throw new DomainException(e.getMessage());
        }

    }

    /**
     * Obtiene una lista de lineas de texto de un archivo local.
     * @param remoteName
     * @param remoteFolder
     * @return
     * @throws DomainException
     */
    public static List<String> getLocalFileText(String remoteName, String remoteFolder) throws DomainException {

        String remotePath = Convert.getRemotePath(remoteName, remoteFolder);
        String localPath = Convert.toLocalPath(remotePath);
        File localFile = new File(localPath);
        List<String> localLines = Collections.emptyList();

        try {
            localLines = FileService.getLocalTextLines(localFile);
        } catch (DataException e) {
            throw new DomainException(e.getMessage());
        }

        return localLines;
    }

    /**
     * Obtiene una lista de lineas de texto de un archivo remoto
     * @param remoteName
     * @param remoteFolder
     * @return
     */
    public static List<String> getRemoteFileText(String remoteName, String remoteFolder) {
        MongoDBConnection connection = MongoDBConnection.getInstance();

        List<String> remoteLines = FileService.getRemoteTextLines(remoteName, remoteFolder, connection);

        return remoteLines;
    }

    /**
     * Comprueba que los archivos local y remoto sean diferentes.
     * @param remoteName
     * @param remoteFolder
     * @throws DomainException
     */
    public static void checkCompareFiles(String remoteName, String remoteFolder) throws DomainException {
        MongoDBConnection connection = MongoDBConnection.getInstance();
        String remotePath = Convert.getRemotePath(remoteName, remoteFolder);
        String localPath = Convert.toLocalPath(remotePath);

        File localFile = new File(localPath);

        Document remoteFile = FileService.findRemoteFile(remoteName, remoteFolder, connection);

        if (remoteFile != null) {
            if (hasTheSameChecksum(localFile, remoteFile))
                throw new DomainException("The md5 is the same");
            //if (isOlderThanRemote(localFile, remoteFile))
              //  throw new DomainException("The last modification is not after the remote file");
        }
    }
}
