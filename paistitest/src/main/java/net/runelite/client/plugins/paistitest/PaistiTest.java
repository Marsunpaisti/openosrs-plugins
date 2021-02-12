package net.runelite.client.plugins.paistitest;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.inject.Provides;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.regex.Pattern;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.ItemDefinition;
import net.runelite.api.ItemID;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetID;
import net.runelite.api.widgets.WidgetInfo;
import static net.runelite.api.widgets.WidgetInfo.SEED_VAULT_ITEM_CONTAINER;
import static net.runelite.api.widgets.WidgetInfo.TO_CHILD;
import static net.runelite.api.widgets.WidgetInfo.TO_GROUP;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.util.QuantityFormatter;
import net.runelite.http.api.examine.ExamineClient;
import okhttp3.OkHttpClient;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "PaistiTest",
	description = "PaistiTest description",
	tags = {"npcs", "items"},
	type = PluginType.UTILITY
)

@Slf4j
public class PaistiTest extends Plugin
{
	@Inject
	private ChatMessageManager chatMessageManager;

	@Inject
	private Client client;

	@Subscribe
	void onMenuOptionClicked(MenuOptionClicked event)
	{
	}

	@Subscribe
	void onChatMessage(ChatMessage event)
	{
	}

	@Subscribe
	void onGameTick(GameTick tick) {
		Widget w = client.getWidget(WidgetID.DIALOG_NPC_GROUP_ID, 0);
		if (w == null) {
			sendGameMessage("w is null");
		} else {
			sendGameMessage("w: " + w);
		}
	}

	public void sendGameMessage(String message)
	{
		chatMessageManager
				.queue(QueuedMessage.builder()
						.type(ChatMessageType.CONSOLE)
						.runeLiteFormattedMessage(
								new ChatMessageBuilder()
										.append(ChatColorType.HIGHLIGHT)
										.append(message)
										.build())
						.build());
	}
}
