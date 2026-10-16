package emt.audit;
import emt.EMT;
import emt.init.*;
import emt.tile.*;
import dev.eugheist.tc4modern.*;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.nodes.*;
import net.minecraft.client.*;
import net.minecraft.core.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.*;
import java.nio.file.*;
public final class NodeCloudClientAudit {
 private static final BlockPos NODE=new BlockPos(0,152,110),CLOUD=new BlockPos(4,152,110);
 private static void shot(Minecraft mc,String name){try(var img=Screenshot.takeScreenshot(mc.getMainRenderTarget())){img.writeToFile(Path.of("audit",name+".png"));}catch(Exception e){throw new RuntimeException(e);}}
 private static void click(Minecraft mc,BlockPos pos,Direction face){mc.gameMode.useItemOn(mc.player,InteractionHand.MAIN_HAND,new BlockHitResult(Vec3.atCenterOf(pos).add(face.getStepX()*.5,face.getStepY()*.5,face.getStepZ()*.5),face,pos,false));}
 public static void tick(Minecraft mc,int t){var server=mc.getSingleplayerServer();
  if(t==2615){mc.player.closeContainer();mc.options.setCameraType(CameraType.FIRST_PERSON);server.execute(()->{var l=server.overworld();var p=server.getPlayerList().getPlayers().getFirst();p.getInventory().selected=0;p.connection.send(new net.minecraft.network.protocol.game.ClientboundSetCarriedItemPacket(0));p.getInventory().clearContent();p.removeAllEffects();for(var slot:EquipmentSlot.values())p.setItemSlot(slot,ItemStack.EMPTY);p.setItemSlot(EquipmentSlot.HEAD,EMTRecipes.stack("emt:electric_goggles"));p.getAbilities().invulnerable=true;p.getAbilities().mayfly=true;p.getAbilities().flying=true;p.onUpdateAbilities();p.teleportTo(l,.5,151,107,0,0);for(var pos:BlockPos.betweenClosed(new BlockPos(-8,150,102),new BlockPos(12,150,118)))l.setBlock(pos,Blocks.STONE.defaultBlockState(),3);l.setBlock(NODE,Blocks.AIR.defaultBlockState(),3);l.setBlock(NODE.below(),Blocks.STONE.defaultBlockState(),3);l.setBlock(CLOUD,TC4Modern.NITOR_BLOCK.get().defaultBlockState(),3);p.setItemInHand(InteractionHand.MAIN_HAND,EMTRecipes.stack("emt:portable_node"));var battery=EMTRecipes.stack("ic2:advanced_re_battery");ic2.api.item.ElectricItem.manager.charge(battery,512,2,true,false);p.getInventory().setItem(1,battery);p.getInventory().setItem(2,EMTRecipes.stack("tc4modern:wand"));for(var r:thaumcraft.api.research.ResearchCategories.getResearchList("EMT").research.values())ResearchProgress.add(p.getPersistentData(),r.key);net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(p,new ScanPayload("",p.getPersistentData().getCompound(ResearchKnowledge.KEY)));});}
  if(t==2635){for(String id:new String[]{"emt:portable_node","emt:electric_cloud"}){var model=mc.getItemRenderer().getModel(EMTRecipes.stack(id),mc.level,mc.player,0);if(model.getParticleIcon(net.neoforged.neoforge.client.model.data.ModelData.EMPTY).contents().name().getPath().equals("missingno"))throw new IllegalStateException("Missing special block icon "+id);}EMT.LOG.info("[emt-client-audit] PASS node/cloud icons");}
  if(t==2640)click(mc,NODE.below(),Direction.UP);if(t==2660){if(!(mc.level.getBlockEntity(NODE) instanceof TilePortableNode n)||n.nodeType!=NodeType.UNSTABLE||n.reservoir.modifier!=NodeModifier.FADING||n.reservoir.aspectsBase.visSize()==0)throw new IllegalStateException("Native portable node placement/type/aspects did not synchronize");shot(mc,"portable_node_emt_goggles");EMT.LOG.info("[emt-client-audit] PASS real placement packet synchronizes native portable node/aspects");}
  if(t==2670){mc.player.getInventory().selected=2;mc.options.keyUse.setDown(true);mc.gameMode.useItem(mc.player,InteractionHand.MAIN_HAND);}if(t==2680)shot(mc,"portable_node_wand_drain");if(t==2690){mc.options.keyUse.setDown(false);mc.gameMode.releaseUsingItem(mc.player);server.execute(()->{var p=server.getPlayerList().getPlayers().getFirst();if(Aspect.getPrimalAspects().stream().noneMatch(a->WandData.get(p.getMainHandItem(),a)>0))throw new IllegalStateException("Native wand packet failed to drain portable node");EMT.LOG.info("[emt-client-audit] PASS native wand packet drains portable node vis");});}
  if(t==2700)server.execute(()->server.getPlayerList().getPlayers().getFirst().teleportTo(server.overworld(),4.5,151,107,0,0));if(t==2705)mc.player.getInventory().selected=1;if(t==2710)click(mc,CLOUD,Direction.NORTH);
  if(t==2735){if(!(mc.level.getBlockEntity(CLOUD) instanceof TileElectricCloud))throw new IllegalStateException("Electric cloud state not received");server.execute(()->{var p=server.getPlayerList().getPlayers().getFirst();if(!server.overworld().getBlockState(CLOUD).is(EMTBlocks.ELECTRIC_CLOUD.get())||ic2.api.item.ElectricItem.manager.getCharge(p.getMainHandItem())!=256)throw new IllegalStateException("Native nitor interaction failed or charged wrong EU");EMT.LOG.info("[emt-client-audit] PASS native nitor→cloud interaction packet/256EU");});}
  if(t==2750)server.execute(()->server.getPlayerList().getPlayers().getFirst().teleportTo(server.overworld(),2.5,151,105,0,0));if(t==2780){var tile=(TileElectricCloud)mc.level.getBlockEntity(CLOUD);if(tile.wisps.isEmpty())throw new IllegalStateException("Cloud client effects not ticking");shot(mc,"electric_cloud_and_portable_node");EMT.LOG.info("[emt-client-audit] PASS native wisp shader/cloud particles tick");}
  if(t==2790)ClientAudit.book(mc,"Portable Node",1);if(t==2815)shot(mc,"book_portable_node");if(t==2825)ClientAudit.book(mc,"Electric Cloud",0);if(t==2840)shot(mc,"book_electric_cloud_text");if(t==2850)ClientAudit.book(mc,"Electric Cloud",1);if(t==2865)shot(mc,"book_electric_cloud_image");
  if(t==2875){mc.player.closeContainer();server.execute(()->server.getPlayerList().getPlayers().getFirst().teleportTo(server.overworld(),.5,151,107,0,0));mc.player.getInventory().selected=0;}
  if(t==2880)mc.options.keyShift.setDown(true);if(t==2890)click(mc,NODE,Direction.NORTH);if(t==2905){mc.options.keyShift.setDown(false);server.execute(()->{if(!server.overworld().isEmptyBlock(NODE))throw new IllegalStateException("Native empty-hand sneak packet did not dispose portable node");EMT.LOG.info("[emt-client-audit] PASS native shift-empty-hand packet disposes portable node");});}
 }
}
