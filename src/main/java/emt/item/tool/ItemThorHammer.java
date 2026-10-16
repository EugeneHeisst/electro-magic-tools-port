package emt.item.tool;
import emt.util.Lightning;
import dev.eugheist.tc4modern.TC4Modern;
import net.minecraft.network.chat.Component;
import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
import thaumcraft.api.IRepairable;
import java.util.*;
/** Original diamond-sword hit plus its follow-up hammer damage; keeps vanilla invulnerability/counter-hit semantics. */
public class ItemThorHammer extends SwordItem implements IRepairable {
 public final boolean broken;
 public ItemThorHammer(boolean broken){super(new HammerTier(broken?1000:2000),new Properties().rarity(Rarity.RARE).attributes(SwordItem.createAttributes(Tiers.DIAMOND,3,-2.4f)));this.broken=broken;}
 @Override public boolean hurtEnemy(ItemStack s,LivingEntity target,LivingEntity attacker){if(attacker instanceof Player p)target.hurt(p.damageSources().playerAttack(p),broken?2:12);return true;}
 @Override public InteractionResultHolder<ItemStack> use(Level l,Player p,InteractionHand hand){var s=p.getItemInHand(hand);p.swing(hand);if(l.isClientSide)return InteractionResultHolder.success(s);boolean used=false;if(broken){for(var offset:new Vec3[]{new Vec3(8,0,-8),new Vec3(-8,0,8),new Vec3(-8,0,-8),new Vec3(8,0,8),new Vec3(0,4,0),new Vec3(0,8,0)}){var orb=new thaumcraft.common.entities.projectile.EntityAlumentum(TC4Modern.ALUMENTUM_PROJECTILE.get(),l);orb.setPos(p.position().add(offset));l.addFreshEntity(orb);}used=true;}else{var hit=Lightning.trace(p);if(hit.getType()!=HitResult.Type.MISS){Lightning.strike(l,p,Vec3.atLowerCornerOf(hit.getBlockPos()));used=true;}}if(used&&!p.isCreative())s.hurtAndBreak(20,p,hand==InteractionHand.MAIN_HAND?EquipmentSlot.MAINHAND:EquipmentSlot.OFFHAND);return InteractionResultHolder.success(s);}
 @Override public void appendHoverText(ItemStack s,TooltipContext ctx,List<Component> lines,TooltipFlag f){super.appendHoverText(s,ctx,lines,f);lines.add(Component.translatable(broken?"tooltip.EMT.hammer.broken.thor":"tooltip.EMT.hammer.broken.The Hammer of Thor"));if(broken){lines.add(Component.translatable("tooltip.EMT.hammer.broken.danger"));lines.add(Component.translatable("tooltip.EMT.hammer.broken.plsNoRightClick"));}}
}
