package emt.audit;
import emt.EMT;
import emt.init.EMTItems;
import emt.item.armor.*;
import ic2.api.item.ElectricItem;
import ic2.core.util.Util;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;
import net.minecraft.world.level.GameType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import java.util.*;
/** Real dedicated-server movement packets, with vanilla floating detection enabled. */
@EventBusSubscriber(modid=EMT.MODID)
public final class MpFlightAudit {
 private static final boolean ON="mpflight".equals(System.getProperty("emt.audit"));private static final String[] CASES={"feather_wings","thaumium_wings","nano_wings","quantum_wings","infused_jetpack","infused_thaumium","infused_nano","infused_quantum"};private static UUID id;private static int stage,t=-1,maxAir,failures;private static boolean kicked,control;private static double startY;
 private static void setup(ServerPlayer p){p.getInventory().clearContent();p.setGameMode(GameType.SURVIVAL);p.getAbilities().mayfly=false;p.getAbilities().flying=false;p.onUpdateAbilities();p.setHealth(20);p.getFoodData().setFoodLevel(20);p.teleportTo(p.serverLevel(),.5,100,.5,0,0);p.setDeltaMovement(net.minecraft.world.phys.Vec3.ZERO);p.fallDistance=0;Util.resetPlayerInAirTime(p);String name=CASES[stage];var chest=new ItemStack(EMTItems.ARMOR.get(stage<4?name:"infused_quantum_chestplate").get());ElectricItem.manager.charge(chest,Double.MAX_VALUE,10,true,false);p.setItemSlot(EquipmentSlot.CHEST,chest);if(stage>=4){var module=stage==4?new ItemStack(ic2.core.ref.Ic2Items.ELECTRIC_JETPACK.get()):new ItemStack(EMTItems.ARMOR.get(switch(stage){case 5->"thaumium_wings";case 6->"nano_wings";default->"quantum_wings";}).get());p.setItemInHand(InteractionHand.MAIN_HAND,module);ItemInfusedChestplate.attach(p,chest);if(stage==4){var tag=EMTArmor.tag(chest);tag.putBoolean("hoverMode",true);EMTArmor.save(chest,tag);}}startY=p.getY();t=0;maxAir=0;EMT.LOG.info("[emt-mp-audit] START {} allow-flight={}",name,p.server.isFlightAllowed());}
 @SubscribeEvent static void join(PlayerEvent.PlayerLoggedInEvent e){if(!ON||!(e.getEntity() instanceof ServerPlayer p))return;id=p.getUUID();stage=0;setup(p);}
 @SubscribeEvent static void leave(PlayerEvent.PlayerLoggedOutEvent e){if(ON&&e.getEntity().getUUID().equals(id)){kicked=true;EMT.LOG.info("[emt-mp-audit] disconnected stage={} tick={} control={}",stage,t,control);}}
 @SubscribeEvent static void tick(ServerTickEvent.Post e){if(!ON||t<0)return;t++;var p=e.getServer().getPlayerList().getPlayer(id);if(!control&&p!=null)maxAir=Math.max(maxAir,Util.playerInAirTime(p));if(!control&&(kicked||t==240)){boolean pass=p!=null&&!kicked&&p.getY()>startY+1&&!p.getAbilities().mayfly&&maxAir<80;EMT.LOG.info("[emt-mp-audit] {} {} real flight: climbed={} maxFloatingTicks={}",pass?"PASS":"FAIL",CASES[stage],p==null?0:p.getY()-startY,maxAir);if(!pass)failures++;if(p==null){EMT.LOG.info("[emt-mp-audit] DONE FAIL");t=-1;e.getServer().halt(false);return;}if(++stage<CASES.length){setup(p);return;}stage=4;setup(p);control=true;Util.auditDisableAirReset=true;EMT.LOG.info("[emt-mp-audit] START control: reset disabled");}if(control&&(kicked||t>=240)){if(!kicked)failures++;EMT.LOG.info("[emt-mp-audit] {} vanilla kick control",kicked?"PASS":"FAIL");EMT.LOG.info("[emt-mp-audit] DONE {} ({} failures)",failures==0?"PASS":"FAIL",failures);Util.auditDisableAirReset=false;t=-1;e.getServer().halt(false);}}
}
