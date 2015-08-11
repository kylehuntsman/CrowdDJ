package com.github.funnygopher.crowddj;

import com.github.funnygopher.crowddj.javafx.CrowdDJController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

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
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/product_icon.png")));
            stage.setOnCloseRequest(windowEvent -> {
                crowdDJ.getServer().stop();
                crowdDJ.getPlayer().reset();
                crowdDJ.getPlaylist().updateDatabaseTable();
            });

            stage.setTitle("CrowdDJ - Let The Crowd Choose");
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
