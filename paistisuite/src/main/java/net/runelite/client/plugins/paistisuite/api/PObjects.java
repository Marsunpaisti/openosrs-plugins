package net.runelite.client.plugins.paistisuite.api;

import kotlin.Pair;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.GameObject;
import net.runelite.api.ObjectDefinition;
import net.runelite.api.queries.GameObjectQuery;
import net.runelite.client.plugins.paistisuite.PaistiSuite;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Slf4j
public class PObjects {

    public static ObjectDefinition getObjectDef(GameObject go) {
        if (go == null) return null;

        ObjectDefinition def = null;
        try {
            if (!PUtils.getClient().isClientThread()) {
                def = PaistiSuite.getInstance().clientExecutor.scheduleAndWait(() -> PUtils.getClient().getObjectDefinition(go.getId()), "getObjectDef");
            } else {
                def = PUtils.getClient().getObjectDefinition(go.getId());
            }
        } catch (Exception e) {
            log.error("Error in getObjectDef: " + e);
        }

        return def;
    }

    private static Future<ObjectDefinition> getFutureObjectDef(GameObject go) {
        if (go == null) return null;

        return PaistiSuite.getInstance().clientExecutor.schedule(() -> PUtils.getClient().getObjectDefinition(go.getId()), "getObjectDef");
    }

    public static Collection<Pair<GameObject, ObjectDefinition>> getAllObjectsWithDefs()
    {
        Collection<GameObject> allObjects = new GameObjectQuery().result(PUtils.getClient());
        Collection<Pair<GameObject, ObjectDefinition>> pObjects = null;
        if (PUtils.getClient().isClientThread()) {
            pObjects = allObjects.stream().map(ob -> new Pair<GameObject, ObjectDefinition>(ob, getObjectDef(ob))).collect(Collectors.toList());
        } else {
            try {
                List<Pair<GameObject, Future<ObjectDefinition>>> futures = allObjects
                        .stream()
                        .map(ob -> new Pair<GameObject, Future<ObjectDefinition>>(ob, getFutureObjectDef(ob)))
                        .collect(Collectors.toList());

                pObjects = futures
                        .stream()
                        .map(pair -> {
                            try {
                                return new Pair<GameObject, ObjectDefinition>(pair.component1(), pair.component2().get());
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

}
