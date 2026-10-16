package emt;
import emt.init.*;
import emt.util.EMTConfig;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.common.Mod;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
@Mod(EMT.MODID)
public class EMT {
 public static final String MODID="emt";
 public static final org.slf4j.Logger LOG=com.mojang.logging.LogUtils.getLogger();
 public static ResourceLocation id(String path){return ResourceLocation.fromNamespaceAndPath(MODID,path);}
 public EMT(IEventBus bus,ModContainer mod){mod.registerConfig(ModConfig.Type.COMMON,EMTConfig.SPEC);EMTBlocks.BLOCKS.register(bus);EMTItems.ITEMS.register(bus);emt.entity.EMTEntities.TYPES.register(bus);EMTBlocks.TILES.register(bus);EMTMenus.MENUS.register(bus);EMTArmorMaterials.MATERIALS.register(bus);EMTItems.TABS.register(bus);bus.addListener(this::setup);bus.addListener(EMTBlocks::capabilities);}
 private void setup(FMLCommonSetupEvent e){e.enqueueWork(()->{EMTRecipes.setup();dev.eugheist.tc4modern.api.TC4Addons.registerBlockBreakHook((stack,pos,player)->stack.getItem() instanceof emt.item.tool.EMTElectricTool tool&&tool.onBlockStartBreak(stack,pos,player));dev.eugheist.tc4modern.api.TC4Addons.registerBoreTool(new dev.eugheist.tc4modern.api.TC4Addons.BoreTool(){public boolean accepts(net.minecraft.world.item.ItemStack s){return s.getItem() instanceof ic2.core.item.tool.ItemElectricTool tool&&tool.canPerformAction(s,net.neoforged.neoforge.common.ItemAbilities.PICKAXE_DIG);}private double cost(net.minecraft.world.item.ItemStack s){return s.getItem() instanceof emt.item.tool.EMTElectricTool tool?tool.energyCost():((ic2.core.item.tool.ItemElectricTool)s.getItem()).operationEnergyCost;}public boolean canUse(net.minecraft.world.item.ItemStack s){return ic2.api.item.ElectricItem.manager.canUse(s,cost(s));}public boolean consume(net.minecraft.world.item.ItemStack s,net.minecraft.server.level.ServerLevel l,net.minecraft.world.entity.LivingEntity p){return ic2.api.item.ElectricItem.manager.use(s,cost(s),null);}});dev.eugheist.tc4modern.api.TC4Addons.registerWandHolder(tile->tile instanceof emt.tile.TileWandCharger w?w.wand.get():null);});}
}
