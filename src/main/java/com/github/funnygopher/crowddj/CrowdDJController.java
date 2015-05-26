package com.github.funnygopher.crowddj;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class CrowdDJController implements Initializable {

    TextField txtPort;
    PasswordField txtPassword;
    Button bStart;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        txtPort.setText("8080");
        bStart.setDisable(true);

        txtPort.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                try {
                    Integer.parseInt(newValue);
                    if (newValue.endsWith("f") || newValue.endsWith("d")) {
                        txtPort.setText(newValue.substring(0, newValue.length() - 1));
                    }
                } catch (NumberFormatException e) {
                    if (newValue.isEmpty()) {
                        txtPort.setText(newValue);
                    } else {
                        txtPort.setText(oldValue);
                    }
                }

                if(!txtPort.getText().isEmpty() && !txtPassword.getText().isEmpty()) {
                    bStart.setDisable(false);
                }
            }
        });

        txtPassword.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(!txtPort.getText().isEmpty() && !txtPassword.getText().isEmpty()) {
                    bStart.setDisable(false);
                }
            }
        });
    }

    private void onStartButtonClick() {
        int port = Integer.parseInt(txtPort.getText());
        String password = txtPassword.getText();

        CrowdDJ crowdDJ = new CrowdDJ(port, password);
    }
}
