package com.github.funnygopher.crowddj.javafx;

import com.github.funnygopher.crowddj.CrowdDJ;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SetupController implements Initializable {

    @FXML
    TextField txtPort;

    @FXML
    PasswordField txtPassword;

    @FXML
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

                if (!txtPort.getText().isEmpty() && !txtPassword.getText().isEmpty()) {
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

        bStart.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                onStartButtonClick();
            }
        });
    }

    private void onStartButtonClick() {
        int port = Integer.parseInt(txtPort.getText());
        String password = txtPassword.getText();

        CrowdDJ crowdDJ = new CrowdDJ(port, password);

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CrowdDJ.fxml"));
            CrowdDJController controller = new CrowdDJController(crowdDJ);
            loader.setController(controller);
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("CrowdDJ");
            stage.setScene(new Scene(root));
            stage.show();

            Stage thisStage = (Stage) bStart.getScene().getWindow();
            thisStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
