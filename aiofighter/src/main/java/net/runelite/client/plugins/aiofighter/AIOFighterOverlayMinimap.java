package net.runelite.client.plugins.aiofighter;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.plugins.paistisuite.api.PUtils;
import net.runelite.client.ui.overlay.*;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;

@Slf4j
@Singleton
public class AIOFighterOverlayMinimap extends Overlay
{
	private final Client client;
	private final AIOFighter plugin;
	private final AIOFighterConfig config;

	@Inject
	private AIOFighterOverlayMinimap(final Client client, final AIOFighter plugin, final AIOFighterConfig config)
	{
		this.client = client;
		this.plugin = plugin;
		this.config = config;

		setPosition(OverlayPosition.DYNAMIC);
		setPriority(OverlayPriority.LOW);
		setLayer(OverlayLayer.ABOVE_WIDGETS);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!config.enableOverlay()) return null;

		if (plugin.searchRadiusCenter != null){
			renderFightArea(graphics);
		}
		return null;
	}

	public void renderFightArea(Graphics2D graphics)
	{
		int radius = plugin.searchRadius*4;
		LocalPoint lp = LocalPoint.fromWorld(PUtils.getClient(), plugin.searchRadiusCenter);
		if (lp == null) return;
		Point mini = Perspective.localToMinimap(PUtils.getClient(), lp);
		if (mini == null) return;
		graphics.setColor(new Color(66, 254, 254, 50));
		graphics.fillOval(mini.getX() - radius, mini.getY() - radius, radius*2, radius*2);
		graphics.setColor(new Color(66, 254, 254, 155));
		graphics.drawOval(mini.getX() - radius, mini.getY() - radius, radius*2, radius*2);
	}
}