import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import java.io.FileInputStream;

public class HelpFileGUI extends Application {

    private static final int SCREEN_HEIGHT = 800;
    private static final int SCREEN_WIDTH = 900;


    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Help File");
        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();

        FileInputStream fin = new FileInputStream("src/main/resources/help.html");
        int i;
        StringBuilder htmlString = new StringBuilder();
        while ((i = fin.read()) != -1) {
            System.out.print((char) i);
            htmlString.append((char) i);
        }
        fin.close();

        webEngine.loadContent(htmlString.toString());
        Scene scene = new Scene(webView, SCREEN_WIDTH, SCREEN_HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.show();

    }


    public static void main(String[] args) {
        launch(args);
    }


}
