/*
 * Copyright (c) 2018, TheLonelyDev <https://github.com/TheLonelyDev>
 * Copyright (c) 2018, Adam <Adam@sigterm.info>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.webwalker;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.paistisuite.api.WebWalker.walker_engine.WalkerEngine;
import net.runelite.client.plugins.paistisuite.api.WebWalker.walker_engine.local_pathfinding.PathAnalyzer;
import net.runelite.client.plugins.paistisuite.api.WebWalker.wrappers.RSTile;
import net.runelite.client.ui.overlay.*;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;
import java.util.List;

@Slf4j
@Singleton
public class WebWalkerOverlay extends Overlay
{
	private final Client client;
	private final WebWalker plugin;
	private final WebWalkerConfig config;

	@Inject
	private WebWalkerOverlay(final Client client, final WebWalker plugin, final WebWalkerConfig config)
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
		RSTile debugFurthestTile = null;
		if (WalkerEngine.getInstance().debugFurthestReachable != null) {
			debugFurthestTile = WalkerEngine.getInstance().debugFurthestReachable.getDestination().getRSTile();
			if (debugFurthestTile != null){
				drawTile(graphics, debugFurthestTile, Color.green);
			}
		}

		synchronized(plugin){
			List<RSTile> path = plugin.path;
			if (path == null) return null;
			for (RSTile tile : path)
			{
				if (debugFurthestTile != null){
					if (tile.equals(debugFurthestTile)) continue;
				}
				drawTile(graphics, tile, Color.cyan);
			}
		}


		return null;
	}

	private void drawTile(Graphics2D graphics, RSTile tile, Color color)
	{
		WorldPoint point = tile.toWorldPoint();
		if (point.getPlane() != client.getPlane())
		{
			return;
		}

		LocalPoint lp = LocalPoint.fromWorld(client, point);
		if (lp == null)
		{
			return;
		}

		Polygon poly = Perspective.getCanvasTilePoly(client, lp);
		if (poly == null)
		{
			return;
		}

		OverlayUtil.renderPolygonThin(graphics, poly, color);
		//OverlayUtil.renderPolygon(graphics, poly, color);
	}
}