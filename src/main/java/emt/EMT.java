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
 public EMT(IEventBus bus,ModContainer mod){mod.registerConfig(ModConfig.Type.COMMON,EMTConfig.SPEC);EMTBlocks.BLOCKS.register(bus);EMTItems.ITEMS.register(bus);EMTBlocks.TILES.register(bus);EMTMenus.MENUS.register(bus);EMTItems.TABS.register(bus);bus.addListener(this::setup);bus.addListener(EMTBlocks::capabilities);}
 private void setup(FMLCommonSetupEvent e){e.enqueueWork(()->{EMTRecipes.setup();dev.eugheist.tc4modern.api.TC4Addons.registerWandHolder(tile->tile instanceof emt.tile.TileWandCharger w?w.wand.get():null);});}
}
