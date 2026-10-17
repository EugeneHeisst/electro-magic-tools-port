package emt.client;

import emt.EMT;
import emt.init.EMTSounds;
import emt.item.armor.ItemInfusedChestplate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;

/**
 * Original infused chestplate jetpack audio: the local player's attached jetpack loops IC2's JetpackLoop while jetpack()
 * actually flies, and the source is removed (no stop sample) the first tick it does not. Only the local player, only the client.
 * The chestplate reports each armour tick; a tick without a report (chestplate removed, pause, death) counts as not flying.
 */
@EventBusSubscriber(modid=EMT.MODID,value=Dist.CLIENT)
public final class JetpackSounds {
    private static Loop loop;
    private static Player reported;
    private static boolean flying;

    private JetpackSounds() {}

    /** Called from the chestplate's client armour tick with the real result of jetpack(). */
    public static void report(Player player,boolean used) {
        if(player!=Minecraft.getInstance().player)return;
        reported=player;
        flying=used;
    }

    public static void update(Minecraft mc) {
        var player=mc.player;
        boolean used=player!=null&&reported==player&&flying&&mc.level!=null&&!mc.isPaused()&&player.isAlive()&&!player.isRemoved();
        reported=null;
        flying=false;
        if(loop!=null&&(loop.owner!=player||loop.isStopped()))stop(mc);
        if(!used) {
            stop(mc);
            return;
        }
        if(loop==null) {
            loop=new Loop(player);
            mc.getSoundManager().play(loop);
        }
        loop.follow();
    }

    public static void stop(Minecraft mc) {
        if(loop==null)return;
        var old=loop;
        loop=null;
        old.end();
        mc.getSoundManager().stop(old);
    }

    @SubscribeEvent static void tick(ClientTickEvent.Post event) { update(Minecraft.getInstance()); }

    @SubscribeEvent public static void logout(ClientPlayerNetworkEvent.LoggingOut event) { reported=null;flying=false;stop(Minecraft.getInstance()); }

    @EventBusSubscriber(modid=EMT.MODID,value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
    static final class Setup {
        @SubscribeEvent static void setup(FMLClientSetupEvent event) { ItemInfusedChestplate.jetpackUse=JetpackSounds::report; }
    }

    private static final class Loop extends AbstractTickableSoundInstance {
        final LocalPlayer owner;

        Loop(LocalPlayer owner) {
            super(EMTSounds.JETPACK_LOOP.get(),SoundSource.PLAYERS,SoundInstance.createUnseededRandom());
            this.owner=owner;
            looping=true;
            delay=0;
            follow();
        }

        // PositionSpec.Backpack: at the player's back, i.e. the body position
        void follow() { x=owner.getX();y=owner.getY()+owner.getBbHeight()*.5;z=owner.getZ(); }
        void end() { stop(); }

        @Override
        public void tick() {
            var mc=Minecraft.getInstance();
            if(mc.player!=owner||mc.level!=owner.level()||mc.isPaused()||!owner.isAlive()||owner.isRemoved())stop();
            else follow();
        }
    }
}
