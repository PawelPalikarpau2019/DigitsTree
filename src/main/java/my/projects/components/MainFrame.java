package my.projects.components;

import my.projects.repository.DigitNodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

@Component
public class MainFrame {
    private ConfigurationPanel configurationPanel;
    private TreePanel treePanel;

    @Autowired
    public MainFrame(ConfigurationPanel configurationPanel, TreePanel treePanel) {
        this.configurationPanel = configurationPanel;
        this.treePanel = treePanel;
    }

    public void show() {
        JFrame frame = new JFrame("Digits Tree");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                DigitNodeRepository.dispose();
            }
        });

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(configurationPanel.getPanel(), BorderLayout.NORTH);
        mainPanel.add(treePanel.getPanel(false), BorderLayout.CENTER);
        frame.getContentPane().add(mainPanel, BorderLayout.CENTER);

        frame.pack();
        frame.setMinimumSize(new Dimension((int) frame.getSize().getWidth() + 50, 300));

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation(dim.width / 2 - frame.getSize().width / 2, dim.height / 2 - frame.getSize().height / 2);

        frame.setVisible(true);
    }
}
