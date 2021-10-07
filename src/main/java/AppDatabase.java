
/*
 * @created 06/{10}/2021 - 12:18 PM
 * @project client-server
 * @author SAGAR DEVKOTA
 */

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AppDatabase implements ClientDao {

    private synchronized Connection getConnection() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/chat-server", "root", "");
    }

    @Override
    public List<String> getClients() throws SQLException, ClassNotFoundException {
        Connection connection = getConnection();
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery("select * from clients");

        //to find out number of row first point to last and get its row number
        List<String> clientList = new ArrayList<>();
        while (rs.next()){
            String userId = rs.getString(1);
            clientList.add(userId);
        }

        connection.close();
        return clientList;
    }



}

