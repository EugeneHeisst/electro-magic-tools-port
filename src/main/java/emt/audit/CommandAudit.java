package emt.audit;

import com.mojang.authlib.GameProfile;
import emt.util.EMTConfig;
import java.util.UUID;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import thaumcraft.api.aspects.Aspect;

public final class CommandAudit {
    private static void check(String name,boolean ok,Object detail) {
        ServerAudit.check("command: "+name,ok,detail);
    }

    public static void run(ServerLevel level,BlockPos anchor) {
        var player=FakePlayerFactory.get(level,new GameProfile(UUID.randomUUID(),"EMTOutputsAudit"));
        player.setPos(anchor.getX()+.5,anchor.getY()+1,anchor.getZ()+.5);
        player.getInventory().clearContent();
        var dispatcher=level.getServer().getCommands().getDispatcher();
        try {
            int result=dispatcher.execute("emt_outputs",player.createCommandSourceStack().withPermission(0));
            var book=player.getInventory().items.stream().filter(stack->stack.is(Items.WRITTEN_BOOK)).findFirst().orElse(ItemStack.EMPTY);
            var content=book.get(DataComponents.WRITTEN_BOOK_CONTENT);
            check("native permission-zero command gives a real signed book",result==1&&content!=null&&content.author().equals("Tombenpotter")&&!content.pages().isEmpty(),book);
            if(content!=null) {
                String text=content.pages().stream().map(page->page.raw().getString()).collect(Collectors.joining("\n"));
                check("every registered primal and compound appears once without truncation",Aspect.aspects.values().stream().allMatch(aspect->text.split(java.util.regex.Pattern.quote(aspect.getTag()+"\n"),-1).length==2),content.pages().size()+" pages / "+Aspect.aspects.size()+" aspects");
                var restored=ItemStack.parseOptional(level.registryAccess(),(net.minecraft.nbt.CompoundTag)book.save(level.registryAccess()));
                check("native book survives item save and reload",restored.get(DataComponents.WRITTEN_BOOK_CONTENT).equals(content),restored);
            }
            double oldAir=EMTConfig.PRIMALS[0].get(),oldCap=EMTConfig.OUTPUT_CAP.get();
            try {
                EMTConfig.PRIMALS[0].set(1234.5d);
                EMTConfig.OUTPUT_CAP.set(777d);
                player.getInventory().clearContent();
                dispatcher.execute("emt_outputs",player.createCommandSourceStack().withPermission(0));
                var configured=player.getInventory().items.stream().filter(stack->stack.is(Items.WRITTEN_BOOK)).findFirst().orElseThrow().get(DataComponents.WRITTEN_BOOK_CONTENT);
                String text=configured.pages().stream().map(page->page.raw().getString()).collect(Collectors.joining("\n"));
                check("command reflects fractional primal config and compound output cap",text.contains("aer\n1234.5 EU\n")&&text.contains(Aspect.ENERGY.getTag()+"\n777 EU\n"),"1234.5 / 777");
            } finally {
                EMTConfig.PRIMALS[0].set(oldAir);
                EMTConfig.OUTPUT_CAP.set(oldCap);
            }
            boolean rejected=false;
            try {dispatcher.execute("emt_outputs",level.getServer().createCommandSourceStack());}
            catch(com.mojang.brigadier.exceptions.CommandSyntaxException expected) {rejected=true;}
            check("console receives native player-only error",rejected,"no null-player crash");
        } catch(Exception failure) {
            throw new RuntimeException("EMT outputs command audit failed",failure);
        } finally {
            player.getInventory().clearContent();
        }
    }
}
