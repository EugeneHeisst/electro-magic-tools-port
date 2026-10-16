package emt.item.armor;
import emt.EMT;
import emt.init.*;
import ic2.api.item.ElectricItem;
import ic2.core.item.armor.ItemArmorElectric;
import ic2.core.ref.Ic2Items;
import ic2.core.util.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
/** Stores the attached item itself, preserving charge, name and components when detached. */
public class ItemInfusedChestplate extends ItemArmorElectric {
 public ItemInfusedChestplate(){super(EMTArmorMaterials.ELECTRIC,ArmorItem.Type.CHESTPLATE,new Properties().rarity(Rarity.RARE),2000000,12000,4);}
 @Override public int getEnchantmentValue(ItemStack s){return emt.util.EMTConfig.ENCHANTING.get()?4:0;}@Override public boolean isEnchantable(ItemStack s){return emt.util.EMTConfig.ENCHANTING.get();}@Override public boolean isBookEnchantable(ItemStack s,ItemStack book){return emt.util.EMTConfig.ENCHANTING.get();}
 @Override public double getDamageAbsorptionRatio(){return 1.1;}@Override public int getEnergyPerDamage(){return 20000;}
 public static int attachment(ItemStack s){return Math.max(0,Math.min(4,EMTArmor.tag(s).getInt("wing")));}
 @Override public ResourceLocation getArmorTexture(ItemStack s,Entity e,EquipmentSlot slot,ArmorMaterial.Layer layer,boolean inner){return EMT.id("textures/models/"+switch(attachment(s)){case 1->"quantum_jetpack";case 2->"quantum_wings_t";case 3->"quantum_wings_n";case 4->"quantum_wings_q";default->"quantum";}+".png");}
 public static boolean attach(Player p,ItemStack chest){var held=p.getMainHandItem();int type=held.is(Ic2Items.ELECTRIC_JETPACK.get())?1:held.is(EMTItems.ARMOR.get("thaumium_wings").get())?2:held.is(EMTItems.ARMOR.get("nano_wings").get())?3:held.is(EMTItems.ARMOR.get("quantum_wings").get())?4:0;var tag=EMTArmor.tag(chest);if(type==0||attachment(chest)!=0||tag.getInt("toggleTimer")>0)return false;tag.put("attachment",held.copyWithCount(1).save(p.registryAccess()));tag.putInt("wing",type);tag.putInt("toggleTimer",30);EMTArmor.save(chest,tag);held.shrink(1);return true;}
 public static boolean detach(Player p,ItemStack chest){var tag=EMTArmor.tag(chest);if(attachment(chest)==0||!p.getMainHandItem().isEmpty()||!p.isShiftKeyDown()||tag.getInt("toggleTimer")>0)return false;var item=ItemStack.parseOptional(p.registryAccess(),tag.getCompound("attachment"));tag.remove("attachment");tag.remove("wing");tag.putInt("toggleTimer",30);EMTArmor.save(chest,tag);if(!item.isEmpty())p.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,item);return true;}
 @Override public void onArmorTick(Level l,Player p,ItemStack s){var tag=EMTArmor.tag(s);int timer=tag.getInt("toggleTimer");if(timer>0){tag.putInt("toggleTimer",timer-1);EMTArmor.save(s,tag);}else if(!l.isClientSide&&p.isShiftKeyDown()&&attachment(s)==0)attach(p,s);
  int type=attachment(s);if(Keyboard.isJumpKeyDown(p)&&Keyboard.isModeSwitchKeyDown(p)&&timer==0){tag=EMTArmor.tag(s);tag.putBoolean("hoverMode",!tag.getBoolean("hoverMode"));tag.putInt("toggleTimer",30);EMTArmor.save(s,tag);}
  boolean hover=EMTArmor.tag(s).getBoolean("hoverMode");if(type==1&&(Keyboard.isJumpKeyDown(p)||hover&&p.getDeltaMovement().y<-.03))jetpack(p,s,hover);if(type>=2)WingLogic.tick(p,s,type-1,true);p.clearFire();
 }
 public boolean jetpack(Player p,ItemStack s,boolean hover){double cost=hover?10:8,charge=ElectricItem.manager.getCharge(s);if(charge<cost)return false;float power=(float)Math.min(1,charge/1500);if(Keyboard.isForwardKeyDown(p))p.moveRelative(.02f,new Vec3(0,0,.4f*power*(hover?.5f:3.5f)*2));double y=Math.min(p.getY(),p.level().getMaxBuildHeight());if(y>p.level().getMaxBuildHeight()-25)power*=(p.level().getMaxBuildHeight()-y)/25;var motion=p.getDeltaMovement();double my=Math.min(motion.y+power*.2,.6);if(hover){double limit=p.isShiftKeyDown()?-.1:Keyboard.isJumpKeyDown(p)?.1:-.025;if(my>limit)my=Math.max(motion.y,limit);}p.setDeltaMovement(motion.x,my,motion.z);if(!p.level().isClientSide)ElectricItem.manager.use(s,cost,null);p.fallDistance=0;Util.resetPlayerInAirTime(p);return true;}
}
