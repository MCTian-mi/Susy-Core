@file:Suppress("AvoidDuplicateDependencies", "UnstableApiUsage")

plugins {
    alias(conventions.plugins.repositories)
    alias(conventions.plugins.minecraft)
    alias(conventions.plugins.shadow)
    alias(conventions.plugins.idea)
    alias(conventions.plugins.test)
    alias(conventions.plugins.jvm)
}

// During the 1.12.2 -> 1.20.1 port, not-yet-ported legacy sources were kept in-tree
// and subtracted from compilation here. That era is over: every 1.12.2-only file has
// been retired to the git-tracked legacy/ tree (mirroring package paths), which is not
// a source root. Everything under src/main/java now compiles against GTCEu-Modern —
// there are no per-file excludes. If a future phase needs a temporary exclude, add it
// below (and prefer retiring the file to legacy/ instead).

dependencies {
    compileOnlyApi(deps.jspecify)
    compileOnlyApi(deps.annotations)

    modCompileOnlyApi(deps.embeddium)
    modCompileOnlyApi(deps.bundles.jei)
    modCompileOnlyApi(deps.bundles.rei)
    modCompileOnlyApi(deps.emi)
    modCompileOnlyApi(deps.ldlib)
    modCompileOnlyApi(deps.registrate)
    modCompileOnlyApi(deps.configuration)
    modCompileOnlyApi(deps.geckolib)
    modCompileOnlyApi(deps.flywheel.forge.api)
    modCompileOnlyApi(deps.ponder)
    modCompileOnlyApi(variantOf(deps.gtceu) { classifier("slim") })
    modCompileOnlyApi(variantOf(deps.create) { classifier("slim") })

    modRuntimeOnly(deps.configuration) // Forces a newer version of ldlib that contains ConfigFormats#YAML
    modRuntimeOnly(deps.ldlib) // Forces a newer version of ldlib that contains SliderWidget
    modRuntimeOnly(deps.embeddium)
    modRuntimeOnly(deps.jei.forge.impl)
//    modRuntimeOnly(deps.bundles.rei.runtime)
//    modRuntimeOnly(deps.emi)
    modRuntimeOnly(deps.bundles.jade)
    modRuntimeOnly(deps.spark)
    modRuntimeOnly(deps.gtceu)
    modRuntimeOnly(deps.geckolib)
    modRuntimeOnly(deps.ponder)
    modRuntimeOnly(variantOf(deps.create) { classifier("slim") })
    modRuntimeOnly(deps.flywheel.forge)
}