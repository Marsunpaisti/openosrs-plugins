package net.runelite.client.plugins.nmzhelperremix.Tasks;

import java.util.List;
import java.util.stream.Collectors;
import net.runelite.api.Client;
import net.runelite.api.ItemID;
import net.runelite.api.MenuEntry;
import net.runelite.api.MenuOpcode;
import net.runelite.api.Skill;
import net.runelite.api.Varbits;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.plugins.nmzhelperremix.MiscUtils;
import net.runelite.client.plugins.nmzhelperremix.NMZHelperConfig;
import net.runelite.client.plugins.nmzhelperremix.NMZHelperPlugin;
import net.runelite.client.plugins.nmzhelperremix.Task;

public class RockCakeTask extends Task
{
	private boolean rockCaking = false;
	private int nextRockCakeHp = 1;

	public RockCakeTask(NMZHelperPlugin plugin, Client client, NMZHelperConfig config)
	{
		super(plugin, client, config);
	}

	@Override
	public boolean validate()
	{
		//fail if:

		//not in the nightmare zone
		if (!MiscUtils.isInNightmareZone(client)){
			rockCaking = false;
			return false;
		}

		//not overloaded
		if (client.getVar(Varbits.NMZ_OVERLOAD) == 0){
			rockCaking = false;
			return false;
		}

		//don't have rock cake
		Widget inventoryWidget = client.getWidget(WidgetInfo.INVENTORY);

		if (inventoryWidget == null)
		{
			rockCaking = false;
			return false;
		}

		if (inventoryWidget.getWidgetItems()
			.stream()
			.filter(item -> item.getId() == ItemID.DWARVEN_ROCK_CAKE_7510)
			.collect(Collectors.toList()).isEmpty()) {

			rockCaking = false;
			return false;
		}

		//out of absorption points
		if (client.getVar(Varbits.NMZ_ABSORPTION) <= 0){
			rockCaking = false;
			return false;
		}

		//already 1 hp
		if (client.getBoostedSkillLevel(Skill.HITPOINTS) <= 1)
		{
			rockCaking = false;
			return false;
		}

		if (rockCaking){
			return true;
		}

		if (client.getBoostedSkillLevel(Skill.HITPOINTS) >= nextRockCakeHp) {
			rockCaking = true;
			double rand = Math.random();
			if (rand <= 0.8) {
				nextRockCakeHp = 2;
			} else if (rand <= 0.95) {
				nextRockCakeHp = 3;
			} else {
				nextRockCakeHp = 4;
			}
			plugin.sendGameMessage("Rock caking. Next rock cake at " + nextRockCakeHp + " hp.");
			return true;
		}

		return false;
	}

	@Override
	public String getTaskDescription()
	{
		return "Rock caking";
	}

	@Override
	public void onGameTick(GameTick event)
	{
		if (NMZHelperPlugin.rockCakeDelay > 0)
		{
			NMZHelperPlugin.rockCakeDelay--;
			return;
		}

		Widget inventoryWidget = client.getWidget(WidgetInfo.INVENTORY);

		if (inventoryWidget == null)
		{
			return;
		}

		List<WidgetItem> items = inventoryWidget.getWidgetItems()
			.stream()
			.filter(item -> item.getId() == ItemID.DWARVEN_ROCK_CAKE_7510)
			.collect(Collectors.toList());

		if (items == null || items.isEmpty())
		{
			return;
		}

		WidgetItem item = items.get(0);

		entry = new MenuEntry("Guzzle", "<col=ff9040>Dwarven rock cake", item.getId(), MenuOpcode.ITEM_THIRD_OPTION.getId(), item.getIndex(), WidgetInfo.INVENTORY.getId(), false);
		click();
	}
}
