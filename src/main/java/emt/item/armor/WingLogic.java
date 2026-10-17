package emt.item.armor;
import emt.util.EMTConfig;
import ic2.api.item.ElectricItem;
import ic2.core.util.Keyboard;
import ic2.core.util.Util;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
/** Original charge-and-release flap and gliding, with per-stack state and guarded electric payments. */
public final class WingLogic {
 public static final float[] THRUST={.11f,.15f,.25f,.33f},DRAG={.9f,.7f,.6f,.5f},GLIDE={.9f,.5f,.3f,.2f},FALL={.75f,.5f,.2f,0};
 public static boolean tick(Player p,ItemStack s,int kind,boolean infused){var tag=EMTArmor.tag(s);boolean jump=Keyboard.isJumpKeyDown(p),held=tag.getBoolean("isHolding");int counter=tag.getInt("flapTicks");boolean flew=false;var motion=p.getDeltaMovement();tag.putBoolean("isJumping",jump);
  if(jump){tag.putBoolean("isHolding",true);counter=Math.min(7,counter+1);}else if(held){tag.putBoolean("isHolding",false);double cost=kind<2?0:THRUST[kind]*counter*10*(kind==2?5:7);boolean paid=kind<2||p.level().isClientSide?kind<2||ElectricItem.manager.canUse(s,cost):ElectricItem.manager.use(s,cost,null);if(paid&&counter>0){double x=motion.x,z=motion.z;if(Math.abs(x)<.5&&Math.abs(z)<.5){x/=DRAG[kind];z/=DRAG[kind];}motion=new Vec3(x,THRUST[kind]*counter,z);flew=true;if(!p.level().isClientSide){p.level().playSound(null,p.blockPosition(),net.minecraft.sounds.SoundEvents.FIRECHARGE_USE,net.minecraft.sounds.SoundSource.PLAYERS,1,1);((net.minecraft.server.level.ServerLevel)p.level()).sendParticles(net.minecraft.core.particles.ParticleTypes.CLOUD,p.getX(),p.getY()-.5,p.getZ(),4,.5,0,.5,.01);}}counter=0;}
  if(jump&&!p.onGround()&&motion.y<0&&(kind<2||ElectricItem.manager.getCharge(s)>0)){motion=motion.multiply(1,GLIDE[kind],1);flew=true;}
  double pull=infused?.05:.03;if(!p.isCreative()){if(p.isInWater())motion=motion.add(0,-pull,0);if(EMTConfig.IMPACT_RAIN.get()&&p.level().isRainingAt(p.blockPosition()))motion=motion.add(0,-pull,0);}
  if(p.isShiftKeyDown()&&!p.onGround()&&motion.y<0)motion=motion.multiply(1,.6,1);
  if(flew){p.setDeltaMovement(motion);if(infused)p.fallDistance=0;else if(motion.y>0&&p.fallDistance>0)p.fallDistance=Math.max(0,p.fallDistance-(float)motion.y);Util.resetPlayerInAirTime(p);}else if(!motion.equals(p.getDeltaMovement()))p.setDeltaMovement(motion);
  tag.putInt("flapTicks",counter);EMTArmor.save(s,tag);return flew;
 }
 public static int kind(ItemStack s){return s.getItem() instanceof ItemDurableWings w?w.reinforced?1:0:s.getItem() instanceof EMTArmor a&&a.wings()?a.kind==EMTArmor.Kind.NANO_WINGS?2:3:-1;}
}
