package emt.entity;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
import net.minecraft.network.syncher.*;
import net.minecraft.nbt.CompoundTag;
import java.util.*;
public class EntityShield extends Entity {
 private Player cachedOwner;
 private static final EntityDataAccessor<Optional<UUID>> OWNER=SynchedEntityData.defineId(EntityShield.class,EntityDataSerializers.OPTIONAL_UUID);
 public EntityShield(EntityType<? extends EntityShield> type,Level l){super(type,l);noCulling=true;}
 public EntityShield(Level l,Player owner){this(EMTEntities.SHIELD.get(),l);cachedOwner=owner;entityData.set(OWNER,Optional.of(owner.getUUID()));follow(owner);}
 public Player owner(){if(cachedOwner==null||cachedOwner.isRemoved()||cachedOwner.level()!=level())cachedOwner=entityData.get(OWNER).map(level()::getPlayerByUUID).orElse(null);return cachedOwner;}
 private void follow(Player p){setPos(p.getX(),p.getY()+.5,p.getZ());setBoundingBox(new AABB(p.getX()-2,p.getY()-2,p.getZ()-2,p.getX()+2,p.getY()+2,p.getZ()+2));}
 @Override protected void defineSynchedData(SynchedEntityData.Builder b){b.define(OWNER,Optional.empty());}
 @Override public void tick(){super.tick();var p=owner();if(p==null){if(!level().isClientSide)discard();return;}if(!level().isClientSide&&(!p.isUsingItem()||!(dev.eugheist.tc4modern.FociContent.getFocus(p.getUseItem()) instanceof emt.item.focus.EMTFocus focus)||focus.kind!=emt.item.focus.EMTFocus.Kind.SHIELD)){discard();return;}follow(p);if(!level().isClientSide)for(var entity:level().getEntities(this,getBoundingBox()))if(entity!=p&&!(entity instanceof EntityShield))entity.push((entity.getX()-getX())/5,0,(entity.getZ()-getZ())/5);}
 @Override public boolean isPickable(){return true;}@Override public boolean isPushable(){return true;}@Override public boolean canBeCollidedWith(){return true;}@Override public boolean canCollideWith(Entity e){return e!=owner()&&super.canCollideWith(e);}
 @Override protected void addAdditionalSaveData(CompoundTag t){entityData.get(OWNER).ifPresent(id->t.putUUID("owner",id));}
 @Override protected void readAdditionalSaveData(CompoundTag t){if(t.hasUUID("owner"))entityData.set(OWNER,Optional.of(t.getUUID("owner")));}
}
