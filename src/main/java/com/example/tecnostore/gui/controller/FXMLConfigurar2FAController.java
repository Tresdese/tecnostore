package com.example.tecnostore.gui.controller;

import java.awt.Desktop;
import java.net.URI;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.Stage;

public class FXMLConfigurar2FAController {

    @FXML private ImageView imgQr;
    @FXML private Label lblSecret;
    @FXML private Label lblQrUrl;
    @FXML private Label lblEstado;
    @FXML private TextArea txtQrUrl;
    @FXML private Button btnContinuar;
    @FXML private Button btnCancelar;
    @FXML private Button btnAbrirNavegador;
    @FXML private Button btnCopiarEnlace;

    private Runnable onContinuar;

    public void setData(String qrUrl, String secret, Runnable onContinuar) {
        this.onContinuar = onContinuar;
        if (qrUrl != null && !qrUrl.isBlank()) {
            Image img = new Image(qrUrl, true);
            img.errorProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal) {
                    lblEstado.setText("No se pudo cargar el QR. Copia/pega el enlace:");
                    lblQrUrl.setText(qrUrl);
                    txtQrUrl.setText(qrUrl);
                    txtQrUrl.setVisible(true);
                    txtQrUrl.setManaged(true);
                }
            });
            imgQr.setImage(img);
            lblQrUrl.setText(qrUrl);
            txtQrUrl.setText(qrUrl);
        }
        if (secret != null) {
            lblSecret.setText(secret);
        }
    }

    @FXML
    private void onAbrirNavegador() {
        String url = txtQrUrl.getText();
        if (url == null || url.isBlank()) return;
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI(url));
            }
        } catch (Exception ignored) {
            // Si falla, el usuario aún puede copiar manualmente
        }
    }

    @FXML
    private void onCopiarEnlace() {
        String url = txtQrUrl.getText();
        if (url == null || url.isBlank()) return;
        ClipboardContent content = new ClipboardContent();
        content.putString(url);
        Clipboard.getSystemClipboard().setContent(content);
        lblEstado.setText("Enlace copiado al portapapeles.");
    }

    @FXML
    private void onContinuar() {
        cerrarVentana();
        if (onContinuar != null) {
            onContinuar.run();
        }
    }

    @FXML
    private void onCancelar() {
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }
}
