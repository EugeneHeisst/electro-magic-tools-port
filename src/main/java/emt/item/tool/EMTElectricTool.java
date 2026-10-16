package emt.item.tool;

import emt.util.EMTConfig;
import ic2.api.item.ElectricItem;
import ic2.core.item.tool.ItemElectricTool;
import ic2.core.util.Keyboard;
import dev.eugheist.tc4modern.ElementalTools;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.IShearable;
import java.util.*;

/** EMT's original EU capacities, mining speeds and compound tool classes on the shared IC2 API. */
public class EMTElectricTool extends ItemElectricTool {
 public enum Kind { THAUMIUM_DRILL, ROCKBREAKER_DRILL, DIAMOND_CHAINSAW, THAUMIUM_CHAINSAW, STREAM_CHAINSAW, IRON_OMNITOOL, DIAMOND_OMNITOOL, THAUMIUM_OMNITOOL }
 public final Kind kind; public final int hitCost; private final float attack;
 private final Map<UUID,Direction> faces=new HashMap<>();
 public EMTElectricTool(Kind kind){super(new Properties(),cost(kind),kind==Kind.IRON_OMNITOOL?HarvestLevel.Iron:HarvestLevel.Diamond,classes(kind));this.kind=kind;
  int index=kind.ordinal();maxCharge=new int[]{60000,900000,40000,60000,900000,20000,70000,140000}[index];transferLimit=new int[]{100,900,300,600,900,200,400,800}[index];tier=kind==Kind.ROCKBREAKER_DRILL||kind==Kind.STREAM_CHAINSAW?3:2;efficiencyOnProperMaterial=new float[]{20,25,16,21,25,13,16,21}[index];hitCost=new int[]{0,400,300,350,500,125,250,650}[index];attack=new float[]{4,12,12,12,12,8,10,10}[index];
 }
 private static int cost(Kind k){return new int[]{250,350,200,250,400,100,200,300}[k.ordinal()];}
 private static Set<ToolClass> classes(Kind k){if(k.ordinal()<2)return EnumSet.of(ToolClass.Pickaxe,ToolClass.Shovel);if(k.ordinal()<5)return EnumSet.of(ToolClass.Axe,ToolClass.Sword,ToolClass.Shears);return EnumSet.of(ToolClass.Pickaxe,ToolClass.Shovel,ToolClass.Axe,ToolClass.Sword,ToolClass.Shears);}
 public double energyCost(){return EMTConfig.TOOLS_IN_BORE.get()&&(kind.ordinal()<2||kind.ordinal()>=5)?1:operationEnergyCost;}
 private boolean cutting(){return toolClasses.contains(ToolClass.Shears);}
 private boolean shearDisabled(ItemStack s){return s.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag().getBoolean("disableShear");}
 @Override public float getDestroySpeed(ItemStack s,BlockState state){return ElectricItem.manager.canUse(s,energyCost())&&isEffectiveOn(state)?efficiencyOnProperMaterial:1;}
 @Override public boolean isCorrectToolForDrops(ItemStack s,BlockState state){return ElectricItem.manager.canUse(s,energyCost())&&super.isCorrectToolForDrops(s,state);}
 @Override public int getEnchantmentValue(ItemStack s){return EMTConfig.ENCHANTING.get()?4:0;}
 @Override public boolean isEnchantable(ItemStack s){return EMTConfig.ENCHANTING.get();}
 @Override public boolean isBookEnchantable(ItemStack s,ItemStack book){return EMTConfig.ENCHANTING.get();}
 @Override public net.minecraft.world.item.component.ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack s){float damage=hitCost>0&&ElectricItem.manager.canUse(s,hitCost)?attack:1;return ItemAttributeModifiers.builder().add(Attributes.ATTACK_DAMAGE,new AttributeModifier(BASE_ATTACK_DAMAGE_ID,damage-1,AttributeModifier.Operation.ADD_VALUE),EquipmentSlotGroup.MAINHAND).build();}
 @Override public boolean hurtEnemy(ItemStack s,LivingEntity target,LivingEntity attacker){if(hitCost>0)ElectricItem.manager.use(s,hitCost,attacker);return true;}
 @Override public boolean onLeftClickEntity(ItemStack s,Player p,Entity target){if(kind==Kind.ROCKBREAKER_DRILL&&!p.level().isClientSide&&ElectricItem.manager.canUse(s,hitCost)&&(!(target instanceof Player)||p.getServer().isPvpAllowed()))target.igniteForSeconds(2);return false;}
 @Override public boolean mineBlock(ItemStack s,Level l,BlockState state,BlockPos pos,LivingEntity actor){if(!l.isClientSide&&state.getDestroySpeed(l,pos)!=0){if(actor==null)ElectricItem.manager.discharge(s,energyCost(),tier,true,false,false);else ElectricItem.manager.use(s,energyCost(),actor);}if(kind==Kind.ROCKBREAKER_DRILL&&actor instanceof ServerPlayer p&&!p.isShiftKeyDown()&&isEffectiveOn(state)){
   var face=faces.getOrDefault(p.getUUID(),Direction.UP);for(int a=-1;a<=1;a++)for(int b=-1;b<=1;b++){if(a==0&&b==0)continue;var at=switch(face.getAxis()){case Y->pos.offset(a,0,b);case Z->pos.offset(a,b,0);case X->pos.offset(0,a,b);};if(!l.hasChunkAt(at)||l.getBlockEntity(at)!=null||!isCorrectToolForDrops(s,l.getBlockState(at)))continue;harvest(p,at,s,energyCost());}
  }return true;}
 private static boolean harvest(ServerPlayer p,BlockPos pos,ItemStack s,double cost){if(!ElectricItem.manager.canUse(s,cost))return false;if(!ElementalTools.harvest(p,pos,3))return false;ElectricItem.manager.use(s,cost,p);return true;}
 public boolean onBlockStartBreak(ItemStack s,BlockPos pos,Player p){if(kind==Kind.ROCKBREAKER_DRILL){var hit=p.pick(6,1,false);var look=p.getLookAngle();faces.put(p.getUUID(),hit.getType()==net.minecraft.world.phys.HitResult.Type.BLOCK&&hit instanceof BlockHitResult block&&block.getBlockPos().equals(pos)?block.getDirection():Direction.getNearest(look.x,look.y,look.z).getOpposite());}
  if(kind==Kind.STREAM_CHAINSAW&&!p.isShiftKeyDown()&&p.level().getBlockState(pos).is(BlockTags.LOGS)){if(p instanceof ServerPlayer server){var target=ElementalTools.furthest(p.level(),pos,p.level().getBlockState(pos).getBlock());harvest(server,target,s,energyCost());net.neoforged.neoforge.network.PacketDistributor.sendToPlayersNear(server.serverLevel(),null,pos.getX(),pos.getY(),pos.getZ(),32,new ElementalTools.BlockBubble(pos));}return true;}
  if(cutting()&&!shearDisabled(s)&&p.level().getBlockState(pos).getBlock() instanceof IShearable shearable&&shearable.isShearable(p,s,p.level(),pos)){
   if(p instanceof ServerPlayer server&&ElectricItem.manager.canUse(s,energyCost())){var state=p.level().getBlockState(pos);var event=new net.neoforged.neoforge.event.level.BlockEvent.BreakEvent(p.level(),pos,state,p);net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(event);if(!event.isCanceled()&&p.level().mayInteract(p,pos)){var drops=shearable.onSheared(p,s,p.level(),pos);p.level().removeBlock(pos,false);ElectricItem.manager.use(s,energyCost(),p);if(!p.isCreative())for(var drop:drops)Block.popResource(p.level(),pos,drop);}}return true;}
  return false;
 }
 @Override public InteractionResult interactLivingEntity(ItemStack s,Player p,LivingEntity target,InteractionHand hand){if(cutting()&&!shearDisabled(s)&&target instanceof IShearable shearable&&shearable.isShearable(p,s,target.level(),target.blockPosition())){if(!p.level().isClientSide&&ElectricItem.manager.use(s,energyCost(),p))for(var drop:shearable.onSheared(p,s,target.level(),target.blockPosition()))target.spawnAtLocation(drop,1);return InteractionResult.sidedSuccess(p.level().isClientSide);}return InteractionResult.PASS;}
 @Override public InteractionResultHolder<ItemStack> use(Level l,Player p,InteractionHand hand){var s=p.getItemInHand(hand);if(cutting()&&Keyboard.isModeSwitchKeyDown(p)&&!l.isClientSide){boolean disabled=!shearDisabled(s);CustomData.update(DataComponents.CUSTOM_DATA,s,tag->tag.putBoolean("disableShear",disabled));p.displayClientMessage(Component.translatable("ic2.tooltip.mode",Component.translatable(disabled?"ic2.tooltip.mode.noShear":"ic2.tooltip.mode.normal")),false);}return InteractionResultHolder.pass(s);}
 @Override public InteractionResult useOn(UseOnContext c){var p=c.getPlayer();if(p==null)return InteractionResult.PASS;
  if(kind==Kind.ROCKBREAKER_DRILL&&p.isShiftKeyDown()){if(!c.getLevel().isClientSide&&ElectricItem.manager.use(c.getItemInHand(),1000,p)&&p instanceof ServerPlayer server)net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(server,new emt.util.OreScanPayload(c.getClickedPos()));return InteractionResult.SUCCESS;}
  if(kind==Kind.STREAM_CHAINSAW&&!p.isShiftKeyDown()&&c.getLevel().getBlockState(c.getClickedPos()).is(BlockTags.LOGS)){if(p instanceof ServerPlayer server)harvest(server,ElementalTools.furthest(c.getLevel(),c.getClickedPos(),c.getLevel().getBlockState(c.getClickedPos()).getBlock()),c.getItemInHand(),energyCost());return InteractionResult.sidedSuccess(c.getLevel().isClientSide);}
  return torches(c);
 }
 public static InteractionResult torches(UseOnContext c){var p=c.getPlayer();if(p==null)return InteractionResult.PASS;for(var torch:p.getInventory().items){if(torch.isEmpty()||!(torch.getItem() instanceof BlockItem)||!torch.getDescriptionId().toLowerCase(Locale.ROOT).contains("torch"))continue;int old=torch.getCount();var result=torch.useOn(new UseOnContext(c.getLevel(),p,c.getHand(),torch,new BlockHitResult(c.getClickLocation(),c.getClickedFace(),c.getClickedPos(),c.isInside())));if(p.isCreative())torch.setCount(old);if(result.consumesAction())return result;}return InteractionResult.PASS;}
}
