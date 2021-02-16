package net.runelite.client.plugins.paistisuite.api;

import kotlin.Pair;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.GameObject;
import net.runelite.api.NPC;
import net.runelite.api.ObjectDefinition;
import net.runelite.api.TileObject;
import net.runelite.api.queries.GameObjectQuery;
import net.runelite.api.queries.NPCQuery;
import net.runelite.api.queries.WallObjectQuery;
import net.runelite.client.plugins.paistisuite.PaistiSuite;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
public class PObjects {

    public static ObjectDefinition getRealDefinition(int id) {
        //log.info("Getting def: " + id);
        ObjectDefinition def = PUtils.getClient().getObjectDefinition(id);
        ObjectDefinition impostor = def.getImpostorIds() != null ? def.getImpostor() : def;
        //if (impostor != null) log.info("Found impostor for id: " + id +  " -> " + impostor.getId());
        if (impostor != null) return impostor;
        //log.info("No impostors for " + id);
        return def;
    }

    public static ObjectDefinition getObjectDef(TileObject go) {
        if (go == null) return null;
        return PUtils.clientOnly(() -> getRealDefinition(go.getId()), "getObjectDef");
    }

    private static Future<ObjectDefinition> getFutureObjectDef(TileObject go) {
        if (go == null) return null;
        return PaistiSuite.getInstance().clientExecutor.schedule(() ->  getRealDefinition(go.getId()), "getObjectDef");
    }

    public static Collection<Pair<TileObject, ObjectDefinition>> getAllObjectsWithDefs()
    {
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

        Collection<Pair<TileObject, ObjectDefinition>> pObjects = null;
        if (PUtils.getClient().isClientThread()) {
            pObjects = allObjects.stream().map(ob -> new Pair<TileObject, ObjectDefinition>(ob, getObjectDef(ob))).collect(Collectors.toList());
        } else {
            try {
                List<Pair<TileObject, Future<ObjectDefinition>>> futures = allObjects
                        .stream()
                        .map(ob -> new Pair<TileObject, Future<ObjectDefinition>>(ob, getFutureObjectDef(ob)))
                        .collect(Collectors.toList());

                pObjects = futures
                        .stream()
                        .map(pair -> {
                            try {
                                return new Pair<TileObject, ObjectDefinition>(pair.component1(), pair.component2().get());
                            } catch (InterruptedException | ExecutionException e) {
                                log.error(e.toString());
                                e.printStackTrace();
                            }
                            return null;
                        })
                        .collect(Collectors.toList());

            } catch (Exception e){
                log.error("Error in getPObjects: " + e);
            }
        }

        return pObjects;
    }

    public static Pair<TileObject, ObjectDefinition> findObject(Predicate<Pair<TileObject, ObjectDefinition>> filter){
        return getAllObjectsWithDefs()
                .stream()
                .filter(filter)
                .findFirst()
                .orElse(null);
    }
    public static List<Pair<TileObject, ObjectDefinition>> findAllObjects(Predicate<Pair<TileObject, ObjectDefinition>> filter){
        return getAllObjectsWithDefs()
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
