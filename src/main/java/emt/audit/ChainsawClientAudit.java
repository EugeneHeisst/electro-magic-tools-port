package emt.audit;

import emt.EMT;
import emt.client.ChainsawSounds;
import emt.init.EMTItems;
import emt.init.EMTSounds;
import java.util.ArrayList;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.sound.PlaySoundEvent;
import net.neoforged.neoforge.common.NeoForge;

/** One synchronous client scene, using the real muted native sound manager and restoring inventory/pause state. */
public final class ChainsawClientAudit {
    private static void check(String name,boolean passed) {
        if(!passed)throw new IllegalStateException("EMT chainsaw sound: "+name);
        EMT.LOG.info("[emt-client-audit] PASS chainsaw sound {}",name);
    }

    private static TickableSoundInstance loop() throws ReflectiveOperationException {
        var field=ChainsawSounds.class.getDeclaredField("loop");
        field.setAccessible(true);
        return (TickableSoundInstance)field.get(null);
    }

    public static void run(Minecraft mc) {
        var player=mc.player;
        var inventory=player.getInventory();
        int selected=inventory.selected;
        var slot0=inventory.getItem(0);
        var slot1=inventory.getItem(1);
        var offhand=player.getOffhandItem();
        var position=player.position();
        var queued=new ArrayList<SoundInstance>();
        Consumer<PlaySoundEvent> listener=event -> {
            var sound=event.getOriginalSound();
            if(sound.getLocation().getNamespace().equals(EMT.MODID))queued.add(sound);
        };
        java.lang.reflect.Field pause=null;
        boolean previousPause=mc.isPaused();
        NeoForge.EVENT_BUS.addListener(listener);
        try {
            pause=Minecraft.class.getDeclaredField("pause");
            pause.setAccessible(true);
            pause.setBoolean(mc,false);
            ChainsawSounds.logout(new ClientPlayerNetworkEvent.LoggingOut(mc.gameMode,player,null));
            queued.clear();
            inventory.selected=0;
            inventory.setItem(0,new ItemStack(EMTItems.TOOLS.get("diamond_chainsaw").get()));
            ChainsawSounds.update(mc);
            var diamond=loop();
            check("empty selected diamond saw queues one native looping instance",diamond!=null&&diamond.isLooping()&&diamond.getLocation().equals(EMTSounds.CHAINSAW_IDLE.getId())&&queued.size()==1&&queued.getFirst()==diamond);
            var resolved=diamond.resolve(mc.getSoundManager());
            check("idle resolves to a real packaged original Ogg sample",resolved!=null&&diamond.getSound()!=SoundManager.EMPTY_SOUND&&mc.getResourceManager().getResource(diamond.getSound().getPath()).isPresent());
            ChainsawSounds.update(mc);
            inventory.setItem(0,inventory.getItem(0).copy());
            ChainsawSounds.update(mc);
            check("same item and stack replacement keep one loop without restarting",loop()==diamond&&queued.size()==1);
            player.setPos(position.add(1,0,0));
            diamond.tick();
            check("native loop follows local player movement",Math.abs(diamond.getX()-player.getX())<.00001&&Math.abs(diamond.getY()-player.getEyeY())<.00001);
            player.setPos(position);
            inventory.setItem(1,new ItemStack(EMTItems.TOOLS.get("stream_chainsaw").get()));
            inventory.selected=1;
            ChainsawSounds.update(mc);
            var stream=loop();
            check("hotbar switch stops diamond and queues stop plus stream idle",diamond.isStopped()&&stream!=null&&stream!=diamond&&queued.size()==3&&queued.get(1).getLocation().equals(EMTSounds.CHAINSAW_STOP.getId()));
            var stop=queued.get(1);
            check("stop resolves to the actual original stop Ogg",stop.resolve(mc.getSoundManager())!=null&&stop.getSound()!=SoundManager.EMPTY_SOUND&&mc.getResourceManager().getResource(stop.getSound().getPath()).isPresent());
            inventory.setItem(1,new ItemStack(EMTItems.TOOLS.get("thaumium_chainsaw").get()));
            ChainsawSounds.update(mc);
            var thaumium=loop();
            check("thaumium saw also replaces the native idle loop",stream.isStopped()&&thaumium!=null&&thaumium!=stream&&queued.size()==5);
            inventory.setItem(1,new ItemStack(Items.STICK));
            player.setItemInHand(InteractionHand.OFF_HAND,new ItemStack(EMTItems.TOOLS.get("stream_chainsaw").get()));
            ChainsawSounds.update(mc);
            check("saws in unselected inventory or offhand do not start a loop",thaumium.isStopped()&&loop()==null&&queued.size()==6&&queued.getLast().getLocation().equals(EMTSounds.CHAINSAW_STOP.getId()));
            inventory.selected=0;
            ChainsawSounds.update(mc);
            var beforePause=loop();
            int count=queued.size();
            pause.setBoolean(mc,true);
            ChainsawSounds.update(mc);
            check("pause immediately stops the loop without a stray stop sample",beforePause!=null&&beforePause.isStopped()&&loop()==null&&queued.size()==count);
            pause.setBoolean(mc,false);
            ChainsawSounds.update(mc);
            var resumed=loop();
            check("resume starts a fresh native loop",resumed!=null&&resumed!=beforePause&&queued.size()==count+1);
            ChainsawSounds.logout(new ClientPlayerNetworkEvent.LoggingOut(mc.gameMode,player,null));
            check("logout handler clears and stops the native loop",resumed.isStopped()&&loop()==null&&queued.size()==count+1);
        } catch(ReflectiveOperationException failure) {
            throw new RuntimeException(failure);
        } finally {
            ChainsawSounds.logout(new ClientPlayerNetworkEvent.LoggingOut(mc.gameMode,player,null));
            NeoForge.EVENT_BUS.unregister(listener);
            inventory.setItem(0,slot0);
            inventory.setItem(1,slot1);
            inventory.selected=selected;
            player.setItemInHand(InteractionHand.OFF_HAND,offhand);
            player.setPos(position);
            if(pause!=null)try {pause.setBoolean(mc,previousPause);}catch(IllegalAccessException failure) {throw new RuntimeException(failure);}
            ChainsawSounds.update(mc);
        }
    }
}
