package emt.util;
import emt.EMT;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
@EventBusSubscriber(modid=EMT.MODID,bus=EventBusSubscriber.Bus.MOD)
public record OreScanPayload(BlockPos pos) implements CustomPacketPayload {
 public static final Type<OreScanPayload> TYPE=new Type<>(EMT.id("ore_scan"));public static final StreamCodec<FriendlyByteBuf,OreScanPayload> CODEC=StreamCodec.composite(BlockPos.STREAM_CODEC,OreScanPayload::pos,OreScanPayload::new);
 public Type<? extends CustomPacketPayload> type(){return TYPE;}
 @SubscribeEvent static void register(RegisterPayloadHandlersEvent e){e.registrar("1").playToClient(TYPE,CODEC,(data,ctx)->ctx.enqueueWork(()->dev.eugheist.tc4modern.ElementalToolsClient.startScan(data.pos)));}
}
