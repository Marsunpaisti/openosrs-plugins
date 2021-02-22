package net.runelite.client.plugins.quester.Generic.ItemAcquisition;

import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.paistisuite.api.Filters;
import net.runelite.client.plugins.paistisuite.api.PInventory;
import net.runelite.client.plugins.quester.Generic.CompositeTask;
import net.runelite.client.plugins.quester.Generic.InteractWithObjectTask;
import net.runelite.client.plugins.quester.Quester;
import net.runelite.client.plugins.quester.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class AcquisitionTasks {
    final HashMap<String, ItemAcquisitionHandlerFactory> factories = new HashMap<String, ItemAcquisitionHandlerFactory>();
    static AcquisitionTasks instance;

    public static AcquisitionTasks getInstance() {
        if (instance == null) {
            instance = new AcquisitionTasks();
        }
        return instance;
    }

    public static List<Task> getHandlers(Quester plugin, String itemName, int quantity){
        ItemAcquisitionHandlerFactory factory = AcquisitionTasks.getInstance().factories.getOrDefault(itemName, null);
        if (factory == null) return null;
        return factory.getHandlers(plugin, quantity);
    }

    public AcquisitionTasks() {
        factories.put("Bucket of Milk", new ItemAcquisitionHandlerFactory() {
            @Override
            public List<Task> getHandlers(Quester plugin, int quantity) {
                List<Task> handlers = new ArrayList<Task>();
                CompositeTask milkCowTask;
                milkCowTask = new CompositeTask() {
                    @Override
                    public boolean condition(){
                        return PInventory.findAllItems(Filters.Items.nameEquals("Bucket")).size()
                                >= quantity - PInventory.findAllItems(Filters.Items.nameEquals("Bucket of Milk")).size();
                    }
                };
                for (int i = 1; i <= quantity; i++){
                    final int targetQuantity;
                    targetQuantity = i;
                    milkCowTask.addTask(new InteractWithObjectTask(
                            plugin,
                            "Dairy cow",
                            new String[]{"Milk"},
                            new WorldPoint(3255, 3270, 0),
                            () -> PInventory.findAllItems(Filters.Items.nameEquals("Bucket of Milk")).size() >= targetQuantity){
                        @Override
                        public boolean isCompleted(){
                            return PInventory.findAllItems(Filters.Items.nameEquals("Bucket of Milk")).size() >= targetQuantity;
                        }
                        @Override
                        public boolean condition(){
                            return PInventory.findItem(Filters.Items.nameEquals("Bucket")) != null;
                        }
                    });
                }

                handlers.add(milkCowTask);
                return handlers;
            }
        });

        factories.put("Bucket", new ItemAcquisitionHandlerFactory() {
            @Override
            public List<Task> getHandlers(Quester plugin, int quantity) {
                List<Task> handlers = new ArrayList<Task>();
                BuyItemFromStoreTask buyBucketsTask = new BuyItemFromStoreTask(plugin, "Bucket", quantity, 4, new WorldPoint(3212, 3247, 0));
                handlers.add(buyBucketsTask);
                return handlers;
            }
        });

        factories.put("Pot of flour", new ItemAcquisitionHandlerFactory() {
            @Override
            public List<Task> getHandlers(Quester plugin, int quantity) {
                List<Task> handlers = new ArrayList<Task>();
                BuyItemFromStoreTask buyFlourpotTask = new BuyItemFromStoreTask(plugin, "Pot of flour", quantity, 15, new WorldPoint(3014, 3206, 0));
                handlers.add(buyFlourpotTask);
                return handlers;
            }
        });

        factories.put("Egg", new ItemAcquisitionHandlerFactory() {
            @Override
            public List<Task> getHandlers(Quester plugin, int quantity) {
                List<Task> handlers = new ArrayList<Task>();
                PickFromGroundTask pickEggTask = new PickFromGroundTask(plugin, "Egg", quantity, new WorldPoint(3231, 3299, 0), true);
                handlers.add(pickEggTask);
                return handlers;
            }
        });

        factories.put("Shears", new ItemAcquisitionHandlerFactory() {
            @Override
            public List<Task> getHandlers(Quester plugin, int quantity) {
                List<Task> handlers = new ArrayList<Task>();
                PickFromGroundTask pickEggTask = new PickFromGroundTask(plugin, "Shears", quantity, new WorldPoint(3191, 3272, 0), false);
                handlers.add(pickEggTask);
                return handlers;
            }
        });
    }
}