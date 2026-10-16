package emt.block;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.*;
/** The original permanent shield: mineable without drops, never destroyed by explosions or entities. */
public class BlockShield extends Block {
 public BlockShield(){super(BlockBehaviour.Properties.of().strength(1,1e37f).noOcclusion().isValidSpawn((s,l,p,e)->false));}
 @Override public boolean canEntityDestroy(BlockState state,BlockGetter level,BlockPos pos,Entity entity){return false;}
 @Override public void onBlockExploded(BlockState state,Level level,BlockPos pos,Explosion explosion){}
}
