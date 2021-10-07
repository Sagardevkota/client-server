import java.sql.SQLException;
import java.util.List;

public interface ClientDao {
   List<String> getClients() throws SQLException, ClassNotFoundException;
}
