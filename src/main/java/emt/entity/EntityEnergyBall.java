package emt.entity;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.*;
import net.minecraft.world.phys.*;
import net.minecraft.network.syncher.*;
import net.minecraft.nbt.CompoundTag;
public class EntityEnergyBall extends Projectile {
 private static final EntityDataAccessor<Boolean> IN_TILE=SynchedEntityData.defineId(EntityEnergyBall.class,EntityDataSerializers.BOOLEAN);
 private static final EntityDataAccessor<org.joml.Vector3f> ACCEL=SynchedEntityData.defineId(EntityEnergyBall.class,EntityDataSerializers.VECTOR3);
 private int activeTicks;
 public EntityEnergyBall(EntityType<? extends EntityEnergyBall> type,Level l){super(type,l);noCulling=true;}
 public EntityEnergyBall(Level l,LivingEntity owner){this(EMTEntities.ENERGY_BALL.get(),l);setOwner(owner);setPos(owner.getEyePosition());var dir=owner.getLookAngle().add(random.nextGaussian()*.004,random.nextGaussian()*.004,random.nextGaussian()*.004).normalize().scale(.1);entityData.set(ACCEL,dir.toVector3f());}
 @Override protected void defineSynchedData(SynchedEntityData.Builder b){b.define(IN_TILE,false);b.define(ACCEL,new org.joml.Vector3f());}
 public boolean inTile(){return entityData.get(IN_TILE);}public void activate(){entityData.set(IN_TILE,true);setDeltaMovement(Vec3.ZERO);}
 @Override public void tick(){super.tick();if(inTile()){if(!level().isClientSide){if(++activeTicks>=60){discard();return;}for(var target:level().getEntities(this,getBoundingBox().inflate(8))){double distance=target.position().distanceTo(position());if(distance<=8){target.hurt(damageSources().magic(),(float)(8-distance));net.neoforged.neoforge.network.PacketDistributor.sendToPlayersNear((net.minecraft.server.level.ServerLevel)level(),null,getX(),getY(),getZ(),64,new dev.eugheist.tc4modern.FociContent.Zap(getId(),target.getId()));}}}}else{var end=position().add(getDeltaMovement());var hit=level().clip(new ClipContext(position(),end,ClipContext.Block.COLLIDER,ClipContext.Fluid.NONE,this));if(hit.getType()!=HitResult.Type.MISS){setPos(hit.getLocation());activate();}else{setPos(end);setDeltaMovement(getDeltaMovement().add(new Vec3(entityData.get(ACCEL))).scale(.85));}if(tickCount>1200)discard();}}
 @Override public boolean isPickable(){return true;}
 @Override public void addAdditionalSaveData(CompoundTag t){super.addAdditionalSaveData(t);t.putBoolean("inTile",inTile());t.putInt("activeTicks",activeTicks);var a=entityData.get(ACCEL);t.putFloat("ax",a.x);t.putFloat("ay",a.y);t.putFloat("az",a.z);}
 @Override public void readAdditionalSaveData(CompoundTag t){super.readAdditionalSaveData(t);entityData.set(IN_TILE,t.getBoolean("inTile"));entityData.set(ACCEL,new org.joml.Vector3f(t.getFloat("ax"),t.getFloat("ay"),t.getFloat("az")));activeTicks=t.getInt("activeTicks");}
}
