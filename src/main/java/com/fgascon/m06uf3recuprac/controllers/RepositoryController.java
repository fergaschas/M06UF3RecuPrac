package com.fgascon.m06uf3recuprac.controllers;

import com.fgascon.m06uf3recuprac.connections.MongoDBConnection;
import com.fgascon.m06uf3recuprac.connections.MongoDBConnectionException;
import com.fgascon.m06uf3recuprac.services.RepositoryService;
import com.fgascon.m06uf3recuprac.utils.Convert;


import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class RepositoryController {

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

    public static void deleteRepository(String repositoryName) throws DomainException {
        MongoDBConnection connection = MongoDBConnection.getInstance();

        if (!repositoryExists(repositoryName)) throw new DomainException("The repository doesn't exist");

        RepositoryService.deleteRepository(repositoryName, connection);

    }

    public static List<String> getRepositoryNames() {

        MongoDBConnection connection = MongoDBConnection.getInstance();
        List<String> repositories;

        repositories = RepositoryService.getRepositoryNames(connection);

        return onlyUserRepositories(repositories, connection.getDatabaseName());
    }

    private static List<String> onlyUserRepositories(List<String> repositories, String database) {

        List<String> userRepositories;

        userRepositories = repositories.stream()
                .filter((repo) -> !repo.startsWith(database))
                .collect(Collectors.toList());

        return userRepositories;
    }

    public static boolean repositoryExists(String repositoryName) {
        List<String> repos = getRepositoryNames();
        return repos.stream().anyMatch(repo -> repo.equals(repositoryName));
    }

    public static void cloneRepository(String selectedRepository) {

    }
}
