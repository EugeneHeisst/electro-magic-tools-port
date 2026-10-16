package emt.util;
import emt.EMT;
import emt.item.armor.ItemInfusedChestplate;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.EquipmentSlot;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
@EventBusSubscriber(modid=EMT.MODID,bus=EventBusSubscriber.Bus.MOD)
public record DetachPayload() implements CustomPacketPayload {
 public static final Type<DetachPayload> TYPE=new Type<>(EMT.id("detach"));public static final StreamCodec<FriendlyByteBuf,DetachPayload> CODEC=StreamCodec.unit(new DetachPayload());
 public Type<? extends CustomPacketPayload> type(){return TYPE;}
 @SubscribeEvent static void register(RegisterPayloadHandlersEvent e){e.registrar("1").playToServer(TYPE,CODEC,(p,ctx)->ctx.enqueueWork(()->{var player=ctx.player();var chest=player.getItemBySlot(EquipmentSlot.CHEST);if(chest.getItem() instanceof ItemInfusedChestplate)ItemInfusedChestplate.detach(player,chest);}));}
}
