from pathlib import Path
from PIL import Image, ImageDraw

ROOT = Path(__file__).resolve().parents[1]
ARMOR_DIR = ROOT / "src/main/resources/assets/supersymmetry/textures/models/armor"
ITEM_DIR = ROOT / "src/main/resources/assets/supersymmetry/textures/item"
LEGACY = ROOT / "assets-legacy/susy/textures"

ARMOR_DIR.mkdir(parents=True, exist_ok=True)
ITEM_DIR.mkdir(parents=True, exist_ok=True)

SUIT_SOURCES = {
    "asbestos": LEGACY / "geo/asbestos_armor/all.png",
    "rebreather": LEGACY / "geo/rebreather_armor/all.png",
    "reflective": LEGACY / "geo/reflective_armor/all.png",
    "filtered": LEGACY / "geo/filtered_armor/all.png",
    "nomex": LEGACY / "geo/nomex_armor/all.png",
}

for family, source in SUIT_SOURCES.items():
    image = Image.open(source).convert("RGBA")
    image.save(ARMOR_DIR / f"{family}_layer_1.png")
    image.save(ARMOR_DIR / f"{family}_layer_2.png")

# The astronaut suit does not have a legacy atlas. Use the neutral asbestos atlas as a
# base and recolor its opaque pixels into a white/blue EVA suit palette.
astronaut = Image.open(SUIT_SOURCES["asbestos"]).convert("RGBA")
pixels = astronaut.load()
for y in range(astronaut.height):
    for x in range(astronaut.width):
        red, green, blue, alpha = pixels[x, y]
        if alpha:
            brightness = (red + green + blue) / (3 * 255)
            pixels[x, y] = (
                int(178 + 65 * brightness),
                int(193 + 55 * brightness),
                int(211 + 44 * brightness),
                alpha,
            )
astronaut.save(ARMOR_DIR / "astronaut_layer_1.png")
astronaut.save(ARMOR_DIR / "astronaut_layer_2.png")

for name, source_name in {
    "gas_mask": "gas_mask.png",
    "gas_tank": "gas_tank.png",
}.items():
    source = Image.open(LEGACY / "armor" / source_name).convert("RGBA")
    source.save(ARMOR_DIR / f"{name}_layer_1.png")

# The simple mask is a compact version of the legacy gas mask texture.
gas_mask = Image.open(LEGACY / "armor/gas_mask.png").convert("RGBA")
simple_mask = Image.new("RGBA", gas_mask.size)
simple_mask.alpha_composite(gas_mask.crop((8, 8, 56, 56)), (8, 8))
simple_mask.save(ARMOR_DIR / "simple_gas_mask_layer_1.png")

# Generate legible inventory icons from the active armor atlases. They are separate from the
# worn-model UVs but share the same family palette, which keeps Registrate's default item models valid.
def icon(source: Image.Image, kind: str) -> Image.Image:
    canvas = Image.new("RGBA", (32, 32))
    draw = ImageDraw.Draw(canvas)
    if kind == "helmet":
        crop = source.crop((0, 0, source.width, source.height // 2)).resize((26, 18), Image.Resampling.NEAREST)
        canvas.alpha_composite(crop, (3, 2))
        draw.rectangle((5, 19, 26, 27), outline=(220, 230, 240, 255), width=1)
    elif kind == "chest":
        crop = source.resize((24, 24), Image.Resampling.NEAREST)
        canvas.alpha_composite(crop, (4, 3))
    elif kind == "legs":
        crop = source.resize((22, 22), Image.Resampling.NEAREST)
        canvas.alpha_composite(crop, (5, 4))
        draw.line((10, 20, 10, 29), fill=(220, 230, 240, 255), width=3)
        draw.line((21, 20, 21, 29), fill=(220, 230, 240, 255), width=3)
    else:
        crop = source.resize((20, 16), Image.Resampling.NEAREST)
        canvas.alpha_composite(crop, (6, 8))
        draw.rectangle((5, 22, 14, 28), outline=(220, 230, 240, 255), width=1)
        draw.rectangle((17, 22, 26, 28), outline=(220, 230, 240, 255), width=1)
    return canvas

icons = {
    "simple_gas_mask": (simple_mask, "helmet"),
    "gas_mask": (gas_mask, "helmet"),
    "gas_tank": (Image.open(LEGACY / "armor/gas_tank.png").convert("RGBA"), "chest"),
}

for family in ["asbestos", "reflective", "nomex", "astronaut"]:
    source = Image.open(ARMOR_DIR / f"{family}_layer_1.png").convert("RGBA")
    display = "mask" if family != "astronaut" else "helmet"
    icons[f"{family}_{display}"] = (source, "helmet")
    icons[f"{family}_chestplate"] = (source, "chest")
    icons[f"{family}_leggings"] = (source, "legs")
    icons[f"{family}_boots"] = (source, "boots")

for family in ["rebreather", "filtered"]:
    source = Image.open(ARMOR_DIR / f"{family}_layer_1.png").convert("RGBA")
    icons[f"{family}_tank"] = (source, "chest")

for name, (source, kind) in icons.items():
    icon(source, kind).save(ITEM_DIR / f"{name}.png")
