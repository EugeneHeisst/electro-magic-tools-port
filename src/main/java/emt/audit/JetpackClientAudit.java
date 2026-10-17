package emt.audit;

import com.mojang.authlib.GameProfile;
import emt.EMT;
import emt.client.JetpackSounds;
import emt.init.EMTItems;
import emt.init.EMTSounds;
import emt.item.armor.EMTArmor;
import ic2.api.item.ElectricItem;
import ic2.core.ref.Ic2Items;
import ic2.core.util.Keyboard;
import java.util.ArrayList;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.sound.PlaySoundEvent;
import net.neoforged.neoforge.common.NeoForge;

/**
 * One synchronous client scene for the infused chestplate's attached-jetpack loop: the real chestplate armour tick on the real
 * local player, the real (muted) native sound manager; equipment, keys, position, motion and pause are restored afterwards.
 */
public final class JetpackClientAudit {
    private static void check(String name,boolean passed) {
        if(!passed)throw new IllegalStateException("EMT jetpack sound: "+name);
        EMT.LOG.info("[emt-client-audit] PASS jetpack sound {}",name);
    }

    private static TickableSoundInstance loop() throws ReflectiveOperationException {
        var field=JetpackSounds.class.getDeclaredField("loop");
        field.setAccessible(true);
        return (TickableSoundInstance)field.get(null);
    }

    private static ItemStack chest(Minecraft mc,int wing,double charge) {
        var chest=new ItemStack(EMTItems.ARMOR.get("infused_quantum_chestplate").get());
        var tag=EMTArmor.tag(chest);
        if(wing==1)tag.put("attachment",new ItemStack(Ic2Items.ELECTRIC_JETPACK.get()).save(mc.player.registryAccess()));
        if(wing==2)tag.put("attachment",new ItemStack(EMTItems.ARMOR.get("thaumium_wings").get()).save(mc.player.registryAccess()));
        if(wing>0)tag.putInt("wing",wing);
        EMTArmor.save(chest,tag);
        ElectricItem.manager.charge(chest,charge,Integer.MAX_VALUE,true,false);
        return chest;
    }

    private static void hover(ItemStack chest,boolean on) { var tag=EMTArmor.tag(chest);tag.putBoolean("hoverMode",on);EMTArmor.save(chest,tag); }

    /** One client tick as the game runs it: the worn chestplate's armour tick, then the client tick handler. */
    private static void tick(Minecraft mc,boolean worn) {
        var player=mc.player;
        var chest=player.getItemBySlot(EquipmentSlot.CHEST);
        // exactly what IC2 ArmorEvents does on PlayerTickEvent.Post for a worn IC2 armour piece
        if(worn&&chest.getItem() instanceof ic2.core.item.armor.ItemArmorIC2 armor)armor.onArmorTick(player.level(),player,chest);
        JetpackSounds.update(mc);
    }

    private static void keys(Minecraft mc,boolean jump) { Keyboard.processKeyUpdate(mc.player,jump?1<<Keyboard.Key.jump.ordinal():0); }

    public static void run(Minecraft mc) {
        var player=mc.player;
        var oldChest=player.getItemBySlot(EquipmentSlot.CHEST);
        var position=player.position();
        var motion=player.getDeltaMovement();
        var queued=new ArrayList<SoundInstance>();
        Consumer<PlaySoundEvent> listener=event -> {
            var sound=event.getOriginalSound();
            if(sound!=null&&sound.getLocation().equals(EMTSounds.JETPACK_LOOP.getId()))queued.add(sound);
        };
        java.lang.reflect.Field pause=null;
        boolean previousPause=mc.isPaused();
        NeoForge.EVENT_BUS.addListener(listener);
        try {
            pause=Minecraft.class.getDeclaredField("pause");
            pause.setAccessible(true);
            pause.setBoolean(mc,false);
            JetpackSounds.stop(mc);
            JetpackSounds.update(mc);
            queued.clear();

            // 1. flying: exactly one native loop that stays and follows the player
            var chest=chest(mc,1,100000);
            player.setItemSlot(EquipmentSlot.CHEST,chest);
            keys(mc,true);
            tick(mc,true);
            var first=loop();
            check("jump with a charged attached jetpack queues one native looping instance",first!=null&&first.isLooping()&&queued.size()==1&&queued.getFirst()==first);
            check("loop resolves to the packaged original JetpackLoop Ogg",first.resolve(mc.getSoundManager())!=null&&first.getSound()!=SoundManager.EMPTY_SOUND&&mc.getResourceManager().getResource(first.getSound().getPath()).isPresent());
            double charge=ElectricItem.manager.getCharge(player.getItemBySlot(EquipmentSlot.CHEST));
            for(int i=0;i<5;i++)tick(mc,true);
            check("continued flight keeps the same instance without restarting",loop()==first&&!first.isStopped()&&queued.size()==1);
            check("client audio path spends no EU (the server pays)",ElectricItem.manager.getCharge(player.getItemBySlot(EquipmentSlot.CHEST))==charge);
            player.setPos(position.add(2,1,0));
            first.tick();
            check("loop follows the local player",Math.abs(first.getX()-player.getX())<1e-5&&Math.abs(first.getZ()-player.getZ())<1e-5&&Math.abs(first.getY()-player.getY()-player.getBbHeight()*.5)<1e-5);
            player.setPos(position);

            // 2. release jump without hover: removed, no stop sample; flying again makes a fresh instance
            keys(mc,false);
            player.setDeltaMovement(Vec3.ZERO);
            tick(mc,true);
            check("releasing jump stops and drops the loop without any other sample",first.isStopped()&&loop()==null&&queued.size()==1);
            keys(mc,true);
            tick(mc,true);
            var second=loop();
            check("next successful use starts a new instance",second!=null&&second!=first&&queued.size()==2);
            keys(mc,false);
            tick(mc,true);

            // 3. hover: sound while it really slows a fall, none when the charge cannot pay
            hover(chest,true);
            player.setDeltaMovement(0,-.5,0);
            tick(mc,true);
            var hovering=loop();
            check("hover braking a fall with enough EU plays the loop",hovering!=null&&queued.size()==3);
            ElectricItem.manager.discharge(chest,Double.MAX_VALUE,Integer.MAX_VALUE,true,false,false);
            ElectricItem.manager.charge(chest,9,Integer.MAX_VALUE,true,false);
            player.setDeltaMovement(0,-.5,0);
            tick(mc,true);
            check("hover with 9 EU (needs 10) is silent",hovering.isStopped()&&loop()==null&&queued.size()==3);
            hover(chest,false);
            ElectricItem.manager.discharge(chest,Double.MAX_VALUE,Integer.MAX_VALUE,true,false,false);
            ElectricItem.manager.charge(chest,7,Integer.MAX_VALUE,true,false);
            keys(mc,true);
            tick(mc,true);
            check("normal flight with 7 EU (needs 8) is silent",loop()==null&&queued.size()==3);

            // 4. no module, wings, not worn, taken off mid-flight
            player.setItemSlot(EquipmentSlot.CHEST,chest(mc,0,100000));
            tick(mc,true);
            check("chestplate without a module is silent",loop()==null&&queued.size()==3);
            player.setItemSlot(EquipmentSlot.CHEST,chest(mc,2,100000));
            player.setDeltaMovement(Vec3.ZERO);
            tick(mc,true);
            check("attached wings never start the jetpack loop",loop()==null&&queued.size()==3);
            player.setItemSlot(EquipmentSlot.CHEST,chest(mc,1,100000));
            tick(mc,true);
            var worn=loop();
            check("worn again with a jetpack plays",worn!=null&&queued.size()==4);
            var off=player.getItemBySlot(EquipmentSlot.CHEST);
            player.setItemSlot(EquipmentSlot.CHEST,ItemStack.EMPTY);
            var slot9=player.getInventory().getItem(9);
            player.getInventory().setItem(9,off);
            tick(mc,false);
            check("taking the chestplate off mid-flight stops the loop",worn.isStopped()&&loop()==null&&queued.size()==4);
            tick(mc,false);
            check("a chestplate in the inventory does not play",loop()==null&&queued.size()==4&&!player.getInventory().getItem(9).isEmpty());
            player.getInventory().setItem(9,slot9);
            player.setItemSlot(EquipmentSlot.CHEST,off);

            // 5. pause, logout, other players
            tick(mc,true);
            var beforePause=loop();
            pause.setBoolean(mc,true);
            tick(mc,true);
            check("pause stops the loop",beforePause!=null&&beforePause.isStopped()&&loop()==null);
            pause.setBoolean(mc,false);
            tick(mc,true);
            var resumed=loop();
            check("resuming flight starts a fresh instance",resumed!=null&&resumed!=beforePause);
            JetpackSounds.logout(new ClientPlayerNetworkEvent.LoggingOut(mc.gameMode,player,null));
            check("logout stops the loop and keeps no old owner",resumed.isStopped()&&loop()==null);
            keys(mc,false);
            var other=new RemotePlayer(mc.level,new GameProfile(UUID.randomUUID(),"JetpackOther"));
            int count=queued.size();
            JetpackSounds.report(other,true);
            JetpackSounds.update(mc);
            check("another player's jetpack never creates the local source",loop()==null&&queued.size()==count);
        } catch(ReflectiveOperationException failure) {
            throw new RuntimeException(failure);
        } finally {
            keys(mc,false);
            JetpackSounds.stop(mc);
            NeoForge.EVENT_BUS.unregister(listener);
            player.setItemSlot(EquipmentSlot.CHEST,oldChest);
            player.setPos(position);
            player.setDeltaMovement(motion);
            if(pause!=null)try {pause.setBoolean(mc,previousPause);}catch(IllegalAccessException failure) {throw new RuntimeException(failure);}
            JetpackSounds.update(mc);
        }
    }
}
