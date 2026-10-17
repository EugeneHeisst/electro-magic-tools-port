package emt.command;

import emt.EMT;
import emt.util.EMTConfig;
import java.math.BigDecimal;
import java.util.ArrayList;
import net.minecraft.commands.Commands;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.network.Filterable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.WrittenBookContent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import thaumcraft.api.aspects.Aspect;

/** The original unrestricted player command; all entries use the current configured EU value. */
@EventBusSubscriber(modid=EMT.MODID)
public final class CommandOutputs {
    @SubscribeEvent
    static void register(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("emt_outputs").executes(context -> {
            var player=context.getSource().getPlayerOrException();
            var book=createBook();
            if(!player.getInventory().add(book)) {
                var dropped=player.drop(book,false);
                if(dropped!=null) {
                    dropped.setNoPickUpDelay();
                    dropped.setTarget(player.getUUID());
                }
            }
            player.containerMenu.broadcastChanges();
            return 1;
        }));
    }

    public static ItemStack createBook() {
        var pages=new ArrayList<Filterable<Component>>();
        var page=Component.translatable("gui.EMT.book.aspect.output.essentia.eu").append("\n\n");
        int entries=0;
        for(var aspect:Aspect.aspects.values()) {
            // Five entries leave room for wrapped numbers and translated headings in the native book GUI.
            page.append(aspect.getTag()+"\n"+BigDecimal.valueOf(EMTConfig.essentiaOutput(aspect)).stripTrailingZeros().toPlainString()+" EU\n");
            if(++entries==5) {
                pages.add(Filterable.passThrough(page));
                page=Component.translatable("gui.EMT.book.aspect.output.essentia.eu").append("\n\n");
                entries=0;
            }
        }
        if(entries>0||pages.isEmpty())pages.add(Filterable.passThrough(page));
        String title=Component.translatable("gui.EMT.book.aspect.output.eu.title").getString();
        if(title.length()>WrittenBookContent.TITLE_MAX_LENGTH)title=title.substring(0,WrittenBookContent.TITLE_MAX_LENGTH);
        var book=new ItemStack(Items.WRITTEN_BOOK);
        book.set(DataComponents.WRITTEN_BOOK_CONTENT,new WrittenBookContent(Filterable.passThrough(title),"Tombenpotter",0,pages,true));
        return book;
    }
}
