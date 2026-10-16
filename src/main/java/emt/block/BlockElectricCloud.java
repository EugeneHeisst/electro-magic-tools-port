package emt.block;
import com.mojang.serialization.MapCodec;
import emt.init.EMTBlocks;
import emt.tile.TileElectricCloud;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.*;
public class BlockElectricCloud extends BaseEntityBlock {
 public static final MapCodec<BlockElectricCloud> CODEC=simpleCodec(BlockElectricCloud::new);
 public BlockElectricCloud(Properties p){super(p);}
 @Override protected MapCodec<? extends BaseEntityBlock> codec(){return CODEC;}
 @Override protected RenderShape getRenderShape(BlockState s){return RenderShape.INVISIBLE;}
 @Override protected VoxelShape getShape(BlockState s,BlockGetter l,BlockPos p,CollisionContext c){return Block.box(4.8,4.8,4.8,11.2,11.2,11.2);}
 @Override public BlockEntity newBlockEntity(BlockPos p,BlockState s){return new TileElectricCloud(p,s);}
 @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level l,BlockState s,BlockEntityType<T> type){return l.isClientSide?createTickerHelper(type,EMTBlocks.ELECTRIC_CLOUD_ENTITY.get(),TileElectricCloud::tick):null;}
}
