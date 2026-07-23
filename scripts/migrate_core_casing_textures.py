#!/usr/bin/env python3
"""Phase 6 core multiblock and grinder-casing texture migration.

Copies the selected static 1.12.2 casing art into the active supersymmetry namespace,
keeping legacy filename differences explicit. Connected-texture sheets, animation metadata,
and all deferred casing families remain outside this focused migration.

Run from the repository root:
    python scripts/migrate_core_casing_textures.py
"""
import os
import shutil
import sys

REPO = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
LEGACY_BLOCKS = os.path.join(REPO, "assets-legacy", "gregtech", "textures", "blocks")
DEST_BLOCKS = os.path.join(REPO, "src", "main", "resources", "assets", "supersymmetry",
                           "textures", "block", "casings")

# destination family/name -> legacy texture path relative to LEGACY_BLOCKS, without .png
MAPPING = {
    "multiblock/aluminium_gearbox": "multiblock_casing/aluminium_gearbox",
    "multiblock/coalescence_plate": "multiblock_casing/coalescence_plate",
    "multiblock/copper_casing_pipe": "multiblock_casing/copper_casing_pipe",
    "multiblock/hydrostatic_casing": "multiblock_casing/hydrostatic_casing",
    "multiblock/monel_casing": "multiblock_casing/monel_500_casing",
    "multiblock/monel_casing_pipe": "multiblock_casing/monel_500_casing_pipe",
    "multiblock/sieve_tray": "multiblock_casing/sieve_tray",
    "multiblock/silicon_carbide_casing": "multiblock_casing/silicon_carbide_casing",
    "multiblock/structural_packing": "multiblock_casing/structural_packing",
    "multiblock/synthetic_mullite_refractory": "multiblock_casing/synthetic_mullite_refractory",
    "multiblock/tabular_alumina_refractory": "multiblock_casing/tabular_alumina_refractory",
    "multiblock/ulv_structural_casing": "multiblock_casing/ulv_structural_casing",
    "grinder_casing/abrasion_resistant_casing": "casings/grinder_casing/abrasion_resistant_casing",
    "grinder_casing/hydraulic_mechanical_gearbox": "casings/grinder_casing/hydraulic_mechanical_gearbox",
    "grinder_casing/intermediate_diaphragm": "casings/grinder_casing/intermediate_diaphragm",
    "grinder_casing/wear_resistant_lined_mill_shell": "casings/grinder_casing/wear_resistant_lined_mill_shell",
    "grinder_casing/wear_resistant_lined_shell_head": "casings/grinder_casing/wear_resistant_lined_shell_head",
}


def main():
    missing = []
    copied = 0
    for destination, source in sorted(MAPPING.items()):
        src = os.path.join(LEGACY_BLOCKS, source.replace("/", os.sep) + ".png")
        dst = os.path.join(DEST_BLOCKS, destination.replace("/", os.sep) + ".png")
        if not os.path.isfile(src):
            missing.append(f"{destination} <- {source}.png")
            continue
        os.makedirs(os.path.dirname(dst), exist_ok=True)
        shutil.copyfile(src, dst)
        metadata = src + ".mcmeta"
        if os.path.isfile(metadata):
            shutil.copyfile(metadata, dst + ".mcmeta")
        copied += 1

    if missing:
        print("MISSING legacy source:", file=sys.stderr)
        for entry in missing:
            print("  " + entry, file=sys.stderr)
        sys.exit(1)
    print(f"copied {copied}/{len(MAPPING)} core casing textures -> {DEST_BLOCKS}")


if __name__ == "__main__":
    main()
