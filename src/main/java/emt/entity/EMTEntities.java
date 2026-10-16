package emt.entity;
import emt.EMT;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.*;
import net.neoforged.neoforge.registries.*;
public final class EMTEntities {
 public static final DeferredRegister<EntityType<?>> TYPES=DeferredRegister.create(Registries.ENTITY_TYPE,EMT.MODID);
 public static final DeferredHolder<EntityType<?>,EntityType<EntityLaser>> LASER=TYPES.register("laser",()->EntityType.Builder.<EntityLaser>of(EntityLaser::new,MobCategory.MISC).sized(.5f,.5f).clientTrackingRange(8).updateInterval(1).build("emt:laser"));
 public static final DeferredHolder<EntityType<?>,EntityType<EntityEnergyBall>> ENERGY_BALL=TYPES.register("energy_ball",()->EntityType.Builder.<EntityEnergyBall>of(EntityEnergyBall::new,MobCategory.MISC).sized(1,1).clientTrackingRange(8).updateInterval(1).build("emt:energy_ball"));
 public static final DeferredHolder<EntityType<?>,EntityType<EntityShield>> SHIELD=TYPES.register("shield",()->EntityType.Builder.<EntityShield>of(EntityShield::new,MobCategory.MISC).sized(4,4).clientTrackingRange(8).updateInterval(1).build("emt:shield"));
}
