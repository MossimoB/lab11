// https://github.com/MossimoB/lab11

package mossimo.bianco.lab11;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource(
                        "/mossimo/bianco/lab11/view/primary.fxml"
                )
        );

        Parent root = loader.load();
        
        Scene scene = new Scene(root);
        stage.setTitle("Campus Restaurant Ordering System");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}