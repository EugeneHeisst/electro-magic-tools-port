package emt.client;
import emt.EMT;
import net.minecraft.client.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.*;
public final class ArmorKeys {
 public static final KeyMapping DETACH=new KeyMapping("gui.EMT.key.unequip",org.lwjgl.glfw.GLFW.GLFW_KEY_Z,"key.categories.emt");
 @EventBusSubscriber(modid=EMT.MODID,value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)static class Registration {@SubscribeEvent static void keys(RegisterKeyMappingsEvent e){e.register(DETACH);}@SubscribeEvent static void setup(net.neoforged.fml.event.lifecycle.FMLClientSetupEvent e){e.enqueueWork(()->net.minecraft.client.renderer.item.ItemProperties.register(emt.init.EMTItems.ARMOR.get("infused_quantum_chestplate").get(),EMT.id("attachment"),(s,l,p,seed)->emt.item.armor.ItemInfusedChestplate.attachment(s)));}}
 @EventBusSubscriber(modid=EMT.MODID,value=Dist.CLIENT)static class Events {@SubscribeEvent static void tick(ClientTickEvent.Post e){while(DETACH.consumeClick())if(Minecraft.getInstance().player!=null&&Minecraft.getInstance().screen==null)net.neoforged.neoforge.network.PacketDistributor.sendToServer(new emt.util.DetachPayload());}}
}
