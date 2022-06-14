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

    /**
     * Genera un dialogo con dos listas, una con el texto en local y la otra con el texto remoto.
     */
    public void showDialog() {

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Comparing file: " + fileName);
        dialog.setHeaderText("Local file vs Remote file");

        dialog.getDialogPane().setContent(pane);
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);

        dialog.showAndWait();

    }

    /**
     * Genera la vista con los componentes FXML y rellena las listas con las lineas de texto local y remoto.
     * @param localText
     * @param remoteText
     * @return
     */
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

    /**
     * Obtiene el texto del fichero local.
     * @return
     */
    private List<String> getLocalFileText() {
        List<String> localText = Collections.emptyList();

        try {
            localText = FileController.getLocalFileText(fileName, folderName);
        } catch (DomainException e) {
            AlertMessage.showError("Error", e.getMessage());
        }

        return localText;
    }

    /**
     * Obtiene el texto del fichero remoto.
     * @return
     */
    private List<String> getRemoteFileText() {
        return FileController.getRemoteFileText(fileName, folderName);
    }
}
