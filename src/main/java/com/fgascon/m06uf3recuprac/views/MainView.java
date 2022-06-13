package com.fgascon.m06uf3recuprac.views;

import com.fgascon.m06uf3recuprac.Main;
import com.fgascon.m06uf3recuprac.connections.MongoDBConnectionException;
import com.fgascon.m06uf3recuprac.controllers.DomainException;
import com.fgascon.m06uf3recuprac.controllers.FolderController;
import com.fgascon.m06uf3recuprac.controllers.RepositoryController;
import com.fgascon.m06uf3recuprac.views.dialogs.AlertMessage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static com.fgascon.m06uf3recuprac.controllers.RepositoryController.*;
import static com.fgascon.m06uf3recuprac.utils.OS.homeDirectory;

public class MainView implements Initializable {

    private final String REPOSITORY_URL_FXML = "/com/fgascon/m06uf3recuprac/repository_view";
    public ListView<String> repositoryList;
    @FXML
    private Label welcomeText;
    @FXML
    private Pane window;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadRepositories();
        repositoryList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    private void loadRepositories() {
        repositoryList.getItems().clear();
        List<String> repositoryNames;

        repositoryNames = getRepositoryNames();
        repositoryList.getItems().addAll(repositoryNames);

    }

    public void createNewRepository(ActionEvent actionEvent) {
        Stage stage = (Stage) window.getScene().getWindow();

        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Choose the local directory");
        directoryChooser.setInitialDirectory(homeDirectory());

        File directory = directoryChooser.showDialog(stage);

        try {
            String remotePath = directory.getCanonicalPath();
            createRepository(directory);
            welcomeText.setText(remotePath);
        } catch (IOException | DomainException e) {
            AlertMessage.showError("ERROR", e.getMessage());
        }

        loadRepositories();
    }

    public void dropRepository(ActionEvent actionEvent) {
        String selectedRepository = repositoryList.getSelectionModel().getSelectedItem();

        if (selectedRepository == null) {
            return; // throw new DomainException no se ha seleccionado nada
        }

        try {
            deleteRepository(selectedRepository);
        } catch (DomainException e) {
            AlertMessage.showError("ERROR", e.getMessage());
        }

        loadRepositories();
    }

    public void openRepository(ActionEvent actionEvent) {

        String selectedRepository = repositoryList.getSelectionModel().getSelectedItem();

        if (!repositoryExists(selectedRepository)) return; // add error behaviour

        Main.connection.setCollection(selectedRepository);

        try {
            Main.connection.connectToRepository();
            Main.changeScene(REPOSITORY_URL_FXML);
        } catch (MongoDBConnectionException | IOException e) {
            AlertMessage.showError("ERROR", e.getMessage());
        }
    }

    public void cloneRepository(ActionEvent actionEvent) {

        String selectedRepository = repositoryList.getSelectionModel().getSelectedItem();

        if (!repositoryExists(selectedRepository)) return; // add error behaviour

        Main.connection.setCollection(selectedRepository);

        try {
            Main.connection.connectToRepository();
            RepositoryController.cloneRepository(selectedRepository);
        } catch (DomainException | MongoDBConnectionException e) {
            AlertMessage.showError("ERROR", e.getMessage());
        }
    }
}