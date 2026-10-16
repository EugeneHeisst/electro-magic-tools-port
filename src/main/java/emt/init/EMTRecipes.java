package emt.init;
import com.google.gson.*;
import emt.EMT;
import emt.util.EMTConfig;
import dev.eugheist.tc4modern.api.TC4Addons;
import dev.eugheist.tc4modern.ArcaneRecipes;
import dev.eugheist.tc4modern.InfusionItemRecipes;
import thaumcraft.api.aspects.*;
import thaumcraft.api.crafting.*;
import thaumcraft.api.research.*;
import ic2.api.recipe.*;
import net.minecraft.core.registries.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import java.util.*;
import java.util.function.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
public final class EMTRecipes {
 public static Item item(String id){var key=ResourceLocation.parse(id);if(!BuiltInRegistries.ITEM.containsKey(key))throw new IllegalStateException("Missing EMT ingredient "+id);return BuiltInRegistries.ITEM.get(key);}
 public static ItemStack stack(String id){return new ItemStack(item(id));}
 private static ItemStack stack(JsonObject data){return new ItemStack(item(data.get("id").getAsString()),data.get("count").getAsInt());}
 private static AspectList aspects(JsonObject data){var list=new AspectList();for(var e:data.entrySet()){try{list.add((Aspect)Aspect.class.getField(e.getKey()).get(null),e.getValue().getAsInt());}catch(ReflectiveOperationException ex){throw new IllegalArgumentException(e.getKey(),ex);}}return list;}
 public static JsonObject definitions(){try(var in=EMTRecipes.class.getResourceAsStream("/assets/emt/integration.json")){if(in==null)throw new IllegalStateException("Missing EMT definitions");return JsonParser.parseReader(new InputStreamReader(in,StandardCharsets.UTF_8)).getAsJsonObject();}catch(IOException e){throw new IllegalStateException(e);}}
 public static void setup(){var data=definitions();Map<String,ResearchPage.PageType> types=new HashMap<>();
  for(var raw:data.getAsJsonArray("recipes")){var d=raw.getAsJsonObject();String key=d.get("key").getAsString(),research=d.get("research").getAsString(),kind=d.get("kind").getAsString();var out=stack(d.getAsJsonObject("result"));var cost=aspects(d.getAsJsonObject("aspects"));
   if(kind.equals("infusion")){var centre=stack(d.getAsJsonObject("central")).getItem();List<Supplier<Item>> parts=new ArrayList<>();for(var part:d.getAsJsonArray("components")){var ingredient=stack(part.getAsJsonObject()).getItem();parts.add(()->ingredient);}TC4Addons.addInfusionRecipe(new InfusionItemRecipes.Recipe(key,research,out::getItem,d.get("instability").getAsInt(),cost,()->centre,List.copyOf(parts)));types.put(key,ResearchPage.PageType.INFUSION_CRAFTING);}
   else if(kind.equals("crucible")){TC4Addons.addCrucibleRecipe(key,new CrucibleRecipe(research,out,Ingredient.of(stack(d.getAsJsonObject("central"))),cost));types.put(key,ResearchPage.PageType.CRUCIBLE_CRAFTING);}
   else {var rows=d.getAsJsonArray("pattern");int width=rows.get(0).getAsString().length();List<Ingredient> ingredients=new ArrayList<>();List<Predicate<ItemStack>> predicates=new ArrayList<>();for(var row:rows)for(char c:row.getAsString().toCharArray()){var ing=c==' '?Ingredient.EMPTY:Ingredient.of(stack(d.getAsJsonObject("keys").getAsJsonObject(String.valueOf(c))));ingredients.add(ing);predicates.add(c==' '?null:ing::test);}TC4Addons.addArcaneRecipe(new ArcaneRecipes.Entry(key,research,width,rows.size(),List.copyOf(ingredients),out,cost,new ArcanePattern<>(research,width,rows.size(),predicates)));types.put(key,ResearchPage.PageType.ARCANE_CRAFTING);}
  }
  ResearchCategories.registerCategory("EMT",EMT.id("textures/misc/emt.png"),EMT.id("textures/misc/background.png"));
  for(var raw:data.getAsJsonArray("researches")){var d=raw.getAsJsonObject();String name=d.get("name").getAsString();var research=new ResearchItem(name,"EMT",aspects(d.getAsJsonObject("aspects")),d.get("x").getAsInt(),d.get("y").getAsInt(),d.get("complexity").getAsInt(),stack(d.getAsJsonObject("icon"))){public String getName(){return net.minecraft.network.chat.Component.translatable("emt.rsname."+key).getString();}public String getText(){return net.minecraft.network.chat.Component.translatable("emt.tag."+key).getString();}};
   var parents=strings(d.getAsJsonArray("parents"));if(!parents.isEmpty())research.setParents(parents.toArray(String[]::new));var hidden=new ArrayList<>(strings(d.getAsJsonArray("hidden")));List<ResearchPage> pages=new ArrayList<>();pages.add(new ResearchPage("emt.text."+name));for(var entry:d.getAsJsonArray("recipes")){String key=entry.getAsString();var page=new ResearchPage("");page.type=types.getOrDefault(key,ResearchPage.PageType.NORMAL_CRAFTING);page.recipeKey=key;pages.add(page);if(page.type==ResearchPage.PageType.INFUSION_CRAFTING&&!hidden.contains("INFUSION"))hidden.add("INFUSION");}if(!hidden.isEmpty())research.setParentsHidden(hidden.toArray(String[]::new));for(String flag:strings(d.getAsJsonArray("flags")))switch(flag){case "AutoUnlock"->research.setAutoUnlock();case "Round"->research.setRound();case "Secondary"->research.setSecondary();case "Concealed"->research.setConcealed();}research.setPages(pages.toArray(ResearchPage[]::new)).registerResearchItem();
  }
  machines();EMT.LOG.info("[emt] Registered {} original magic recipes and {} researches",data.getAsJsonArray("recipes").size(),data.getAsJsonArray("researches").size());
 }
 private static List<String> strings(JsonArray a){return a.asList().stream().map(JsonElement::getAsString).toList();}
 private static void recipe(IMachineRecipeManager manager,String input,CompoundTag metadata,boolean replace,ItemStack... outputs){var in=stack(input);if(replace)manager.getRecipes().keySet().removeIf(k->k.matches(in));manager.addRecipe(new RecipeInputItemStack(in),metadata,outputs);}
 private static void machines(){
  recipe(Recipes.metalformerRolling,"tc4modern:thaumium_ingot",null,false,stack("emt:thaumium_plate"));
  for(String metal:List.of("iron","gold","copper","tin","silver","lead")){var out=stack("ic2:small_"+metal+"_dust");out.setCount(22);String cluster="tc4modern:"+metal+"_cluster";if(BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(cluster)))recipe(Recipes.macerator,cluster,null,true,out);else Recipes.macerator.addRecipe(new RecipeInputOreDict("c:clusters/"+metal,1),null,out);}
  for(String material:List.of("amber","cinnabar")){String metal=material.equals("amber")?"copper":"tin",result=material.equals("amber")?"amber":"quicksilver";for(String prefix:material.equals("cinnabar")?List.of("","deepslate_"):List.of("")){var out=stack("emt:crushed_"+material);out.setCount(2);recipe(Recipes.macerator,"tc4modern:"+prefix+material+"_ore",null,EMTConfig.CLEAR_ORE_RECIPES.get(),out);}var wash=new CompoundTag();wash.putInt("fluid",1000);recipe(Recipes.oreWashing,"emt:crushed_"+material,wash,false,stack("emt:purified_"+material),stack("ic2:small_"+metal+"_dust").copyWithCount(2),stack("ic2:stone_dust"));var heat=new CompoundTag();heat.putInt("heat",1000);recipe(Recipes.centrifuge,"emt:purified_"+material,heat,false,stack("ic2:small_"+metal+"_dust"),stack("tc4modern:"+result));}
 }
}
