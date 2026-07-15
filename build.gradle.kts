@file:Suppress("AvoidDuplicateDependencies")

plugins {
    alias(conventions.plugins.repositories)
    alias(conventions.plugins.minecraft)
    alias(conventions.plugins.shadow)
    alias(conventions.plugins.idea)
    alias(conventions.plugins.test)
    alias(conventions.plugins.jvm)
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