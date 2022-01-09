package net.runelite.client.plugins.paistisuite.api.WebWalker.walker_engine.real_time_collision;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.plugins.paistisuite.api.PPlayer;
import net.runelite.client.plugins.paistisuite.api.PUtils;
import net.runelite.client.plugins.paistisuite.api.WebWalker.wrappers.RSTile;
import net.runelite.rs.api.RSClient;

@Slf4j
public class CollisionDataCollector {
    private static Client client = PUtils.getClient();

    public static int[][] getCollisionData(){
        //log.info(((RSClient)client).getCollisionMaps().length + " total maps");
        int[][] collisionData = ((RSClient)client).getCollisionMaps()[PUtils.getClient().getPlane()].getFlags();
        return collisionData;
    }

    private static int lastGeneratedTick = -1;
    public static synchronized void generateRealTimeCollision(){
        if (lastGeneratedTick == client.getTickCount()) return;
        lastGeneratedTick = client.getTickCount();
        RealTimeCollisionTile.clearMemory();

        RSTile playerPosition = new RSTile(PPlayer.getWorldLocation());
        int[][] collisionData = getCollisionData();

        if (collisionData == null) {
            return;
        }

        for (int i = 0; i < collisionData.length; i++) {
            for (int j = 0; j < collisionData[i].length; j++) {
                RSTile localTile = new RSTile(i, j, playerPosition.getPlane(), RSTile.TYPES.LOCAL);
                RSTile worldTile = localTile.toWorldTile(PUtils.getClient());
                RealTimeCollisionTile.create(worldTile.getX(), worldTile.getY(), worldTile.getPlane(), collisionData[i][j]);
            }
        }
    }

    public static synchronized void updateRealTimeCollision(){
        RSTile playerPosition = new RSTile(PPlayer.location());
        int[][] collisionData = getCollisionData();
        if(collisionData == null)
            return;
        for (int i = 0; i < collisionData.length; i++) {
            for (int j = 0; j < collisionData[i].length; j++) {
                RSTile localTile = new RSTile(i, j, playerPosition.getPlane(), RSTile.TYPES.LOCAL);
                RSTile worldTile = localTile.toWorldTile(PUtils.getClient());
                RealTimeCollisionTile realTimeCollisionTile = RealTimeCollisionTile.get(worldTile.getX(), worldTile.getY(), worldTile.getPlane());
                if (realTimeCollisionTile != null){
                    realTimeCollisionTile.setCollisionData(collisionData[i][j]);
                } else {
                    RealTimeCollisionTile.create(worldTile.getX(), worldTile.getY(), worldTile.getPlane(), collisionData[i][j]);
                }
            }
        }
    }

}