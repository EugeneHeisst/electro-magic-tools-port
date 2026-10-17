package emt.audit;
import emt.init.*;
import emt.item.tool.*;
import emt.util.EMTConfig;
import ic2.api.item.ElectricItem;
import ic2.core.item.tool.ItemElectricTool;
import ic2.core.ref.Ic2Items;
import dev.eugheist.tc4modern.*;
import dev.eugheist.tc4modern.api.TC4Addons;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.phys.*;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.event.level.BlockEvent;
import java.util.*;
import java.util.function.Consumer;
public final class ToolAudit {
 private static void check(String name,boolean ok,Object detail){ServerAudit.check("tool: "+name,ok,detail);}
 private static ItemStack charged(String name,double charge){var stack=EMTRecipes.stack("emt:"+name);ElectricItem.manager.charge(stack,charge,Integer.MAX_VALUE,true,false);return stack;}
 private static UseOnContext context(Player p,BlockPos pos){return new UseOnContext(p,InteractionHand.MAIN_HAND,new BlockHitResult(Vec3.atCenterOf(pos),Direction.UP,pos,false));}
 private static int air(Level l,BlockPos pos){int count=0;for(int x=-1;x<=1;x++)for(int z=-1;z<=1;z++)if(l.isEmptyBlock(pos.offset(x,0,z)))count++;return count;}
 private static void plane(Level l,BlockPos pos){for(int x=-1;x<=1;x++)for(int z=-1;z<=1;z++)l.setBlock(pos.offset(x,0,z),Blocks.STONE.defaultBlockState(),3);}
 public static void run(ServerLevel l,BlockPos anchor){try{
  var base=anchor.offset(64,0,64);for(int x=-1;x<=2;x++)for(int z=-1;z<=2;z++)l.setChunkForced((base.getX()>>4)+x,(base.getZ()>>4)+z,true);
  var actor=FakePlayerFactory.getMinecraft(l);actor.setGameMode(GameType.SURVIVAL);actor.getInventory().clearContent();actor.setShiftKeyDown(false);
  for(var entry:EMTItems.TOOLS.entrySet())if(entry.getValue().get() instanceof EMTElectricTool tool){var stack=new ItemStack(tool);check(entry.getKey()+" empty speed",tool.getDestroySpeed(stack,Blocks.STONE.defaultBlockState())==1,ElectricItem.manager.getCharge(stack));ElectricItem.manager.charge(stack,1000,Integer.MAX_VALUE,true,false);var effective=tool.kind.ordinal()>=2&&tool.kind.ordinal()<5?Blocks.OAK_LOG.defaultBlockState():Blocks.STONE.defaultBlockState();check(entry.getKey()+" charged speed and shared EU",tool.getDestroySpeed(stack,effective)==tool.efficiencyOnProperMaterial&&ElectricItem.manager.getCharge(stack)==1000,tool.getDestroySpeed(stack,effective));check(entry.getKey()+" tier and capacity",tool.tier==(tool.kind==EMTElectricTool.Kind.ROCKBREAKER_DRILL||tool.kind==EMTElectricTool.Kind.STREAM_CHAINSAW?3:2)&&tool.getMaxCharge(stack)>=20000,tool.getMaxCharge(stack));}
  var rock=charged("rockbreaker_drill",900000);actor.setItemInHand(InteractionHand.MAIN_HAND,rock);var pos=base;plane(l,pos);actor.setPos(pos.getX()+.5,pos.getY()+2,pos.getZ()+.5);actor.setYRot(0);actor.setXRot(90);actor.gameMode.destroyBlock(pos);check("real native 3x3 break pays9 operations",air(l,pos)==9&&ElectricItem.manager.getCharge(rock)==900000-9*350,air(l,pos)+" / "+ElectricItem.manager.getCharge(rock));
  pos=base.east(5);plane(l,pos);actor.setPos(pos.getX()+.5,pos.getY()+2,pos.getZ()+.5);actor.setShiftKeyDown(true);double before=ElectricItem.manager.getCharge(rock);actor.gameMode.destroyBlock(pos);check("sneak limits rockbreaker to one block",air(l,pos)==1&&ElectricItem.manager.getCharge(rock)==before-350,air(l,pos));actor.setShiftKeyDown(false);
  pos=base.east(10);plane(l,pos);rock=charged("rockbreaker_drill",700);actor.setItemInHand(InteractionHand.MAIN_HAND,rock);actor.setPos(pos.getX()+.5,pos.getY()+2,pos.getZ()+.5);actor.gameMode.destroyBlock(pos);check("3x3 stops when EU runs out",air(l,pos)==2&&ElectricItem.manager.getCharge(rock)==0,air(l,pos));
  pos=base.east(15);plane(l,pos);var protectedPos=pos.east();rock=charged("rockbreaker_drill",900000);actor.setItemInHand(InteractionHand.MAIN_HAND,rock);actor.setPos(pos.getX()+.5,pos.getY()+2,pos.getZ()+.5);Consumer<BlockEvent.BreakEvent> cancel=e->{if(e.getPos().equals(protectedPos))e.setCanceled(true);};net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(cancel);try{actor.gameMode.destroyBlock(pos);}finally{net.neoforged.neoforge.common.NeoForge.EVENT_BUS.unregister(cancel);}check("AoE respects real protection event and only pays successful blocks",l.getBlockState(protectedPos).is(Blocks.STONE)&&air(l,pos)==8&&ElectricItem.manager.getCharge(rock)==900000-8*350,air(l,pos));
  var tree=base.south(6);for(int y=0;y<7;y++)l.setBlock(tree.above(y),Blocks.OAK_LOG.defaultBlockState(),3);var saw=charged("stream_chainsaw",900000);actor.setItemInHand(InteractionHand.MAIN_HAND,saw);actor.gameMode.destroyBlock(tree);check("native stream saw takes furthest log once",l.isEmptyBlock(tree.above(6))&&l.getBlockState(tree).is(Blocks.OAK_LOG)&&ElectricItem.manager.getCharge(saw)==899600,ElectricItem.manager.getCharge(saw));actor.setItemInHand(InteractionHand.MAIN_HAND,charged("stream_chainsaw",0));actor.gameMode.destroyBlock(tree);check("empty stream saw cannot fell for free",l.getBlockState(tree.above(5)).is(Blocks.OAK_LOG),"no EU");
  var sheep=EntityType.SHEEP.create(l);sheep.moveTo(Vec3.atCenterOf(base.south(10)));l.addFreshEntity(sheep);saw=charged("diamond_chainsaw",1000);actor.setItemInHand(InteractionHand.MAIN_HAND,saw);saw.interactLivingEntity(actor,sheep,InteractionHand.MAIN_HAND);check("chainsaw shears sheep for original EU cost",sheep.isSheared()&&ElectricItem.manager.getCharge(saw)==800,ElectricItem.manager.getCharge(saw));sheep.setSheared(false);CustomData.update(DataComponents.CUSTOM_DATA,saw,t->t.putBoolean("disableShear",true));saw.interactLivingEntity(actor,sheep,InteractionHand.MAIN_HAND);check("shearing mode disables entity shearing",!sheep.isSheared()&&ElectricItem.manager.getCharge(saw)==800,"disabled");
  var torchPos=base.south(15);l.setBlock(torchPos,Blocks.STONE.defaultBlockState(),3);l.setBlock(torchPos.above(),Blocks.AIR.defaultBlockState(),3);actor.getInventory().setItem(1,new ItemStack(Items.TORCH,4));actor.setItemInHand(InteractionHand.MAIN_HAND,charged("iron_omnitool",1000));actor.getMainHandItem().useOn(context(actor,torchPos));check("omnitool places actual inventory torch",l.getBlockState(torchPos.above()).is(Blocks.TORCH)&&actor.getInventory().getItem(1).getCount()==3&&ElectricItem.manager.getCharge(actor.getMainHandItem())==1000,"torch placed");
  var hoePos=base.east(22);for(int x=-1;x<=1;x++)for(int z=-1;z<=1;z++){l.setBlock(hoePos.offset(x,0,z),Blocks.DIRT.defaultBlockState(),3);l.setBlock(hoePos.offset(x,1,z),Blocks.AIR.defaultBlockState(),3);}var hoe=charged("electric_hoe_growth",1000);actor.setItemInHand(InteractionHand.MAIN_HAND,hoe);hoe.useOn(context(actor,hoePos));boolean tilled=true;for(int x=-1;x<=1;x++)for(int z=-1;z<=1;z++)tilled&=l.getBlockState(hoePos.offset(x,0,z)).is(Blocks.FARMLAND);check("growth hoe tills nine blocks for225EU",tilled&&ElectricItem.manager.getCharge(hoe)==775,ElectricItem.manager.getCharge(hoe));l.setBlock(hoePos.above(),Blocks.WHEAT.defaultBlockState(),3);before=ElectricItem.manager.getCharge(hoe);hoe.useOn(context(actor,hoePos.above()));check("growth hoe uses native bonemeal for250EU",l.getBlockState(hoePos.above()).getValue(CropBlock.AGE)>0&&ElectricItem.manager.getCharge(hoe)==before-250,l.getBlockState(hoePos.above()));
  hoeProtection(l,base.east(28),actor);
  var ink=charged("electric_scribing_tools",400);check("electric scribing tools full ink mirrors EU",ScribingToolsItem.hasInk(ink)&&ink.getDamageValue()==0,ink.getDamageValue());ink.setDamageValue(ink.getDamageValue()+1);check("native research ink consumption spends one EU",ElectricItem.manager.getCharge(ink)==399&&ink.getDamageValue()==1,ElectricItem.manager.getCharge(ink));ink.setDamageValue(400);check("empty electric ink is retained for recharge",!ScribingToolsItem.hasInk(ink)&&!ink.isEmpty()&&ElectricItem.manager.getCharge(ink)==0,"empty not broken");ElectricItem.manager.charge(ink,5,1,false,false);check("IC2 recharges electric ink at original transfer rate",ScribingToolsItem.hasInk(ink)&&ElectricItem.manager.getCharge(ink)==5&&ink.getDamageValue()==395,ink.getDamageValue());
  var tablePos=base.south(20);l.setBlock(tablePos,TC4Modern.TABLE.get().defaultBlockState(),3);l.setBlock(tablePos.east(),TC4Modern.TABLE.get().defaultBlockState(),3);actor.setItemInHand(InteractionHand.MAIN_HAND,ink);ink.getItem().onItemUseFirst(ink,context(actor,tablePos));check("electric ink creates a real research table with charge preserved",l.getBlockEntity(tablePos) instanceof ResearchTableEntity table&&ScribingToolsItem.hasInk(table.inventory.getItem(0))&&ElectricItem.manager.getCharge(table.inventory.getItem(0))==5,"table created");
  var infusion=TC4Addons.infusionRecipes().stream().filter(r->r.key().equals("emt:thaumiumDrill")).findFirst().orElseThrow();var crafted=infusion.outputStack(new ItemStack(Ic2Items.DIAMOND_DRILL.get()));check("native TC infusion preserves configured charged result",crafted.is(EMTItems.TOOLS.get("thaumium_drill").get())&&ElectricItem.manager.getCharge(crafted)==10,ElectricItem.manager.getCharge(crafted));
  bore(l,base.south(28),actor);
 }catch(Exception ex){check("audit exception",false,ex.toString());throw new RuntimeException(ex);}}
 /** Real ServerLevel.mayInteract rejects the adjacent column; the clicked column remains usable. */
 private static void hoeProtection(ServerLevel l,BlockPos pos,net.neoforged.neoforge.common.util.FakePlayer actor){
  var border=l.getWorldBorder();double centreX=border.getCenterX(),centreZ=border.getCenterZ(),size=border.getSize(),target=border.getLerpTarget();long remaining=border.getLerpRemainingTime();
  var hoe=charged("electric_hoe_growth",1000);actor.setItemInHand(InteractionHand.MAIN_HAND,hoe);
  for(int x=-1;x<=1;x++)for(int z=-1;z<=1;z++){l.setBlock(pos.offset(x,0,z),Blocks.DIRT.defaultBlockState(),3);l.setBlock(pos.offset(x,1,z),Blocks.AIR.defaultBlockState(),3);}
  try{border.setCenter(pos.getX()+32,pos.getZ()+.5);border.setSize(64);boolean boundary=l.mayInteract(actor,pos)&&!l.mayInteract(actor,pos.west());hoe.useOn(context(actor,pos));boolean preserved=true;int tilled=0;for(int x=-1;x<=1;x++)for(int z=-1;z<=1;z++){var state=l.getBlockState(pos.offset(x,0,z));if(x<0)preserved&=state.is(Blocks.DIRT);if(state.is(Blocks.FARMLAND))tilled++;}check("growth hoe respects adjacent world protection and pays only six allowed blocks",boundary&&preserved&&tilled==6&&ElectricItem.manager.getCharge(hoe)==850,tilled+" / "+ElectricItem.manager.getCharge(hoe));}
  finally{border.setCenter(centreX,centreZ);if(remaining>0)border.lerpSizeBetween(size,target,remaining);else border.setSize(size);}
  for(int x=-1;x<=1;x++)for(int z=-1;z<=1;z++)l.setBlock(pos.offset(x,0,z),Blocks.DIRT.defaultBlockState(),3);
  var protectedPos=pos.east();Consumer<BlockEvent.BlockToolModificationEvent> cancel=e->{if(e.getPos().equals(protectedPos))e.setCanceled(true);};net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(cancel);
  double before=ElectricItem.manager.getCharge(hoe);try{hoe.useOn(context(actor,pos));}finally{net.neoforged.neoforge.common.NeoForge.EVENT_BUS.unregister(cancel);}int tilled=0;for(int x=-1;x<=1;x++)for(int z=-1;z<=1;z++)if(l.getBlockState(pos.offset(x,0,z)).is(Blocks.FARMLAND))tilled++;check("growth hoe respects real tool-modification protection without spending denied EU",l.getBlockState(protectedPos).is(Blocks.DIRT)&&tilled==8&&ElectricItem.manager.getCharge(hoe)==before-200,tilled+" / "+ElectricItem.manager.getCharge(hoe));
 }
 private static void field(Object o,String name,Object value)throws Exception{var f=o.getClass().getDeclaredField(name);f.setAccessible(true);f.set(o,value);}
 private static Object method(Object o,String name)throws Exception{var m=o.getClass().getDeclaredMethod(name);m.setAccessible(true);return m.invoke(o);}
 private static void bore(ServerLevel l,BlockPos pos,net.neoforged.neoforge.common.util.FakePlayer actor)throws Exception{
  l.setBlock(pos.below(),ArcaneBore.BASE.get().defaultBlockState(),3);l.setBlock(pos,ArcaneBore.BORE.get().defaultBlockState(),3);var bore=(ArcaneBore.BoreEntity)l.getBlockEntity(pos);bore.setItem(0,EMTRecipes.stack("tc4modern:focus_excavation"));var target=pos.above(4);field(bore,"fake",actor);field(bore,"dig",target);
  var tool=charged("thaumium_drill",1000);bore.setItem(1,tool);check("real TC bore accepts charged EMT drill",bore.canPlaceItem(1,tool)&&(boolean)method(bore,"validItems"),"accepted");l.setBlock(target,Blocks.STONE.defaultBlockState(),3);method(bore,"mine");check("real TC bore mines and pays EMT EU instead of durability",l.isEmptyBlock(target)&&ElectricItem.manager.getCharge(tool)==750,ElectricItem.manager.getCharge(tool));
  boolean config=EMTConfig.TOOLS_IN_BORE.get();try{EMTConfig.TOOLS_IN_BORE.set(true);l.setBlock(target,Blocks.STONE.defaultBlockState(),3);method(bore,"mine");check("original toolsInBore config uses one EU per block",l.isEmptyBlock(target)&&ElectricItem.manager.getCharge(tool)==749,ElectricItem.manager.getCharge(tool));}finally{EMTConfig.TOOLS_IN_BORE.set(config);}
  tool=charged("thaumium_drill",0);bore.setItem(1,tool);l.setBlock(target,Blocks.STONE.defaultBlockState(),3);method(bore,"mine");check("empty electric bore tool preserves target",l.getBlockState(target).is(Blocks.STONE)&&!(boolean)method(bore,"validItems"),"stopped");
  var ic2=new ItemStack(Ic2Items.DIAMOND_DRILL.get());ElectricItem.manager.charge(ic2,1000,3,true,false);bore.setItem(1,ic2);method(bore,"mine");check("TC bore also supports native IC2 drills",l.isEmptyBlock(target)&&ElectricItem.manager.getCharge(ic2)==1000-((ItemElectricTool)ic2.getItem()).operationEnergyCost,ElectricItem.manager.getCharge(ic2));
  var vanilla=new ItemStack(Items.DIAMOND_PICKAXE);bore.setItem(1,vanilla);l.setBlock(target,Blocks.STONE.defaultBlockState(),3);method(bore,"mine");check("ordinary bore pickaxes retain original durability payment",l.isEmptyBlock(target)&&vanilla.getDamageValue()==1,vanilla.getDamageValue());
 }
}
