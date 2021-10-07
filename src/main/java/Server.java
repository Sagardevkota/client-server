
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Server {

    private static final int PORT_NUM = 5999;

    private static final HashMap<String,String> clientMessages = new HashMap<>();

    private static ServerSocket serverSocket;
    private final Stack<ServerThread> clients = new Stack<>();
    private final ChatServer chatServer;

    Server(ChatServer chatServer) throws IOException {
        serverSocket = new ServerSocket(PORT_NUM);
        this.chatServer = chatServer;
    }

    public void init() {
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                //create thread for each new client
                ServerThread serverThread = new ServerThread(socket);
                clients.add(serverThread);
                serverThread.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void sendMessage(String message) throws IOException {

        clients.peek().sendMessage(message);
//        clients.forEach(client -> {
//            try {
//                client.sendMessage(message);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        });
    }

    public void closeServerSocket() throws IOException {
        serverSocket.close();
    }


    class ServerThread extends Thread {
        private Socket socket;
        private DataInputStream dataInputStream;
        private DataOutputStream dataOutputStream;
        private String currentClientName = "";
        private boolean isFirstTime = true;

        ServerThread(Socket socket) throws IOException {
            this.socket = socket;
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
        }

        public void sendMessage(String message) throws IOException {

            int type = 1;
            if (!isFirstTime) type = 2;

            isFirstTime = false;


            dataOutputStream.writeByte(type);
            dataOutputStream.writeUTF(message);
            dataOutputStream.flush(); // Send off the data

        }

        @Override
        public void run() {
            super.run();
            while (true) {
                try {
                    readMessage();
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }


        public void readMessage() throws IOException, InterruptedException {
            byte messageType = dataInputStream.readByte();
            String message = dataInputStream.readUTF();
            String msg = "";

            if (messageType == 1) {
                msg = message + " connected to server at " + getFormattedDate();
                currentClientName = message;
                sendMessage(" I see you have connected how are you? ");
            } else {
                msg = currentClientName + " " + getFormattedDate() + ": " + message;
            }
            clientMessages.put(currentClientName,msg);
            chatServer.setClientsMsgArea(msg);

            System.out.println(msg);

        }

        public void closeSocket() throws IOException {
            socket.close();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ServerThread)) return false;
            ServerThread that = (ServerThread) o;
            return isFirstTime == that.isFirstTime && Objects.equals(socket, that.socket) && Objects.equals(dataInputStream, that.dataInputStream) && Objects.equals(dataOutputStream, that.dataOutputStream) && Objects.equals(currentClientName, that.currentClientName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(socket, dataInputStream, dataOutputStream, currentClientName, isFirstTime);
        }
    }
    public String getFormattedDate() {
        LocalDateTime myDateObj = java.time.LocalDateTime.now();
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return  myDateObj.format(myFormatObj);
    }


}
