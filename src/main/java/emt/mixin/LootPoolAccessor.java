package emt.mixin;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Accessor;
import java.util.List;
@Mixin(LootPool.class)
public interface LootPoolAccessor {
 @Accessor("entries") List<LootPoolEntryContainer> emt$entries();
 @Mutable @Accessor("entries") void emt$entries(List<LootPoolEntryContainer> entries);
}
