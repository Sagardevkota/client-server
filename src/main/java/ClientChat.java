import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ClientChat extends Application {

    private static final int SCREEN_HEIGHT = 700;
    private static final int SCREEN_WIDTH = 800;

    private final TextArea serverMsgArea = new TextArea();
    private final ObservableList<String> clientList = FXCollections.observableArrayList();

    private Client client;
    private String currentClient;


    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Client Chat");
        VBox mainContainer = new VBox();
        mainContainer.setPrefWidth(SCREEN_WIDTH);
        mainContainer.setPrefHeight(SCREEN_HEIGHT);
        mainContainer.setPadding(new javafx.geometry.Insets(10,20,10,35));

        //first row
        HBox firstRow = new HBox();
        firstRow.setPrefWidth(SCREEN_WIDTH);
        firstRow.setPadding(new Insets(20,0,10,0));

        Label label = new Label("Available Users:");
        label.setPrefWidth(SCREEN_WIDTH/3f);
        Label label1 = new Label("Selected Users:");
        label1.setPrefWidth(SCREEN_WIDTH/3f);

        firstRow.getChildren().add(label);
        firstRow.getChildren().add(label1);

        //second row
        HBox secondRow = new HBox(10);
        secondRow.setPrefWidth(SCREEN_WIDTH);

        ListView<String> listView = new ListView<>(clientList);
        listView.setPrefWidth(SCREEN_WIDTH/3f);
        listView.setPrefHeight(SCREEN_HEIGHT/4f);

        //source drag
        listView.setOnDragDetected(event -> {
            System.out.println("onDragDetected");

            /* allow any transfer mode */
            Dragboard db = listView.startDragAndDrop(TransferMode.ANY);


            /* put a string on dragboard */
            ClipboardContent content = new ClipboardContent();
            content.putString("copied text");
            db.setContent(content);

            event.consume();
        });



        TextField selectedUser = new TextField();
        selectedUser.setPrefWidth(SCREEN_WIDTH/3f);

        handleDND(listView,selectedUser);


        Button connectBtn = new Button("Connect");
        connectBtn.setPrefWidth(SCREEN_WIDTH/6f);

        connectBtn.setOnAction(event -> {
            String selectedClient = selectedUser.getText();
            if (currentClient!=null&&currentClient.equals(selectedClient))
                return;
            currentClient = selectedClient;
            if (!selectedClient.isEmpty()) {
                try {
                    client = new Client(selectedClient, this);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

        });

        secondRow.getChildren().add(listView);
        secondRow.getChildren().add(selectedUser);
        secondRow.getChildren().add(connectBtn);


        Label label3 = new Label("Messages");
        label3.setPadding(new Insets(15,0,5,0));

        HBox lastRow = new HBox(10);
        lastRow.setPrefWidth(SCREEN_WIDTH);
        lastRow.setPrefHeight(SCREEN_HEIGHT/5f);

        TextArea clientMsgArea = new TextArea();
        clientMsgArea.setPrefHeight(SCREEN_HEIGHT/7f);
        clientMsgArea.setPrefWidth(SCREEN_WIDTH/1.2);


        Label label2 = new Label("Message to send");
        label2.setPadding(new Insets(5,0,15,0));

        Button sendBtn = new Button("Send");
        sendBtn.setOnAction(e -> {
            String message = clientMsgArea.getText();
            if (!message.isEmpty()) {
                try {
                    client.sendMessage(message, 2);
                    clientMsgArea.clear();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

        });


        lastRow.getChildren().add(clientMsgArea);
        lastRow.getChildren().add(sendBtn);

        mainContainer.getChildren().add(firstRow);
        mainContainer.getChildren().add(secondRow);
        mainContainer.getChildren().add(label3);
        mainContainer.getChildren().add(serverMsgArea);
        mainContainer.getChildren().add(label2);
        mainContainer.getChildren().add(lastRow);

        Scene scene = new Scene(mainContainer,SCREEN_WIDTH,SCREEN_HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.show();
        new Thread(() -> {
            try {
                setClientsArea();
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }).start();

    }

    private void handleDND(ListView<String> source, TextField target) {

        source.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> param) {

                ListCell<String> listCell = new ListCell<String>()
                {
                    @Override
                    protected void updateItem( String item, boolean empty )
                    {
                        super.updateItem( item, empty );
                        setText( item );
                    }
                };
                listCell.setOnDragDetected( ( MouseEvent event ) ->
                {
                    Dragboard db = listCell.startDragAndDrop( TransferMode.COPY );
                    ClipboardContent content = new ClipboardContent();
                    content.putString( listCell.getItem() );
                    db.setContent( content );
                    event.consume();
                } );

                return listCell;
            }

        });


        target.setOnDragOver(event -> {
            /* data is dragged over the target */

            /* accept it only if it is  not dragged from the same node
             * and if it has a string data */
            if (event.getGestureSource() != target &&
                    event.getDragboard().hasString()) {
                /* allow for both copying and moving, whatever user chooses */
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }

            event.consume();
        });

        target.setOnDragEntered(event -> {
            /* the drag-and-drop gesture entered the target */
            /* show to the user that it is an actual gesture target */
            if (event.getGestureSource() != target &&
                    event.getDragboard().hasString()) {

                target.setStyle("-fx-text-fill: green;");
            }

            event.consume();
        });

        target.setOnDragExited(event -> {
            /* mouse moved away, remove the graphical cues */

            target.setStyle("-fx-text-fill: black; ");

            event.consume();
        });

        target.setOnDragDropped(event -> {
            /* data dropped */
            /* if there is a string data on dragboard, read it and use it */
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasString()) {
                target.setText(db.getString());
                success = true;
            }
            /* let the source know whether the string was successfully
             * transferred and used */
            event.setDropCompleted(success);

            event.consume();
        });

    }


    public void setClientsArea() throws SQLException, ClassNotFoundException {
        AppDatabase appDatabase = new AppDatabase();
        List<String> clients = appDatabase.getClients();
        clientList.addAll(clients);
    }

    public void setServerMsgArea(String message, int i) {
        if (i==1) serverMsgArea.clear();
        serverMsgArea.appendText(message + "\n");
    }


    public static void main(String[] args) {
        launch(args);
    }
}
