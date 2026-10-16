package emt.audit;
import emt.EMT;
import emt.init.*;
import emt.item.ItemOneRing;
import dev.eugheist.tc4modern.*;
import baubles.api.BaublesApi;
import ic2.api.item.ElectricItem;
import net.minecraft.client.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.*;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.*;
import thaumcraft.api.aspects.Aspect;
import java.nio.file.*;
public final class RingsClientAudit {
 private static void shot(Minecraft mc,String name){try(var image=Screenshot.takeScreenshot(mc.getMainRenderTarget())){image.writeToFile(Path.of("audit",name+".png"));}catch(Exception ex){throw new RuntimeException(ex);}}
 private static void open(Minecraft mc){mc.getSingleplayerServer().execute(()->{var p=mc.getSingleplayerServer().getPlayerList().getPlayers().getFirst();p.openMenu(new SimpleMenuProvider((id,inv,who)->new BaublesMenu(id,inv),Component.empty()));});}
 private static void click(Minecraft mc,int slot){mc.gameMode.handleInventoryMouseClick(mc.player.containerMenu.containerId,slot,0,ClickType.PICKUP,mc.player);}
 public static void tick(Minecraft mc,int t){var server=mc.getSingleplayerServer();
  if(t==1515){mc.player.closeContainer();mc.options.setCameraType(CameraType.FIRST_PERSON);server.execute(()->{var p=server.getPlayerList().getPlayers().getFirst();var worn=BaublesApi.getBaubles(p);for(int i=0;i<worn.getContainerSize();i++)worn.setItem(i,ItemStack.EMPTY);p.getInventory().clearContent();for(var slot:EquipmentSlot.values())p.setItemSlot(slot,ItemStack.EMPTY);p.removeAllEffects();p.getAbilities().invulnerable=false;p.getAbilities().mayfly=true;p.getAbilities().flying=true;p.onUpdateAbilities();var chest=EMTRecipes.stack("emt:infused_quantum_chestplate");p.setItemSlot(EquipmentSlot.CHEST,chest);p.getInventory().setItem(1,EMTRecipes.stack("emt:inventory_charging_ring"));p.setItemInHand(InteractionHand.MAIN_HAND,EMTRecipes.stack("emt:armor_charging_ring"));p.getInventory().setItem(2,EMTRecipes.stack("emt:thaumium_drill"));for(var r:thaumcraft.api.research.ResearchCategories.getResearchList("EMT").research.values())ResearchProgress.add(p.getPersistentData(),r.key);net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(p,new ScanPayload("",p.getPersistentData().getCompound(ResearchKnowledge.KEY)));});}
  if(t==1535){for(var entry:EMTItems.RINGS.entrySet()){var stack=new ItemStack(entry.getValue().get());var model=mc.getItemRenderer().getModel(stack,mc.level,mc.player,0);if(model.getParticleIcon(net.neoforged.neoforge.client.model.data.ModelData.EMPTY).contents().name().getPath().equals("missingno"))throw new IllegalStateException("Missing ring icon "+entry.getKey());}EMT.LOG.info("[emt-client-audit] PASS all3 ring icons");mc.gameMode.useItem(mc.player,InteractionHand.MAIN_HAND);}
  if(t==1550)server.execute(()->{var p=server.getPlayerList().getPlayers().getFirst();p.setItemInHand(InteractionHand.MAIN_HAND,p.getInventory().removeItem(1,1));});if(t==1560)mc.gameMode.useItem(mc.player,InteractionHand.MAIN_HAND);if(t==1580)open(mc);
  if(t==1600){shot(mc,"baubles_charging_rings");server.execute(()->{var p=server.getPlayerList().getPlayers().getFirst();var worn=BaublesApi.getBaubles(p);if(!worn.getItem(1).is(EMTItems.RINGS.get("armor_charging_ring").get())||!worn.getItem(2).is(EMTItems.RINGS.get("inventory_charging_ring").get())||ElectricItem.manager.getCharge(p.getItemBySlot(EquipmentSlot.CHEST))<=0||ElectricItem.manager.getCharge(p.getInventory().getItem(2))<=0)throw new IllegalStateException("Native ring equip packets or automatic EU tick failed");EMT.LOG.info("[emt-client-audit] PASS native ring packets, Baubles menu and real tick EU");});}
  if(t==1610)click(mc,10);if(t==1620)click(mc,40);
  if(t==1640){mc.player.closeContainer();server.execute(()->{var p=server.getPlayerList().getPlayers().getFirst();var worn=BaublesApi.getBaubles(p);if(!worn.getItem(1).isEmpty())throw new IllegalStateException("Native menu failed to detach charging ring");for(int i=0;i<worn.getContainerSize();i++)worn.setItem(i,ItemStack.EMPTY);p.getInventory().clearContent();p.getAbilities().invulnerable=false;p.onUpdateAbilities();p.setItemInHand(InteractionHand.MAIN_HAND,EMTRecipes.stack("emt:one_ring"));});}
  if(t==1650)mc.gameMode.useItem(mc.player,InteractionHand.MAIN_HAND);if(t==1665)open(mc);
  if(t==1680){if(!mc.player.isInvisible()||!mc.player.getAbilities().invulnerable)throw new IllegalStateException("One Ring invisibility/abilities not synchronized");shot(mc,"baubles_one_ring_locked");EMT.LOG.info("[emt-client-audit] PASS One Ring invisibility and invulnerability synchronize");}
  if(t==1690)click(mc,10);if(t==1700)server.execute(()->{var p=server.getPlayerList().getPlayers().getFirst();if(!BaublesApi.getBaubles(p).getItem(1).is(EMTItems.RINGS.get("one_ring").get())||!p.containerMenu.getCarried().isEmpty())throw new IllegalStateException("One Ring removed before600ticks");EMT.LOG.info("[emt-client-audit] PASS native GUI rejects locked One Ring pickup packet");});
  if(t==1720)ClientAudit.book(mc,"Inventory Charging Ring",1);if(t==1735)shot(mc,"book_inventory_charging_ring");if(t==1745)ClientAudit.book(mc,"Armor Charging Ring",1);if(t==1760)shot(mc,"book_armor_charging_ring");if(t==1770)mc.player.closeContainer();if(t==2240)open(mc);
  if(t==2260){var slot=mc.player.containerMenu.getSlot(10);if(!slot.mayPickup(mc.player))throw new IllegalStateException("One Ring unlock component did not reach client");click(mc,10);}if(t==2275){shot(mc,"baubles_one_ring_removed");server.execute(()->{var p=server.getPlayerList().getPlayers().getFirst();if(!BaublesApi.getBaubles(p).getItem(1).isEmpty()||!p.containerMenu.getCarried().is(EMTItems.RINGS.get("one_ring").get())||p.isInvisible()||p.getAbilities().invulnerable||p.getPersistentData().contains(ItemOneRing.CORRUPTION))throw new IllegalStateException("Unlocked native pickup did not restore player");EMT.LOG.info("[emt-client-audit] PASS native GUI unlocks601ticks, removes ring and restores player");});}
 }
}
