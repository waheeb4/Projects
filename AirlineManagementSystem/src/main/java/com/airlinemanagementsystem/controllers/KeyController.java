package com.airlinemanagementsystem.controllers;

import javafx.scene.Node;
import javafx.scene.control.Alert;

import java.io.IOException;

public interface KeyController {
    void sceneLoader(String fxmlpath, Node node, String title) throws IOException;
    void showAlert(String content, String header, Alert.AlertType alertType);
}
