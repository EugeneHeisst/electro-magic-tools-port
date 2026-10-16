package emt.item;
import emt.util.EMTConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import java.util.List;
public class ItemSolarPanel extends BlockItem {
 private final String kind;private final int stage;
 public ItemSolarPanel(Block block,String name){super(block,new Item.Properties());stage=name.startsWith("double_")?1:name.startsWith("triple_")?2:0;kind=name.replace("double_","").replace("triple_","").replace("_solar","");}
 public void appendHoverText(ItemStack stack,TooltipContext ctx,List<Component> lines,TooltipFlag flag){super.appendHoverText(stack,ctx,lines,flag);lines.add(Component.translatable("tooltip.EMT.normalConditions").append(": "+EMTConfig.SOLAR[stage].get()+" EU/t").withStyle(ChatFormatting.AQUA));String key=switch(kind){case "water"->"raining";case "dark"->"night";case "order"->"output.triple";case "fire"->"output.double.nether";case "air"->"output.double.half.height";case "earth"->"output.double.half.low";default->null;};if(key!=null){var text=Component.translatable("tooltip.EMT.specialEffect").append(": ");if(kind.equals("air")||kind.equals("earth"))text.append(Component.translatable("tooltip.EMT.output.double.half")).append(" ");if(kind.equals("fire"))text.append(Component.translatable("tooltip.EMT.output.double")).append(" ");text.append(Component.translatable("tooltip.EMT."+key));lines.add(text.withStyle(ChatFormatting.GREEN));}}
}
