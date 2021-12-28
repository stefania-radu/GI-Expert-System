package ui;

import helper.FileWriterHelper;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("startScene.fxml")));
        primaryStage.setTitle("Expert System");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

//        FileWriterHelper.openFile();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
