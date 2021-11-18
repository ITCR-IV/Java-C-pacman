import javax.swing.*;

public class Window extends JFrame {
    private PlayingField field;

    public Window() {
        field = new PlayingField();
        add(field);
    }

    public PlayingField getField() {
        return field;
    }
}
