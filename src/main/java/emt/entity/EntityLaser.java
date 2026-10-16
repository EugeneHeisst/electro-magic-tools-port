package emt.entity;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
import net.minecraft.nbt.CompoundTag;
public class EntityLaser extends ThrowableProjectile {
 public float strength=1;
 public EntityLaser(EntityType<? extends EntityLaser> type,Level l){super(type,l);}
 public EntityLaser(Level l,LivingEntity owner){this(EMTEntities.LASER.get(),l);setOwner(owner);setPos(owner.getEyePosition().add(owner.getLookAngle().scale(.2)));shootFromRotation(owner,owner.getXRot(),owner.getYRot(),0,1,0);}
 @Override protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder b){}
 @Override protected double getDefaultGravity(){return 0;}
 @Override protected void onHit(HitResult hit){super.onHit(hit);if(!level().isClientSide&&hit.getType()!=HitResult.Type.MISS){var pos=hit.getLocation();level().explode(this,pos.x,pos.y,pos.z,strength,true,Level.ExplosionInteraction.TNT);discard();}}
 @Override public void tick(){super.tick();if(tickCount>=1200)discard();}
 @Override public void addAdditionalSaveData(CompoundTag tag){super.addAdditionalSaveData(tag);tag.putFloat("strength",strength);}
 @Override public void readAdditionalSaveData(CompoundTag tag){super.readAdditionalSaveData(tag);strength=tag.contains("strength")?tag.getFloat("strength"):1;}
}
