package emt.audit;

import com.mojang.authlib.GameProfile;
import emt.init.EMTItems;
import emt.item.armor.ItemInfusedChestplate;
import emt.util.EMTConfig;
import ic2.core.item.armor.ItemArmorIC2;
import ic2.core.util.Keyboard;
import java.util.LinkedHashMap;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ServerLevelData;
import net.neoforged.neoforge.common.util.FakePlayerFactory;

/** Uses real water/AABB overlap at a shore, with dry feet-column and head exposed to native rain. */
public final class WingEnvironmentAudit {
    private static void check(String name,boolean passed,Object detail) {
        ServerAudit.check("wing environment: "+name,passed,detail);
    }

    public static void run(ServerLevel level,BlockPos anchor) {
        var pos=anchor.offset(112,0,112);
        for(int x=-1;x<=1;x++)for(int z=-1;z<=1;z++)level.getChunk((pos.getX()>>4)+x,(pos.getZ()>>4)+z);
        var original=new LinkedHashMap<BlockPos,BlockState>();
        for(var at:BlockPos.betweenClosed(pos.offset(-1,-1,-1),pos.offset(1,2,1)))original.put(at.immutable(),level.getBlockState(at));
        var data=(ServerLevelData)level.getLevelData();
        int clearTime=data.getClearWeatherTime(),rainTime=data.getRainTime(),thunderTime=data.getThunderTime();
        boolean raining=data.isRaining(),thundering=data.isThundering(),rainImpact=EMTConfig.IMPACT_RAIN.get();
        float rainStrength=level.getRainLevel(1);
        var player=new ServerPlayer(level.getServer(),level,new GameProfile(UUID.randomUUID(),"EMTWingWaterAudit"),ClientInformation.createDefault());
        player.connection=FakePlayerFactory.getMinecraft(level).connection;
        player.setGameMode(GameType.SURVIVAL);
        player.setOnGround(false);
        player.setShiftKeyDown(false);
        Keyboard.processKeyUpdate(player,0);
        try {
            for(var at:original.keySet())level.setBlock(at,at.getY()==pos.getY()-1?Blocks.STONE.defaultBlockState():Blocks.AIR.defaultBlockState(),3);
            level.setBlock(pos.west(),Blocks.WATER.defaultBlockState(),3);
            EMTConfig.IMPACT_RAIN.set(true);
            var waterUpdate=Entity.class.getDeclaredMethod("updateInWaterStateAndDoFluidPushing");
            waterUpdate.setAccessible(true);
            for(boolean infused:new boolean[]{false,true}) {
                var wings=new ItemStack(EMTItems.ARMOR.get(infused?"infused_quantum_chestplate":"thaumium_wings").get());
                if(infused) {
                    player.setItemInHand(InteractionHand.MAIN_HAND,new ItemStack(EMTItems.ARMOR.get("thaumium_wings").get()));
                    if(!ItemInfusedChestplate.attach(player,wings))throw new IllegalStateException("Wing environment attachment");
                }
                player.setItemSlot(EquipmentSlot.CHEST,wings);
                double pull=infused?.05:.03;
                for(int environment=0;environment<3;environment++) {
                    boolean rain=environment!=0,water=environment!=1;
                    level.setWeatherParameters(rain?0:6000,rain?6000:0,rain,false);
                    level.setRainLevel(rain?1:0);
                    // Water-only: feet inside water. Both: just the AABB enters water across the shore.
                    double x=environment==0?pos.getX()-.5:environment==1?pos.getX()+.5:pos.getX()+.05;
                    player.setPos(x,pos.getY()+.1,pos.getZ()+.5);
                    waterUpdate.invoke(player);
                    boolean nativeWater=player.isInWater(),nativeRain=level.isRainingAt(player.blockPosition());
                    boolean dryHead=!level.getFluidState(BlockPos.containing(player.getEyePosition())).is(FluidTags.WATER);
                    check((infused?"infused ":"standalone ")+"native water/rain fixture "+environment,nativeWater==water&&nativeRain==rain&&dryHead,"feet="+player.blockPosition()+" water="+nativeWater+" rain="+nativeRain+" dryHead="+dryHead);
                    player.setDeltaMovement(0,-.4,0);
                    ((ItemArmorIC2)wings.getItem()).onArmorTick(level,player,wings);
                    double expected=-.4-pull*((water?1:0)+(rain?1:0));
                    check((infused?"infused ":"standalone ")+"separate water/rain pull "+environment,Math.abs(player.getDeltaMovement().y-expected)<.000001,player.getDeltaMovement().y+" expected "+expected);
                }
                EMTConfig.IMPACT_RAIN.set(false);
                player.setDeltaMovement(0,-.4,0);
                ((ItemArmorIC2)wings.getItem()).onArmorTick(level,player,wings);
                check((infused?"infused ":"standalone ")+"rain config leaves independent water pull",Math.abs(player.getDeltaMovement().y-(-.4-pull))<.000001,player.getDeltaMovement().y);
                EMTConfig.IMPACT_RAIN.set(true);
                player.setGameMode(GameType.CREATIVE);
                player.setDeltaMovement(0,-.4,0);
                ((ItemArmorIC2)wings.getItem()).onArmorTick(level,player,wings);
                check((infused?"infused ":"standalone ")+"creative ignores both environmental penalties",Math.abs(player.getDeltaMovement().y+.4)<.000001,player.getDeltaMovement().y);
                player.setGameMode(GameType.SURVIVAL);
            }
        } catch(ReflectiveOperationException failure) {
            throw new RuntimeException(failure);
        } finally {
            EMTConfig.IMPACT_RAIN.set(rainImpact);
            level.setWeatherParameters(clearTime,rainTime,raining,thundering);
            data.setThunderTime(thunderTime);
            level.setRainLevel(rainStrength);
            original.forEach((at,state)->level.setBlock(at,state,3));
            player.setItemSlot(EquipmentSlot.CHEST,ItemStack.EMPTY);
            player.getInventory().clearContent();
        }
    }
}
