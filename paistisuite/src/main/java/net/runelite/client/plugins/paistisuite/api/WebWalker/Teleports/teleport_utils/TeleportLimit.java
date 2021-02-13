package net.runelite.client.plugins.paistisuite.api.WebWalker.Teleports.teleport_utils;

/**
 * Check limits of a teleport, e.g. wilderness level under 20 or 30.
 */
public interface TeleportLimit {
    boolean canCast();
}