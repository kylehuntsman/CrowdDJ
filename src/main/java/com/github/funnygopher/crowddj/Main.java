package com.github.funnygopher.crowddj;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    private static CrowdDJ crowdDJ;

    public static void main(String args[]) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        /*
        FXMLLoader loader = new FXMLLoader(RectifierGUIController.class.getResource("/RectifierGUI.fxml"));
        AnchorPane pane = loader.load();
        RectifierGUIController controller = loader.getController();
        controller.setParent(pane);

        primaryStage.setTitle("Image Rectifier");
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/divide-64.png")));
        Scene scene = new Scene(pane);
        primaryStage.setScene(scene);
        primaryStage.show();
        */
    }

    public static void setCrowdDJ(CrowdDJ crowdDJ) {

    }
}
