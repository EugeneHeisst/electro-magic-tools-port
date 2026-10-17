package emt.audit;

import emt.init.EMTItems;
import emt.item.tool.EMTElectricTool;
import ic2.api.item.ElectricItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.util.FakePlayerFactory;

/** Real native breaks and interactions for the three EMT chainsaws and three omnitools. */
public final class ShearsAudit {
    private ShearsAudit() {}

    private static void check(String name, boolean ok, Object detail) { ServerAudit.check("shears: " + name, ok, detail); }
    private static double charge(ItemStack stack) { return ElectricItem.manager.getCharge(stack); }
    private static ItemStack stack(EMTElectricTool tool, double eu, boolean disabled) {
        ItemStack stack = new ItemStack(tool);
        ElectricItem.manager.charge(stack, eu, Integer.MAX_VALUE, true, false);
        if (disabled) CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putBoolean("disableShear", true));
        return stack;
    }

    public static void run(ServerLevel level, BlockPos anchor) {
        var player = FakePlayerFactory.getMinecraft(level);
        player.setGameMode(GameType.SURVIVAL);
        player.setShiftKeyDown(false);
        player.getInventory().clearContent();
        player.getInventory().selected = 0;
        for (var slot : EquipmentSlot.values()) player.setItemSlot(slot, ItemStack.EMPTY);
        BlockPos pos = anchor.offset(-48, 0, 64);
        for (int x = -1; x <= 1; x++) for (int z = -1; z <= 1; z++) level.setChunkForced((pos.getX() >> 4) + x, (pos.getZ() >> 4) + z, true);
        var box = new AABB(pos).inflate(4);
        player.moveTo(pos.getX() - 1.5, pos.getY(), pos.getZ() + .5);
        int types = 0;
        for (var entry : EMTItems.TOOLS.entrySet()) {
            if (!(entry.getValue().get() instanceof EMTElectricTool tool) || tool.kind.ordinal() < 2) continue;
            types++;
            String name = entry.getKey();
            double cost = tool.energyCost();
            for (int mode = 0; mode < 3; mode++) {
                ItemStack stack = stack(tool, mode == 0 ? 0 : cost, mode == 2);
                player.setItemInHand(InteractionHand.MAIN_HAND, stack);
                level.setBlock(pos, Blocks.OAK_LEAVES.defaultBlockState().setValue(LeavesBlock.PERSISTENT, true), 3);
                level.getEntitiesOfClass(ItemEntity.class, box).forEach(Entity::discard);
                player.gameMode.destroyBlock(pos);
                int leaves = level.getEntitiesOfClass(ItemEntity.class, box).stream().filter(e -> e.getItem().is(Items.OAK_LEAVES)).mapToInt(e -> e.getItem().getCount()).sum();
                check(name + " leaf break " + new String[]{"empty", "last full operation", "disabled"}[mode],
                    leaves == (mode == 1 ? 1 : 0) && charge(stack) == 0 && level.getBlockState(pos).isAir(), "leaves=" + leaves + " EU=" + charge(stack));
                level.getEntitiesOfClass(ItemEntity.class, box).forEach(Entity::discard);
                level.removeBlock(pos, false);
            }
            for (int mode = 0; mode < 3; mode++) {
                ItemStack stack = stack(tool, mode == 0 ? 0 : cost, mode == 2);
                var sheep = EntityType.SHEEP.create(level);
                sheep.setNoAi(true);
                sheep.moveTo(Vec3.atCenterOf(pos));
                level.addFreshEntity(sheep);
                player.setItemInHand(InteractionHand.MAIN_HAND, stack);
                var result = tool.interactLivingEntity(stack, player, sheep, InteractionHand.MAIN_HAND);
                check(name + " sheep " + new String[]{"empty passes", "charged shears", "disabled passes"}[mode],
                    sheep.isSheared() == (mode == 1) && result.consumesAction() == (mode == 1) && charge(stack) == (mode == 2 ? cost : 0), "sheared=" + sheep.isSheared() + " EU=" + charge(stack) + " result=" + result);
                sheep.discard();
                level.getEntitiesOfClass(ItemEntity.class, box).forEach(Entity::discard);
            }
            ItemStack stack = stack(tool, cost * 3, false);
            player.setItemInHand(InteractionHand.MAIN_HAND, stack);
            level.setBlock(pos, Blocks.PUMPKIN.defaultBlockState(), 3);
            player.gameMode.useItemOn(player, level, stack, InteractionHand.MAIN_HAND, new BlockHitResult(Vec3.atCenterOf(pos), Direction.NORTH, pos, false));
            check(name + " does not expose free native shears actions", level.getBlockState(pos).is(Blocks.PUMPKIN) && charge(stack) == cost * 3
                && ItemAbilities.DEFAULT_SHEARS_ACTIONS.stream().filter(a -> a != ItemAbilities.SHEARS_DIG).noneMatch(stack::canPerformAction), level.getBlockState(pos));
            level.removeBlock(pos, false);
        }
        check("all six cutting tools tested", types == 6, types);
        player.getInventory().clearContent();
    }
}
