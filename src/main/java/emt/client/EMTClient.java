package emt.client;
import emt.EMT;
import emt.init.EMTMenus;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
@EventBusSubscriber(modid=EMT.MODID,value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public class EMTClient {
 @SubscribeEvent static void screens(RegisterMenuScreensEvent e){e.register(EMTMenus.TILE.get(),emt.client.gui.GuiEMT::new);}
}
