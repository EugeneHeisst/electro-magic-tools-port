package emt.tile;
import emt.init.EMTBlocks;
import dev.eugheist.tc4modern.AuraNodeEntity;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.api.nodes.*;
/** A genuine Thaumcraft node with the reference subtype's permanent fading/unstable nature. */
public class TilePortableNode extends AuraNodeEntity {
 public TilePortableNode(BlockPos pos,BlockState state){super(EMTBlocks.PORTABLE_NODE_ENTITY.get(),pos,state);forceNature();}
 public boolean forceNature(){boolean changed=nodeType!=NodeType.UNSTABLE||reservoir.modifier!=NodeModifier.FADING;nodeType=NodeType.UNSTABLE;reservoir.modifier=NodeModifier.FADING;return changed;}
 @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider registries){super.loadAdditional(tag,registries);forceNature();}
 public static void tick(Level l,BlockPos pos,BlockState state,TilePortableNode node){node.forceNature();AuraNodeEntity.tick(l,pos,state,node);if(node.forceNature()&&!l.isClientSide)node.changed();}
}
