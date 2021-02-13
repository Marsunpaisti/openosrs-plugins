package net.runelite.client.plugins.paistisuite.sidepanel;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.ui.PluginPanel;
import javax.inject.Inject;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import static javax.swing.BoxLayout.Y_AXIS;


@Slf4j
public class PaistiSuitePanel extends PluginPanel {
    JComboBox selector;
    ExecutorService executorService;
    @Inject
    public PaistiSuitePanel(
            final Client client
    )
    {
        super(false);
        this.selector = new JComboBox<>();
        this.executorService = Executors.newSingleThreadExecutor();
        init();
    }


    private void init()
    {
        selector.addItem("Option 1");
        selector.addItem("Option 2");
        selector.addItem("Option 3");
        selector.addItem("Option 4");
        selector.setSelectedIndex(0);
        selector.addActionListener((e) ->
        {
            String name = (String) selector.getSelectedItem();
            log.info("Selected " + name);
        });


        final JLabel title = new JLabel();
        title.setText("PaistiSuite scripts");
        title.setForeground(Color.WHITE);
        title.setHorizontalAlignment(JLabel.CENTER);
        title.setVerticalAlignment(JLabel.CENTER);

        final JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, Y_AXIS));
        topPanel.setBorder(new EmptyBorder(0, 0, 10, 0));
        topPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        topPanel.add(title);
        topPanel.add(selector);

        add(topPanel);

    }
}
