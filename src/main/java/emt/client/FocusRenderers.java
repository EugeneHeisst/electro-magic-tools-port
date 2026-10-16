package emt.client;
import emt.EMT;
import emt.entity.*;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
@EventBusSubscriber(modid=EMT.MODID,value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class FocusRenderers {
 @SubscribeEvent static void renderers(EntityRenderersEvent.RegisterRenderers e){e.registerEntityRenderer(EMTEntities.LASER.get(),Laser::new);e.registerEntityRenderer(EMTEntities.SHIELD.get(),Shield::new);e.registerEntityRenderer(EMTEntities.ENERGY_BALL.get(),Ball::new);}
 private static void vertex(VertexConsumer v,PoseStack.Pose p,float x,float y,float z,float u,float w,int color){v.addVertex(p,x,y,z).setColor(color).setUv(u,w).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(p,0,1,0);}
 static class Laser extends EntityRenderer<EntityLaser>{private static final ResourceLocation TEX=EMT.id("textures/models/lasermodel.png");Laser(EntityRendererProvider.Context c){super(c);}@Override public ResourceLocation getTextureLocation(EntityLaser e){return TEX;}@Override public void render(EntityLaser e,float yaw,float dt,PoseStack p,MultiBufferSource buffers,int light){p.pushPose();p.mulPose(Axis.YP.rotationDegrees(e.getYRot()-90));p.mulPose(Axis.ZP.rotationDegrees(e.getXRot()));p.mulPose(Axis.XP.rotationDegrees(45));p.scale(.05625f,.05625f,.05625f);p.translate(-4,0,0);var v=buffers.getBuffer(RenderType.entityCutoutNoCull(TEX));for(int i=0;i<4;i++){p.mulPose(Axis.XP.rotationDegrees(90));var pose=p.last();vertex(v,pose,-8,0,0,.4f,0,-1);vertex(v,pose,8,0,0,.7f,0,-1);vertex(v,pose,8,4,0,.7f,5f/32,-1);vertex(v,pose,-8,4,0,.4f,5f/32,-1);}p.popPose();super.render(e,yaw,dt,p,buffers,light);}}
 static class Shield extends EntityRenderer<EntityShield>{private static final ResourceLocation TEX=EMT.id("textures/models/shield.png");Shield(EntityRendererProvider.Context c){super(c);}@Override public ResourceLocation getTextureLocation(EntityShield e){return TEX;}@Override public void render(EntityShield e,float yaw,float dt,PoseStack p,MultiBufferSource b,int light){var v=b.getBuffer(RenderType.entityTranslucent(TEX));for(int h=-90;h<90;h+=15)for(int a=0;a<360;a+=15){point(v,p.last(),h,a,1,1);point(v,p.last(),h+15,a,1,0);point(v,p.last(),h+15,a+15,0,0);point(v,p.last(),h,a+15,0,1);}super.render(e,yaw,dt,p,b,light);}private static void point(VertexConsumer v,PoseStack.Pose p,int h,int a,float u,float w){double hr=Math.toRadians(h),ar=Math.toRadians(a);vertex(v,p,(float)(Math.sin(ar)*Math.cos(hr)*2),(float)(Math.sin(hr)*2),(float)(Math.cos(ar)*Math.cos(hr)*2),u,w,-1);}}
 /** The original energy ball is its animated node-bolt/spark field; arcs use TC4's native Zap renderer. */
 static class Ball extends EntityRenderer<EntityEnergyBall>{Ball(EntityRendererProvider.Context c){super(c);}@Override public ResourceLocation getTextureLocation(EntityEnergyBall e){return EMT.id("textures/item/focus_energyball.png");}}
}
