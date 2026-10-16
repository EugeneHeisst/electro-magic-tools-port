package emt.util;
import net.minecraft.core.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.*;
import net.minecraft.world.phys.*;
public final class Lightning {
 /** Original5000block liquid-inclusive trace, stopping at unloaded chunks rather than forcing world generation. */
 public static BlockHitResult trace(LivingEntity actor){var l=actor.level();var start=actor.getEyePosition();var end=start.add(actor.getLookAngle().scale(5000));double distance=BlockGetter.traverseBlocks(start,end,l,(world,pos)->world.hasChunkAt(pos)?null:Math.max(0,new AABB(pos).clip(start,end).map(start::distanceTo).orElse(0d)-.001),world->5000d);return l.clip(new ClipContext(start,start.add(actor.getLookAngle().scale(distance)),ClipContext.Block.OUTLINE,ClipContext.Fluid.ANY,actor));}
 public static void strike(Level l,LivingEntity cause,Vec3 at){var bolt=EntityType.LIGHTNING_BOLT.create(l);if(bolt!=null){bolt.moveTo(at);if(cause instanceof ServerPlayer p)bolt.setCause(p);l.addFreshEntity(bolt);}}
}
