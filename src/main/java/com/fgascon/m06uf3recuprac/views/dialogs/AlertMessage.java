package com.fgascon.m06uf3recuprac.views.dialogs;


import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

/**
 * Clase de utilidad para proporcionar alertas simples de distintos tipos
 */
public class AlertMessage {

    /**
     * Muestra una alerta con un mensaje de error
     * @param title
     * @param error
     */
    public static void showError(String title, String error) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);

        alert.setContentText(error);
        alert.showAndWait();
    }

    /**
     * Muestra una alerta con informacion al usuario
     * @param title
     * @param confirmation
     */
    public static void showConfirmation(String title, String confirmation) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(confirmation);
        alert.showAndWait();
    }

    /**
     * Pregunta al usuario y espera a que responda Si o No
     * @param title
     * @param question
     * @return
     */
    public static Optional<ButtonType> askForConfirmation(String title, String question){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setContentText(question);
        return alert.showAndWait();
    }
}
