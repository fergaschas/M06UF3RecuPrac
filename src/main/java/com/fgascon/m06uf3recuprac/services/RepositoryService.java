package com.fgascon.m06uf3recuprac.services;

import com.fgascon.m06uf3recuprac.connections.MongoDBConnection;
import com.fgascon.m06uf3recuprac.connections.MongoDBConnectionException;
import com.fgascon.m06uf3recuprac.controllers.FileController;
import com.fgascon.m06uf3recuprac.models.RemoteFile;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class RepositoryService {
    /**
     * Crea un repositorio, creando una coleccion en la base de datos, si no existe.
     * @param repository
     * @param connection
     * @throws MongoDBConnectionException
     */
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

    /**
     * Extrae el nombre de los repositorios de una base de datos
     * @param connection
     * @return
     */
    public static List<String> getRepositoryNames(MongoDBConnection connection) {
        List<String> repositories = new ArrayList<>();

        MongoIterable<String> collections = connection.getDatabase().listCollectionNames();

        for (String collection : collections) {
            repositories.add(collection);
        }

        return repositories;
    }

    /**
     * Elimina el repositorio a partir de su nombre.
     * @param repositoryName
     * @param connection
     */
    public static void deleteRepository(String repositoryName, MongoDBConnection connection) {

        MongoCollection<Document> repository = connection.getDatabase().getCollection(repositoryName);

        repository.drop();
    }

    /**
     * Clona un repositorio remoto a local(de momento todos los archivos).
     * @param repository
     * @param connection
     * @throws DataException
     */
    public static void cloneRepository(String repository, MongoDBConnection connection) throws DataException {

        // estaria bien optimizar esto
        MongoCursor<Document> files = connection.getCollection().find(Filters.exists("folder")).iterator();

        while (files.hasNext()) {
            try {
                RemoteFile remote = RemoteFile.DocumentToRemoteFile(files.next());
                FileService.cloneRemoteFile(remote);
            } catch (DataException e) {
                throw new DataException(e.getMessage());
            }
        }
    }
}
