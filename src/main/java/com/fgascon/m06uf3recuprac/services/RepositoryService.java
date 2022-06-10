package com.fgascon.m06uf3recuprac.services;

import com.fgascon.m06uf3recuprac.connections.MongoDBConnection;
import com.fgascon.m06uf3recuprac.connections.MongoDBConnectionException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoIterable;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class RepositoryService {
    public static void createRepository(String repository, MongoDBConnection connection) throws MongoDBConnectionException {
        boolean collectionExists = false;

        MongoIterable<String> collections = connection.getDatabase().listCollectionNames();

        for (String collection : collections) {
            if (repository.equals(collection)) {
                collectionExists = true;
                break;
            }
        }

        if (collectionExists) {
            throw new MongoDBConnectionException("The repository " + repository + " already exists");
        }

        connection.getDatabase().createCollection(repository);
    }

    public static List<String> getRepositoryNames(MongoDBConnection connection) {
        List<String> repositories = new ArrayList<>();

        MongoIterable<String> collections = connection.getDatabase().listCollectionNames();

        for (String collection : collections) {
            repositories.add(collection);
        }

        return repositories;
    }

    public static void deleteRepository(String repositoryName, MongoDBConnection connection) {

        MongoCollection<Document> repository = connection.getDatabase().getCollection(repositoryName);

        repository.drop();
    }
}
