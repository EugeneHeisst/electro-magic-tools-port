package emt.client.gui;
import emt.EMT;
import ic2.core.GuiIC2;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
public class GuiEMT extends GuiIC2<ContainerEMT<?>> {
 public GuiEMT(ContainerEMT<?> c,Inventory i,Component t){super(c,i,t,176,165);}
 public Component getName(){return Component.translatable(menu instanceof ContainerEMT.Wand?"block.emt.industrial_wand_charger":"block.emt.ethereal_processor");}
 protected void renderLabels(net.minecraft.client.gui.GuiGraphics g,int x,int y){drawFit(g,getName(),6,6,164,4210752);g.drawString(font,Component.translatable("gui.EMT.inventory"),8,71,4210752,false);}
 public ResourceLocation getResourceLocation(){return EMT.id("textures/guis/"+(menu instanceof ContainerEMT.Wand?"wandcharger":"etherealmacerator")+".png");}
}
