package emt.tile;
import emt.init.*;
import emt.util.EMTConfig;
import ic2.core.*;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.*;
import net.minecraft.core.*;
import net.minecraft.core.registries.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.block.state.BlockState;
/** Original free double smelting and thaumium bonus, with guarded outputs and bonus on the final input too. */
public class TileEtherealProcessor extends TileEntityInventory implements IHasGui {
 public final InvSlot input=new InvSlot(this,"input",InvSlot.Access.I,1,InvSlot.InvSide.TOP){public boolean accepts(ItemStack stack){return !forbidden(stack)&&(level==null||level.getRecipeManager().getRecipeFor(RecipeType.SMELTING,new SingleRecipeInput(stack),level).isPresent());}};
 public final InvSlotOutput bonus=new InvSlotOutput(this,"bonus",1),output=new InvSlotOutput(this,"output",1);
 public int progress;private ItemStack processing=ItemStack.EMPTY;
 public TileEtherealProcessor(BlockPos p,BlockState s){super(EMTBlocks.type("ethereal_processor"),p,s);}
 public int[] getSlotsForFace(Direction side){return side==Direction.UP?new int[]{0}:side==Direction.DOWN?new int[]{2,1}:new int[]{1};}
 public boolean canPlaceItemThroughFace(int index,ItemStack stack,Direction side){return side==Direction.UP&&index==0&&input.accepts(stack);}
 public boolean canTakeItemThroughFace(int index,ItemStack stack,Direction side){return side==Direction.DOWN&&(index==1||index==2)||side.getAxis().isHorizontal()&&index==1;}
 public static ItemStack thaumiumNugget(){return new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse("tc4modern:thaumium_nugget")));}
 public boolean forbidden(ItemStack stack){var id=BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath();return stack.is(ItemTags.STONE_CRAFTING_MATERIALS)||stack.is(ItemTags.SAND)||stack.is(Items.POTATO)||stack.is(Items.PORKCHOP)||stack.is(Items.BEEF)||stack.is(Items.CHICKEN)||stack.is(Items.COD)||stack.is(Items.SALMON)||stack.is(Items.TROPICAL_FISH)||stack.is(Items.PUFFERFISH)||stack.getItem() instanceof ArmorItem||stack.getItem() instanceof DiggerItem||id.contains("dust")||id.contains("armor")||id.contains("armour")||id.contains("helm")||id.contains("legging")||id.contains("boot")||id.contains("chestp")||id.contains("pick")||id.contains("shovel")||id.contains("axe");}
 public ItemStack result(){var in=input.get();if(in.isEmpty()||forbidden(in))return ItemStack.EMPTY;return level.getRecipeManager().getRecipeFor(RecipeType.SMELTING,new SingleRecipeInput(in),level).map(r->{var out=r.value().assemble(new SingleRecipeInput(in),level.registryAccess());return out.copyWithCount(out.getCount()*2);}).orElse(ItemStack.EMPTY);}
 public boolean processTick(){var result=result();if(result.isEmpty()||result.getCount()>result.getMaxStackSize()||!output.canAdd(result)||!bonus.canAdd(thaumiumNugget())){progress=0;processing=ItemStack.EMPTY;return false;}if(!ItemStack.isSameItemSameComponents(processing,input.get())){processing=input.get().copyWithCount(1);progress=0;}if(++progress>=EMTConfig.PROCESS_TICKS.get()){output.add(result);input.get().shrink(1);if(level.random.nextInt(EMTConfig.BONUS_CHANCE.get())==0)bonus.add(thaumiumNugget());progress=0;processing=ItemStack.EMPTY;}setChanged();return true;}
 protected void updateEntityServer(){setActive(processTick());}
 public void readFromNBT(CompoundTag n,HolderLookup.Provider r){super.readFromNBT(n,r);progress=Math.max(0,Math.min(EMTConfig.PROCESS_TICKS.get()-1,n.getInt("progress")));processing=ItemStack.parseOptional(r,n.getCompound("processing"));}public void writeToNBT(CompoundTag n,HolderLookup.Provider r){super.writeToNBT(n,r);n.putInt("progress",progress);if(!processing.isEmpty())n.put("processing",processing.save(r));}
 public ContainerBase<?> getGuiContainer(int id,Inventory i){return new emt.client.gui.ContainerEMT.Processor(id,i,this);}
}
