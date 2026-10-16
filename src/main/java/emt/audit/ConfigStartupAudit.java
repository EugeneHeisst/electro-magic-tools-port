package emt.audit;
import emt.EMT;
import emt.init.EMTRecipes;
import emt.util.EMTConfig;
import dev.eugheist.tc4modern.api.TC4Addons;
import thaumcraft.api.research.ResearchCategories;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import java.util.*;
@EventBusSubscriber(modid=EMT.MODID)
public final class ConfigStartupAudit {
 @SubscribeEvent static void start(ServerStartedEvent event){
  if(!"config".equals(System.getProperty("emt.audit")))return;
  var data=EMTRecipes.definitions();var expected=EMTRecipes.enabledResearches(data,new HashSet<>(EMTConfig.DISABLED_RESEARCH.get()));
  if(!ResearchCategories.getResearchList("EMT").research.keySet().equals(expected))throw new IllegalStateException("Disabled research leaked or active research missing");
  var wanted=new HashSet<String>();var all=new HashSet<String>();for(var raw:data.getAsJsonArray("recipes")){var recipe=raw.getAsJsonObject();String key=recipe.get("key").getAsString();all.add(key);if(expected.contains(recipe.get("research").getAsString()))wanted.add(key);}
  var actual=new HashSet<String>();TC4Addons.arcaneRecipes().forEach(r->actual.add(r.key()));TC4Addons.infusionRecipes().forEach(r->actual.add(r.key()));actual.addAll(TC4Addons.crucibleRecipes().keySet());actual.retainAll(all);
  if(!actual.equals(wanted))throw new IllegalStateException("Disabled recipe leaked or active recipe missing: "+actual+" expected "+wanted);
  thaumcraft.common.config.ConfigResearchData.init();if(ResearchCategories.getResearch("INFUSION")==null)throw new IllegalStateException("Addon category suppressed native research");
  EMT.LOG.info("[emt-config-audit] DONE PASS disabled={} activeResearch={} activeRecipes={} nativeINFUSION=present",EMTConfig.DISABLED_RESEARCH.get(),expected.size(),actual.size());event.getServer().halt(false);
 }
}
