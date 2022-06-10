package com.fgascon.m06uf3recuprac.views;

import com.fgascon.m06uf3recuprac.Main;
import com.fgascon.m06uf3recuprac.controllers.FolderController;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class RepositoryView implements Initializable {

    public ListView<String> folderList;
    public ListView<String> folderPreviewList;
    public Label repositoryTitle;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setRepository();
        refreshFolderList();
        //refreshFolderPreviewList();
    }

    private void setRepository() {
        String repositoryName = Main.connection.getCollectionName();
        repositoryTitle.setText(repositoryName);
    }

    private void refreshFolderList() {
        folderList.getItems().clear();
        List<String> folders;
        try{
            folders = FolderController.getFolderNames();
            folderList.getItems().addAll(folders);
        }catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void showFolderFiles(MouseEvent mouseEvent) {
    }

    public void addFolder(ActionEvent actionEvent) {
    }

    public void deleteFolder(ActionEvent actionEvent) {
    }

    public void addFiles(ActionEvent actionEvent) {
    }

    public void deleteFiles(ActionEvent actionEvent) {
    }
}
