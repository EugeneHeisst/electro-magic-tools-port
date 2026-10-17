package emt.init;

import emt.EMT;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.RegisterEvent;

@EventBusSubscriber(modid=EMT.MODID,bus=EventBusSubscriber.Bus.MOD)
public final class EMTSounds {
    public static final DeferredHolder<SoundEvent,SoundEvent> CHAINSAW_IDLE=DeferredHolder.create(Registries.SOUND_EVENT,EMT.id("chainsaw.idle"));
    public static final DeferredHolder<SoundEvent,SoundEvent> CHAINSAW_STOP=DeferredHolder.create(Registries.SOUND_EVENT,EMT.id("chainsaw.stop"));
    /** IC2's original Tools/Jetpack/JetpackLoop.ogg, played by the jetpack attached to the infused quantum chestplate. */
    public static final DeferredHolder<SoundEvent,SoundEvent> JETPACK_LOOP=DeferredHolder.create(Registries.SOUND_EVENT,EMT.id("jetpack.loop"));

    @SubscribeEvent
    static void register(RegisterEvent event) {
        event.register(Registries.SOUND_EVENT,helper -> {
            helper.register(CHAINSAW_IDLE.getId(),SoundEvent.createVariableRangeEvent(CHAINSAW_IDLE.getId()));
            helper.register(CHAINSAW_STOP.getId(),SoundEvent.createVariableRangeEvent(CHAINSAW_STOP.getId()));
            helper.register(JETPACK_LOOP.getId(),SoundEvent.createVariableRangeEvent(JETPACK_LOOP.getId()));
        });
    }
}
