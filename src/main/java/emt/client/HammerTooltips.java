package emt.client;
import emt.EMT;
import emt.item.tool.*;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
@EventBusSubscriber(modid=EMT.MODID,value=Dist.CLIENT)
public final class HammerTooltips {
 @SubscribeEvent static void tooltip(ItemTooltipEvent e){if(e.getItemStack().getItem() instanceof ItemElectricThorHammer){for(int i=0;i<e.getToolTip().size();i++){var line=e.getToolTip().get(i);if(line.getString().contains("%player%"))e.getToolTip().set(i,Component.literal(line.getString().replace("%player%",e.getEntity()==null?"Thor":e.getEntity().getName().getString())));}}else if(e.getItemStack().getItem() instanceof ItemThorHammer hammer&&hammer.broken&&Screen.hasShiftDown()){var old=Component.translatable("tooltip.EMT.hammer.broken.plsNoRightClick").getString();for(int i=0;i<e.getToolTip().size();i++)if(e.getToolTip().get(i).getString().equals(old))e.getToolTip().set(i,Component.translatable("tooltip.EMT.hammer.broken.plsRightClick"));}}
}
