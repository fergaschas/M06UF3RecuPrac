package com.fgascon.m06uf3recuprac;

import com.fgascon.m06uf3recuprac.connections.MongoDBConnection;
import com.fgascon.m06uf3recuprac.connections.MongoDBConnectionException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    public static MongoDBConnection connection;

    public static Scene scene;

    @Override
    public void start(Stage stage) throws IOException, MongoDBConnectionException {
        connection = MongoDBConnection.getInstance();
        connection.connectDB();
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("main_view.fxml"));
        scene = new Scene(fxmlLoader.load(), 1280, 920);
        stage.setTitle("GET BD");
        stage.setScene(scene);
        stage.show();
    }
    public static void changeScene(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }
    public static void main(String[] args) {
        launch();
    }
}