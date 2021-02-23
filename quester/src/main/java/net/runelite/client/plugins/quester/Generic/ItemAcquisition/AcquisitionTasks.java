package net.runelite.client.plugins.quester.Generic.ItemAcquisition;

import net.runelite.api.NPC;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.plugins.paistisuite.api.*;
import net.runelite.client.plugins.paistisuite.api.WebWalker.wrappers.Keyboard;
import net.runelite.client.plugins.quester.Generic.CompositeTask;
import net.runelite.client.plugins.quester.Generic.DropAllItemsTask;
import net.runelite.client.plugins.quester.Generic.InteractWithNpcTask;
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
        factories.put("Bucket of milk", new ItemAcquisitionHandlerFactory() {
            @Override
            public List<Task> getHandlers(Quester plugin, int quantity) {
                List<Task> handlers = new ArrayList<Task>();
                CompositeTask milkCowTask;
                milkCowTask = new CompositeTask() {
                    @Override
                    public boolean condition(){
                        return PInventory.getCount("Bucket") >= quantity - PInventory.getCount("Bucket of milk");
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
                            () -> PInventory.getCount("Bucket of milk") >= targetQuantity){
                        @Override
                        public boolean isCompleted(){
                            return PInventory.getCount("Bucket of milk") >= targetQuantity;
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
                PickFromGroundTask pickEggTask = new PickFromGroundTask(plugin, "Shears", quantity, new WorldPoint(3190, 3272, 0), false);
                handlers.add(pickEggTask);
                return handlers;
            }
        });

        factories.put("Wool", new ItemAcquisitionHandlerFactory() {
            @Override
            public List<Task> getHandlers(Quester plugin, int quantity) {
                List<Task> handlers = new ArrayList<Task>();
                CompositeTask shearSheepTask;

                shearSheepTask = new CompositeTask(){
                    @Override
                    public boolean condition() {
                        return PInventory.getEmptySlots() >= quantity + 1 - PInventory.getCount("Shears") - PInventory.getCount("Wool");
                    }
                    public boolean isCompleted(){
                        if (PInventory.getCount("Wool") >= quantity) this.isCompleted = true;
                        return this.isCompleted;
                    }
                };
                shearSheepTask.addTask(
                        new AcquireItemTask(plugin, "Shears", 1)
                );
                for (int i = 1; i <= quantity; i++){
                    final int targetQuantity;
                    targetQuantity = i;
                    shearSheepTask.addTask(new InteractWithNpcTask(
                            plugin,
                            "Sheep",
                            new String[]{"Shear"},
                            new WorldPoint(3202, 3266, 0),
                            () -> PInventory.getCount("Wool") >= targetQuantity){
                        @Override
                        public NPC findTarget(){
                            return PObjects.findNPC(
                                    Filters.NPCs.nameEquals("Sheep")
                                            .and(Filters.NPCs.actionsContains("Shear"))
                                            .and(Filters.NPCs.actionsDontContain("Talk-to"))
                                            .and(tar -> tar.getWorldLocation().distanceTo(location()) < 10));
                        }
                        @Override
                        public boolean isCompleted(){
                            return PInventory.getCount("Wool") >= targetQuantity;
                        }
                        @Override
                        public boolean condition(){
                            return PInventory.getCount("Shears") >= 1;
                        }
                    });
                }
                shearSheepTask.addTask(
                        new DropAllItemsTask(plugin, "Shears")
                );

                handlers.add(shearSheepTask);
                return handlers;
            }
        });

        factories.put("Ball of wool", new ItemAcquisitionHandlerFactory() {
            @Override
            public List<Task> getHandlers(Quester plugin, int quantity) {
                List<Task> handlers = new ArrayList<Task>();
                CompositeTask spinWoolTask;

                spinWoolTask = new CompositeTask(){
                    @Override
                    public boolean condition(){
                        return PInventory.getEmptySlots() >= quantity - PInventory.getCount("Wool") - PInventory.getCount("Ball of Wool");
                    }
                    public boolean isCompleted(){
                        return PInventory.getCount("Ball of wool") >= quantity;
                    }
                };
                spinWoolTask.addTask(
                        new AcquireItemTask(plugin, "Wool", quantity - PInventory.getCount("Ball of Wool"))
                );

                spinWoolTask.addTask(
                        new InteractWithObjectTask(
                                plugin,
                                "Spinning wheel",
                                new String[]{"Spin"},
                                new WorldPoint(3209, 3214, 1),
                                () -> {
                                    Widget w = PWidgets.get(WidgetInfo.MULTI_SKILL_MENU);
                                    if (w == null) return false;
                                    PUtils.sleepNormal(400, 1000);
                                    Keyboard.pressSpacebar();
                                    PUtils.sleepNormal(400, 1000);
                                    PUtils.waitCondition(15000, () -> PInventory.getCount("Ball of wool") >= quantity);
                                    return PInventory.getCount("Ball of wool") >= quantity;
                                }
                        )
                );

                handlers.add(spinWoolTask);
                return handlers;
            }
        });

    }
}