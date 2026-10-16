package emt.item;
import dev.eugheist.tc4modern.TC4Modern;
import net.minecraft.world.entity.*;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
public class ItemTaintedFeather extends Item {
 public ItemTaintedFeather(){super(new Item.Properties());}
 public void inventoryTick(ItemStack s,Level l,Entity e,int slot,boolean selected){if(!l.isClientSide&&e instanceof LivingEntity living&&!living.isInvertedHealAndHarm()&&!living.hasEffect(TC4Modern.FLUX_TAINT)&&l.random.nextInt(4321)<=s.getCount()){living.addEffect(new MobEffectInstance(TC4Modern.FLUX_TAINT,120,0,false,true));s.shrink(1);}}
}
