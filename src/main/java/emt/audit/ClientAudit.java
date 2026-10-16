package emt.audit;
import emt.*;
import emt.init.*;
import emt.tile.*;
import dev.eugheist.tc4modern.*;
import dev.eugheist.tc4modern.api.AspectContainers;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.research.*;
import net.minecraft.client.*;
import net.minecraft.core.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import java.nio.file.*;
import java.util.*;
@EventBusSubscriber(modid=EMT.MODID,value=Dist.CLIENT)
public class ClientAudit {
 private static final boolean ON="client".equals(System.getProperty("emt.audit"));private static int tick;private static final BlockPos BASE=new BlockPos(0,150,0);private static final Map<String,BlockPos> positions=new LinkedHashMap<>();
 private static void shot(Minecraft mc,String name){try{Files.createDirectories(Path.of("audit"));try(var image=Screenshot.takeScreenshot(mc.getMainRenderTarget())){image.writeToFile(Path.of("audit",name+".png"));}}catch(Exception e){throw new RuntimeException(e);}}
 private static void open(Minecraft mc,String name){var server=mc.getSingleplayerServer();server.execute(()->{var p=server.getPlayerList().getPlayers().getFirst();var pos=positions.get(name);p.teleportTo(server.overworld(),pos.getX()+.5,pos.getY()+1,pos.getZ()-3,0,20);var gui=(ic2.core.IHasGui)server.overworld().getBlockEntity(pos);p.openMenu(new SimpleMenuProvider((id,inv,actor)->gui.getGuiContainer(id,inv),Component.empty()),buf->buf.writeBlockPos(pos));});}
 private static void book(Minecraft mc,String research,int page){try{var screen=new ThaumonomiconScreen(new ThaumonomiconMenu(0,mc.player.getInventory()),mc.player.getInventory(),Component.empty());mc.setScreen(screen);var method=ThaumonomiconScreen.class.getDeclaredMethod("auditOpen",ResearchItem.class,int.class);method.setAccessible(true);method.invoke(screen,ResearchCategories.getResearch(research),page);}catch(Exception e){throw new RuntimeException(e);}}
 @SubscribeEvent static void tick(ClientTickEvent.Post e){if(!ON)return;var mc=Minecraft.getInstance();if(mc.player==null||mc.getSingleplayerServer()==null)return;tick++;var server=mc.getSingleplayerServer();if(tick%100==0)EMT.LOG.info("[emt-client-audit] tick {}",tick);
  if(tick==30)server.execute(()->{var l=server.overworld();var p=server.getPlayerList().getPlayers().getFirst();p.setGameMode(net.minecraft.world.level.GameType.CREATIVE);p.getAbilities().flying=true;p.onUpdateAbilities();l.setDayTime(6000);l.setWeatherParameters(6000,0,false,false);for(var pos:BlockPos.betweenClosed(BASE.offset(-4,-1,-4),BASE.offset(32,-1,24)))l.setBlock(pos,Blocks.STONE.defaultBlockState(),3);for(var pos:BlockPos.betweenClosed(BASE.offset(-4,0,-4),BASE.offset(32,6,24)))l.setBlock(pos,Blocks.AIR.defaultBlockState(),3);l.getEntitiesOfClass(net.minecraft.world.entity.item.ItemEntity.class,new net.minecraft.world.phys.AABB(net.minecraft.world.phys.Vec3.atLowerCornerOf(BASE.offset(-4,-1,-4)),net.minecraft.world.phys.Vec3.atLowerCornerOf(BASE.offset(33,8,25)))).forEach(net.minecraft.world.entity.Entity::discard);
   int n=0;for(var entry:EMTBlocks.MACHINES.entrySet()){var pos=BASE.offset(n%7*4,0,n/7*5);l.setBlock(pos,entry.getValue().get().defaultBlockState(),3);positions.put(entry.getKey(),pos);var tile=l.getBlockEntity(pos);if(tile instanceof TileEssentiaGenerator g){l.setBlock(pos.above(2),EssentiaDevices.JAR.get().defaultBlockState(),3);AspectContainers.find(l,pos.above(2)).add(g.aspect,64);}if(tile instanceof TileWandCharger w){w.wand.put(EMTRecipes.stack("tc4modern:wand"));w.energy=100000;}if(tile instanceof TileEtherealProcessor proc)proc.input.put(new ItemStack(Items.RAW_IRON,8));n++;}
   p.teleportTo(l,12,154,-10,0,16);p.getInventory().clearContent();int i=0;for(var item:EMTItems.MATERIALS.values())p.getInventory().setItem(i++,new ItemStack(item.get()));for(var r:ResearchCategories.getResearchList("EMT").research.values())ResearchProgress.add(p.getPersistentData(),r.key);net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(p,new ScanPayload("",p.getPersistentData().getCompound(ResearchKnowledge.KEY)));
  });
  if(tick==110){for(var entry:EMTBlocks.MACHINES.entrySet()){var pos=positions.get(entry.getKey());var state=mc.level.getBlockState(pos);for(var side:new Direction[]{null,Direction.UP,Direction.DOWN,Direction.NORTH,Direction.EAST,Direction.SOUTH,Direction.WEST})for(var quad:mc.getBlockRenderer().getBlockModel(state).getQuads(state,side,net.minecraft.util.RandomSource.create()))if(quad.getSprite().contents().name().getPath().equals("missingno"))throw new IllegalStateException("Missing EMT texture "+entry.getKey());}EMT.LOG.info("[emt-client-audit] PASS all28 block models have textures");shot(mc,"panels");}
  if(tick==120)server.execute(()->server.getPlayerList().getPlayers().getFirst().teleportTo(server.overworld(),12,154,8,0,25));if(tick==145)shot(mc,"generators");
  if(tick==155)open(mc,"industrial_wand_charger");if(tick==175)shot(mc,"gui_wand_charger");if(tick==180)mc.player.closeContainer();if(tick==190)open(mc,"ethereal_processor");if(tick==210)shot(mc,"gui_ethereal_processor");if(tick==215)mc.player.closeContainer();
  if(tick==225){var screen=new ThaumonomiconScreen(new ThaumonomiconMenu(0,mc.player.getInventory()),mc.player.getInventory(),Component.empty());mc.setScreen(screen);screen.auditBrowser("EMT",-4,-5);}if(tick==240)shot(mc,"research_solars");
  if(tick==250)book(mc,"Compressed Solars",0);if(tick==265)shot(mc,"book_compressed_text");if(tick==275)book(mc,"Compressed Solars",2);if(tick==290)shot(mc,"book_compressed_recipes");if(tick==300)book(mc,"Potentia Generator",0);if(tick==315)shot(mc,"book_potentia");if(tick==325)book(mc,"Water Infused Solar Panels",0);if(tick==340)shot(mc,"book_water_solar");
  if(tick==350&&net.neoforged.fml.ModList.get().isLoaded("jei")){if(!ic2.core.jei.IC2JeiPlugin.showForAudit("macerator"))throw new IllegalStateException("Missing IC2 JEI runtime");EMT.LOG.info("[emt-client-audit] PASS JEI combined runtime");}if(tick==365&&net.neoforged.fml.ModList.get().isLoaded("jei"))shot(mc,"jei_machine_recipes");if(tick==375)EMT.LOG.info("[emt-client-audit] DONE");if(tick>=380)mc.stop();
 }
}
