package emt.mixin;
import emt.item.armor.EMTArmor;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Mixin(Player.class)
abstract class TravellerAirSpeedMixin {
 @Inject(method="getFlyingSpeed",at=@At("RETURN"),cancellable=true)
 private void emt$airSpeed(CallbackInfoReturnable<Float> ci){var speed=EMTArmor.airSpeed((Player)(Object)this);if(speed!=null)ci.setReturnValue(speed);}
}
