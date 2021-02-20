package net.runelite.client.plugins.aiofighter;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.queries.NPCQuery;
import net.runelite.client.plugins.paistisuite.api.PPlayer;
import net.runelite.client.plugins.paistisuite.api.PUtils;
import net.runelite.client.plugins.paistisuite.api.WebWalker.walker_engine.local_pathfinding.AStarNode;
import net.runelite.client.plugins.paistisuite.api.WebWalker.walker_engine.real_time_collision.CollisionDataCollector;
import net.runelite.client.plugins.paistisuite.api.WebWalker.wrappers.RSTile;
import net.runelite.client.ui.overlay.*;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Slf4j
@Singleton
public class AIOFighterOverlay extends Overlay
{
	private final Client client;
	private final AIOFighter plugin;
	private final AIOFighterConfig config;

	@Inject
	private AIOFighterOverlay(final Client client, final AIOFighter plugin, final AIOFighterConfig config)
	{
		this.client = client;
		this.plugin = plugin;
		this.config = config;

		setPosition(OverlayPosition.DYNAMIC);
		setPriority(OverlayPriority.LOW);
		setLayer(OverlayLayer.ABOVE_SCENE);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!config.enableOverlay()) return null;


		if (plugin.searchRadiusCenter != null){
			drawTile(graphics, plugin.searchRadiusCenter, new Color(66, 254, 254, 35), new Color(66, 254, 254, 120));
		}
		if (plugin.safeSpot != null){
			drawTile(graphics, plugin.safeSpot, new Color(0, 255, 0, 35), new Color(0, 255, 0, 120));
		}

		if (plugin.enemiesToTarget != null && plugin.searchRadiusCenter != null){
			java.util.List<NPC> validTargets = plugin.getValidTargets();
			if (validTargets != null && validTargets.size() > 0){
				for (NPC n : validTargets){
					highlightNpc(graphics, n, new Color(66, 254, 254, 35), new Color(66, 254, 254, 120));
					//OverlayUtil.renderActorTextOverlay(graphics, n, "" + plugin.pathFindDistance(n.getWorldLocation()), new Color(255, 0, 0));
				}
			}
		}
		return null;
	}

	private void highlightNpc(Graphics2D graphics, NPC npc, Color fillColor, Color borderColor){
		Shape hull = npc.getConvexHull();
		if (hull == null) return;
		Color originalColor = graphics.getColor();
		Stroke originalStroke = graphics.getStroke();
		graphics.setStroke(new BasicStroke(2));
		graphics.setColor(fillColor);
		graphics.fill(hull);
		graphics.setColor(borderColor);
		graphics.draw(hull);
		graphics.setStroke(originalStroke);
		graphics.setColor(originalColor);
	}

	private void drawTile(Graphics2D graphics, WorldPoint tile, Color fillColor, Color borderColor)
	{
		if (tile.getPlane() != client.getPlane())
		{
			return;
		}

		LocalPoint lp = LocalPoint.fromWorld(client, tile);
		if (lp == null)
		{
			return;
		}

		Polygon poly = Perspective.getCanvasTilePoly(client, lp);
		if (poly == null)
		{
			return;
		}
		final Stroke originalStroke = graphics.getStroke();
		graphics.setStroke(new BasicStroke(1));
		graphics.setColor(fillColor);
		graphics.fillPolygon(poly);
		graphics.setColor(borderColor);
		graphics.drawPolygon(poly);
		graphics.setStroke(originalStroke);
	}
}