package net.runelite.client.plugins.testplugin;

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
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
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
	name = "TestPlugin",
	description = "TestPlugin description",
	tags = {"npcs", "items"},
	type = PluginType.UTILITY
)
@Slf4j
public class TestPlugin extends Plugin
{
	@Inject
	private ChatMessageManager chatMessageManager;

	@Subscribe
	void onMenuOptionClicked(MenuOptionClicked event)
	{
	}

	@Subscribe
	void onChatMessage(ChatMessage event)
	{
	}
}
