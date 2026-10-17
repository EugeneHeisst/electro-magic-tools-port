package emt.item.armor;
import emt.EMT;
import emt.init.EMTArmorMaterials;
import ic2.core.item.armor.ItemArmorIC2;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import thaumcraft.api.*;
import thaumcraft.api.aspects.Aspect;
public class ItemDurableWings extends ItemArmorIC2 implements IVisDiscountGear,IRepairable {
 public final boolean reinforced;
 public ItemDurableWings(boolean reinforced){super(reinforced?dev.eugheist.tc4modern.ThaumiumContent.ARMOR:EMTArmorMaterials.FEATHER,ArmorItem.Type.CHESTPLATE,new Properties().durability(reinforced?250:120).rarity(Rarity.UNCOMMON));this.reinforced=reinforced;}
 @Override public int getVisDiscount(ItemStack s,Player p,Aspect a){return reinforced?4:0;}
 @Override public void appendHoverText(ItemStack s,TooltipContext c,java.util.List<net.minecraft.network.chat.Component> lines,TooltipFlag f){super.appendHoverText(s,c,lines,f);if(reinforced)lines.add(net.minecraft.network.chat.Component.translatable("tooltip.EMT.visDiscount").append(": 4%"));}
 @Override public boolean isMetalArmor(ItemStack s,Player p){return reinforced;}
 @Override public ResourceLocation getArmorTexture(ItemStack s,Entity e,EquipmentSlot slot,ArmorMaterial.Layer layer,boolean inner){return EMT.id("textures/models/"+(reinforced?"thaumiumwing":"featherwing")+".png");}
 @Override public void onArmorTick(Level l,Player p,ItemStack s){WingLogic.tick(p,s,reinforced?1:0,false);}
}
