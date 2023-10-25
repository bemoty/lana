package dev.bemoty.lana;

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import javafx.scene.control.Alert;

import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class AutoModeKeyboardHook implements NativeKeyListener {
    private final AtomicBoolean ctrlPressed = new AtomicBoolean(false);
    private final StringTransformer stringTransformer;

    public AutoModeKeyboardHook(final StringTransformer stringTransformer) {
        this.stringTransformer = stringTransformer;
    }

    @Override
    public void nativeKeyReleased(final NativeKeyEvent nativeEvent) {
        if (NativeKeyEvent.getKeyText(nativeEvent.getKeyCode()).equalsIgnoreCase("ctrl")) {
            ctrlPressed.set(false);
        }
    }

    @Override
    public void nativeKeyPressed(final NativeKeyEvent nativeEvent) {
        if (nativeEvent.getKeyCode() == NativeKeyEvent.VC_ESCAPE) {
            System.exit(0);
        }
        if (NativeKeyEvent.getKeyText(nativeEvent.getKeyCode()).equalsIgnoreCase("ctrl")) {
            ctrlPressed.set(true);
        }
        if (nativeEvent.getKeyCode() == NativeKeyEvent.VC_B && this.ctrlPressed.get()) {
            try {
                stringTransformer.setClipboardValue(stringTransformer.getReversedString(stringTransformer.getClipboardValue()));
            } catch (UnsupportedFlavorException | IOException ex) {
                final Alert errorAlert = new Alert(Alert.AlertType.ERROR, String.format("Error while reading clipboard: %s", ex.getMessage()));
                errorAlert.setTitle("Lana Error");
                errorAlert.show();
                ex.printStackTrace();
            }
        }
    }
}
