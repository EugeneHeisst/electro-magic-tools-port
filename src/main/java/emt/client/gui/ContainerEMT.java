package emt.client.gui;
import emt.init.EMTMenus;
import emt.tile.*;
import ic2.core.ContainerBase;
import ic2.core.block.TileEntityInventory;
import ic2.core.slot.SlotInvSlot;
import net.minecraft.world.entity.player.Inventory;
public abstract class ContainerEMT<T extends TileEntityInventory> extends ContainerBase<T> {
 protected ContainerEMT(int id,Inventory i,T b){super(EMTMenus.TILE.get(),id,i,b);for(int row=0;row<3;row++)for(int col=0;col<9;col++)addSlot(new net.minecraft.world.inventory.Slot(i,9+row*9+col,8+col*18,84+row*18));for(int col=0;col<9;col++)addSlot(new net.minecraft.world.inventory.Slot(i,col,8+col*18,142));}
 public static class Wand extends ContainerEMT<TileWandCharger>{public Wand(int id,Inventory i,TileWandCharger b){super(id,i,b);addSlot(new SlotInvSlot(b.wand,0,80,35));sync(()->b.energy,v->b.energy=v);}}
 public static class Processor extends ContainerEMT<TileEtherealProcessor>{public Processor(int id,Inventory i,TileEtherealProcessor b){super(id,i,b);addSlot(new SlotInvSlot(b.input,0,56,26));addSlot(new SlotInvSlot(b.output,0,116,26));addSlot(new SlotInvSlot(b.bonus,0,116,53));sync(()->b.progress,v->b.progress=(int)v);}}
}
