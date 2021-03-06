package com.fgascon.m06uf3recuprac.controllers;

import com.fgascon.m06uf3recuprac.connections.MongoDBConnection;
import com.fgascon.m06uf3recuprac.connections.MongoDBConnectionException;
import com.fgascon.m06uf3recuprac.services.DataException;
import com.fgascon.m06uf3recuprac.services.RepositoryService;
import com.fgascon.m06uf3recuprac.utils.Convert;


import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class RepositoryController {

    /**
     * Crea un repositorio remoto a partir de una carpeta local.
     * @param directory
     * @throws DomainException
     */
    public static void createRepository(File directory) throws DomainException {
        MongoDBConnection connection = MongoDBConnection.getInstance();

        String remotePath;

        try {
            remotePath = Convert.toRemotePath(directory.getCanonicalPath());
        } catch (IOException e) {
            throw new DomainException("Failed to convert local path to remote path. Can't reach the selected directory");
        }

        try {
            RepositoryService.createRepository(remotePath, connection);
        } catch (MongoDBConnectionException e) {
            throw new DomainException(e.getMessage());
        }
    }

    /**
     * Elimina un repositorio a partir de su nombre.
     * @param repositoryName
     * @throws DomainException
     */
    public static void deleteRepository(String repositoryName) throws DomainException {
        MongoDBConnection connection = MongoDBConnection.getInstance();

        if (!repositoryExists(repositoryName)) throw new DomainException("The repository doesn't exist");

        RepositoryService.deleteRepository(repositoryName, connection);

    }

    /**
     * Devuelve un listado de los nombres de los repositorios de la base de datos
     * @return
     */
    public static List<String> getRepositoryNames() {

        MongoDBConnection connection = MongoDBConnection.getInstance();
        List<String> repositories;

        repositories = RepositoryService.getRepositoryNames(connection);

        return onlyUserRepositories(repositories, connection.getDatabaseName());
    }

    /**
     * Muestra solo los repositorios creados por el usuario. Elimina de la lista los repositorios creados para
     * gestionar archivos grandes.
     * @param repositories
     * @param database
     * @return
     */
    private static List<String> onlyUserRepositories(List<String> repositories, String database) {

        List<String> userRepositories;

        userRepositories = repositories.stream()
                .filter((repo) -> !repo.startsWith(database))
                .collect(Collectors.toList());

        return userRepositories;
    }

    /**
     * Comprueba  si el repositorio existe
     * @param repositoryName
     * @return
     */
    public static boolean repositoryExists(String repositoryName) {
        List<String> repos = getRepositoryNames();
        return repos.stream().anyMatch(repo -> repo.equals(repositoryName));
    }

    /**
     * Clona un repositorio. Descarga todos los archivos remotos a local y crea las carpetas necesarias para ello.
     * @param repository
     * @throws DomainException
     */
    public static void cloneRepository(String repository) throws DomainException {
        MongoDBConnection connection = MongoDBConnection.getInstance();
        try {
            RepositoryService.cloneRepository(repository, connection);
        } catch (DataException e) {
            throw new DomainException(e.getMessage());
        }
    }
}
