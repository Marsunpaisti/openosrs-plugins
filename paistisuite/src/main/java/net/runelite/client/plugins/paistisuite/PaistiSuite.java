package net.runelite.client.plugins.paistisuite;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.inject.Injector;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.events.*;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.plugins.paistisuite.framework.ClientExecutor;
import net.runelite.client.plugins.paistisuite.framework.MenuInterceptor;
import net.runelite.client.plugins.paistisuite.sidepanel.PaistiSuitePanel;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;
import org.pf4j.Extension;

import java.awt.image.BufferedImage;

@Extension
@PluginDescriptor(
	name = "PaistiSuite",
	description = "Scripting framework by Paisti",
	tags = {"npcs", "items"},
	type = PluginType.UTILITY
)

@Slf4j
@Singleton
public class PaistiSuite extends Plugin
{
	@Inject
	public ClientExecutor clientExecutor;
	@Inject
	public Client client;
	@Inject
	public ChatMessageManager chatMessageManager;
	@Inject
	public ItemManager itemManager;
	@Inject
	private ClientToolbar clientToolbar;
	@Inject
	protected Injector injector;

	private PaistiSuitePanel panel;
	private NavigationButton navButton;
	private static PaistiSuite instance;

	public static PaistiSuite getInstance(){
		return instance;
	}

	@Override
	protected void startUp() {
		addSidePanel();
		instance = this;
		if (clientExecutor != null) clientExecutor.clearAllTasks();
	}

	@Override
	protected void shutDown() {
		if (clientExecutor != null) clientExecutor.clearAllTasks();
	}

	@Subscribe
	private void onClientTick(ClientTick t) {
		clientExecutor.runAllTasks();
	}

	@Subscribe
	private void onMenuOptionClicked(MenuOptionClicked event){
		MenuInterceptor.onMenuOptionClicked(event);
	}

	private void addSidePanel(){
		final BufferedImage icon = ImageUtil.getResourceStreamFromClass(getClass(), "nav.png");

		if (injector != null){
			panel = injector.getInstance(PaistiSuitePanel.class);
			navButton = NavigationButton.builder()
					.tooltip("PaistiSuite")
					.icon(icon)
					.priority(100)
					.panel(panel)
					.build();
			clientToolbar.addNavigation(navButton);
		}
	}
}
