package emt.item;
import baubles.api.*;
import emt.EMT;
import net.minecraft.network.chat.Component;
import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.effect.*;
/** Keeps corruption in its own persistent key; never reloads or erases unrelated player ForgeData. */
public class ItemOneRing extends Item implements IBauble {
 public static final String CORRUPTION="emt.MindCorruption",PREVIOUS_INVULNERABLE="emt.RingPreviousInvulnerable";
 public ItemOneRing(){super(new Properties().stacksTo(1).rarity(Rarity.EPIC));}
 @Override public BaubleType getBaubleType(ItemStack s){return BaubleType.RING;}
 @Override public boolean canEquip(ItemStack s,LivingEntity p){return p instanceof Player;}
 @Override public boolean canUnequip(ItemStack s,LivingEntity p){return p instanceof Player player&&(player.level().isClientSide?s.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA,net.minecraft.world.item.component.CustomData.EMPTY).copyTag().getBoolean("emtRingUnlocked"):player.getPersistentData().getInt(CORRUPTION)>600);}
 // Creative/spectator immunity follows the current game mode and must not survive a later return to survival.
 @Override public void onEquipped(ItemStack s,LivingEntity e){if(e instanceof Player p&&!p.level().isClientSide){var tag=p.getPersistentData();if(!tag.contains(PREVIOUS_INVULNERABLE))tag.putBoolean(PREVIOUS_INVULNERABLE,p.getAbilities().invulnerable&&!p.isCreative()&&!p.isSpectator());tag.putInt(CORRUPTION,0);net.minecraft.world.item.component.CustomData.update(net.minecraft.core.component.DataComponents.CUSTOM_DATA,s,data->data.remove("emtRingUnlocked"));}}
 @Override public void onWornTick(ItemStack s,LivingEntity e){if(!(e instanceof Player p)||p.level().isClientSide)return;var tag=p.getPersistentData();int corruption=tag.getInt(CORRUPTION);boolean unlocked=corruption>=600;if(s.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA,net.minecraft.world.item.component.CustomData.EMPTY).copyTag().getBoolean("emtRingUnlocked")!=unlocked)net.minecraft.world.item.component.CustomData.update(net.minecraft.core.component.DataComponents.CUSTOM_DATA,s,data->data.putBoolean("emtRingUnlocked",unlocked));boolean previous=p.getAbilities().invulnerable;p.getAbilities().invulnerable=true;if(corruption==0)p.displayClientMessage(Component.translatable("message.emt.one_ring_warning"),false);else if(corruption>6000&&corruption<24000&&p.getRandom().nextInt(2000)==0)p.addEffect(new MobEffectInstance(MobEffects.BLINDNESS,500,2,false,false));else if(corruption>=6000&&corruption<24000&&p.getRandom().nextInt(2000)==0)p.addEffect(new MobEffectInstance(MobEffects.CONFUSION,500,2,false,false));else if(corruption>=24000&&corruption<72000&&p.getRandom().nextInt(2000)==0){p.getAbilities().invulnerable=false;p.hurt(p.damageSources().magic(),5);}else if(corruption>=72000&&corruption<120000&&p.getRandom().nextInt(4000)==0){p.getAbilities().invulnerable=false;p.setDeltaMovement(p.getDeltaMovement().add(0,2,0));p.hurtMarked=true;}else if(corruption>=120000&&p.getRandom().nextInt(10000)==0){p.getAbilities().invulnerable=false;p.addEffect(new MobEffectInstance(MobEffects.WITHER,5000,4,false,false));}else if(corruption>=Integer.MAX_VALUE-100)p.kill();tag.putInt(CORRUPTION,corruption==Integer.MAX_VALUE?corruption:corruption+1);if(previous!=p.getAbilities().invulnerable)p.onUpdateAbilities();p.setInvisible(true);}
 @Override public void onUnequipped(ItemStack s,LivingEntity e){if(!(e instanceof Player p)||p.level().isClientSide)return;var worn=baubles.api.BaublesApi.getBaubles(p);for(int i=0;i<worn.getContainerSize();i++)if(worn.getItem(i)!=s&&!worn.getItem(i).isEmpty()&&worn.getItem(i).getItem() instanceof ItemOneRing)return;var tag=p.getPersistentData();p.setInvisible(p.hasEffect(MobEffects.INVISIBILITY));p.getAbilities().invulnerable=p.isCreative()||p.isSpectator()||tag.getBoolean(PREVIOUS_INVULNERABLE);tag.remove(CORRUPTION);tag.remove(PREVIOUS_INVULNERABLE);p.addEffect(new MobEffectInstance(MobEffects.CONFUSION,500,2,false,false));p.onUpdateAbilities();}
 @Override public InteractionResultHolder<ItemStack> use(Level l,Player p,InteractionHand hand){var held=p.getItemInHand(hand);if(!l.isClientSide)dev.eugheist.tc4modern.BaublesInventory.equipFromHand(p,held);return InteractionResultHolder.sidedSuccess(held,l.isClientSide);}
}
