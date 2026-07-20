#!/usr/bin/env python3
"""Phase 6 decorative-block texture migration.

Promotes the 1.12.2 legacy art (parked under assets-legacy/gregtech/textures/blocks/**)
into the active supersymmetry domain at textures/block/<block_name>.png, resolving the
1.12.2 -> 1.20.1 name differences. Pairs with the SusyBlocks decorative helpers, which
now derive their cube-all texture from the block name (supersymmetry:block/<name>).

Run from repo root:  python scripts/migrate_decorative_block_textures.py
"""
import os
import shutil
import sys

REPO = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
LEGACY = os.path.join(REPO, "assets-legacy", "gregtech", "textures", "blocks")
DEST = os.path.join(REPO, "src", "main", "resources", "assets",
                    "supersymmetry", "textures", "block")

# block_name (active) -> legacy relative path under assets-legacy/gregtech/textures/blocks/
MAPPING = {
    # ---- createStoneDecorativeBlock (susy_stones / hardened_blocks / bmrf_blocks) ----
    "grey_industrial_concrete": "susy_stones/industrial_concrete_grey",
    "mossy_industrial_concrete": "susy_stones/industrial_concrete_mossy",
    "silver_industrial_concrete": "susy_stones/industrial_concrete_silver",
    "white_industrial_concrete": "susy_stones/industrial_concrete_white",
    "dotted_panel": "susy_stones/dotted_panel",
    "dotted_panel_border": "susy_stones/dotted_panel_bordered",
    "dotted_panel_comb": "susy_stones/dotted_panel_comb",
    "dotted_panel_grid": "susy_stones/dotted_panel_grid",
    "industrial_cinder_bricks": "susy_stones/industrial_cinder_bricks",
    "industrial_cinder_bricks_cement": "susy_stones/industrial_cinder_bricks_cement",
    "industrial_cinder_bricks_cement_gray": "susy_stones/industrial_cinder_bricks_cement_grey",
    "industrial_cinder_bricks_dark": "susy_stones/industrial_cinder_bricks_dark",
    "industrial_cinder_bricks_dark_gray": "susy_stones/industrial_cinder_bricks_dark_grey",
    "industrial_cinder_bricks_gray": "susy_stones/industrial_cinder_bricks_grey",
    "smooth_industrial_concrete": "susy_stones/smooth_industrial_concrete",
    "smooth_industrial_concrete_gray": "susy_stones/smooth_industrial_concrete_grey",
    "smooth_industrial_concrete_white": "susy_stones/smooth_industrial_concrete_white",
    "lunar_concrete_smooth": "susy_stones/lunar_concrete_smooth",
    "lunar_concrete_bricks": "susy_stones/lunar_concrete_bricks",
    "lunar_concrete_bricks_cracked": "susy_stones/lunar_concrete_bricks_cracked",
    "lunar_concrete_bricks_small": "susy_stones/lunar_concrete_bricks_small",
    "lunar_concrete_bricks_square": "susy_stones/lunar_concrete_bricks_square",
    "lunar_concrete_chiseled": "susy_stones/lunar_concrete_chiseled",
    "lunar_concrete_cobble": "susy_stones/lunar_concrete_cobble",
    "lunar_concrete_polished": "susy_stones/lunar_concrete_polished",
    "lunar_concrete_tiled": "susy_stones/lunar_concrete_tiled",
    "lunar_concrete_tiled_small": "susy_stones/lunar_concrete_tiled_small",
    "lunar_concrete_windmill_a": "susy_stones/lunar_concrete_windmill_a",
    "lunar_concrete_windmill_b": "susy_stones/lunar_concrete_windmill_b",
    "hardened_lair10": "hardened_blocks/lair10",
    "hardened_kryp8": "hardened_blocks/kryp8",
    "hardened_kryp7": "hardened_blocks/kryp7",
    "hardened_lair11": "hardened_blocks/lair11",
    "hardened_lair7": "hardened_blocks/lair7",
    "industrial_concrete_hardened": "susy_stones/industrial_concrete_bricks",
    "military_concrete_cobblestone_hardened": "susy_stones/military_concrete_cobble",
    "military_concrete_hardened": "susy_stones/military_concrete",
    "bmrf1": "bmrf_blocks/bmrf_block_1",
    "bmrf2": "bmrf_blocks/bmrf_block_2",
    "bmrf3": "bmrf_blocks/bmrf_block_3",
    "bmrf4": "bmrf_blocks/bmrf_block_4",
    "bmrf5": "bmrf_blocks/bmrf_block_5",
    "bmrf6": "bmrf_blocks/bmrf_block_6",
    "bmrf7": "bmrf_blocks/bmrf_block_7",
    "bmrf8": "bmrf_blocks/bmrf_block_8",
    "bmrf9": "bmrf_blocks/bmrf_block_9",
    # susy_stone_* were 1.12.2 multi-variant selectors; collapse to their legacy default.
    "susy_stone_smooth": "susy_stones/gabbro",
    "susy_stone_cobble": "susy_stones/gabbro_cobble",
    "susy_stone_bricks": "susy_stones/gabbro_bricks",

    # ---- createMetalDecorativeBlock (resource / custom_sheets / structural / flares) ----
    "bauxite_block": "resource/bauxite",
    "caliche_block": "resource/caliche",
    "non_marine_evaporite_block": "resource/non_marine_evaporite",
    "halide_evaporite_block": "resource/halide_evaporite",
    "sulfate_evaporite_block": "resource/sulfate_evaporite",
    "carbonate_evaporite_block": "resource/carbonate_evaporite",
    "monazite_alluvial_block": "resource/monazite_alluvial",
    "bastnasite_alluvial_block": "resource/bastnasite_alluvial",
    "euxenite_alluvial_block": "resource/euxenite_alluvial",
    "xenotime_alluvial_block": "resource/xenotime_alluvial",
    "platinum_placer_block": "resource/platinum_placer",
    "gold_alluvial_block": "resource/gold_alluvial",
    "phosphorite_block": "resource/phosphorite",
    "potash_block": "resource/potash",
    "sulfur_block": "resource/sulfur",
    "coal_block": "resource/coal",
    "native_copper_block": "resource/native_copper",
    "anthracite_block": "resource/anthracite",
    "lignite_block": "resource/lignite",
    "dark_white_metal_sheet": "custom_sheets/darker_white_metal_sheet",
    "lighter_gray_metal_sheet": "custom_sheets/lighter_gray_metal_sheet",
    "decorative_copper_sheet": "custom_sheets/space_grade_decorative_copper",
    "decorative_copper_bricks": "custom_sheets/space_grade_decorative_copper_bricks",
    "base_structural_block": "decorative/structural_blocks/base_structural_block",
    "structural_block_low": "decorative/structural_blocks/structural_block_low",
    "structural_block_lowlight": "decorative/structural_blocks/structural_block_lowlight",
    "structural_block_danger_a": "decorative/structural_blocks/structural_block_danger_a",
    "structural_block_danger_b": "decorative/structural_blocks/structural_block_danger_b",
    "structural_block_danger_c": "decorative/structural_blocks/structural_block_danger_c",
    "structural_block_danger_d": "decorative/structural_blocks/structural_block_danger_d",
    "structural_block_column": "decorative/structural_blocks/structural_block_column",
    "structural_block_column_old": "decorative/structural_blocks/structural_block_column_old",
    "structural_block_light": "decorative/structural_blocks/structural_block_light",
    "structural_block_light_broken": "decorative/structural_blocks/structural_block_light_broken",
    "structural_block_light_cable": "decorative/structural_blocks/structural_block_light_cable",
    "structural_block_instruments": "decorative/structural_blocks/structural_block_instruments",
    "structural_block_sign_0": "decorative/structural_blocks/structural_block_sign_0",
    "structural_block_sign_1": "decorative/structural_blocks/structural_block_sign_1",
    "structural_block_sign_2": "decorative/structural_blocks/structural_block_sign_2",
    "structural_block_exposed": "decorative/structural_blocks/structural_block_exposed",
    "structural_block_exposed_1": "decorative/structural_blocks/structural_block_exposed_1",
    "structural_block_exposed_2": "decorative/structural_blocks/structural_block_exposed_2",
    "structural_block_danger_sign": "decorative/structural_blocks/structural_block_danger_sign",
    "structural_block_cable": "decorative/structural_blocks/structural_block_cable",
    "structural_block_cable_horizontal": "decorative/structural_blocks/structural_block_cable_horizontal",
    "structural_block_cable_junction": "decorative/structural_blocks/structural_block_cable_junction",
    "structural_block_pipocalypse": "decorative/structural_blocks/structural_block_pipocalypse",
    "structural_block_vent": "decorative/structural_blocks/structural_block_vent",
    "structural_block_vent_broken": "decorative/structural_blocks/structural_block_vent_broken",

    # ---- createWoolDecorativeBlock (wool/) ; 1.12.2 "silver" == modern light_gray ----
    "white_fake_wool": "wool/wool_colored_white",
    "orange_fake_wool": "wool/wool_colored_orange",
    "magenta_fake_wool": "wool/wool_colored_magenta",
    "light_blue_fake_wool": "wool/wool_colored_light_blue",
    "yellow_fake_wool": "wool/wool_colored_yellow",
    "lime_fake_wool": "wool/wool_colored_lime",
    "pink_fake_wool": "wool/wool_colored_pink",
    "gray_fake_wool": "wool/wool_colored_gray",
    "light_gray_fake_wool": "wool/wool_colored_silver",
    "cyan_fake_wool": "wool/wool_colored_cyan",
    "purple_fake_wool": "wool/wool_colored_purple",
    "blue_fake_wool": "wool/wool_colored_blue",
    "brown_fake_wool": "wool/wool_colored_brown",
    "green_fake_wool": "wool/wool_colored_green",
    "red_fake_wool": "wool/wool_colored_red",
    "black_fake_wool": "wool/wool_colored_black",

    # ---- createSandDecorativeBlock (susy_stones/regolith/) ----
    "highland_regolith": "susy_stones/regolith/highland",
    "lowland_regolith": "susy_stones/regolith/lowland",

    # ---- createUnbreakableDecorativeBlock (deposit/) ----
    "orthomagmatic_deposit": "deposit/orthomagmatic",
    "metamorphic_deposit": "deposit/metamorphic",
    "sedimentary_deposit": "deposit/sedimentary",
    "hydrothermal_deposit": "deposit/hydrothermal",
    "alluvial_deposit": "deposit/alluvial",
    "magmatic_hydrothermal_deposit": "deposit/magmatic_hydrothermal",
    # deposit/ice_cap.png does not exist in legacy art -> neutral rock stand-in (see FALLBACKS).
    "ice_cap_deposit": "deposit/sedimentary",
    "evaporite_deposit": "deposit/evaporite",
}

# Supporting faces and emissive overlays used by the special-case models in SusyBlocks.
# They are not standalone block registrations, but must live in the active resource domain.
EXTRA_TEXTURES = {
    "structural_block_lowlight_bloom": "decorative/structural_blocks/structural_block_lowlight_bloom",
    "structural_block_lowlight_top_bloom": "decorative/structural_blocks/structural_block_lowlight_top_bloom",
    "structural_block_light_bloom": "decorative/structural_blocks/structural_block_light_bloom",
    "structural_block_instruments_bloom": "decorative/structural_blocks/structural_block_instruments_bloom",
    "bandit_flare_bottom": "raid_flare/bandit_flare/flare_bottom",
    "bandit_flare_side": "raid_flare/bandit_flare/flare_side",
    "bandit_flare_top": "raid_flare/bandit_flare/flare_top",
    "fed_flare_bottom": "raid_flare/fed_flare/flare_bottom",
    "fed_flare_side": "raid_flare/fed_flare/flare_side",
    "fed_flare_top": "raid_flare/fed_flare/flare_top",
}

# Blocks whose legacy source is a deliberate stand-in (legacy art genuinely missing).
FALLBACKS = {"ice_cap_deposit"}


def main():
    os.makedirs(DEST, exist_ok=True)
    copied, missing, notes = 0, [], []
    sources = MAPPING | EXTRA_TEXTURES
    for name, rel in sorted(sources.items()):
        src = os.path.join(LEGACY, rel.replace("/", os.sep) + ".png")
        dst = os.path.join(DEST, name + ".png")
        if not os.path.isfile(src):
            missing.append(f"{name} <- {rel}.png")
            continue
        shutil.copyfile(src, dst)
        metadata = src + ".mcmeta"
        if os.path.isfile(metadata):
            shutil.copyfile(metadata, dst + ".mcmeta")
        copied += 1
        if name in FALLBACKS:
            notes.append(f"{name} uses stand-in {rel} (legacy art missing)")
    print(f"copied {copied}/{len(sources)} decorative textures -> {DEST}")
    for n in notes:
        print("  note:", n)
    if missing:
        print("MISSING legacy source:", file=sys.stderr)
        for m in missing:
            print("  " + m, file=sys.stderr)
        sys.exit(1)


if __name__ == "__main__":
    main()
