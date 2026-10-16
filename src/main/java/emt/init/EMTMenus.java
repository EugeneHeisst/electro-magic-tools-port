package emt.init;
import emt.EMT;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.*;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import ic2.core.*;
public final class EMTMenus {
 public static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,EMT.MODID);
 public static final DeferredHolder<MenuType<?>,MenuType<emt.client.gui.ContainerEMT<?>>> TILE=MENUS.register("tile",()->IMenuTypeExtension.create((id,inv,buf)->{var tile=inv.player.level().getBlockEntity(buf.readBlockPos());if(!(tile instanceof IHasGui gui))throw new IllegalStateException("Missing EMT tile");return (emt.client.gui.ContainerEMT<?>)gui.getGuiContainer(id,inv);}));
}
