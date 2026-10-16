package emt.init;
import emt.EMT;
import java.util.*;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.registries.DeferredRegister;
public final class EMTArmorMaterials {
 public static final DeferredRegister<ArmorMaterial> MATERIALS=DeferredRegister.create(Registries.ARMOR_MATERIAL,EMT.MODID);
 public static final Holder<ArmorMaterial> ELECTRIC=make("electric_armor",false),FEATHER=make("feather_armor",true);
 private static Holder<ArmorMaterial> make(String name,boolean feather){return MATERIALS.register(name,()->{var defense=new EnumMap<ArmorItem.Type,Integer>(ArmorItem.Type.class);for(var type:ArmorItem.Type.values())defense.put(type,feather&&type==ArmorItem.Type.CHESTPLATE?2:0);return new ArmorMaterial(defense,feather?6:0,feather?SoundEvents.ARMOR_EQUIP_LEATHER:SoundEvents.ARMOR_EQUIP_DIAMOND,()->Ingredient.EMPTY,List.of(new ArmorMaterial.Layer(EMT.id(name))),0,0);});}
}
