package emt.tile;
import emt.init.EMTBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import java.util.*;
public class TileElectricCloud extends BlockEntity {
 public record Wisp(thaumcraft.common.tiles.NitorWisp motion,float red){}
 public final List<Wisp> wisps=new ArrayList<>();
 public static java.util.function.Consumer<TileElectricCloud> effects=tile->{};
 public TileElectricCloud(BlockPos p,BlockState s){super(EMTBlocks.ELECTRIC_CLOUD_ENTITY.get(),p,s);}
 public static void tick(Level l,BlockPos p,BlockState s,TileElectricCloud cloud){if(l.isClientSide)effects.accept(cloud);}
}
