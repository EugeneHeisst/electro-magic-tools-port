package emt.item.focus;
import emt.init.*;
import emt.entity.*;
import emt.util.EMTConfig;
import dev.eugheist.tc4modern.*;
import ic2.api.item.ElectricItem;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
import thaumcraft.api.aspects.*;
/** Genuine native wand foci, including manipulator upgrades and the wand use/tick/release lifecycle. */
public final class EMTFocus extends FociContent.Focus {
 public enum Kind {EXPLOSION,CHRISTMAS,SHIELD,ENERGY_BALL,CHARGE,WAND_CHARGING}
 public final Kind kind;
 public EMTFocus(Kind kind){this.kind=kind;}
 @Override public AspectList getVisCost(ItemStack s){return switch(kind){case EXPLOSION->new AspectList().add(Aspect.FIRE,200).add(Aspect.ENTROPY,200);case CHRISTMAS->new AspectList().add(Aspect.ORDER,500).add(Aspect.AIR,500);case SHIELD->new AspectList().add(Aspect.ORDER,5).add(Aspect.WATER,5).add(Aspect.AIR,5);case ENERGY_BALL->new AspectList();default->{var list=new AspectList();for(var a:Aspect.getPrimalAspects())list.add(a,10);yield list;}};}
 @Override public String getSortingHelperBase(){return switch(kind){case EXPLOSION->"EXPLOSION";case CHRISTMAS->"CHRISTMAS";case SHIELD->"SHIELD";case ENERGY_BALL->"ENERGYBALL";case CHARGE->"WANDCHARGING";case WAND_CHARGING->"ELECTRICCHARGING";};}
 @Override public int getFocusColor(ItemStack s){return switch(kind){case EXPLOSION->9109504;case CHRISTMAS->65535;case SHIELD->2003199;case ENERGY_BALL->255;case CHARGE->16776960;case WAND_CHARGING->268432464;};}
 @Override public int[] getPossibleUpgradesByRank(ItemStack s,int rank){return kind==Kind.EXPLOSION||kind==Kind.CHARGE||kind==Kind.WAND_CHARGING?new int[]{0,1}:null;}
 @Override public boolean isVisCostPerTick(ItemStack s){return kind==Kind.WAND_CHARGING;}
 private static void start(Player p,ItemStack wand){FociContent.startUsing(p,p.getOffhandItem()==wand?InteractionHand.OFF_HAND:InteractionHand.MAIN_HAND);}
 private boolean pay(Player p,ItemStack wand){return p.isCreative()||FociContent.consumeAllVis(wand,p,getVisCost(wand),!p.level().isClientSide);}
 @Override public boolean onFocusRightClick(ItemStack wand,Level l,Player p){
  if(kind==Kind.WAND_CHARGING){start(p,wand);return true;}
  if(kind==Kind.SHIELD){if(!pay(p,wand))return true;start(p,wand);if(!l.isClientSide)l.addFreshEntity(new EntityShield(l,p));return true;}
  if(kind==Kind.CHRISTMAS){var hit=p.pick(32,1,false);if(!(hit instanceof BlockHitResult b)||hit.getType()!=HitResult.Type.BLOCK)return true;if(pay(p,wand)&&!l.isClientSide){var snow=new SnowGolem(EntityType.SNOW_GOLEM,l);snow.setPos(b.getBlockPos().getX()+.5,b.getBlockPos().getY()+1,b.getBlockPos().getZ()+.5);l.addFreshEntity(snow);}p.swing(InteractionHand.MAIN_HAND);return true;}
  if(l.isClientSide)return true;
  if(kind==Kind.ENERGY_BALL){var armor=p.getItemBySlot(EquipmentSlot.CHEST);if(ElectricItem.manager.canUse(armor,5120)&&ElectricItem.manager.use(armor,5120,null))l.addFreshEntity(new EntityEnergyBall(l,p));return true;}
  if(kind==Kind.EXPLOSION){if(pay(p,wand)){var laser=new EntityLaser(l,p);laser.strength=1+getUpgradeLevel(FociContent.getFocusItem(wand),0)*.5f;l.addFreshEntity(laser);}return true;}
  double budget=EMTConfig.FOCUS_PRODUCTION.get()*Math.pow(1.1,getUpgradeLevel(FociContent.getFocusItem(wand),0));double wanted=0;for(var slot:new EquipmentSlot[]{EquipmentSlot.FEET,EquipmentSlot.LEGS,EquipmentSlot.CHEST,EquipmentSlot.HEAD})wanted+=ElectricItem.manager.charge(p.getItemBySlot(slot),budget,4,false,true);for(var s:p.getInventory().items)wanted+=ElectricItem.manager.charge(s,budget,4,false,true);if(wanted<=0||!pay(p,wand))return true;
  for(var slot:new EquipmentSlot[]{EquipmentSlot.FEET,EquipmentSlot.LEGS,EquipmentSlot.CHEST,EquipmentSlot.HEAD})budget-=ElectricItem.manager.charge(p.getItemBySlot(slot),budget,4,false,false);for(var s:p.getInventory().items){if(budget<=0)break;budget-=ElectricItem.manager.charge(s,budget,4,false,false);}return true;
 }
 @Override public void onUsingFocusTick(ItemStack wand,Player p,int count){if(p.level().isClientSide)return;if(kind==Kind.SHIELD){p.removeAllEffects();if(!pay(p,wand))p.stopUsingItem();return;}if(kind==Kind.WAND_CHARGING){var source=p.getItemBySlot(EquipmentSlot.LEGS);double amount=Math.pow(1.1,getUpgradeLevel(FociContent.getFocusItem(wand),0)),cost=EMTConfig.FOCUS_WAND_COST.get();boolean room=Aspect.getPrimalAspects().stream().anyMatch(a->WandData.get(wand,a)<WandData.capacity(wand));if(room&&ElectricItem.manager.canUse(source,cost)&&ElectricItem.manager.use(source,cost,null)){var tag=NoteData.tag(wand);for(var a:Aspect.getPrimalAspects())thaumcraft.common.lib.vis.VisStorage.storeVis(tag,a,Math.min(WandData.capacity(wand),WandData.get(wand,a)+(int)(amount*100)));wand.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA,net.minecraft.world.item.component.CustomData.of(tag));}}}
}
