package com.github.funnygopher.crowddj;

import com.github.funnygopher.crowddj.javafx.CrowdDJController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
            CrowdDJController controller = new CrowdDJController(crowdDJ);
            loader.setController(controller);
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("CrowdDJ");
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
