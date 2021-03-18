package net.runelite.client.plugins.paistisuite.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.inject.Inject;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;
import net.runelite.client.plugins.paistisuite.PaistiSuite;
import net.runelite.client.ui.ClientUI;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.util.ImageUtil;

public class PaistiSuitePanel extends PluginPanel
{
	final static Color PANEL_BACKGROUND_COLOR = ColorScheme.DARK_GRAY_COLOR;
	final static Color BACKGROUND_COLOR = ColorScheme.DARKER_GRAY_COLOR;

	static final Font NORMAL_FONT = FontManager.getRunescapeFont();
	static final Font SMALL_FONT = FontManager.getRunescapeSmallFont();

	private static final ImageIcon HELP_ICON;
	private static final ImageIcon HELP_HOVER_ICON;

	private final PaistiSuite suite;

	static
	{
		final BufferedImage helpIcon =
			ImageUtil.recolorImage(
				ImageUtil.getResourceStreamFromClass(PaistiSuite.class, "help.png"), ColorScheme.GRAND_EXCHANGE_PRICE
			);
		HELP_ICON = new ImageIcon(helpIcon);
		HELP_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset(helpIcon, 0.53f));
	}

	@Inject
	public PaistiSuitePanel(PaistiSuite suite)
	{
		super(false);
		this.suite = suite;

		this.setBackground(PANEL_BACKGROUND_COLOR);
		this.setLayout(new BorderLayout());

		buildPanel();
	}

	void buildPanel()
	{
		removeAll();
		JPanel contentPanel = new JPanel(new BorderLayout());

		contentPanel.add(tabbedPane(), BorderLayout.NORTH);

		add(titleBar(), BorderLayout.NORTH);
		add(contentPanel, BorderLayout.CENTER);

		revalidate();
		repaint();
	}

	private JPanel titleBar()
	{
		JPanel titlePanel = new JPanel(new BorderLayout());
		titlePanel.setBorder(new EmptyBorder(10, 10, 10, 10));

		JLabel title = new JLabel();
		JLabel help = new JLabel(HELP_ICON);

		title.setText("PaistiSuite configuration");
		title.setForeground(Color.WHITE);

		help.setToolTipText("Info");
		help.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent mouseEvent)
			{
				JOptionPane.showMessageDialog(
					ClientUI.getFrame(),
					"<html><center>The configs in this panel can be used to configure PaistiSuite use.<br>",
					"PaistiSuite Configuration",
					JOptionPane.QUESTION_MESSAGE
				);
			}

			@Override
			public void mouseEntered(MouseEvent mouseEvent)
			{
				help.setIcon(HELP_HOVER_ICON);
			}

			@Override
			public void mouseExited(MouseEvent mouseEvent)
			{
				help.setIcon(HELP_ICON);
			}
		});
		help.setBorder(new EmptyBorder(0, 3, 0, 0));

		titlePanel.add(title, BorderLayout.WEST);
		titlePanel.add(help, BorderLayout.EAST);

		return titlePanel;
	}

	private JTabbedPane tabbedPane()
	{
		JTabbedPane mainTabPane = new JTabbedPane();

		JScrollPane accountPanel = wrapContainer(new PaistiSuiteAccountPanel(this.suite));

		mainTabPane.add("Accounts", accountPanel);

		return mainTabPane;
	}

	public static JScrollPane wrapContainer(final JPanel container)
	{
		final JPanel wrapped = new JPanel(new BorderLayout());
		wrapped.add(container, BorderLayout.NORTH);
		wrapped.setBackground(PANEL_BACKGROUND_COLOR);

		final JScrollPane scroller = new JScrollPane(wrapped);
		scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroller.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));
		scroller.setBackground(PANEL_BACKGROUND_COLOR);

		return scroller;
	}


}
