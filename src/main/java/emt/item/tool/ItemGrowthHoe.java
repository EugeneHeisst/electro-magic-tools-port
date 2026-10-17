package emt.item.tool;
import ic2.core.item.BaseElectricItem;
import ic2.api.item.ElectricItem;
import net.minecraft.core.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.levelgen.feature.*;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import dev.eugheist.tc4modern.*;
public class ItemGrowthHoe extends BaseElectricItem {
 public ItemGrowthHoe(){super(new Properties().stacksTo(1),20000,1000,1);}
 @Override public boolean canProvideEnergy(ItemStack s){return true;}
 @Override public boolean canPerformAction(ItemStack s,net.neoforged.neoforge.common.ItemAbility a){return a==net.neoforged.neoforge.common.ItemAbilities.HOE_TILL;}
 @Override public InteractionResult useOn(UseOnContext c){var p=c.getPlayer();if(p==null)return InteractionResult.PASS;var l=c.getLevel();var s=c.getItemInHand();boolean did=false;
  for(int x=-1;x<=1;x++)for(int z=-1;z<=1;z++){var pos=c.getClickedPos().offset(x,0,z);if(!l.hasChunkAt(pos)||!l.mayInteract(p,pos)||c.getClickedFace()==Direction.DOWN||!l.isEmptyBlock(pos.above())||!p.mayUseItemAt(pos,c.getClickedFace(),s)||!ElectricItem.manager.canUse(s,25))continue;var state=l.getBlockState(pos);var changed=state.getToolModifiedState(new UseOnContext(l,p,c.getHand(),s,new net.minecraft.world.phys.BlockHitResult(net.minecraft.world.phys.Vec3.atCenterOf(pos),c.getClickedFace(),pos,false)),net.neoforged.neoforge.common.ItemAbilities.HOE_TILL,false);if(changed==null)continue;did=true;if(!l.isClientSide&&ElectricItem.manager.use(s,25,p)){l.setBlock(pos,changed,3);sparkle((ServerLevel)l,pos,8401408,2);}}
  if(did)return InteractionResult.sidedSuccess(l.isClientSide);if(!ElectricItem.manager.canUse(s,250))return InteractionResult.PASS;
  var at=c.getClickedPos();if(!l.hasChunkAt(at)||!p.mayUseItemAt(at,c.getClickedFace(),s)||!l.mayInteract(p,at))return InteractionResult.PASS;
  if(l.isClientSide)return InteractionResult.SUCCESS;did=BoneMealItem.applyBonemeal(new ItemStack(Items.BONE_MEAL),l,at,p);
  if(!did){var state=l.getBlockState(at);boolean great=state.is(TC4Modern.GREATWOOD_SAPLING.get()),silver=state.is(SilverwoodContent.SAPLING.get());if(great||silver){var server=(ServerLevel)l;l.removeBlock(at,false);did=great?TC4Modern.GREATWOOD_FEATURE.get().grow(new FeaturePlaceContext<NoneFeatureConfiguration>(java.util.Optional.empty(),server,server.getChunkSource().getGenerator(),server.random,at,NoneFeatureConfiguration.INSTANCE),false):SilverwoodContent.FEATURE.get().grow(server,server.random,at,false,7,5);if(!did)l.setBlock(at,state,3);}}
  if(did){ElectricItem.manager.use(s,250,p);sparkle((ServerLevel)l,at,0,3);}return did?InteractionResult.SUCCESS:InteractionResult.PASS;
 }
 private static void sparkle(ServerLevel l,BlockPos pos,int rgb,int count){net.neoforged.neoforge.network.PacketDistributor.sendToPlayersNear(l,null,pos.getX(),pos.getY(),pos.getZ(),32,new InfusionContent.FxPayload(3,pos.getX(),pos.getY(),pos.getZ(),count,0,0,rgb));}
}
