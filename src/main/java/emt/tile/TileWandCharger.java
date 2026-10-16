package emt.tile;
import emt.init.*;
import emt.util.EMTConfig;
import dev.eugheist.tc4modern.WandData;
import thaumcraft.api.aspects.Aspect;
import ic2.api.energy.EnergyNet;
import ic2.api.energy.tile.IEnergySink;
import ic2.core.*;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlot;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
/** Consumes one configured EU payment for one vis in each primal with room; keeps both EU and wand across saves. */
public class TileWandCharger extends TileEntityInventory implements IEnergySink,IHasGui {
 public final InvSlot wand=new InvSlot(this,"wand",InvSlot.Access.IO,1){public boolean accepts(ItemStack s){return WandData.isWand(s);}};
 public double energy;private boolean registered;
 public TileWandCharger(BlockPos p,BlockState s){super(EMTBlocks.type("industrial_wand_charger"),p,s);}
 public boolean chargeWand(){var stack=wand.get();if(!WandData.isWand(stack)||energy<EMTConfig.WAND_COST.get())return false;boolean room=false;for(var a:Aspect.getPrimalAspects())room|=WandData.get(stack,a)<WandData.capacity(stack);if(!room)return false;for(var a:Aspect.getPrimalAspects())WandData.add(stack,a,1,true);energy-=EMTConfig.WAND_COST.get();setChanged();return true;}
 protected void updateEntityServer(){setActive(chargeWand());}
 public double getDemandedEnergy(){return 100000-energy;}public int getSinkTier(){return 4;}public boolean acceptsEnergyFrom(BlockEntity b,Direction d){return true;}
 public double injectEnergy(Direction d,double amount,double voltage){if(!Double.isFinite(amount)||amount<=0)return amount;double accepted=Math.min(amount,getDemandedEnergy());energy+=accepted;if(accepted>0)setChanged();return amount-accepted;}
 public void onLoaded(){super.onLoaded();if(!level.isClientSide&&!registered){EnergyNet.instance.addTile(this);registered=true;}}public void onUnloaded(){if(registered){EnergyNet.instance.removeTile(this);registered=false;}super.onUnloaded();}
 public void readFromNBT(CompoundTag n,HolderLookup.Provider r){super.readFromNBT(n,r);double saved=n.getDouble("energy");energy=Double.isFinite(saved)?Math.max(0,Math.min(100000,saved)):0;}public void writeToNBT(CompoundTag n,HolderLookup.Provider r){super.writeToNBT(n,r);n.putDouble("energy",energy);}
 public ContainerBase<?> getGuiContainer(int id,Inventory i){return new emt.client.gui.ContainerEMT.Wand(id,i,this);}
}
