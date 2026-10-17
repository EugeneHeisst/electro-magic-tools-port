package emt.client;

import emt.EMT;
import emt.init.EMTSounds;
import emt.item.tool.EMTElectricTool;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;

/** Original local main-hand saw idle loop and stop sample, tied to the actual client player lifetime. */
@EventBusSubscriber(modid=EMT.MODID,value=Dist.CLIENT)
public final class ChainsawSounds {
    private static Idle loop;

    @SubscribeEvent
    static void tick(ClientTickEvent.Post event) { update(Minecraft.getInstance()); }

    @SubscribeEvent
    public static void logout(ClientPlayerNetworkEvent.LoggingOut event) { stop(Minecraft.getInstance(),false); }

    public static void update(Minecraft mc) {
        var player=mc.player;
        if(player==null||mc.level==null||mc.isPaused()||!player.isAlive()||player.isRemoved()) {
            stop(mc,false);
            return;
        }
        var item=player.getMainHandItem().getItem();
        boolean saw=isSaw(item);
        if(loop!=null&&(loop.owner!=player||loop.owner.level()!=mc.level||loop.item!=item))stop(mc,loop.owner==player&&loop.owner.level()==mc.level);
        if(!saw)return;
        // A reload may discard native channels while the held item stays the same. Muting needs no retries.
        if(loop!=null&&(loop.isStopped()||mc.options.getSoundSourceVolume(SoundSource.MASTER)>0&&mc.options.getSoundSourceVolume(SoundSource.PLAYERS)>0&&!mc.getSoundManager().isActive(loop)))stop(mc,false);
        if(loop==null) {
            loop=new Idle(player,item);
            mc.getSoundManager().play(loop);
        }
        loop.follow();
    }

    private static boolean isSaw(Item item) {
        return item instanceof EMTElectricTool tool&&(tool.kind==EMTElectricTool.Kind.DIAMOND_CHAINSAW||tool.kind==EMTElectricTool.Kind.THAUMIUM_CHAINSAW||tool.kind==EMTElectricTool.Kind.STREAM_CHAINSAW);
    }

    private static void stop(Minecraft mc,boolean sample) {
        if(loop==null)return;
        var old=loop;
        loop=null;
        old.end();
        mc.getSoundManager().stop(old);
        if(sample)mc.getSoundManager().play(new SimpleSoundInstance(EMTSounds.CHAINSAW_STOP.get(),SoundSource.PLAYERS,1.2f,1,SoundInstance.createUnseededRandom(),old.owner.getX(),old.owner.getEyeY(),old.owner.getZ()));
    }

    private static final class Idle extends AbstractTickableSoundInstance {
        final LocalPlayer owner;
        final Item item;

        Idle(LocalPlayer owner,Item item) {
            super(EMTSounds.CHAINSAW_IDLE.get(),SoundSource.PLAYERS,SoundInstance.createUnseededRandom());
            this.owner=owner;
            this.item=item;
            looping=true;
            delay=0;
            volume=1.2f;
            follow();
        }

        void follow() { x=owner.getX();y=owner.getEyeY();z=owner.getZ(); }
        void end() { stop(); }

        @Override
        public void tick() {
            var mc=Minecraft.getInstance();
            if(mc.player!=owner||mc.level!=owner.level()||mc.isPaused()||!owner.isAlive()||owner.isRemoved()||owner.getMainHandItem().getItem()!=item)stop();
            else follow();
        }
    }
}
