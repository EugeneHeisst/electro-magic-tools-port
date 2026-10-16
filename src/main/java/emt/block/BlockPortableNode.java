package emt.block;
import com.mojang.serialization.MapCodec;
import emt.init.EMTBlocks;
import emt.tile.TilePortableNode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.phys.*;
import net.minecraft.world.phys.shapes.*;
public class BlockPortableNode extends BaseEntityBlock {
 public static final MapCodec<BlockPortableNode> CODEC=simpleCodec(BlockPortableNode::new);
 public BlockPortableNode(Properties p){super(p);}
 @Override protected MapCodec<? extends BaseEntityBlock> codec(){return CODEC;}
 @Override protected RenderShape getRenderShape(BlockState s){return RenderShape.INVISIBLE;}
 @Override protected VoxelShape getShape(BlockState s,BlockGetter l,BlockPos p,CollisionContext c){return Block.box(4.8,4.8,4.8,11.2,11.2,11.2);}
 @Override public BlockEntity newBlockEntity(BlockPos p,BlockState s){return new TilePortableNode(p,s);}
 @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level l,BlockState s,BlockEntityType<T> type){return createTickerHelper(type,EMTBlocks.PORTABLE_NODE_ENTITY.get(),TilePortableNode::tick);}
 @Override public void setPlacedBy(Level l,BlockPos p,BlockState s,LivingEntity actor,ItemStack stack){super.setPlacedBy(l,p,s,actor,stack);if(!l.isClientSide&&l instanceof net.minecraft.server.level.ServerLevel world){dev.eugheist.tc4modern.AuraNodeFeature.createRandomNodeAt(world,p,l.random,false,false,false);if(l.getBlockEntity(p) instanceof TilePortableNode node){node.forceNature();node.changed();}}}
 @Override protected InteractionResult useWithoutItem(BlockState s,Level l,BlockPos p,Player player,BlockHitResult hit){if(!l.isClientSide&&player.isShiftKeyDown()&&player.getMainHandItem().isEmpty()&&player.mayBuild()&&l.mayInteract(player,p))l.removeBlock(p,false);return InteractionResult.sidedSuccess(l.isClientSide);}
 @Override public boolean canEntityDestroy(BlockState s,BlockGetter l,BlockPos p,Entity e){return false;}
 @Override public void onBlockExploded(BlockState s,Level l,BlockPos p,Explosion e){}
}
