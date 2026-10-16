package emt.tile;
import emt.util.EMTConfig;
import dev.eugheist.tc4modern.api.*;
import thaumcraft.api.aspects.Aspect;
import net.minecraft.core.*;
import net.minecraft.world.level.block.state.BlockState;
import ic2.core.util.Util;
/** Original 20-tick, [-4,3] source scan and output/30. Never drain a source when its EU will not fit. */
public class TileEssentiaGenerator extends TileEnergySource {
 public final Aspect aspect;private int ticks;
 public TileEssentiaGenerator(BlockPos p,BlockState s,String kind){super(p,s,kind+"_generator",1e9,3);aspect=switch(kind){case "potentia"->Aspect.ENERGY;case "ignis"->Aspect.FIRE;case "auram"->Aspect.AURA;case "arbor"->Aspect.TREE;default->Aspect.AIR;};}
 protected void updateEntityServer(){if(ticks++%20==0)createEnergy();}
 public int createEnergy(){double unit=EMTConfig.essentiaOutput(aspect)/30;int drained=0;if(unit<=0){setActive(false);return 0;}for(int x=-4;x<4;x++)for(int y=-4;y<4;y++)for(int z=-4;z<4;z++){
   if(capacity-energy<unit)continue;var pos=worldPosition.offset(x,y,z);var tile=Util.loadedBlockEntity(level,pos);if(tile==null||tile==this||!(tile instanceof dev.eugheist.tc4modern.WardedJarEntity||tile instanceof dev.eugheist.tc4modern.EssentiaReservoir.ReservoirEntity||tile instanceof AspectSource))continue;var container=AspectContainers.find(tile);boolean taken=container!=null?container.canExtract()&&container.take(aspect,1):tile instanceof AspectSource source&&source.takeFromContainer(aspect,1);if(taken){addEnergy(unit);drained++;net.neoforged.neoforge.network.PacketDistributor.sendToPlayersNear((net.minecraft.server.level.ServerLevel)level,null,worldPosition.getX(),worldPosition.getY(),worldPosition.getZ(),32,new dev.eugheist.tc4modern.InfusionContent.FxPayload(0,pos.getX(),pos.getY(),pos.getZ(),worldPosition.getX(),worldPosition.getY(),worldPosition.getZ(),aspect.getColor()));}
  }setActive(drained>0);return drained;}
}
