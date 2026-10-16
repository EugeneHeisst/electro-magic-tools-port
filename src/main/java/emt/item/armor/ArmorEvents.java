package emt.item.armor;
import emt.EMT;
import ic2.api.item.ElectricItem;
import ic2.core.util.Keyboard;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.damagesource.DamageTypes;
import net.neoforged.bus.api.*;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
@EventBusSubscriber(modid=EMT.MODID)
public final class ArmorEvents {
 public static final ResourceLocation STEP=EMT.id("traveller_step");
 @SubscribeEvent static void tick(PlayerTickEvent.Pre e){var p=e.getEntity();var boots=p.getItemBySlot(EquipmentSlot.FEET);var step=p.getAttribute(Attributes.STEP_HEIGHT);boolean worn=boots.getItem() instanceof EMTArmor a&&a.boots();if(step!=null){boolean enabled=worn&&!p.isShiftKeyDown()&&!p.getAbilities().flying&&Keyboard.isForwardKeyDown(p);if(!enabled)step.removeModifier(STEP);else if(!step.hasModifier(STEP))step.addTransientModifier(new AttributeModifier(STEP,Math.max(0,1-step.getValue()),AttributeModifier.Operation.ADD_VALUE));}}
 @SubscribeEvent static void jump(LivingEvent.LivingJumpEvent e){if(e.getEntity() instanceof Player p&&p.getItemBySlot(EquipmentSlot.FEET).getItem() instanceof EMTArmor a&&a.boots())p.setDeltaMovement(p.getDeltaMovement().add(0,a.jumpBonus(),0));}
 @SubscribeEvent static void fall(LivingFallEvent e){var p=e.getEntity();if(p.level().isClientSide)return;var boots=p.getItemBySlot(EquipmentSlot.FEET);if(boots.getItem() instanceof EMTArmor a&&a.boots()&&ElectricItem.manager.use(boots,a.perDamage,null)){e.setCanceled(true);return;}var chest=p.getItemBySlot(EquipmentSlot.CHEST);if(chest.getItem() instanceof ItemInfusedChestplate a){double cost=a.getEnergyPerDamage()*Math.max((int)e.getDistance()-10,0);if(ElectricItem.manager.getCharge(chest)>=cost){ElectricItem.manager.discharge(chest,cost,4,true,false,false);e.setCanceled(true);}}}
 @SubscribeEvent(priority=EventPriority.HIGHEST) static void damage(LivingIncomingDamageEvent e){if(!e.getSource().is(DamageTypes.FALL))return;int kind=WingLogic.kind(e.getEntity().getItemBySlot(EquipmentSlot.CHEST));if(kind>=0)e.setAmount(e.getAmount()*WingLogic.FALL[kind]);}
}
