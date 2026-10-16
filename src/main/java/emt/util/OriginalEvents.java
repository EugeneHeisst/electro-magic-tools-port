package emt.util;
import emt.EMT;
import emt.init.*;
import emt.item.ItemOneRing;
import ic2.api.item.ElectricItem;
import dev.eugheist.tc4modern.TC4Modern;
import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.bus.api.*;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
@EventBusSubscriber(modid=EMT.MODID)
public final class OriginalEvents {
 @SubscribeEvent static void target(LivingChangeTargetEvent e){if(!(e.getEntity() instanceof Mob)||!(e.getNewAboutToBeSetTarget() instanceof Player p))return;var worn=baubles.api.BaublesApi.getBaubles(p);for(int i=0;i<worn.getContainerSize();i++)if(worn.getItem(i).getItem() instanceof ItemOneRing){e.setNewAboutToBeSetTarget(null);return;}}
 @SubscribeEvent static void drops(LivingDropsEvent e){var mob=e.getEntity();if(mob.level().isClientSide)return;ItemStack extra=ItemStack.EMPTY;if(mob instanceof Creeper creeper&&creeper.isPowered())extra=new ItemStack(EMTItems.material("lightning_summoner"));if(BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType()).toString().equals("tc4modern:taint_chicken")){int n=mob.getRandom().nextInt(3);if(n>0)extra=new ItemStack(EMTItems.material("tainted_feather"),n);}if(!extra.isEmpty())e.getDrops().add(new ItemEntity(mob.level(),mob.getX(),mob.getY()+1,mob.getZ(),extra));}
 @SubscribeEvent(priority=EventPriority.LOWEST) static void cloud(PlayerInteractEvent.RightClickBlock e){if(e.getHand()!=InteractionHand.MAIN_HAND||e.getLevel().isClientSide||!e.getEntity().mayBuild()||!e.getLevel().mayInteract(e.getEntity(),e.getPos())||!e.getLevel().getBlockState(e.getPos()).is(TC4Modern.NITOR_BLOCK.get()))return;var held=e.getEntity().getMainHandItem();double packet=ElectricItem.manager.discharge(held,256,4,false,true,true);if(packet<256)return;if(e.getLevel().setBlock(e.getPos(),EMTBlocks.ELECTRIC_CLOUD.get().defaultBlockState(),3)){ElectricItem.manager.discharge(held,256,4,false,true,false);e.setCanceled(true);e.setCancellationResult(InteractionResult.SUCCESS);}}
}
