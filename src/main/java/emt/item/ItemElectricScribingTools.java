package emt.item;
import ic2.api.item.*;
import dev.eugheist.tc4modern.ScribingToolsItem;
import net.minecraft.world.item.*;
/** IC2 charge is the ink level. TC4's IScribeTools paths call get/setDamage instead of breaking the item. */
public class ItemElectricScribingTools extends ScribingToolsItem implements IElectricItem {
 public ItemElectricScribingTools(){super(new Item.Properties().durability(400).setNoRepair());}
 public double getMaxCharge(ItemStack s){return 400;}public double getTransferLimit(ItemStack s){return 5;}public int getTier(ItemStack s){return 1;}public boolean canProvideEnergy(ItemStack s){return false;}
 @Override public int getDamage(ItemStack s){return (int)Math.ceil(400-ElectricItem.manager.getCharge(s));}
 @Override public void setDamage(ItemStack s,int damage){double desired=Math.max(0,400-damage),current=ElectricItem.manager.getCharge(s);if(current>desired)ElectricItem.manager.discharge(s,current-desired,1,true,false,false);else if(current<desired)ElectricItem.manager.charge(s,desired-current,1,true,false);}
 @Override public boolean isBarVisible(ItemStack s){return true;}@Override public int getBarWidth(ItemStack s){return Math.round(13f*(float)ElectricItem.manager.getCharge(s)/400);}
 @Override public void appendHoverText(ItemStack s,TooltipContext c,java.util.List<net.minecraft.network.chat.Component> lines,TooltipFlag f){lines.add(net.minecraft.network.chat.Component.literal(ElectricItem.manager.getToolTip(s)));}
}
