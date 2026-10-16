package emt.item.tool;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
/** TieredItem replaces property durability with the tier uses, so the original custom hammer life belongs here. */
public record HammerTier(int uses) implements Tier {
 @Override public int getUses(){return uses;}@Override public float getSpeed(){return Tiers.DIAMOND.getSpeed();}@Override public float getAttackDamageBonus(){return Tiers.DIAMOND.getAttackDamageBonus();}@Override public TagKey<Block> getIncorrectBlocksForDrops(){return Tiers.DIAMOND.getIncorrectBlocksForDrops();}@Override public int getEnchantmentValue(){return Tiers.DIAMOND.getEnchantmentValue();}@Override public Ingredient getRepairIngredient(){return Tiers.DIAMOND.getRepairIngredient();}
}
