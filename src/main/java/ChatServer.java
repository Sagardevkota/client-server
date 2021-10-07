import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class ChatServer extends Application {

    private static final int SCREEN_HEIGHT = 600;
    private static final int SCREEN_WIDTH = 800;

    //to set list of clients from db
    private final TextArea clientMsgArea = new TextArea();
    private Server server;


    @Override
    public void start(Stage primaryStage) throws IOException {
        server = new Server(this);
        //start server in new thread to avoid going into infinite loop and freeze
        new Thread(() -> server.init()).start();
        primaryStage.setTitle("Chat Server");
        VBox mainContainer = new VBox();
        mainContainer.setPrefWidth(SCREEN_WIDTH);
        mainContainer.setPrefHeight(SCREEN_HEIGHT);
        mainContainer.setPadding(new Insets(10, 20, 10, 35));

        Label label = new Label("Messages");
        label.setPadding(new Insets(10, 0, 5, 0));

        clientMsgArea.setPrefWidth(SCREEN_WIDTH);
        clientMsgArea.setPrefHeight(SCREEN_HEIGHT / 2f);
        clientMsgArea.setEditable(false);

        Label label1 = new Label("Message to send");
        label1.setPadding(new Insets(20, 0, 20, 0));

        HBox lastRow = new HBox(10);
        lastRow.setPrefWidth(SCREEN_WIDTH);
        lastRow.setPrefHeight(SCREEN_HEIGHT / 5f);

        TextArea serverMsgArea = new TextArea();
        serverMsgArea.setPrefHeight(SCREEN_HEIGHT / 5f);
        serverMsgArea.setPrefWidth(SCREEN_WIDTH / 1.2);

        Button sendBtn = new Button("Send");

        lastRow.getChildren().add(serverMsgArea);
        lastRow.getChildren().add(sendBtn);

        mainContainer.getChildren().add(label);
        mainContainer.getChildren().add(clientMsgArea);
        mainContainer.getChildren().add(label1);
        mainContainer.getChildren().add(lastRow);

        Scene scene = new Scene(mainContainer, SCREEN_WIDTH, SCREEN_HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.show();

        sendBtn.setOnAction(event -> {
            String message = serverMsgArea.getText();
            if (!message.isEmpty()) {
                try {
                    server.sendMessage(message);
                    serverMsgArea.clear();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    //set list of clients
    public void setClientsMsgArea(String message) {
        clientMsgArea.appendText(message + "\n");
    }

    //chat gui entry point
    public static void main(String[] args) {
        launch(args);
    }


}
