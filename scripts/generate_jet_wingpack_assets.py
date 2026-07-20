from pathlib import Path
from PIL import Image

ROOT = Path(__file__).resolve().parents[1]
ARMOR_DIR = ROOT / "src/main/resources/assets/supersymmetry/textures/models/armor"
LEGACY = ROOT / "assets-legacy/susy/textures/armor/jet_wingpack.png"

ARMOR_DIR.mkdir(parents=True, exist_ok=True)

# Promote the legacy 64x64 jet wingpack atlas to the active worn-armor texture. The
# chestplate slot samples layer_1, matching the breathing-armor naming convention.
image = Image.open(LEGACY).convert("RGBA")
image.save(ARMOR_DIR / "jet_wingpack_layer_1.png")
