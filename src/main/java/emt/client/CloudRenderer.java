package emt.client;
import emt.EMT;
import emt.init.EMTBlocks;
import emt.tile.*;
import dev.eugheist.tc4modern.*;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import org.joml.Vector3f;
/** Native TC4 wisp sheet/shader and lightning particles with the original purple cloud palette. */
@EventBusSubscriber(modid=EMT.MODID,value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class CloudRenderer implements BlockEntityRenderer<TileElectricCloud> {
 public CloudRenderer(BlockEntityRendererProvider.Context c){}
 @SubscribeEvent static void register(EntityRenderersEvent.RegisterRenderers e){e.registerBlockEntityRenderer(EMTBlocks.ELECTRIC_CLOUD_ENTITY.get(),CloudRenderer::new);e.registerBlockEntityRenderer(EMTBlocks.PORTABLE_NODE_ENTITY.get(),AuraNodeRenderer::new);}
 @SubscribeEvent static void setup(net.neoforged.fml.event.lifecycle.FMLClientSetupEvent e){e.enqueueWork(()->TileElectricCloud.effects=CloudRenderer::tick);}
 private static void tick(TileElectricCloud tile){if(!(tile.getLevel() instanceof net.minecraft.client.multiplayer.ClientLevel l))return;var pos=tile.getBlockPos();tile.wisps.removeIf(w->!w.motion().tick());var r=l.random;java.util.random.RandomGenerator random=new java.util.random.RandomGenerator(){public long nextLong(){return r.nextLong();}public float nextFloat(){return r.nextFloat();}};if(r.nextInt(4)==0)tile.wisps.add(new TileElectricCloud.Wisp(new thaumcraft.common.tiles.NitorWisp(random,.4+r.nextFloat()*.2,.4+r.nextFloat()*.2,.25f,1,-.02f),.35f));if(r.nextInt(3)==0)tile.wisps.add(new TileElectricCloud.Wisp(new thaumcraft.common.tiles.NitorWisp(random,.5+(r.nextInt(40)/160f-.125),.5+(r.nextInt(40)/160f-.125),.4f+r.nextInt(40)/100f,1,-.02f),r.nextInt(40)/100f));if(r.nextInt(12)==0){bolt(l,pos);if(r.nextInt(2)==0)bolt(l,pos);if(r.nextInt(4)==0)bolt(l,pos);}if(r.nextInt(3)==0)l.addParticle(net.minecraft.core.particles.ParticleTypes.END_ROD,pos.getX()+.5,pos.getY()+.5,pos.getZ()+.5,(r.nextFloat()-r.nextFloat())/30f,.005,(r.nextFloat()-r.nextFloat())/30f);}
 private static void bolt(net.minecraft.client.multiplayer.ClientLevel l,net.minecraft.core.BlockPos p){NodeBoltParticle.spawn(l,p.getX()+.5,p.getY()+.5,p.getZ()+.5,p.getX()+l.random.nextInt(40)/20f-.5,p.getY()+l.random.nextInt(40)/20f-.5,p.getZ()+l.random.nextInt(40)/20f-.5);}
 @Override public void render(TileElectricCloud tile,float partial,PoseStack p,MultiBufferSource b,int light,int overlay){var facing=Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation();var vertices=b.getBuffer(NodeRenderTypes.wisp());for(var colored:tile.wisps){var w=colored.motion();float x=(float)(w.previousX+(w.x-w.previousX)*partial),y=(float)(w.previousY+(w.y-w.previousY)*partial),z=(float)(w.previousZ+(w.z-w.previousZ)*partial),radius=w.radius();for(var corner:new float[][]{{-1,-1,.125f,1},{-1,1,.125f,.875f},{1,1,0,.875f},{1,-1,0,1}}){var v=new Vector3f(corner[0]*radius,corner[1]*radius,0).rotate(facing);vertices.addVertex(p.last(),x+v.x,y+v.y,z+v.z).setColor((int)(colored.red()*255),0,255,192).setUv(corner[2],corner[3]);}}}
}
