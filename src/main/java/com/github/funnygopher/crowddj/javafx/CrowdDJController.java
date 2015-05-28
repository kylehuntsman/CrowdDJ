package com.github.funnygopher.crowddj.javafx;

import com.github.funnygopher.crowddj.CrowdDJ;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class CrowdDJController implements Initializable {

    @FXML
    TextField txtVLCPath;

    @FXML
    Button bStartVLC;

    private CrowdDJ crowdDJ;

    public CrowdDJController(CrowdDJ crowdDJ) {
        this.crowdDJ = crowdDJ;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        txtVLCPath.setText("C:/Program Files (x86)/VideoLAN/VLC/vlc.exe");
        crowdDJ.setVLCPath(txtVLCPath.getText());

        bStartVLC.setDisable(true);
        bStartVLC.setDisable(!crowdDJ.hasValidVLCPath());

        txtVLCPath.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                String alteredValue = newValue.replace("\\", "/");
                crowdDJ.setVLCPath(alteredValue);

                bStartVLC.setDisable(!crowdDJ.hasValidVLCPath());
            }
        });

        bStartVLC.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                onStartVLCClick();
            }
        });
    }

    private void onStartVLCClick() {
        crowdDJ.startVLC();
    }
}
