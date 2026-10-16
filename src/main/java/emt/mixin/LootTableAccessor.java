package emt.mixin;
import net.minecraft.world.level.storage.loot.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import java.util.List;
@Mixin(LootTable.class)
public interface LootTableAccessor {@Accessor("pools") List<LootPool> emt$pools();}
