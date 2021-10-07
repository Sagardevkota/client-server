
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Stack;

public class Client{

    private static final String HOST_NAME = "localhost";
    private static final int PORT_NUM = 5999;

    private Socket socket;
    private DataInputStream dataInputStream;
    private final Stack<String> clientMessages = new Stack<>(),
            serverMessages = new Stack<>();
    private DataOutputStream dataOutputStream;
    private final ClientChat clientChat;
    private ClientThread clientThread;

    Client(String clientName, ClientChat clientChat) throws IOException {
        this.clientChat = clientChat;
        clientThread = new ClientThread();
        clientThread.createSocket();
        clientThread.sendMessage(clientName,1);
        clientThread.start();
    }

    public void sendMessage(String message,int type) throws IOException {
        clientThread.sendMessage(message,type);
    }


    class ClientThread extends Thread{

        @Override
        public void run() {
            while (true) {
                try {
                    readMessage();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        public void readMessage() throws IOException {
            byte messageType = dataInputStream.readByte();
            String message = dataInputStream.readUTF();
            String msg = "Server " + getFormattedDate() + ": " + message;

            System.out.println(msg);
            serverMessages.add(msg);
            clientChat.setServerMsgArea(msg,messageType);

        }
        private void createSocket() throws IOException {
            socket = new Socket(HOST_NAME, PORT_NUM);
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
        }


        public void sendMessage(String message, int type) throws IOException {
            clientMessages.add(message);
            // Send first message
            dataOutputStream.writeByte(type);
            dataOutputStream.writeUTF(message);
            dataOutputStream.flush(); // Send off the data
        }

        public void closeSocket() throws IOException {
            dataOutputStream.close();
            socket.close();
        }
    }



    public String getFormattedDate() {
        LocalDateTime myDateObj = java.time.LocalDateTime.now();
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return myDateObj.format(myFormatObj);
    }



}
