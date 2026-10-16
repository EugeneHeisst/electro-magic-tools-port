package emt.util;
import emt.EMT;
import emt.init.EMTItems;
import emt.mixin.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.*;
import net.minecraft.world.level.storage.loot.entries.*;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.LootTableLoadEvent;
import java.util.*;
/** Adds weighted entries to the existing dungeon roll, preserving all vanilla and other-mod entries. */
@EventBusSubscriber(modid=EMT.MODID)
public final class DungeonLoot {
 @SubscribeEvent static void load(LootTableLoadEvent e){if(e.getName().equals(ResourceLocation.withDefaultNamespace("chests/simple_dungeon")))add(e.getTable());}
 public static void add(LootTable table){var pools=((LootTableAccessor)(Object)table).emt$pools();if(pools.isEmpty())return;var pool=(LootPoolAccessor)(Object)pools.getFirst();var entries=new ArrayList<>(pool.emt$entries());if(!EMTConfig.DISABLE_ONE_RING.get()&&EMTConfig.ONE_RING_WEIGHT.get()>0)entries.add(entry(EMTItems.RINGS.get("one_ring").get(),EMTConfig.ONE_RING_WEIGHT.get()));if(!EMTConfig.DISABLE_MJOLNIR_LOOT.get()&&EMTConfig.MJOLNIR_WEIGHT.get()>0)entries.add(entry(EMTItems.HAMMERS.get("tainted_mjolnir").get(),EMTConfig.MJOLNIR_WEIGHT.get()));pool.emt$entries(List.copyOf(entries));}
 private static LootPoolEntryContainer entry(net.minecraft.world.item.Item item,int weight){return LootItem.lootTableItem(item).setWeight(weight).apply(SetItemCountFunction.setCount(UniformGenerator.between(0,1))).build();}
}
