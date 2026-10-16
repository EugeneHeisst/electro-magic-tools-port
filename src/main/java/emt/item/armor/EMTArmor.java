package emt.item.armor;
import emt.EMT;
import emt.init.EMTArmorMaterials;
import emt.util.EMTConfig;
import emt.tile.TileSolar;
import ic2.api.item.*;
import ic2.core.item.armor.ItemArmorElectric;
import ic2.core.util.Keyboard;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import thaumcraft.api.*;
import thaumcraft.api.nodes.IRevealer;
import thaumcraft.api.aspects.Aspect;
import java.util.*;
/** Charged EMT equipment uses IC2 absorption and the real Thaumcraft revealing/vis interfaces. */
public class EMTArmor extends ItemArmorElectric implements IVisDiscountGear,IGoggles,IRevealer {
 public enum Kind { ELECTRIC_GOGGLES,NANO_GOGGLES,QUANTUM_GOGGLES,SOLAR_GOGGLES,ELECTRIC_BOOTS_TRAVELLER,NANO_BOOTS_TRAVELLER,QUANTUM_BOOTS_TRAVELLER,NANO_WINGS,QUANTUM_WINGS }
 public final Kind kind;public final int discount,perDamage;private final String texture;
 private static final double[] CAP={10000,100000,10000000,10000000,100000,1000000,10000000,100000,1000000};
 private static final double[] TRANSFER={10,100,10000,10000,100,1000,10000,1000,10000};
 private static final int[] TIERS={2,3,3,3,2,2,2,2,2},DISCOUNT={5,6,8,7,2,4,5,5,6},DAMAGE={100,1000,1000,10000,100,500,1000,100,1000};
 private static final String[] TEXTURES={"electricgoggles","thaumicnanohelmet","thaumicquantumhelmet","solarrevealinghelmet","electricboots","nanobootstravel","quantumbootstravel","nanowing","quantumwing"};
 public EMTArmor(Kind kind){super(EMTArmorMaterials.ELECTRIC,kind.ordinal()<4?ArmorItem.Type.HELMET:kind.ordinal()<7?ArmorItem.Type.BOOTS:ArmorItem.Type.CHESTPLATE,new Properties().rarity(Rarity.RARE),CAP[kind.ordinal()],TRANSFER[kind.ordinal()],TIERS[kind.ordinal()]);this.kind=kind;discount=DISCOUNT[kind.ordinal()];perDamage=DAMAGE[kind.ordinal()];texture=TEXTURES[kind.ordinal()];}
 public boolean goggles(){return kind.ordinal()<4;}public boolean boots(){return kind.ordinal()>=4&&kind.ordinal()<7;}public boolean wings(){return kind.ordinal()>=7;}
 @Override public double getDamageAbsorptionRatio(){return 1.1;}@Override public double getBaseAbsorptionRatio(){return .15;}@Override public int getEnergyPerDamage(){return perDamage;}
 @Override public int getVisDiscount(ItemStack s,Player p,Aspect a){return discount;}
 @Override public boolean showNodes(ItemStack s,LivingEntity p){return goggles();}@Override public boolean showIngamePopups(ItemStack s,LivingEntity p){return goggles();}
 @Override public ResourceLocation getArmorTexture(ItemStack s,Entity e,EquipmentSlot slot,ArmorMaterial.Layer layer,boolean inner){return EMT.id("textures/models/"+texture+".png");}
 @Override public int getEnchantmentValue(ItemStack s){return EMTConfig.ENCHANTING.get()?4:0;}@Override public boolean isEnchantable(ItemStack s){return EMTConfig.ENCHANTING.get();}@Override public boolean isBookEnchantable(ItemStack s,ItemStack book){return EMTConfig.ENCHANTING.get();}
 @Override public void appendHoverText(ItemStack s,TooltipContext c,List<Component> lines,TooltipFlag f){super.appendHoverText(s,c,lines,f);lines.add(Component.translatable("tooltip.EMT.visDiscount").append(": "+discount+"%"));}
 public static CompoundTag tag(ItemStack s){return s.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag();}public static void save(ItemStack s,CompoundTag tag){s.set(DataComponents.CUSTOM_DATA,CustomData.of(tag));}
 @Override public void onArmorTick(Level l,Player p,ItemStack s){
  if(wings()){WingLogic.tick(p,s,kind==Kind.NANO_WINGS?2:3,false);return;}
  if(boots()){var tag=tag(s);int timer=tag.getInt("boostTimer");if(Keyboard.isBoostKeyDown(p)&&Keyboard.isModeSwitchKeyDown(p)&&timer==0){tag.putBoolean("speedBoostActive",!tag.getBoolean("speedBoostActive"));timer=10;if(!l.isClientSide)p.displayClientMessage(Component.translatable(tag.getBoolean("speedBoostActive")?"message.emt.boost_on":"message.emt.boost_off"),false);}tag.putInt("boostTimer",Math.max(0,timer-1));save(s,tag);if(!p.getAbilities().flying&&Keyboard.isForwardKeyDown(p)&&p.onGround()&&tag.getBoolean("speedBoostActive"))p.moveRelative((float)speedBonus(),new net.minecraft.world.phys.Vec3(0,0,1));return;}
  if(l.isClientSide)return;
  if(kind==Kind.QUANTUM_GOGGLES||kind==Kind.SOLAR_GOGGLES){if(p.getAirSupply()<100&&ElectricItem.manager.use(s,1000,null))p.setAirSupply(Math.min(p.getMaxAirSupply(),p.getAirSupply()+200));for(var effect:new ArrayList<>(p.getActiveEffects())){int cost=effect.getEffect().equals(MobEffects.POISON)?10000:effect.getEffect().equals(MobEffects.WITHER)?15000:effect.getEffect().equals(MobEffects.CONFUSION)?5000:0;if(cost>0&&ElectricItem.manager.use(s,cost*(effect.getAmplifier()+1),null)){p.removeEffectsCuredBy(net.neoforged.neoforge.common.EffectCures.MILK);break;}}}
  // The reference's night vision costs zero EU; the disabling config remains available.
  if(kind!=Kind.ELECTRIC_GOGGLES&&!EMTConfig.NIGHTVISION_OFF.get())p.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION,300,0,false,false));
  if(kind==Kind.SOLAR_GOGGLES){var pos=p.blockPosition().above();boolean sky=TileSolar.seesSky(l,pos);double budget=!sky?0:l.isDay()&&!(l.getBiome(pos).value().hasPrecipitation()&&(l.isRaining()||l.isThundering()))?256:128;for(var slot:new EquipmentSlot[]{EquipmentSlot.FEET,EquipmentSlot.LEGS,EquipmentSlot.CHEST,EquipmentSlot.HEAD})budget-=ElectricItem.manager.charge(p.getItemBySlot(slot),budget,4,false,false);for(var item:p.getInventory().items){if(budget<=0)break;budget-=ElectricItem.manager.charge(item,budget,4,false,false);}if(budget>0)ElectricItem.manager.charge(p.getOffhandItem(),budget,4,false,false);}
 }
 public double speedBonus(){return kind==Kind.ELECTRIC_BOOTS_TRAVELLER?.055:kind==Kind.NANO_BOOTS_TRAVELLER?EMTConfig.NANO_SPEED.get():EMTConfig.QUANTUM_SPEED.get();}
 public double jumpBonus(){return kind==Kind.ELECTRIC_BOOTS_TRAVELLER?.3:kind==Kind.NANO_BOOTS_TRAVELLER?EMTConfig.NANO_JUMP.get():EMTConfig.QUANTUM_JUMP.get();}
 public static Float airSpeed(Player p){var s=p.getItemBySlot(EquipmentSlot.FEET);return s.getItem() instanceof EMTArmor armor&&armor.boots()&&!p.getAbilities().flying&&Keyboard.isForwardKeyDown(p)&&tag(s).getBoolean("speedBoostActive")?(float)armor.jumpBonus():null;}
}
