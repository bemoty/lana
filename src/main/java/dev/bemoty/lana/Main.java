package dev.bemoty.lana;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.InputStream;

public class Main extends Application {
    private StringTransformer transformer;

    public static void main(String[] args) {
        com.sun.javafx.application.LauncherImpl.launchApplication(Main.class, LanaPreloader.class, args);
    }

    public void start(final Stage primaryStage) {
        // Transformer instance
        transformer = StringTransformer.getInstance();

        // Stage settings
        primaryStage.setResizable(false);
        primaryStage.setIconified(false);
        final ClassLoader classLoader = this.getClass().getClassLoader();
        final InputStream arrowLeftStream = classLoader.getResourceAsStream("arrow-left-double-3.png");
        if (arrowLeftStream == null) {
            final Alert errorAlert = new Alert(Alert.AlertType.ERROR, "Failed to load required resources");
            errorAlert.setTitle("Lana Information");
            errorAlert.show();
            System.exit(-1);
        }
        primaryStage.getIcons().add(new Image(arrowLeftStream));
        primaryStage.setTitle("Lana");

        // Layout
        final VBox vBox = new VBox(5.0D);
        final HBox hBox = new HBox(10.0D);
        vBox.setPadding(new Insets(0, 0, 10, 0));

        // Status label
        final Label status = new Label();
        status.setTranslateY(status.getTranslateY() + 4.0D);
        status.setText("");

        // Entry box
        final Label textBoxLabel = new Label();
        textBoxLabel.setText("Enter string:");
        final TextArea textbox = new TextArea();
        textbox.textProperty().addListener(e -> status.setText(""));

        // Footer buttons
        final Button copyButton = new Button("Convert & Copy");
        final Button switchToAutoButton = new Button("Switch to Auto");
        copyButton.setDefaultButton(true);
        copyButton.setOnAction(e -> {
            if (textbox.getText().isEmpty()) {
                status.setText("Text Box must not  be empty!");
                return;
            }
            transformer.setClipboardValue(transformer.getReversedString(textbox.getText()));
            status.setText("Copied!");
        });
        switchToAutoButton.setOnAction(e -> {
            try {
                GlobalScreen.registerNativeHook();
            } catch (NativeHookException ex) {
                final Alert errorAlert = new Alert(Alert.AlertType.ERROR, "An error occurred while registering the native keyboard hook");
                errorAlert.setTitle("Lana Error");
                errorAlert.show();
                ex.printStackTrace();
                System.exit(1);
            }
            GlobalScreen.addNativeKeyListener(new AutoModeKeyboardHook(transformer));
            final Alert infoAlert = new Alert(Alert.AlertType.INFORMATION, "CTRL+B to reverse clipboard, press [Esc] to exit");
            infoAlert.setTitle("Lana Information");
            infoAlert.show();
            primaryStage.close();
        });

        // Add control elements to layout
        hBox.getChildren().addAll(copyButton, switchToAutoButton, status);
        vBox.getChildren().addAll(textBoxLabel, textbox);

        // Use BorderPane as root layout
        BorderPane bp = new BorderPane();
        bp.setCenter(vBox);
        bp.setBottom(hBox);

        // Set padding
        bp.setPadding(new Insets(10.0D));

        // Window scale
        Scene s = new Scene(bp, 400.0D, 250.0D);
        primaryStage.setScene(s);

        // Show window
        primaryStage.show();
    }
}
