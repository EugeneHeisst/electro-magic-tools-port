package emt.audit;
import emt.EMT;
import emt.item.armor.ItemInfusedChestplate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.DisconnectedScreen;
import net.minecraft.world.entity.EquipmentSlot;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
@EventBusSubscriber(modid=EMT.MODID,value=Dist.CLIENT)
public final class MpClientAudit {
 private static int ticks;
 @SubscribeEvent static void tick(ClientTickEvent.Post e){if(!"mpclient".equals(System.getProperty("emt.audit")))return;var mc=Minecraft.getInstance();ticks++;if(mc.player!=null&&mc.getConnection()!=null){var chest=mc.player.getItemBySlot(EquipmentSlot.CHEST);mc.options.keyJump.setDown(chest.getItem() instanceof ItemInfusedChestplate&&ItemInfusedChestplate.attachment(chest)==1||ticks%10<8);}if(mc.screen instanceof DisconnectedScreen ds){EMT.LOG.info("[emt-mp-audit] client disconnected: {}",ds.getNarrationMessage().getString());mc.stop();}if(ticks>12000)mc.stop();}
}
