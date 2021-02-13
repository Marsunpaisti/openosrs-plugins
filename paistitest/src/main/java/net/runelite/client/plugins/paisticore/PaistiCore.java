package net.runelite.client.plugins.paisticore;

import javax.inject.Inject;
import javax.inject.Singleton;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.events.*;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.plugins.paisticore.framework.ClientExecutor;
import net.runelite.client.plugins.paisticore.framework.MenuInterceptor;
import net.runelite.client.plugins.paisticore.framework.PScriptRunner;
import net.runelite.client.plugins.paisticore.scripts.TestScript;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "PaistiTest",
	description = "PaistiTest description",
	tags = {"npcs", "items"},
	type = PluginType.UTILITY
)

@Slf4j
@Singleton
public class PaistiCore extends Plugin
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
	public MenuInterceptor menuInterceptor;

	private PScriptRunner scriptRunner;
	private Thread scriptThread;
	private static PaistiCore instance;

	public static PaistiCore getInstance(){
		return instance;
	}

	@Override
	protected void startUp() {
		instance = this;
		if (clientExecutor != null) clientExecutor.clearAllTasks();
		try {
			scriptRunner = new PScriptRunner(TestScript.class);
		} catch (Exception e){
			log.error("Error: " + ExceptionUtils.getStackTrace(e));
			return;
		}
		scriptThread = new Thread(scriptRunner);
		scriptThread.start();
	}

	@Override
	protected void shutDown() {
		if (scriptRunner != null) scriptRunner.requestStop();
		if (clientExecutor != null) clientExecutor.clearAllTasks();
	}

	@Subscribe
	private void onClientTick(ClientTick t) {
		clientExecutor.runAllTasks();
	}

	@Subscribe void onGameTick(GameTick t){
	}

	@Subscribe
	private void onMenuOptionClicked(MenuOptionClicked event){
		menuInterceptor.onMenuOptionClicked(event);
	}

}
