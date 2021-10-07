import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class AppConfig {

    public static Border getBorder() {
        Border border = BorderFactory.createLineBorder(Color.BLACK);
        return BorderFactory.createCompoundBorder(border,
                BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }


}
