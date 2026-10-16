# Electro-Magic Tools PORT — Claude ⇄ Codex handoff

User scope: port IC2 → ASP → EMT → Gravi Suit to MC1.21.1 / NeoForge21.1.256 and verify the mods work together. Repositories PRIVATE. No push/release/install/production-server changes without user authorization. Local commits and isolated test clients/servers are authorized. All clients hidden (Xvfb) and silent (ALSOFT_DRIVERS=null + all volume categories zero). Never compile while a dev JVM is running from this project's build/classes; run server/client/build sequentially. Verify own JVM cwd before stopping it.

## Current batch 0.1.0 (Codex, 2026-10-16)

28 working machine variants:
- 21 panels: compressed / water / entropy / order / fire / air / earth, each single/double/triple. Original 10/100/1000 EU bases, tier4, 10k EU storage, 65-tick sky checks. Order x3 clear day; water rain/thunder; dark clear night; fire Nether-only x2; air Y>=160 x2.5; earth Y<=10 x2.5. Original height thresholds retained. Shared IC2 energy net and wrench lifecycle. Panel tooltips describe conditions.
- Potentia/Ignis/Auram/Arbor/Aer generators: actual Thaumcraft warded/void jars and reservoirs, plus addon AspectSource providers. Original asymmetric cube [-4,3] every20ticks; each source yields one essentia, output=sum of original configurable primal outputs /30, tier3, 1e9 EU buffer. Full buffers preserve essentia. Compound output caps work even when an aspect is below the cap. Original essentia trail packets use TC4's renderer. No chunk loading during source scans.
- Industrial wand charger: IC2 sink tier4 / 100k EU, cost50000 per tick charging **one vis (100 centivis) in each primal** with room; no EU waste for a full wand. WandData.add takes whole vis, despite get returning centivis! EU + wand persist. Registered TC4Addons wand holder for the vis relay.
- Ethereal processor: original free double smelting /400ticks + 1/10 thaumium nugget bonus; excludes foods/cobble/sand/dust/tools/armor. Input recipe filter, top input/bottom outputs/side bonus, output guards, progress persistence, no progress transfer on input changes, bonus works on the last input.

16 split original metadata materials registered, original textures/en+ru names. Lightning summoner holds40ticks then server-side5000-block trace/lightning/exact held-stack consumption; tainted feather retains its random Flux Taint effect. Original crafting/smelting chains for plates, cardboard, rubber ball→glue, tape, feather mesh, cardboard wing, tainted feather and processed ores. Uranium cluster was unused/not in the original creative tab; still registered as a material. Thaumium wing and UU drop are registered, their later wing/UU recipes are not yet ported.

Magic content: 28 original machine recipes (2 arcane,4 infusion,22 crucible), 16 original research entries/tab with coordinates/flags/aspects/parents/INFUSION hidden requirement. `tools/gen.py` parses the original selected definitions and validates mapped ingredients; outputs assets/emt/integration.json loaded through TC4Addons. Original recipe keys remain `emt:<camelCaseOriginalField>` for book pages. Generator fails on unsupported mapping instead of substituting air. Remaining original research-disable config flags not yet ported.

IC2 machine recipes: thaumium rolling; native iron/gold/copper clusters→22 small dusts; amber/cinnabar (incl deepslate) crushing→2 crushed, washing1000mB→purified+2 small copper/tin+stone dust, centrifuge heat1000→small metal+amber/quicksilver. The current TC4 port lacks tin/silver/lead native cluster items: those macerator inputs use optional `c:clusters/<metal>` tags, and do not crash startup. Additional cluster harvesting/support remains part of later interoperability work.

## Dependencies and tests

Build Java21. IC2 **0.1.18/0d5bc6a** (lazy JEI list fixes real class-init deadlock), Thaumcraft **0.17.172**. ASP0.1.0/72e90bd is included at runtime for combined testing; it is not an EMT requirement. Gradle properties select archived JARs. TerraBlender bundled by TC4 loads automatically. JEI optional via -PwithJei=true, local path in build.gradle.

Server audit **76 checks, 0 failed**: all21 original panel formulas, actual solar→BatBox, actual MFSU→wand charger, real jar drains for all5 aspects, safe full buffers, persistence, roofs/day/night/rain/Nether/height, one-vis conversion/overflow/full-wand guard/relay API, processor outputs/bonus/progress/exclusions/sided input, actual TC arcane workbench (locked research rejected, unlocked craft spends vis and consumes9 panels), native crucible selection, shared IC2 recipe metadata.

Hidden silent client with IC2 + ASP + TC4 + JEI: all28 block models checked for missing textures; inspected panels/generators/jar trails, both GUI screens, material icons, EMT research graph, original text/arcane/infusion/crucible book pages, IC2 JEI runtime. Existing TC4 asset warning `textures/blocks/es_i_1 - copy.png` is unrelated to EMT; no EMT missing textures or exceptions. Server/client/build artifacts in versions/0.1.0; no production changes.

Reproduce:
- Own test server run-server: port25583, offline, flat, eula=true, dist4. `JAVA_HOME=/usr/lib/jvm/java-21-openjdk JAVA_TOOL_OPTIONS=-Demt.audit=server ./gradlew runServer -q`
- Client world run-audit/saves/EMTAudit copied from stopped run-server/world; run-audit/options.txt has accessibility disabled and all volumes0. `./audit-client.sh -PwithJei=true` → run-audit/audit/*.png.
- `./build.sh` → build/libs/electro-magic-tools-modern-0.1.0.jar.

## Next work — not implemented

1. Electric tools: iron/diamond/thaumium omnitools, diamond/thaumium/stream chainsaws, thaumium/rockbreaker drills, electric hoe of growth, Mjolnir/broken tainted/electric Mjolnir. Electric scribing tools need a TC4 integration point (scribing/research table currently recognizes its own tools).
2. Armor: electric/nano/quantum goggles (revealing + discounts/night vision/quantum functions), solar goggles, electric/nano/quantum traveller boots, feather/thaumium/nano/quantum wings, infused quantum chestplate. Use shared IC2 charge/damage/key APIs and TC4 revealing/discount hooks; verify multiplayer flight (IC2 Util.resetPlayerInAirTime).
3. Foci: explosion, Christmas, shield, energy ball, charging, wand charging; their entities. Extend TC4 focus extension points if missing, do not fake them with unrelated items.
4. Baubles: One Ring, armor/inventory charging rings; TC4/baubles integration, dungeon loot.
5. Portable node; shield block; electric clouds; UU infusion/transmutations and all remaining recipes/researches/config switches/aspects/native cluster harvest hooks.
6. Gravi Suit project/reference from shared ic2_port.md; then real combined all-addon compatibility audit and dedicated-server GUI/flight checks. Do not call all of EMT or all-addon compatibility complete after this batch.

Reference: related/emt-reference/src (120 remapped1.7.10 classes), jar/assets/emt. Current takeover was explicitly requested while Claude idle, not a usage-limit reset. Keep this file and shared memory current. When returning work to Claude, stop edits and verify actual resumed tool work before idle.

## Codex batch 0.1.1 — ten electric tools (2026-10-16 21:31 Moscow)

Done: thaumium/rockbreaker drills, diamond/thaumium/stream chainsaws, iron/diamond/thaumium omnitools, electric hoe of growth, electric scribing tools. Original EU capacities/tiers/transfers/speeds/hit costs; compound tool tags, charged/empty creative stacks, optional enchantability, torch placement, shearing+mode switch, rockbreaker plane3x3/sneak single/paid1000EU native ore scan, stream furthest-log felling/attracted loot, growth hoe3x3till25EU/block and native bonemeal/TC tree growth250EU, real ink uses1EU (400capacity/5transfer), rechargeable without breaking. ToolsInBore option1EU for original applicable tools. All10 magic recipes, original early shaped recipes, charged10EU infusion outputs, research graph now26entries and38magic recipes. Remaining Mjolnir variants deferred with guardian entities.

Required TC4 dependency is now **0.17.173** (local build only). TC4Addons adds typed BoreTool, BlockBreakHook (dispatches through existing mixin at native destroyBlock), and component-bearing infusion output hooks. Public ElementalTools.harvest/furthest and ElementalToolsClient.startScan reuse original behavior. Native bore accepts/pays electric picks, stops for empty/cancelled targets, still damages ordinary picks normally. No TC4 production install/release.

Server **123/123**: real native3x3/sneak/limitedEU/protection event, native stream felling, sheep shearing+disabled mode, torch,9-block till+growth, native research ink/table creation, charged infusion outputs, actual native bore.mine EMT+IC2+vanilla regression. Fixed ray face capture to use current tick + valid target (old partial0 chose stale plane in fake/rapid-look cases). Hidden silent combined client passes all28machine models and10tool icons, research recipes and native scan payload/server payment; scanned ore visible through wall. Build passed; versions/0.1.1 archives. No publish/deploy.

Next batch: armor/goggles/traveller boots/wings/infused chest; then Mjolnir/foci/entities/rings/portable node/shield/electric clouds/UU recipes/research-disable options/complete compatibility→Gravi. No separate multiplayer EMT flight check yet. Current user explicitly requested continuous autonomous work until told to stop; active Codex goal continues between batches.
