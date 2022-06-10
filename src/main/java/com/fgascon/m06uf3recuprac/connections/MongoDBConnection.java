package com.fgascon.m06uf3recuprac.connections;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import org.bson.Document;

public class MongoDBConnection {

    private static volatile MongoDBConnection instance;

    private String host;
    private int port;
    private String databaseName;
    private String collectionName;

    private MongoClient client;
    private MongoDatabase database;
    private MongoCollection<Document> collection;
    private GridFSBucket fileStorage;

    private MongoDBConnection() {
        this.host = "localhost";
        this.port = 27017;
        this.databaseName = "GETBD";
    }

    /**
     * Devuelve un objeto de conexión a MongoDB. Si no existe lo crea.
     *
     * @return a unique connection
     */
    public static MongoDBConnection getInstance() {
        MongoDBConnection connection = instance;
        if (connection != null) {
            return connection;
        }

        synchronized (MongoDBConnection.class) {
            if (instance == null) {
                instance = new MongoDBConnection();
            }
            return instance;
        }
    }

    /**
     * Guarda el nombre de una base de datos para permitir su conexión mas adelante.
     *
     * @param database Nombre de la base de datos
     * @return MongoDBConnection mongo db connection
     */
    public MongoDBConnection setDatabase(String database) {
        databaseName = database;
        return instance;
    }

    /**
     * Guarda el nombre de una colección para permitir su conexión mas adelante.
     *
     * @param collection Nombre de la colección
     * @return MongoDBConnection mongo db connection
     */
    public MongoDBConnection setCollection(String collection) {
        collectionName = collection;
        return instance;
    }

    /**
     * Conecta a un repositorio con el cliente MongoDB. Para que la conexión sea correcta hay que introducir
     * los siguientes campos primero: host, port, databaseName, collectionName
     *
     * @throws MongoDBConnectionException
     */
    public void connectToRepository() throws MongoDBConnectionException {
        boolean canConnect = false;

        connectDB();

        MongoIterable<String> collections = database.listCollectionNames();

        for (String name : collections) {
            if (collectionName.equals(name)) {
                canConnect = true;
                break;
            }
        }

        if (!canConnect) {
            throw new MongoDBConnectionException("El repositorio " + collectionName + " no existe");
        }

        collection = database.getCollection(collectionName);
    }

    /**
     * Conecta a un repositorio con el cliente MongoDB. Para que la conexión sea correcta hay que introducir
     * los siguientes campos primero: host, port, databaseName.
     * Crea un Almacen de archivos si no existe.
     * @throws MongoDBConnectionException
     */
    public void connectDB() throws MongoDBConnectionException {
        boolean canConnect = false;

        connectClient();

        MongoIterable<String> databases = client.listDatabaseNames();

        for (String name : databases) {
            if (databaseName.equals(name)) {
                canConnect = true;
                break;
            }
        }

        if (!canConnect) {
            throw new MongoDBConnectionException("La base de datos " + databaseName + " no existe");
        }

        database = client.getDatabase(databaseName);

        createFileStorage(database);
    }

    /**
     * Crea un GridFSBucket para almacenar archivos grandes. Si ya existe lanza una excepción.
     * @throws MongoDBConnectionException
     */
    private void createFileStorage(MongoDatabase database) throws MongoDBConnectionException {

        String newFileStorageName = databaseName + "fs";
        boolean hasFileStorage = false;

        MongoIterable<String> collections = database.listCollectionNames();

        for (String name : collections) {
            if (name.equals(newFileStorageName)) {
                hasFileStorage = true;
                break;
            }
        }

        if(hasFileStorage){
            throw new MongoDBConnectionException("La base de datos ya contiene un almacenador de archivos");
        }

        fileStorage = GridFSBuckets.create(getDatabase(), newFileStorageName);
    }

    /**
     * Conecta al cliente mongoDB. Si la conexión no es válida muestra una excepción. Se requiere primero pasar
     * el host y el puerto a la instancia de esta clase
     */
    private void connectClient() {
        client = new MongoClient(host, port);
    }

    /**
     * Devuelve la coleccion asignada a la conexión de MongoDB.
     *
     * @return MongoCollection mongo collection
     */
    public MongoCollection<Document> getCollection() {
        return collection;
    }

    /**
     * Devuelve la base de datos asignada a la conexión de MongoDB.
     *
     * @return MongoDatabase mongo database
     */
    public MongoDatabase getDatabase() {
        return database;
    }

    /**
     * Devuelve cliente de la conexión de MongoDB.
     *
     * @return MongoClient client
     */
    public MongoClient getClient() {
        return client;
    }

    /**
     * Devuelve el nombre de la base de datos a la que apunta el objeto MongoDBConnection
     * @return
     */
    public String getDatabaseName() {
        if (databaseName == null) {
            return "";
        }
        return databaseName;
    }

    /**
     * Devuelve el nombre de la coleccion a la que apunta el objeto MongoDBConnection
     * @return
     */
    public String getCollectionName() {
        if (collectionName == null) {
            return "";
        }
        return collectionName;
    }

    /**
     * Devuelve el Almacenador de archivos asociado a la base de datos actual
     * @return
     */
    public GridFSBucket getFileStorage() {
        return fileStorage;
    }
}