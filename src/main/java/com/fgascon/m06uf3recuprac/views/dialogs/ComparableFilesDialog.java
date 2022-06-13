package com.fgascon.m06uf3recuprac.views.dialogs;

import com.fgascon.m06uf3recuprac.controllers.DomainException;
import com.fgascon.m06uf3recuprac.controllers.FileController;
import com.fgascon.m06uf3recuprac.utils.Extract;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import java.util.List;

public class ComparableFilesDialog {

    private List<String> files;
    ListView<String> comparableFilesList;
    private BorderPane pane;

    public ComparableFilesDialog(List<String> remoteFiles) {
        this.files = remoteFiles;
        this.pane = generatePane(remoteFiles);
    }

    public void showDialog() {

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Files to compare");
        dialog.setHeaderText("Select a remote file to compare with your local file");

        dialog.getDialogPane().setContent(pane);
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);

        dialog.showAndWait();

    }

    private BorderPane generatePane(List<String> remoteFiles) {
        BorderPane pane = new BorderPane();

        Button compareFileBtn = new Button("Compare File");
        compareFileBtn.setOnAction((it) -> compareFile());

        HBox buttonBox = new HBox();
        buttonBox.getChildren().add(compareFileBtn);
        pane.setBottom(new HBox());

        comparableFilesList = new ListView<>();
        comparableFilesList.getItems().addAll(files);

        pane.setCenter(comparableFilesList);
        pane.setBottom(buttonBox);

        return pane;
    }

    private void compareFile() {
        String remotePath = comparableFilesList.getSelectionModel().getSelectedItem();


        if (remotePath == null) {
            AlertMessage.showError("ERROR", "You have to select a file from the list");
            return;
        }

        String remoteFile = Extract.nameFromRemotePath(remotePath);
        String remoteFolder = Extract.folderFromRemotePath(remotePath);

        try {
            FileController.checkCompareFiles(remoteFile, remoteFolder);
        } catch (DomainException e) {
            AlertMessage.showError("Error", e.getMessage());
            return;
        }

        CompareFileDialog compareFileDialog = new CompareFileDialog(remoteFile, remoteFolder);
        compareFileDialog.showDialog();

    }
}
