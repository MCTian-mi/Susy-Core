@file:Suppress("AvoidDuplicateDependencies")

plugins {
    alias(conventions.plugins.repositories)
    alias(conventions.plugins.minecraft)
    alias(conventions.plugins.shadow)
    alias(conventions.plugins.idea)
    alias(conventions.plugins.test)
    alias(conventions.plugins.jvm)
}

// During the 1.12.2 -> 1.20.1 port, the not-yet-ported legacy sources (still the
// 1.12.2 tree, package `supersymmetry`) do not compile against GTCEu-Modern. They
// are progressively ported and re-included. We exclude each NOT-yet-ported legacy
// subtree; everything left (the package root + the already-ported subtrees below)
// is compiled. NOTE: Gradle excludes override includes, so we subtract subtrees
// rather than exclude-root-then-include.
//
// Already ported (kept): the package root (Supersymmetry/SupersymmetryGTAddon/
// SuSyValues), api/registry, api/unification, common/data, common/materials,
// config. When a subtree is ported, delete its line here (and git-rm the
// superseded legacy files).
// All subtrees now use the new package io.github.symmetricdevs.supersymmetry.*.
// The legacy exclusions have been cleared as each subtree was ported.
// Remaining exclusions are per-file for 1.12.2-only sources or deferred subsystems.
val legacySourceExcludes = listOf<String>()

val susyPackage = "io/github/symmetricdevs/supersymmetry/"
tasks.withType<JavaCompile>().configureEach {
    legacySourceExcludes.forEach { exclude("$susyPackage$it**") }
    // Legacy files sitting directly under a package (not in an excluded subtree).
    exclude("${susyPackage}api/SusyLog.java") // 1.12.2
    // EventHandlers.java is ported — no longer excluded.
    // Phase 4 machines: the legacy common/metatileentities/** tree is superseded by
    // common/data/SusyMachines.java and is deleted as it is ported. Exclude only the
    // legacy sources kept for reference in later sub-phases (4b/4c + deferred scope).
    // Individual per-file exclusions — all subtrees are fully ported.
    // Only files that still have 1.12.2-only references or MUI2 dependencies are excluded.
    exclude("${susyPackage}common/metatileentities/SuSyMetaTileEntities.java") // 1.12.2 registry checklist
    // Deferred ImmersiveRailroading bridge (no 1.20.1 IR port; rocketry/space scope).
    exclude("cam72cam/**")

    // Phase 5: remove directory-level exclusions as subtrees are ported.
    // Keep per-file excludes for individual legacy files in ported directories.

    // api/util/ — all files ported to new package; DataStorageLoader etc. are 1.12.2 only.
    exclude("${susyPackage}api/util/DataStorageLoader.java") // 1.12.2
    exclude("${susyPackage}api/util/ElytraFlyingUtils.java") // 1.12.2
    exclude("${susyPackage}api/util/FisherPlane.java") // 1.12.2
    exclude("${susyPackage}api/util/MaterialBlockModelLoader.java") // 1.12.2
    // RenderMaskManager.java is ported — no longer excluded.
    exclude("${susyPackage}api/util/StructAnalysis.java") // 1.12.2

    // client/renderer/ — deferred unported files (codechicken.lib, sussypatches, 1.12.2 API)
    exclude("${susyPackage}client/renderer/textures/SuSyConnectedTextures.java")
    exclude("${susyPackage}client/renderer/textures/custom/ExtenderRender.java")
    exclude("${susyPackage}client/renderer/particles/**")
    exclude("${susyPackage}client/renderer/sky/SkyRendererMoon.java")
    exclude("${susyPackage}client/renderer/handler/entity/RocketModel.java")
    // VariantCoverableBlockRenderer + TileEntityCoverable: codechicken.lib rendering pipeline (deferred to Phase 5)
    exclude("${susyPackage}client/renderer/handler/VariantCoverableBlockRenderer.java")
    exclude("${susyPackage}common/tileentities/TileEntityCoverable.java")

    // common/cover/ — fully ported (new files in common/cover/, NOT common/covers/)

    // Phase 7: capability/impl/ — custom RecipeLogic subclasses that extend
    // 1.12.2 base classes (RecipeLogicEnergy, AbstractRecipeLogic, etc.) which
    // have no Modern equivalent. These need re-derivation on the Modern
    // RecipeLogic/machine.trait pipeline.
    exclude("${susyPackage}api/capability/impl/**")

    // Phase 7: api/gui/SusyGuiTextures.java — uses SteamTexture/TextureArea
    // (removed from GTCEu-Modern) and MUI2 UITexture.
    exclude("${susyPackage}api/gui/SusyGuiTextures.java")

    // Phase 7: MUI2-dependent files (com.cleanroommc.modularui removed).
    exclude("${susyPackage}common/mui/**")

    // Phase 7: api/metatileentity/logistics/ — MetaTileEntityDelegator (MUI2 + logistics)
    exclude("${susyPackage}api/metatileentity/logistics/**")

    // Phase 7: stock interaction (MUI2 + railcraft dependency)
    exclude("${susyPackage}api/stockinteraction/**")

    // Phase 7: covers with MUI2 references
    exclude("${susyPackage}common/covers/CoverSteamConveyor.java")
    exclude("${susyPackage}common/covers/CoverSteamPump.java")

    // StrandShaperMachine is ported and is the base for strand-line controllers.
    // Keep it compiled so its subclasses can resolve it.

    // Phase 7: api/metatileentity/Mui2Utils.java — deprecated MUI2 utilities
    exclude("${susyPackage}api/metatileentity/Mui2Utils.java")

    // Phase 7: multiblockpart files with MUI2 UI code
    exclude("${susyPackage}common/metatileentities/multiblockpart/MetaTileEntityBeamLineHatch.java")
    exclude("${susyPackage}common/metatileentities/multiblockpart/MetaTileEntityComponentRedstoneController.java")
    exclude("${susyPackage}common/metatileentities/multiblockpart/MetaTileEntityComponentScanner.java")
    exclude("${susyPackage}common/metatileentities/multiblockpart/MetaTileEntityStrandBus.java")
    exclude("${susyPackage}common/metatileentities/multiblockpart/SusyMetaTileEntityDumpingHatch.java")
    exclude("${susyPackage}common/metatileentities/multiblockpart/SusyMetaTileEntityEnergyHatch.java")
    exclude("${susyPackage}common/metatileentities/multiblockpart/SusyMetaTileEntitySubstationEnergyHatch.java")
    exclude("${susyPackage}common/metatileentities/multiblockpart/MetaTileEntityPrimitiveItemBus.java")

    // Phase 7: rocket/space machines (deferred)
    exclude("${susyPackage}common/metatileentities/multi/rocket/**")
    exclude("${susyPackage}api/rocketry/**")

    // Phase 7: single-machine files with MUI2 references
    exclude("${susyPackage}common/metatileentities/single/electric/MetaTileEntityFuelCell.java")
    exclude("${susyPackage}common/metatileentities/single/electric/MetaTileEntityIncinerator.java")
    exclude("${susyPackage}common/metatileentities/single/electric/MetaTileEntityRTG.java")
    exclude("${susyPackage}common/metatileentities/single/electric/SuSyMetaTileEntitySingleCombustion.java")
    exclude("${susyPackage}common/metatileentities/single/railinterfaces/MetaTileEntityLocomotiveController.java")
    exclude("${susyPackage}common/metatileentities/single/railinterfaces/MetaTileEntityStockInteractor.java")
    exclude("${susyPackage}common/metatileentities/single/steam/SuSyLiquidBoiler.java")
    exclude("${susyPackage}common/metatileentities/single/steam/SuSySimpleSteamMetaTileEntity.java")
}

dependencies {
    compileOnlyApi(deps.jspecify)
    compileOnlyApi(deps.annotations)

    modCompileOnlyApi(deps.bundles.jei)
    modCompileOnlyApi(deps.bundles.rei)
    modCompileOnlyApi(deps.emi)
    modCompileOnlyApi(deps.ldlib)
    modCompileOnlyApi(deps.registrate)
    modCompileOnlyApi(deps.configuration)
    modCompileOnlyApi(variantOf(deps.gtceu) { classifier("slim") }) { isTransitive = false }

    modRuntimeOnly(deps.configuration) // Forces a newer version of ldlib that contains ConfigFormats#YAML
    modRuntimeOnly(deps.ldlib) // Forces a newer version of ldlib that contains SliderWidget
    modRuntimeOnly(deps.jei.forge.impl)
//    modRuntimeOnly(deps.bundles.rei.runtime)
//    modRuntimeOnly(deps.emi)
    modRuntimeOnly(deps.bundles.jade)
    modRuntimeOnly(deps.spark)
    modRuntimeOnly(deps.gtceu)
}