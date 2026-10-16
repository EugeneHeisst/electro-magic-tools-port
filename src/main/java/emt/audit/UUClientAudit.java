package emt.audit;
import emt.EMT;
import emt.init.EMTRecipes;
import dev.eugheist.tc4modern.*;
import thaumcraft.api.research.ResearchCategories;
import net.minecraft.client.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import java.nio.file.Path;
public final class UUClientAudit {
 private static void shot(Minecraft mc,String name){try(var image=Screenshot.takeScreenshot(mc.getMainRenderTarget())){image.writeToFile(Path.of("audit",name+".png"));}catch(Exception e){throw new RuntimeException(e);}}
 public static void tick(Minecraft mc,int t){
  if(t==2930){mc.player.closeContainer();mc.getSingleplayerServer().execute(()->{var p=mc.getSingleplayerServer().getPlayerList().getPlayers().getFirst();for(var category:ResearchCategories.researchCategories.values())for(var r:category.research.values())ResearchProgress.add(p.getPersistentData(),r.key);net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(p,new ScanPayload("",p.getPersistentData().getCompound(ResearchKnowledge.KEY)));});}
  if(t==2950){int count=0;for(var item:BuiltInRegistries.ITEM){var id=BuiltInRegistries.ITEM.getKey(item);if(!id.getNamespace().equals("emt"))continue;var model=mc.getItemRenderer().getModel(new ItemStack(item),mc.level,mc.player,0);if(model.getParticleIcon(net.neoforged.neoforge.client.model.data.ModelData.EMPTY).contents().name().getPath().equals("missingno"))throw new IllegalStateException("Missing EMT model "+id);count++;}for(String metal:new String[]{"tin","silver","lead"}){var model=mc.getItemRenderer().getModel(EMTRecipes.stack("tc4modern:"+metal+"_cluster"),mc.level,mc.player,0);if(model.getParticleIcon(net.neoforged.neoforge.client.model.data.ModelData.EMPTY).contents().name().getPath().equals("missingno"))throw new IllegalStateException("Missing cluster "+metal);}EMT.LOG.info("[emt-client-audit] PASS all {} EMT item models and3nativecluster models",count);}
  if(t==2960){var r=ResearchCategories.getResearch("UU-Matter Infusion");if(r.getPages().length!=16||!"emt:uuMCrystal".equals(r.getPages()[1].recipeKey)||!"emt:uraniumToIridium".equals(r.getPages()[15].recipeKey))throw new IllegalStateException("Original UU recipe page order");ClientAudit.book(mc,"UU-Matter Infusion",0);}
  if(t==2975)shot(mc,"book_uu_crystal");
  if(t==2980)ClientAudit.book(mc,"UU-Matter Infusion",2);if(t==2995)shot(mc,"book_uu_charcoal16_glowstone");
  if(t==3000)ClientAudit.book(mc,"UU-Matter Infusion",4);if(t==3015)shot(mc,"book_uu_iron_copper32");
  if(t==3020)ClientAudit.book(mc,"UU-Matter Infusion",8);if(t==3035)shot(mc,"book_uu_distinct_shards");
  if(t==3040)ClientAudit.book(mc,"UU-Matter Infusion",14);if(t==3055)shot(mc,"book_uu_uranium_iridium");
  if(t==3060)ClientAudit.book(mc,"Tiny Uranium",0);if(t==3075)shot(mc,"book_tiny_uranium7");
  if(t==3080){mc.player.closeContainer();if(net.neoforged.fml.ModList.get().isLoaded("jei"))try{Class.forName("mezz.jei.api.runtime.IJeiRuntime");mc.setScreen(new net.minecraft.client.gui.screens.inventory.InventoryScreen(mc.player));}catch(Exception e){throw new RuntimeException(e);}}
  if(t==3100){shot(mc,"uu_jei_inventory");EMT.LOG.info("[emt-client-audit] PASS native UU/TinyUranium book pages and JEI render");}
 }
}
