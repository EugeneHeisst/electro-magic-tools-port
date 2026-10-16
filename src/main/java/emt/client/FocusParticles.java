package emt.client;
import emt.EMT;
import emt.entity.EntityEnergyBall;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
/** Original three random node bolts per tick around the energy ball, separate from the damage arcs. */
@EventBusSubscriber(modid=EMT.MODID,value=Dist.CLIENT)
public final class FocusParticles {
 @SubscribeEvent static void tick(EntityTickEvent.Post e){if(e.getEntity() instanceof EntityEnergyBall ball&&ball.level() instanceof net.minecraft.client.multiplayer.ClientLevel level){for(int i=0;i<3;i++)dev.eugheist.tc4modern.NodeBoltParticle.spawn(level,ball.getX(),ball.getY(),ball.getZ(),ball.getX()+(level.random.nextDouble()-.5)*8,ball.getY()+(level.random.nextDouble()-.5)*8,ball.getZ()+(level.random.nextDouble()-.5)*8);}}
}
