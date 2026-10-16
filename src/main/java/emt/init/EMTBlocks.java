package emt.init;
import emt.EMT;
import emt.tile.*;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.*;
import net.neoforged.neoforge.capabilities.*;
import ic2.core.block.*;
import java.util.*;
public final class EMTBlocks {
 public static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks(EMT.MODID);
 public static final DeferredRegister<BlockEntityType<?>> TILES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,EMT.MODID);
 public static final Map<String,DeferredBlock<BlockTileEntity>> MACHINES=new LinkedHashMap<>();
 public static final Map<String,DeferredHolder<BlockEntityType<?>,BlockEntityType<?>>> TYPES=new LinkedHashMap<>();
 static {
  for(String kind:List.of("compressed","water","dark","order","fire","air","earth"))for(int stage=0;stage<3;stage++){String name=(stage==0?"":stage==1?"double_":"triple_")+kind+"_solar";final int rank=stage;add(name,(pos,state)->new TileSolar(pos,state,name,kind,rank));}
  for(String kind:List.of("potentia","ignis","auram","arbor","aer"))add(kind+"_generator",(pos,state)->new TileEssentiaGenerator(pos,state,kind));
  add("industrial_wand_charger",TileWandCharger::new);add("ethereal_processor",TileEtherealProcessor::new);
 }
 private static void add(String name,BlockEntityType.BlockEntitySupplier<TileEntityBlock> factory){var block=BLOCKS.register(name,()->new BlockTileEntity(BlockBehaviour.Properties.of().strength(4).sound(SoundType.METAL).requiresCorrectToolForDrops(),()->(BlockEntityType<? extends TileEntityBlock>)type(name)));MACHINES.put(name,block);TYPES.put(name,TILES.register(name,()->BlockEntityType.Builder.of(factory,block.get()).build(null)));if(name.endsWith("_solar"))EMTItems.ITEMS.register(name,()->new emt.item.ItemSolarPanel(block.get(),name));else EMTItems.ITEMS.registerSimpleBlockItem(name,block);}
 public static Block block(String name){return MACHINES.get(name).get();}public static BlockEntityType<?> type(String name){return TYPES.get(name).get();}
 public static void capabilities(RegisterCapabilitiesEvent e){for(var type:TYPES.values())e.registerBlockEntity(Capabilities.ItemHandler.BLOCK,type.get(),(be,side)->be instanceof TileEntityInventory inv?side==null?new net.neoforged.neoforge.items.wrapper.InvWrapper(inv):new net.neoforged.neoforge.items.wrapper.SidedInvWrapper(inv,side):null);}
}
