package emt.tile;
import emt.init.EMTBlocks;
import ic2.api.energy.EnergyNet;
import ic2.api.energy.tile.IEnergySource;
import ic2.core.block.TileEntityBlock;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
public abstract class TileEnergySource extends TileEntityBlock implements IEnergySource {
 public double energy;public final double capacity;public final int tier;private boolean registered;
 protected TileEnergySource(BlockPos p,BlockState s,String name,double capacity,int tier){super(EMTBlocks.type(name),p,s);this.capacity=capacity;this.tier=tier;}
 public void addEnergy(double amount){if(Double.isFinite(amount)&&amount>0){energy=Math.min(capacity,energy+amount);setChanged();}}
 public boolean emitsEnergyTo(BlockEntity other,Direction d){return true;}public int getSourceTier(){return tier;}public double getOfferedEnergy(){return Math.min(energy,EnergyNet.instance.getPowerFromTier(tier));}public void drawEnergy(double amount){if(Double.isFinite(amount)&&amount>0){energy=Math.max(0,energy-amount);setChanged();}}
 public void onLoaded(){super.onLoaded();if(!level.isClientSide&&!registered){EnergyNet.instance.addTile(this);registered=true;}}public void onUnloaded(){if(registered){EnergyNet.instance.removeTile(this);registered=false;}super.onUnloaded();}
 public void readFromNBT(CompoundTag n,HolderLookup.Provider r){super.readFromNBT(n,r);double saved=n.getDouble("energy");energy=Double.isFinite(saved)?Math.max(0,Math.min(capacity,saved)):0;}public void writeToNBT(CompoundTag n,HolderLookup.Provider r){super.writeToNBT(n,r);n.putDouble("energy",energy);}
}
