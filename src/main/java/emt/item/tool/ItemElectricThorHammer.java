package emt.item.tool;
import emt.entity.*;
import emt.util.*;
import ic2.api.item.*;
import ic2.core.item.ElectricItemManager;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import java.util.*;
public class ItemElectricThorHammer extends SwordItem implements IElectricItem {
 public ItemElectricThorHammer(){super(new HammerTier(27),new Properties().stacksTo(1).setNoRepair().rarity(Rarity.EPIC).attributes(SwordItem.createAttributes(Tiers.DIAMOND,3,-2.4f)));}
 @Override public boolean canProvideEnergy(ItemStack s){return false;}@Override public double getMaxCharge(ItemStack s){return 1000000;}@Override public int getTier(ItemStack s){return 2;}@Override public double getTransferLimit(ItemStack s){return 100;}
 @Override public boolean hurtEnemy(ItemStack s,LivingEntity target,LivingEntity attacker){if(attacker instanceof Player p&&ElectricItem.manager.use(s,5000,attacker))target.hurt(p.damageSources().playerAttack(p),19);return false;}
 @Override public void postHurtEnemy(ItemStack s,LivingEntity target,LivingEntity attacker){}
 @Override public boolean mineBlock(ItemStack s,Level l,net.minecraft.world.level.block.state.BlockState state,net.minecraft.core.BlockPos pos,LivingEntity miner){return true;}
 @Override public boolean isBarVisible(ItemStack s){return true;}@Override public int getBarWidth(ItemStack s){return Math.round(13f*(float)Mth.clamp(ElectricItemManager.getRawCharge(s)/getMaxCharge(s),0,1));}@Override public int getBarColor(ItemStack s){return Mth.hsvToRgb((float)Mth.clamp(ElectricItemManager.getRawCharge(s)/getMaxCharge(s),0,1)/3,1,1);}
 @Override public int getEnchantmentValue(ItemStack s){return EMTConfig.ENCHANTING.get()?4:0;}@Override public boolean isEnchantable(ItemStack s){return EMTConfig.ENCHANTING.get();}@Override public boolean isBookEnchantable(ItemStack s,ItemStack b){return EMTConfig.ENCHANTING.get();}
 @Override public InteractionResultHolder<ItemStack> use(Level l,Player p,InteractionHand hand){var s=p.getItemInHand(hand);if(p.isCreative())return InteractionResultHolder.pass(s);p.swing(hand);if(l.isClientSide)return InteractionResultHolder.success(s);if(!ElectricItem.manager.canUse(s,75000)){Lightning.strike(l,p,p.position());p.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.HARM,1,1));return InteractionResultHolder.success(s);}ElectricItem.manager.use(s,75000,null);for(var offset:stormOffsets())Lightning.strike(l,p,p.position().add(offset));for(var offset:new Vec3[]{new Vec3(8,2,-8),new Vec3(-8,2,8),new Vec3(-8,2,-8),new Vec3(8,2,8)}){var archer=new EntityArcher(EMTEntities.ARCHER.get(),l);archer.setPos(p.position().add(offset));l.addFreshEntity(archer);}return InteractionResultHolder.success(s);}
 public static List<Vec3> stormOffsets(){var list=new ArrayList<Vec3>();for(int[] v:new int[][]{{8,-8},{-8,8},{-8,-8},{8,8},{8,0},{-8,0},{0,-8},{0,8}})list.add(new Vec3(v[0],0,v[1]));for(int i=1;i<=4;i++){list.add(new Vec3(i%2==1?8:-8,0,i));list.add(new Vec3(i%2==1?8:-8,0,-i));list.add(new Vec3(i,0,i%2==1?8:-8));list.add(new Vec3(-i,0,i%2==1?8:-8));}return List.copyOf(list);}
 @Override public void appendHoverText(ItemStack s,TooltipContext ctx,List<Component> lines,TooltipFlag f){var eu=ElectricItem.manager.getToolTip(s);if(eu!=null)lines.add(Component.literal(eu));lines.add(Component.translatable("tooltip.EMT.hammer.electric"));}
}
