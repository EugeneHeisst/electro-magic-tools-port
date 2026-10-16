package emt.entity;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvents;
/** The original snow-golem guardian: forty health, hostile-mob targeting, bow arrows and a death explosion. */
public class EntityArcher extends SnowGolem {
 private boolean exploded;
 public EntityArcher(EntityType<? extends EntityArcher> type,Level l){super(type,l);setItemSlot(EquipmentSlot.MAINHAND,new ItemStack(Items.BOW));setDropChance(EquipmentSlot.MAINHAND,0);}
 public static AttributeSupplier.Builder attributes(){return SnowGolem.createAttributes().add(Attributes.MAX_HEALTH,40).add(Attributes.MOVEMENT_SPEED,.3);}
 @Override protected void registerGoals(){super.registerGoals();goalSelector.addGoal(1,new FloatGoal(this));}
 @Override public void performRangedAttack(LivingEntity target,float strength){var arrow=new Arrow(level(),this,new ItemStack(Items.ARROW),getMainHandItem());double dx=target.getX()-getX(),dz=target.getZ()-getZ(),dy=target.getY()+target.getEyeHeight()-1.1-arrow.getY();arrow.shoot(dx,dy+Math.sqrt(dx*dx+dz*dz)*.2,dz,1.6f,14-level().getDifficulty().getId()*4);arrow.setBaseDamage(strength*2+random.nextGaussian()*.25+level().getDifficulty().getId()*.11);if(level() instanceof net.minecraft.server.level.ServerLevel server)net.minecraft.world.item.enchantment.EnchantmentHelper.onProjectileSpawned(server,getMainHandItem(),arrow,entity->{});playSound(SoundEvents.ARROW_SHOOT,1,1/(random.nextFloat()*.4f+.8f));level().addFreshEntity(arrow);}
 @Override public void die(DamageSource source){if(exploded)return;exploded=true;super.die(source);if(!level().isClientSide){level().explode(this,getX(),getY(),getZ(),1,true,Level.ExplosionInteraction.MOB);discard();}}
}
