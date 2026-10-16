package emt.item;
import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
public class ItemLightningSummoner extends Item {
 public ItemLightningSummoner(){super(new Item.Properties());}
 public InteractionResultHolder<ItemStack> use(Level l,Player p,InteractionHand h){p.startUsingItem(h);return InteractionResultHolder.consume(p.getItemInHand(h));}
 public int getUseDuration(ItemStack s,LivingEntity p){return 72000;}
 public UseAnim getUseAnimation(ItemStack s){return UseAnim.BOW;}
 public void onUseTick(Level l,LivingEntity entity,ItemStack s,int remaining){if(l.isClientSide||getUseDuration(s,entity)-remaining<40)return;var hit=entity.pick(5000,0,true);if(hit.getType()!=HitResult.Type.MISS){var bolt=EntityType.LIGHTNING_BOLT.create(l);if(bolt!=null){bolt.moveTo(hit.getLocation());if(entity instanceof net.minecraft.server.level.ServerPlayer player)bolt.setCause(player);l.addFreshEntity(bolt);if(!(entity instanceof Player p&&p.isCreative()))s.shrink(1);}}entity.stopUsingItem();}
}
