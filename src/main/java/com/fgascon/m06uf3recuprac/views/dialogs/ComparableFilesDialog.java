package com.fgascon.m06uf3recuprac.views.dialogs;

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
        String remoteFile = comparableFilesList.getSelectionModel().getSelectedItem();

        if (remoteFile == null) {
            AlertMessage.showError("ERROR", "You have to select a file from the list");
            return;
        }

        CompareFileDialog compareFileDialog = new CompareFileDialog("a", List.of("a", "b"), List.of("a", "c"));
        compareFileDialog.showDialog();

    }
}
