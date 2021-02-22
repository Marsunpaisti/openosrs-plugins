package net.runelite.client.plugins.paistisuite.api;

import kotlin.Pair;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.NPC;
import net.runelite.api.ObjectComposition;
import net.runelite.api.TileObject;
import net.runelite.api.queries.GameObjectQuery;
import net.runelite.api.queries.GroundObjectQuery;
import net.runelite.api.queries.NPCQuery;
import net.runelite.api.queries.WallObjectQuery;
import net.runelite.client.plugins.paistisuite.PaistiSuite;
import net.runelite.client.plugins.paistisuite.api.types.PTileObject;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
public class PObjects {

    public static ObjectComposition getRealDefinition(int id) {
        ObjectComposition def = PUtils.getClient().getObjectDefinition(id);
        ObjectComposition impostor = def.getImpostorIds() != null ? def.getImpostor() : null;
        if (impostor != null) return impostor;
        return def;
    }

    public static ObjectComposition getObjectDef(TileObject go) {
        if (go == null) return null;
        return PUtils.clientOnly(() -> getRealDefinition(go.getId()), "getObjectDef");
    }

    private static Future<ObjectComposition> getFutureObjectDef(TileObject go) {
        if (go == null) return null;
        return PaistiSuite.getInstance().clientExecutor.schedule(() ->  getRealDefinition(go.getId()), "getObjectDef");
    }

    public static Collection<PTileObject> getAllObjects()
    {
        return PUtils.clientOnly(() -> {
            Collection<TileObject> allObjects = new GameObjectQuery()
                    .result(PUtils.getClient())
                    .list
                    .stream()
                    .map(go -> (TileObject)go)
                    .collect(Collectors.toList());

            new WallObjectQuery()
                    .result(PUtils.getClient())
                    .list
                    .stream()
                    .map(go -> (TileObject)go)
                    .forEach(allObjects::add);
            new GroundObjectQuery()
                    .result(PUtils.getClient())
                    .list
                    .stream()
                    .map(go -> (TileObject)go)
                    .forEach(allObjects::add);
            return allObjects
                    .stream()
                    .map(PTileObject::new)
                    .collect(Collectors.toList());
        }, "getAllObjects");
    }

    public static PTileObject findObject(Predicate<PTileObject> filter){
        return getAllObjects()
                .stream()
                .filter(filter)
                .sorted((a,b) -> (int)Math.round(PPlayer.distanceTo(a) - PPlayer.distanceTo(b)))
                .findFirst()
                .orElse(null);
    }
    public static List<PTileObject> findAllObjects(Predicate<PTileObject> filter){
        return getAllObjects()
                .stream()
                .filter(filter)
                .collect(Collectors.toList());
    }


    public static NPC findNPC(Predicate<NPC> pred){
        return new NPCQuery()
                .filter(pred)
                .result(PUtils.getClient())
                .nearestTo(PPlayer.get());
    }
    public static List<NPC> findAllNPCs(Predicate<NPC> pred){
        return new NPCQuery()
                .filter(pred)
                .result(PUtils.getClient())
                .list;
    }
}
