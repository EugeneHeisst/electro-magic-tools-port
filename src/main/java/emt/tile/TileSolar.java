package emt.tile;
import emt.util.EMTConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
/** Original EMT output rules: 10/100/1000 EU, tier4/10k buffer; Ignis panels only generate in the Nether. */
public class TileSolar extends TileEnergySource {
 public final String kind;public final int stage;public double output;private boolean sun;private int ticks;
 public TileSolar(BlockPos p,BlockState s,String name,String kind,int stage){super(p,s,name,10000,4);this.kind=kind;this.stage=stage;}
 public static boolean seesSky(Level l,BlockPos p){return l.dimensionType().hasSkyLight()&&l.getChunkAt(p).getSkyLightSources().getLowestSourceY(p.getX()&15,p.getZ()&15)<=p.getY();}
 public void checkSun(){sun=seesSky(level,worldPosition.above())&&level.isDay()&&!(level.getBiome(worldPosition).value().hasPrecipitation()&&(level.isRaining()||level.isThundering()));}
 public double generation(){double base=EMTConfig.SOLAR[stage].get();boolean weather=level.isRaining()||level.isThundering();if(kind.equals("fire"))return level.dimension().equals(Level.NETHER)?base*2:0;if(!level.dimensionType().hasSkyLight())return 0;return switch(kind){case "water"->weather&&seesSky(level,worldPosition.above())?base:0;case "dark"->!level.isDay()&&!weather&&seesSky(level,worldPosition.above())?base:0;case "order"->sun?base*3:0;case "air"->sun?base*(worldPosition.getY()>=160?2.5:1):0;case "earth"->sun?base*(worldPosition.getY()<=10?2.5:1):0;default->sun?base:0;};}
 protected void updateEntityServer(){if(ticks++%65==0)checkSun();output=generation();addEnergy(output);setActive(output>0);}
}
