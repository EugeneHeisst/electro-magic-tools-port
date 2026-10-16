package emt.init;
import emt.EMT;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.neoforged.neoforge.registries.*;
import java.util.*;
public final class EMTItems {
 public static final DeferredRegister.Items ITEMS=DeferredRegister.createItems(EMT.MODID);
 public static final DeferredRegister<CreativeModeTab> TABS=DeferredRegister.create(Registries.CREATIVE_MODE_TAB,EMT.MODID);
 public static final List<String> MATERIAL_NAMES=List.of("uranium_cluster","crushed_amber","purified_amber","crushed_cinnabar","purified_cinnabar","thaumium_plate","lightning_summoner","feather_mesh","glue","duct_tape","rubber_ball","cardboard","feather_glued_cardboard_wing","tainted_feather","thaumium_wing","uu_matter_drop");
 public static final Map<String,DeferredItem<Item>> MATERIALS=new LinkedHashMap<>();
 static {for(String name:MATERIAL_NAMES)MATERIALS.put(name,name.equals("lightning_summoner")?ITEMS.register(name,emt.item.ItemLightningSummoner::new):name.equals("tainted_feather")?ITEMS.register(name,emt.item.ItemTaintedFeather::new):ITEMS.registerSimpleItem(name));TABS.register("emt",()->CreativeModeTab.builder().title(Component.translatable("itemGroup.emt")).icon(()->new ItemStack(MATERIALS.get("thaumium_plate").get())).displayItems((params,out)->{ITEMS.getEntries().forEach(e->out.accept(e.get()));}).build());}
 public static Item material(String name){return MATERIALS.get(name).get();}
}
