package com.fgascon.m06uf3recuprac.views;

import com.fgascon.m06uf3recuprac.controllers.DomainException;
import com.fgascon.m06uf3recuprac.controllers.RepositoryController;
import com.fgascon.m06uf3recuprac.utils.Convert;
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

import static com.fgascon.m06uf3recuprac.utils.OS.homeDirectory;

public class HelloController implements Initializable {
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

        repositoryNames = RepositoryController.getRepositoryNames();
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
            RepositoryController.createRepository(directory);
            welcomeText.setText(remotePath);
        } catch (IOException e) {
            welcomeText.setText("Error trying to open selected directory");
        } catch (DomainException e) {
            welcomeText.setText(e.getMessage());
        }

    }

    public void dropRepository(ActionEvent actionEvent) {
        String selectedRepository = repositoryList.getSelectionModel().getSelectedItem();

        if (selectedRepository == null) {
            return; // throw new DomainException no se ha seleccionado nada
        }

        try {
            RepositoryController.deleteRepository(selectedRepository);
        } catch (Exception e) {

        }

        loadRepositories();
    }

    public void openRepository(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/delete_repository_dialog.fxml"));
            DialogPane deleteRepositoryView = fxmlLoader.load();

            Dialog<ButtonType> deleteRepositoryDialog = new Dialog<>();
            deleteRepositoryDialog.setDialogPane(deleteRepositoryView);

            deleteRepositoryDialog.showAndWait();
            loadRepositories();
        } catch (IOException e) {
            System.out.println("per acabar");
        }
    }
}