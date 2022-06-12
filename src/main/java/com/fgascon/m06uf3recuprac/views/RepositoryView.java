package com.fgascon.m06uf3recuprac.views;

import com.fgascon.m06uf3recuprac.Main;
import com.fgascon.m06uf3recuprac.connections.MongoDBConnection;
import com.fgascon.m06uf3recuprac.connections.MongoDBConnectionException;
import com.fgascon.m06uf3recuprac.controllers.DomainException;
import com.fgascon.m06uf3recuprac.controllers.FileController;
import com.fgascon.m06uf3recuprac.controllers.FolderController;
import com.fgascon.m06uf3recuprac.services.FolderService;
import com.fgascon.m06uf3recuprac.utils.Convert;
import com.fgascon.m06uf3recuprac.utils.OS;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static com.fgascon.m06uf3recuprac.utils.OS.homeDirectory;

public class RepositoryView implements Initializable {

    private static final String MAIN_VIEW_URL_FXML = "/com/fgascon/m06uf3recuprac/main_view";
    private MongoDBConnection connection;
    public ListView<String> folderList;
    public ListView<String> folderPreviewList;
    public Label repositoryTitle;
    @FXML
    private Pane window;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setRepository();
        refreshFolderList();
    }


    private void setRepository() {
        String repositoryName = Main.connection.getCollectionName();
        repositoryTitle.setText(repositoryName);
    }

    private void refreshFolderList() {
        folderList.getItems().clear();
        List<String> folders;
        try {
            folders = FolderController.getFolderNames();
            folderList.getItems().addAll(folders);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void refreshFolderPreviewList(String folder) {
        folderPreviewList.getItems().clear();
        if (folder == null || folder.isEmpty()) return;

        List<String> folderItems;

        try {
            folderItems = FolderController.getFolderContent(folder);
            folderPreviewList.getItems().addAll(folderItems);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void showFolderFiles(MouseEvent mouseEvent) {
        String selectedFolder = folderList.getSelectionModel().getSelectedItem();
        refreshFolderPreviewList(selectedFolder);
    }

    public void addFolder(ActionEvent actionEvent) {
        connection = Main.connection;
        if (connection == null) return;

        Stage stage = (Stage) window.getScene().getWindow();
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Choose a folder to add");
        directoryChooser.setInitialDirectory(OS.localRepository(connection));

        File localDirectory = directoryChooser.showDialog(stage);

        try {
            FolderController.addFolderToRemoteRepository(localDirectory);
        } catch (DomainException e) {
            System.out.println(e.getMessage());
        }

        refreshFolderList();
    }

    public void deleteFolder(ActionEvent actionEvent) {
        String selectedFolder = folderList.getSelectionModel().getSelectedItem();

        if (selectedFolder == null) return;

        FolderController.deleteFolderFromRepository(selectedFolder);

        refreshFolderList();
        refreshFolderPreviewList(null);
    }

    public void addFile(ActionEvent actionEvent) {
        connection = Main.connection;
        if (connection == null) return;

        Stage stage = (Stage) window.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose a file to add");
        fileChooser.setInitialDirectory(OS.localRepository(connection));

        String remoteFolder = folderList.getSelectionModel().getSelectedItem();
        if(remoteFolder != null)
            fileChooser.setInitialDirectory(new File(Convert.toLocalPath(remoteFolder)));

        File localFile = fileChooser.showOpenDialog(stage);

        try {
            FileController.addFileToRemoteRepository(localFile);
        } catch (DomainException e) {
            System.out.println(e.getMessage());
        }

        refreshFolderPreviewList(remoteFolder);
    }

    public void deleteFile(ActionEvent actionEvent) {
        String selectedFile = folderPreviewList.getSelectionModel().getSelectedItem();
        String selectedFolder = folderList.getSelectionModel().getSelectedItem();

        if (selectedFolder == null) return;

        try {
            FileController.deleteFileFromRepository(selectedFile, selectedFolder);
        } catch (DomainException e) {
            System.out.println(e.getMessage());
        }

        refreshFolderPreviewList(selectedFolder);
    }

    public void downloadFolder(ActionEvent actionEvent) {
        String remoteFolder = folderList.getSelectionModel().getSelectedItem();

        if(remoteFolder == null || remoteFolder.isEmpty()) return;

        try{
            FolderController.downloadRemoteFolder(remoteFolder);
        }catch (DomainException e){
            System.out.println(e.getMessage());
        }
    }

    public void downloadFile(ActionEvent actionEvent) {
        String remoteName = folderPreviewList.getSelectionModel().getSelectedItem();
        String remoteFolder = folderList.getSelectionModel().getSelectedItem();

        if(remoteName == null || remoteName.isEmpty()) return;
        if(remoteFolder == null || remoteFolder.isEmpty()) return;

        try {
            FileController.downloadRemoteFile(remoteName, remoteFolder);
        } catch (DomainException e) {
            System.out.println(e.getMessage());
        }
    }

    public void goToMainView(ActionEvent actionEvent) {
        try {
            Main.changeScene(MAIN_VIEW_URL_FXML);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
