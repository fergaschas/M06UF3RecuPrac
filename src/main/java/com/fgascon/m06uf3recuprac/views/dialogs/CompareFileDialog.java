package com.fgascon.m06uf3recuprac.views.dialogs;

import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import java.util.List;

public class CompareFileDialog {
    private List<String> localText;
    private List<String> remoteText;
    ListView<String> localLinesList;
    ListView<String> remoteLinesList;
    private String fileName;
    private BorderPane pane;

    public CompareFileDialog(String fileName, List<String> localText, List<String> remoteText) {
        this.fileName = fileName;
        this.localText = localText;
        this.remoteText = remoteText;
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
}
