package com.fgascon.m06uf3recuprac.views.dialogs;

import com.fgascon.m06uf3recuprac.controllers.DomainException;
import com.fgascon.m06uf3recuprac.controllers.FileController;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import java.util.Collections;
import java.util.List;

public class CompareFileDialog {
    private List<String> localText;
    private List<String> remoteText;
    ListView<String> localLinesList;
    ListView<String> remoteLinesList;
    private String fileName;
    private String folderName;
    private BorderPane pane;

    public CompareFileDialog(String remoteFile, String remoteFolder) {
        this.fileName = remoteFile;
        this.folderName = remoteFolder;

        this.remoteText = getRemoteFileText();
        this.localText = getLocalFileText();

        this.pane = generatePane(localText, remoteText);
    }

    public void showDialog() {

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Comparing file: " + fileName);
        dialog.setHeaderText("Local file vs Remote file");

        dialog.getDialogPane().setContent(pane);
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);

        dialog.showAndWait();

    }

    private BorderPane generatePane(List<String> localText, List<String> remoteText) {
        BorderPane pane = new BorderPane();

        localLinesList = new ListView<>();
        localLinesList.getItems().addAll(localText);
        remoteLinesList = new ListView<>();
        remoteLinesList.getItems().addAll(remoteText);

        pane.setLeft(localLinesList);
        pane.setRight(remoteLinesList);

        return pane;
    }

    private List<String> getLocalFileText() {
        List<String> localText = Collections.emptyList();

        try {
            localText = FileController.getLocalFileText(fileName, folderName);
        } catch (DomainException e) {
            AlertMessage.showError("Error", e.getMessage());
        }

        return localText;
    }

    private List<String> getRemoteFileText() {
        return FileController.getRemoteFileText(fileName, folderName);
    }
}
