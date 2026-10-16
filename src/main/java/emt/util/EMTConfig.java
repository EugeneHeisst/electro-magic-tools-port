package emt.util;
import net.neoforged.neoforge.common.ModConfigSpec;
import thaumcraft.api.aspects.Aspect;
public final class EMTConfig {
 public static final ModConfigSpec SPEC;
 public static final ModConfigSpec.BooleanValue TOOLS_IN_BORE,ENCHANTING,NIGHTVISION_OFF,IMPACT_RAIN;
 public static final ModConfigSpec.DoubleValue NANO_SPEED,QUANTUM_SPEED,NANO_JUMP,QUANTUM_JUMP;
 public static final ModConfigSpec.DoubleValue[] SOLAR=new ModConfigSpec.DoubleValue[3],PRIMALS=new ModConfigSpec.DoubleValue[6];
 public static final ModConfigSpec.DoubleValue OUTPUT_CAP,WAND_COST,FOCUS_PRODUCTION,FOCUS_WAND_COST;
 public static final ModConfigSpec.IntValue PROCESS_TICKS,BONUS_CHANCE;
 public static final ModConfigSpec.BooleanValue CLEAR_ORE_RECIPES;
 static {var b=new ModConfigSpec.Builder();b.push("numeric");for(int i=0;i<3;i++)SOLAR[i]=b.defineInRange(new String[]{"compressedSolarOutput","doubleCompressedSolarOutput","tripleCompressedSolarOutput"}[i],new double[]{10,100,1000}[i],0,1e9);FOCUS_PRODUCTION=b.defineInRange("chargeFocusProduction",64d,0,1e9);FOCUS_WAND_COST=b.defineInRange("wandChargeFocusCost",40000d,1,1e9);NANO_SPEED=b.defineInRange("nanoBootsSpeed",.25,0,10);QUANTUM_SPEED=b.defineInRange("quantumBootsSpeed",.5,0,10);NANO_JUMP=b.defineInRange("nanoBootsJump",.6,0,10);QUANTUM_JUMP=b.defineInRange("quantumBootsJump",.9,0,10);WAND_COST=b.defineInRange("wandChargerConsumption",50000d,1,1e9);PROCESS_TICKS=b.defineInRange("etherealProcessorBaseSpeed",400,1,1000000);BONUS_CHANCE=b.defineInRange("etherealProcessorBonus",10,1,1000000);b.pop().push("essentia");for(int i=0;i<6;i++)PRIMALS[i]=b.defineInRange(new String[]{"airOutput","waterOutput","fireOutput","orderOutput","entropyOutput","earthOutput"}[i],new double[]{15000,5000,20000,16000,10000,2000}[i],0,1e12);OUTPUT_CAP=b.defineInRange("outputCap",-1d,-1,1e12);b.pop().push("general");TOOLS_IN_BORE=b.define("toolsInBore",false);ENCHANTING=b.define("enchanting",false);NIGHTVISION_OFF=b.define("nightVisionOff",false);IMPACT_RAIN=b.define("impactOfRain",true);CLEAR_ORE_RECIPES=b.define("removeAmberAndCinnabarMacerating",true);b.pop();SPEC=b.build();}
 public static double essentiaOutput(Aspect a){Aspect[] primals={Aspect.AIR,Aspect.WATER,Aspect.FIRE,Aspect.ORDER,Aspect.ENTROPY,Aspect.EARTH};for(int i=0;i<6;i++)if(a==primals[i])return PRIMALS[i].get();var components=a.getComponents();if(components==null)return 0;double result=essentiaOutput(components[0])+essentiaOutput(components[1]);return OUTPUT_CAP.get()<0?result:Math.min(result,OUTPUT_CAP.get());}
}
