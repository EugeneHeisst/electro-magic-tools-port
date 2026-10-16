#!/usr/bin/env python3
"""Import original EMT assets and machine/research definitions with explicit modern item mappings."""
import json,re,shutil
from pathlib import Path
ROOT=Path(__file__).resolve().parent.parent;REF=ROOT/'related/emt-reference';SRC=REF/'src/emt';ASSETS=REF/'jar/assets/emt';RES=ROOT/'src/generated/resources';A=RES/'assets/emt';D=RES/'data/emt'
def write(p,data):p.parent.mkdir(parents=True,exist_ok=True);p.write_text(json.dumps(data,ensure_ascii=False,indent=2)+'\n')
def split(s):
 out=[];start=0;depth=0;quote=None
 for i,c in enumerate(s):
  if quote:
   if c==quote and (i==0 or s[i-1]!='\\'):quote=None
  elif c in '\"\'':quote=c
  elif c in '([{':depth+=1
  elif c in ')]}':depth-=1
  elif c==',' and depth==0:out.append(s[start:i].strip());start=i+1
 out.append(s[start:].strip());return out
materials=['uranium_cluster','crushed_amber','purified_amber','crushed_cinnabar','purified_cinnabar','thaumium_plate','lightning_summoner','feather_mesh','glue','duct_tape','rubber_ball','cardboard','feather_glued_cardboard_wing','tainted_feather','thaumium_wing','uu_matter_drop']
tools={'thaumiumDrill':'thaumium_drill','rockbreakerDrill':'rockbreaker_drill','diamondChainsaw':'diamond_chainsaw','thaumiumChainsaw':'thaumium_chainsaw','streamChainsaw':'stream_chainsaw','ironOmnitool':'iron_omnitool','diamondOmnitool':'diamond_omnitool','thaumiumOmnitool':'thaumium_omnitool','electricHoeGrowth':'electric_hoe_growth','electricScribingTools':'electric_scribing_tools'}
tooltex={'thaumium_drill':'tools/drill_thaumium','rockbreaker_drill':'tools/drill_rockbreaker','diamond_chainsaw':'tools/chainsaw_diamond','thaumium_chainsaw':'tools/chainsaw_thaumium','stream_chainsaw':'tools/chainsaw_stream','iron_omnitool':'tools/omnitool_iron','diamond_omnitool':'tools/omnitool_diamond','thaumium_omnitool':'tools/omnitool_thaumium','electric_hoe_growth':'tools/hoe_growth','electric_scribing_tools':'electric_scribing_tools'}
materialtex=['clusteruranium','crushedamber','purifiedamber','crushedcinnabar','purifiedcinnabar','thaumiumplate','lightningsummoner','feathermesh','glue','ducttape','rubberball','cardboard','cardboardsheet','taintedfeather','thaumiumwing','uumatterdrop']
materiallang=['oreClusterUranium','crushedOreAmber','purifiedOreAmber','crushedOreCinnabar','purifiedOreCinnabar','thaumiumPlate','lightningSummoner','featherMesh','glue','ductTape','rubberBall','cardboard','featherGluedCardboardWing','taintedFeather','thaumiumWing','uumatterDrop']
solars=[prefix+kind+'_solar' for kind in ['compressed','water','dark','order','fire','air','earth'] for prefix in ['','double_','triple_']]
gens=[kind+'_generator' for kind in ['potentia','ignis','auram','arbor','aer']];machines=solars+gens+['industrial_wand_charger','ethereal_processor']
for f in (ASSETS/'textures').rglob('*'):
 if f.is_file():
  rel=f.relative_to(ASSETS/'textures').as_posix().lower().replace('blocks/','block/').replace('items/','item/');dest=A/'textures'/rel;dest.parent.mkdir(parents=True,exist_ok=True);shutil.copy2(f,dest)
  if f.suffix==".mcmeta":write(dest,json.loads(f.read_text()))
for iid,tex in tooltex.items():write(A/'models/item'/f'{iid}.json',{'parent':'minecraft:item/handheld','textures':{'layer0':'emt:item/'+tex}})
for iid,tex in zip(materials,materialtex):write(A/'models/item'/f'{iid}.json',{'parent':'minecraft:item/generated','textures':{'layer0':'emt:item/materials/'+tex}})
for bid in machines:
 for active in [False,True]:
  suffix='_active' if active else '';tex={}
  if bid in solars:
   rank=1 if bid.startswith('double_') else 2 if bid.startswith('triple_') else 0;kind=bid.replace('double_','').replace('triple_','').removesuffix('_solar');side='side' if kind=='compressed' else kind+'/'+kind+'side';top=['solartop','doublesolartop','triplesolartop'][rank];tex={'up':'solars/'+top,'down':'solars/bottom',**{face:'solars/'+side for face in ['north','south','west','east']}}
  elif bid in gens:tex={'up':'essentiagenerator/top','down':'essentiagenerator/top',**{face:'essentiagenerator/side' for face in ['north','south','west','east']}};tex['north']='essentiagenerator/'+bid.removesuffix('_generator')+'front'
  elif bid=='industrial_wand_charger':tex={face:'machines/side' for face in ['up','down','north','south','west','east']};tex['up']='machines/top';tex['north']='machines/wandcharger'
  else:tex={face:'machines/etherealmacerator' for face in ['up','down','north','south','west','east']};tex['up']='machines/etherealmaceratortop';tex['north']='machines/etherealmaceratorfront'+('active' if active else '')
  tex={face:'emt:block/'+path for face,path in tex.items()};tex['particle']=tex['south'];write(A/'models/block'/f'{bid}{suffix}.json',{'parent':'minecraft:block/cube','textures':tex})
 variants={}
 for face,angle in [('north',0),('east',90),('south',180),('west',270),('up',0),('down',0)]:
  for active in [False,True]:variants[f'facing={face},active={str(active).lower()}']={'model':'emt:block/'+bid+('_active' if active else ''),'y':angle}
 write(A/'blockstates'/f'{bid}.json',{'variants':variants});write(A/'models/item'/f'{bid}.json',{'parent':'emt:block/'+bid})
 drop='ic2:machine' if bid in ['industrial_wand_charger','ethereal_processor'] else 'emt:'+bid
 write(D/'loot_table/blocks'/f'{bid}.json',{'type':'minecraft:block','pools':[{'rolls':1,'entries':[{'type':'minecraft:item','name':drop}],'conditions':[{'condition':'minecraft:survives_explosion'}]}]})
write(RES/'data/minecraft/tags/block/mineable/pickaxe.json',{'replace':False,'values':['emt:'+m for m in machines]})
write(RES/'data/c/tags/item/plates/thaumium.json',{'replace':False,'values':['emt:thaumium_plate']});write(RES/'data/c/tags/item/slime_balls.json',{'replace':False,'values':['emt:glue']})
for lang,old in [('en_us','en_US'),('ru_ru','ru_RU')]:
 data={}
 for line in (ASSETS/'lang'/f'{old}.lang').read_text().splitlines():
  if '=' in line and not line.startswith('#'):k,v=line.split('=',1);data[k]=v
 for iid,key in zip(materials,materiallang):data['item.emt.'+iid]=data.get('item.EMT.'+key+'.name',data.get('item.EMT.'+key,iid.replace('_',' ').title()))
 for i,bid in enumerate(solars):
  old='tile.EMT.solar'+('2' if i>=16 else '')+'.'+bid.removesuffix('_solar').replace('_','')+'.name';data['block.emt.'+bid]=data.get(old,bid.replace('_',' ').title())
 for bid in gens:data['block.emt.'+bid]=data['tile.EMT.essentia.'+bid.removesuffix('_generator')+'.name']
 data['block.emt.industrial_wand_charger']=data['gui.EMT.wandRecharge.title'];data['block.emt.ethereal_processor']=data['gui.EMT.etherealMacerator.title'];data['itemGroup.emt']='Electro-Magic Tools';write(A/'lang'/f'{lang}.json',data)
# Translate only implemented output groups. Unknown ingredients fail generation instead of becoming air.
ic2={'solarPanel':'solar_panel','semifluidGenerator':'semifluid_generator','mvTransformer':'mv_transformer','advancedMachine':'advanced_machine','orewashingplant':'ore_washing_plant','scrap':'scrap','replicator':'replicator','iridiumPlate':'iridium_plate','electroFurnace':'electric_furnace','macerator':'macerator','diamondDrill':'diamond_drill','denseplateiron':'dense_iron_plate','carbonPlate':'carbon_plate','plateobsidian':'obsidian_plate','lapotronCrystal':'lapotron_crystal','overclockerUpgrade':'overclocker_upgrade','reinforcedStone':'reinforced_stone','electricHoe':'electric_hoe','elemotor':'electric_motor','coil':'coil','energyCrystal':'energy_crystal','electronicCircuit':'electronic_circuit','advancedCircuit':'advanced_circuit'}
def item(s):
 s=s.strip()
 if s.startswith('EMTRandomHelper.getChargedItem('):
  args=split(s[len('EMTRandomHelper.getChargedItem('):-1]);return {'id':'emt:'+tools[args[0].split('.')[-1]],'count':1,'charge':int(args[1])}
 m=re.fullmatch(r'IC2Items.getItem\("([^"]+)"\)',s)
 if m:return {'id':'ic2:'+ic2[m[1]],'count':1}
 if not s.startswith('new ItemStack('):raise ValueError(s)
 a=split(s[len('new ItemStack('):-1]);base=a[0];count=int(a[1]) if len(a)>1 else 1;meta=int(a[2]) if len(a)>2 else 0
 if base.startswith('IC2Items.getItem('):result=item(base.removesuffix('.getItem()'));result['count']=count;return result
 if base.startswith('EMTItems.') and base.split('.')[-1] in tools:return {'id':'emt:'+tools[base.split('.')[-1]],'count':count}
 if base=='Items.dye' and meta==15:return {'id':'minecraft:bone_meal','count':count}
 if base=='Blocks.sapling':return {'id':'minecraft:oak_sapling','count':count}
 if base=='EMTBlocks.emtSolars':iid=solars[meta]
 elif base=='EMTBlocks.emtSolars2':iid=solars[16+meta]
 elif base=='EMTBlocks.essentiaGens':iid=gens[meta]
 elif base=='EMTBlocks.emtMachines':iid=['industrial_wand_charger','ethereal_processor'][meta]
 elif base=='EMTItems.itemEMTItems':iid=materials[meta]
 elif base.startswith('Items.'):return {'id':'minecraft:'+base[6:],'count':count}
 elif base.startswith('Blocks.'):return {'id':'minecraft:'+base[7:],'count':count}
 else:
  mapping={'ConfigItems.itemFocusTrade':'focus_trade','ConfigBlocks.blockJar':'warded_jar','ConfigBlocks.blockStoneDevice':'wand_pedestal','ConfigBlocks.blockMetalDevice':'alchemical_construct','ConfigItems.itemShard':'entropy_shard','ConfigItems.itemAxeElemental':'elemental_axe','ConfigItems.itemPickElemental':'elemental_pickaxe','ConfigItems.itemShovelElemental':'elemental_shovel','ConfigItems.itemHoeElemental':'elemental_hoe','ConfigBlocks.blockMagicalLog':'greatwood_log','ConfigItems.itemInkwell':'scribing_tools'}
  return {'id':'tc4modern:'+mapping[base],'count':count}
 return {'id':'emt:'+iid,'count':count}
def aspects(filename):
 s=(SRC/'util'/filename).read_text();out={}
 for name,body in re.findall(r'public static AspectList (\w+)\s*=\s*(.*?);',s,re.S):out[name]={a:int(n) for a,n in re.findall(r'\.add\(Aspect\.(\w+),\s*(\d+)\)',body)}
 return out
costs=aspects('EMTCraftingAspects.java');tags=aspects('EMTResearchAspects.java');recipes=[];source=(SRC/'init/EMTRecipes.java').read_text()
for m in re.finditer(r'(\w+) = ThaumcraftApi.add(InfusionCraftingRecipe|ArcaneCraftingRecipe|ShapelessArcaneCraftingRecipe|CrucibleRecipe)\(',source):
 end=source.index(';',m.start());a=split(source[m.end():end].strip()[:-1]);key=m[1];kind=m[2];raw=a[1]
 if not any('EMTBlocks.'+name in raw for name in ['emtSolars','essentiaGens','emtMachines']) and not any('EMTItems.'+name+',' in raw or 'EMTItems.'+name+')' in raw for name in tools):continue
 out=item(raw);entry={'key':'emt:'+key,'research':json.loads(a[0]),'result':out}
 if kind=='InfusionCraftingRecipe':entry.update(kind='infusion',instability=int(a[2]),aspects=costs[a[3].split('.')[-1]],central=item(a[4]),components=[item(x) for x in split(a[5][a[5].index('{')+1:-1])])
 elif kind=='CrucibleRecipe':entry.update(kind='crucible',central=item(a[2]),aspects=costs[a[3].split('.')[-1]])
 else:
  args=split(a[3][a[3].index('{')+1:-1]);rows=[]
  if kind=='ShapelessArcaneCraftingRecipe':
   entry.update(kind='arcane',aspects=costs[a[2].split('.')[-1]],pattern=[''.join(chr(65+i) for i in range(len(args)))],keys={chr(65+i):item(arg) for i,arg in enumerate(args)},shapeless=True);recipes.append(entry);continue
  while args and args[0].startswith('"'):rows.append(json.loads(args.pop(0)))
  keys={}
  while args:c=args.pop(0).strip("'");keys[c]=item(args.pop(0))
  entry.update(kind='arcane',aspects=costs[a[2].split('.')[-1]],pattern=rows,keys=keys)
 recipes.append(entry)
researches=[];wanted={r['research'] for r in recipes}|{'Electric Magic Tools','Thaumium Plate','Iron Omnitool','Diamond Chainsaw'};source=(SRC/'init/EMTResearches.java').read_text()
for m in re.finditer(r'new EMTResearchItem\(',source):
 end=source.index(';',m.start());block=source[m.end():end];depth=1;quote=False;cut=0
 for i,c in enumerate(block):
  if c=='"':quote=not quote
  if not quote:
   if c=='(':depth+=1
   if c==')':depth-=1
  if depth==0:cut=i;break
 a=split(block[:cut]);name=json.loads(a[0])
 if name not in wanted:continue
 offset=1 if a[1].startswith('EMTResearchAspects.') else 0
 icon={'id':'emt:thaumium_plate','count':1} if 'ResourceLocation' in a[-1] else item(a[-1]);record={'name':name,'x':int(a[1+offset]),'y':int(a[2+offset]),'complexity':int(a[3+offset]),'icon':icon,'aspects':tags[a[1].split('.')[-1]] if offset else {}}
 for field,method in [('parents','setParents'),('hidden','setParentsHidden')]:
  match=re.search(r'\.'+method+r'\(new String\[\]\{([^}]+)\}',block);record[field]=re.findall(r'"([^"]+)"',match[1]) if match else []
 record['flags']=[flag for flag in ['Secondary','Concealed','AutoUnlock','Round'] if '.set'+flag+'()' in block];record['recipes']=[r['key'] for r in recipes if r['research']==name]
 if name=='Thaumium Plate':record['recipes']=['emt:thaumium_plate']
 if name=='Iron Omnitool':record['recipes']=['emt:iron_omnitool']
 if name=='Diamond Chainsaw':record['recipes']=['emt:diamond_chainsaw']
 researches.append(record)
write(A/'integration.json',{'recipes':recipes,'researches':researches})
# Material crafting, smelting and IC2 rolling form the early production chain.
def shaped(name,pattern,keys):write(D/'recipe'/f'{name}.json',{'type':'ic2:shaped','pattern':pattern.split('|'),'key':{k:{'item':v} for k,v in keys.items()},'result':{'id':'emt:'+name,'count':1}})
shaped('thaumium_plate','X|Y|Z',{'X':'ic2:forge_hammer','Y':'tc4modern:thaumium_ingot','Z':'minecraft:obsidian'})
shaped('duct_tape','XXX|YYY|XXX',{'X':'emt:glue','Y':'minecraft:paper'});shaped('feather_mesh','XYX|YXY|XYX',{'X':'minecraft:feather','Y':'emt:duct_tape'});shaped('feather_glued_cardboard_wing','XYZ|XYZ|XYZ',{'X':'emt:feather_mesh','Y':'emt:glue','Z':'emt:cardboard'})
shaped('rubber_ball','XX|XX',{'X':'ic2:rubber'})
for name,result,count in [('crushed_amber','tc4modern:amber',1),('purified_amber','tc4modern:amber',1),('crushed_cinnabar','tc4modern:quicksilver',1),('purified_cinnabar','tc4modern:quicksilver',1),('rubber_ball','emt:glue',2)]:write(D/'recipe'/f'smelt_{name}.json',{'type':'minecraft:smelting','ingredient':{'item':'emt:'+name},'result':{'id':result,'count':count},'experience':0,'cookingtime':200})
print(f'Generated {len(machines)} machines, {len(materials)} materials, {len(recipes)} original magic recipes and {len(researches)} researches')

shaped('tainted_feather',' X |XYX| X ',{'X':'tc4modern:tainted_goo','Y':'minecraft:feather'})
write(D/'recipe/cardboard.json',{'type':'ic2:shaped','pattern':['XXX','YYY','XXX'],'key':{'X':{'item':'minecraft:paper'},'Y':{'tag':'minecraft:logs'}},'result':{'id':'emt:cardboard','count':1}})

# Early electric tool crafting; IC2 recipes preserve charge from their inputs.
shaped('iron_omnitool','X|Z|Y',{'X':'ic2:chainsaw','Y':'ic2:mining_drill','Z':'ic2:iron_plate'})
shaped('diamond_chainsaw',' X |XYX',{'X':'minecraft:diamond','Y':'ic2:chainsaw'})
for name in ['iron_omnitool','diamond_chainsaw']:
 data=json.loads((D/'recipe'/f'{name}.json').read_text());data['result']['components']={'ic2:charge':10.0};write(D/'recipe'/f'{name}.json',data)
for tag in ['mining','mining_loot','weapon']:
 values=list(tools.values())
 if tag=='weapon':values=[name for name in values if 'chainsaw' in name or 'omnitool' in name]
 write(RES/'data/minecraft/tags/item/enchantable'/f'{tag}.json',{'replace':False,'values':['emt:'+name for name in values]})
# Match original translation keys by registry field and icon name.
legacy_names={'thaumium_drill':'drill.thaumium','rockbreaker_drill':'drill.rockbreaker','diamond_chainsaw':'chainsaw.diamond','thaumium_chainsaw':'chainsaw.thaumium','stream_chainsaw':'chainsaw.stream','iron_omnitool':'omnitool.iron','diamond_omnitool':'omnitool.diamond','thaumium_omnitool':'omnitool.thaumium','electric_hoe_growth':'hoeofgrowth.electric','electric_scribing_tools':'scribingtools.electric'}
for lang in ['en_us','ru_ru']:
 path=A/'lang'/f'{lang}.json';data=json.loads(path.read_text())
 for iid,key in legacy_names.items():data['item.emt.'+iid]=data.get('item.EMT.'+key+'.name',iid.replace('_',' ').title())
 write(path,data)

write(A/'models/item/electric_scribing_tools.json',{'parent':'tc4modern:item/scribing_tools'})
