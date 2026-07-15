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
// SuSyValues), api/registry, common/data, config. When a subtree is ported, delete
// its line here (and git-rm the superseded legacy files).
val legacySourceExcludes = listOf(
    "api/blocks/",
    "api/capability/",
    "api/event/",
    "api/fluids/",
    "api/gui/",
    "api/items/",
    "api/metatileentity/",
    "api/mixin/",
    "api/particle/",
    "api/recipes/",
    "api/recycling/",
    "api/rocketry/",
    "api/sound/",
    "api/space/",
    "api/stockinteraction/",
    "api/unification/",
    "api/util/",
    "asm/",
    "client/",
    "common/blocks/",
    "common/command/",
    "common/covers/",
    "common/entities/",
    "common/event/",
    "common/faction/",
    "common/item/",
    "common/materials/",
    "common/metatileentities/",
    "common/mui/",
    "common/network/",
    "common/recipes/",
    "common/rocketry/",
    "common/tileentities/",
    "common/util/",
    "common/world/",
    "integration/",
    "loaders/",
    "mixins/",
    "modules/",
)

val susyPackage = "io/github/symmetricdevs/supersymmetry/"
tasks.withType<JavaCompile>().configureEach {
    legacySourceExcludes.forEach { exclude("$susyPackage$it**") }
    // Legacy files sitting directly under a package (not in an excluded subtree).
    exclude("${susyPackage}api/SusyLog.java")
    exclude("${susyPackage}common/CommonProxy.java")
    exclude("${susyPackage}common/EventHandlers.java")
    exclude("${susyPackage}common/SusyMetaEntities.java")
    // Deferred ImmersiveRailroading bridge (no 1.20.1 IR port; rocketry/space scope).
    exclude("cam72cam/**")
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