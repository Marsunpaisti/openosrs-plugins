package net.runelite.client.plugins.paistisuite;

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
import net.runelite.client.plugins.paistisuite.framework.ClientExecutor;
import net.runelite.client.plugins.paistisuite.framework.MenuInterceptor;
import net.runelite.client.plugins.paistisuite.framework.PScriptRunner;
import net.runelite.client.plugins.paistisuite.scripts.TestScript;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.pf4j.Extension;

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

	private PScriptRunner scriptRunner;
	private Thread scriptThread;
	private static PaistiSuite instance;

	public static PaistiSuite getInstance(){
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
		MenuInterceptor.onMenuOptionClicked(event);
	}

}
