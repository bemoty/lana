package dev.bemoty.lana;

import javafx.application.Preloader;
import javafx.stage.Stage;

public class LanaPreloader extends Preloader {
    @Override
    public void start(Stage stage) {
        com.sun.glass.ui.Application.GetApplication().setName("Lana");
    }
}