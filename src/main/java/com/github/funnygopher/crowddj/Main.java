package com.github.funnygopher.crowddj;

import com.github.funnygopher.crowddj.javafx.SetupController;
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
        FXMLLoader loader = new FXMLLoader(SetupController.class.getResource("/fxml/Setup.fxml"));
        SetupController controller = new SetupController();
        loader.setController(controller);

        Parent root = loader.load();
        primaryStage.setTitle("CrowdDJ Setup");

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void setCrowdDJ() {

    }
}
