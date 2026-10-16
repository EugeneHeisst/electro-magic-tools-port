package emt.client;
import emt.EMT;
import emt.init.EMTItems;
import emt.item.armor.*;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.*;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.extensions.common.*;
import java.util.*;
/** Original EMT wing boxes and UVs, parented to the modern humanoid body so poses do not accumulate offsets. */
@EventBusSubscriber(modid=EMT.MODID,value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class WingsModels {
 private static final Map<Integer,HumanoidModel<LivingEntity>> MODELS=new HashMap<>();
 public static HumanoidModel<LivingEntity> model(int type){return MODELS.computeIfAbsent(type,k->{var mesh=HumanoidModel.createMesh(new CubeDeformation(k==0?0:1),0);var root=mesh.getRoot();if(k==0)for(String name:List.of("head","hat","body","right_arm","left_arm","right_leg","left_leg"))root.addOrReplaceChild(name,CubeListBuilder.create(),PartPose.ZERO);var body=root.getChild("body");if(k==1){body.addOrReplaceChild("jetpack",CubeListBuilder.create().texOffs(28,72).addBox(-4,0,2,8,12,4,new CubeDeformation(.5f)),PartPose.ZERO);}else{int v=k==0?0:32;body.addOrReplaceChild("right_wing",CubeListBuilder.create().texOffs(24,v).addBox(-11,-1,k==0?-1.5f:0,11,13,1),PartPose.offsetAndRotation(-2,0,3,0,.1570796f,0));body.addOrReplaceChild("left_wing",CubeListBuilder.create().texOffs(0,v).mirror().addBox(0,-1,k==0?-1.5f:0,11,13,1),PartPose.offsetAndRotation(2,0,3,0,-.1570796f,0));body.addOrReplaceChild("center",CubeListBuilder.create().texOffs(0,k==0?14:46).mirror().addBox(0,0,k==0?-.5f:1,8,14,1),PartPose.offset(-4,0,3));}return new HumanoidModel<>(LayerDefinition.create(mesh,k==1?128:64,k==1?96:64).bakeRoot());});}
 @SubscribeEvent static void register(RegisterClientExtensionsEvent e){var items=EMTItems.ARMOR.values().stream().map(v->v.get()).filter(i->i instanceof ItemDurableWings||i instanceof EMTArmor a&&a.wings()||i instanceof ItemInfusedChestplate).toArray(net.minecraft.world.item.Item[]::new);e.registerItem(new IClientItemExtensions(){@Override public HumanoidModel<?> getHumanoidArmorModel(LivingEntity entity,ItemStack stack,EquipmentSlot slot,HumanoidModel<?> original){boolean infused=stack.getItem() instanceof ItemInfusedChestplate;int attachment=infused?ItemInfusedChestplate.attachment(stack):0;if(infused&&attachment==0)return original;int type=infused?attachment==1?1:2:0;var model=model(type);if(type!=1){boolean jump=EMTArmor.tag(stack).getBoolean("isJumping");model.body.getChild("right_wing").yRot=jump?.5f:.1570796f;model.body.getChild("left_wing").yRot=jump?-.5f:-.1570796f;}return model;}},items);}
}
