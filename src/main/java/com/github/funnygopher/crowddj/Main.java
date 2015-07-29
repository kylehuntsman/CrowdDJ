package com.github.funnygopher.crowddj;

import com.github.funnygopher.crowddj.javafx.AudioPlayer;
import com.github.funnygopher.crowddj.javafx.CrowdDJController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    private static CrowdDJ crowdDJ;

    public static void main(String args[]) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        CrowdDJ crowdDJ = new CrowdDJ();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CrowdDJ.fxml"));
            CrowdDJController controller = crowdDJ.getController();
            loader.setController(controller);
            Parent root = loader.load();

            Platform.setImplicitExit(true);
            Stage stage = new Stage();
            stage.setOnCloseRequest(windowEvent -> {
                crowdDJ.stopServer();

                AudioPlayer player = crowdDJ.getController().getPlayer();
                MediaPlayer mediaPlayer = player.getCurrentMediaPlayer();
                if(mediaPlayer != null) {
                    //mediaPlayer.stop();
                    mediaPlayer.dispose();
                }
            });

            stage.setTitle("CrowdDJ");
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
