package emt.item;
import baubles.api.*;
import emt.util.EMTConfig;
import ic2.api.item.ElectricItem;
import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
/** The two original rings produce a single conserved32EU budget per worn server tick. */
public class ItemChargingRing extends Item implements IBauble {
 public final boolean armor;
 public ItemChargingRing(boolean armor){super(new Properties().stacksTo(1).rarity(Rarity.RARE));this.armor=armor;}
 @Override public BaubleType getBaubleType(ItemStack s){return BaubleType.RING;}
 @Override public void onWornTick(ItemStack s,LivingEntity entity){if(!(entity instanceof Player p)||p.level().isClientSide)return;double budget=(armor?EMTConfig.ARMOR_RING_OUTPUT:EMTConfig.INVENTORY_RING_OUTPUT).get();if(armor){for(var slot:new EquipmentSlot[]{EquipmentSlot.FEET,EquipmentSlot.LEGS,EquipmentSlot.CHEST,EquipmentSlot.HEAD}){if(budget<=0)break;budget-=ElectricItem.manager.charge(p.getItemBySlot(slot),budget,4,false,false);}}else for(var item:p.getInventory().items){if(budget<=0)break;budget-=ElectricItem.manager.charge(item,budget,4,false,false);}}
 @Override public InteractionResultHolder<ItemStack> use(Level l,Player p,InteractionHand hand){var held=p.getItemInHand(hand);if(!l.isClientSide)dev.eugheist.tc4modern.BaublesInventory.equipFromHand(p,held);return InteractionResultHolder.sidedSuccess(held,l.isClientSide);}
}
