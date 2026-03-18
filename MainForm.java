import java.awt.*;
import javax.swing.*;

public class MainFrame extends JFrame {
    public void initialize(User user) {
        JPanel infoPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        
        infoPanel.add(new JLabel("Name"));
        infoPanel.add(new JLabel(user.name));
        infoPanel.add(new JLabel("Email"));
        infoPanel.add(new JLabel(user.email));
        infoPanel.add(new JLabel("Phone"));
        infoPanel.add(new JLabel(user.phone));
        infoPanel.add(new JLabel("Address"));
        infoPanel.add(new JLabel(user.address));

        Component[] components = infoPanel.getComponents();
        for (Component component : components) {
            component.setFont(new Font("Segoe print", Font.BOLD, 18));
        }

        add(infoPanel, BorderLayout.NORTH);
