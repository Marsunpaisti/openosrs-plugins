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
package net.runelite.client.plugins.quester;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.plugins.paistisuite.api.PWidgets;
import net.runelite.client.plugins.paistisuite.api.WebWalker.walker_engine.WalkerEngine;
import net.runelite.client.plugins.paistisuite.api.WebWalker.walker_engine.WebWalkerDebugRenderer;
import net.runelite.client.plugins.paistisuite.api.WebWalker.walker_engine.local_pathfinding.PathAnalyzer;
import net.runelite.client.ui.overlay.*;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Slf4j
@Singleton
public class QuesterOverlay extends Overlay
{
	private final Client client;
	private final Quester plugin;
	private final QuesterConfig config;

	@Inject
	private QuesterOverlay(final Client client, final Quester plugin, final QuesterConfig config)
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
		WebWalkerDebugRenderer.render(graphics);
		renderInfoBox(graphics);
		return null;
	}

	public void renderInfoBox(Graphics2D graphics){
		if (!plugin.isRunning()) return;
		Widget base = null;
		if (PWidgets.isSubstantiated(WidgetInfo.CHATBOX)){
			base = PWidgets.get(WidgetInfo.CHATBOX_PARENT);
		} else {
			base = PWidgets.get(WidgetInfo.CHATBOX_BUTTONS);
		}
		if (base == null) return;
		double topOfWidget  = base.getBounds().getY();
		double infoBoxHeight = 40;
		double infoBoxWidth = base.getBounds().getWidth();
		double drawPos = topOfWidget - infoBoxHeight;
		graphics.setColor(Color.white);
		graphics.fillRect(0, (int)drawPos, (int)infoBoxWidth, (int)infoBoxHeight);
		graphics.setColor(Color.black);
		graphics.setFont(new Font("Arial Bold", Font.PLAIN, 18));
		int rowHeight = 18;
		int currentRowPos = (int)drawPos + 16;
		int horizontalPos1 = (int)base.getBounds().getX() + 3;
		int horizontalPos2 = (int)base.getBounds().getX() + 3 + (int)infoBoxWidth/2;
		long d = Duration.between(plugin.startedTimestamp, Instant.now()).getSeconds();
		String runTimeStr = String.format("%d:%02d:%02d", d / 3600, (d % 3600) / 60, (d % 60));

		graphics.drawString("Runtime: " + runTimeStr, horizontalPos1, currentRowPos);
		currentRowPos += rowHeight;

		if (plugin.getQuestTaskRunner() == null) return;
		String taskName = plugin.getQuestTaskRunner().getCurrentTaskName();
		String questName = plugin.getQuestTaskRunner().getCurrentQuestName();
		if (questName == null || taskName == null) return;
		graphics.drawString(questName + " > " + taskName, horizontalPos1, currentRowPos);
		currentRowPos += rowHeight;

		//graphics.drawString("Runtime: " + runTimeStr, horizontalPos1, currentRowPos);
		//currentRowPos += rowHeight;
	}

}