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
 public static final Map<String,DeferredItem<Item>> RINGS=new LinkedHashMap<>();
 public static final Map<String,DeferredItem<Item>> FOCI=new LinkedHashMap<>();
 public static final Map<String,DeferredItem<Item>> ARMOR=new LinkedHashMap<>();
 public static final Map<String,DeferredItem<Item>> TOOLS=new LinkedHashMap<>();
 public static final Map<String,DeferredItem<Item>> MATERIALS=new LinkedHashMap<>();
 static {RINGS.put("one_ring",ITEMS.register("one_ring",emt.item.ItemOneRing::new));RINGS.put("armor_charging_ring",ITEMS.register("armor_charging_ring",()->new emt.item.ItemChargingRing(true)));RINGS.put("inventory_charging_ring",ITEMS.register("inventory_charging_ring",()->new emt.item.ItemChargingRing(false)));for(var kind:emt.item.focus.EMTFocus.Kind.values()){String id="focus_"+kind.name().toLowerCase(Locale.ROOT);FOCI.put(id,ITEMS.register(id,()->new emt.item.focus.EMTFocus(kind)));}for(var kind:emt.item.armor.EMTArmor.Kind.values())ARMOR.put(kind.name().toLowerCase(Locale.ROOT),ITEMS.register(kind.name().toLowerCase(Locale.ROOT),()->new emt.item.armor.EMTArmor(kind)));ARMOR.put("feather_wings",ITEMS.register("feather_wings",()->new emt.item.armor.ItemDurableWings(false)));ARMOR.put("thaumium_wings",ITEMS.register("thaumium_wings",()->new emt.item.armor.ItemDurableWings(true)));ARMOR.put("infused_quantum_chestplate",ITEMS.register("infused_quantum_chestplate",emt.item.armor.ItemInfusedChestplate::new));for(var kind:emt.item.tool.EMTElectricTool.Kind.values())TOOLS.put(kind.name().toLowerCase(Locale.ROOT),ITEMS.register(kind.name().toLowerCase(Locale.ROOT),()->new emt.item.tool.EMTElectricTool(kind)));TOOLS.put("electric_hoe_growth",ITEMS.register("electric_hoe_growth",emt.item.tool.ItemGrowthHoe::new));TOOLS.put("electric_scribing_tools",ITEMS.register("electric_scribing_tools",emt.item.ItemElectricScribingTools::new));for(String name:MATERIAL_NAMES)MATERIALS.put(name,name.equals("lightning_summoner")?ITEMS.register(name,emt.item.ItemLightningSummoner::new):name.equals("tainted_feather")?ITEMS.register(name,emt.item.ItemTaintedFeather::new):ITEMS.registerSimpleItem(name));TABS.register("emt",()->CreativeModeTab.builder().title(Component.translatable("itemGroup.emt")).icon(()->new ItemStack(MATERIALS.get("thaumium_plate").get())).displayItems((params,out)->{ITEMS.getEntries().forEach(e->{out.accept(e.get());if(e.get() instanceof ic2.api.item.IElectricItem){var charged=new ItemStack(e.get());ic2.api.item.ElectricItem.manager.charge(charged,Double.MAX_VALUE,10,true,false);out.accept(charged);}});}).build());}
 public static Item material(String name){return MATERIALS.get(name).get();}
}
