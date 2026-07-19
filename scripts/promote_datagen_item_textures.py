from pathlib import Path

from PIL import Image, ImageDraw

ROOT = Path(__file__).resolve().parents[1]
SOURCE = ROOT / "assets-legacy/gregtech/textures/items/metaitems"
TARGET = ROOT / "src/main/resources/assets/supersymmetry/textures/item"

TEXTURES = {
    "catalyst_bed_support_grid": "catalyst_bed_support_grid",
    "conveyor_steam": "conveyor.steam",
    "pump_steam": "pump.steam",
    "air_vent": "air_vent",
    "track_segment": "track_segment",
    "restrictive_filter": "restrictive_filter",
    "earth_orbital_scrap": "orbital.scrap.earth",
    "code_breacher": "code_breacher",
    "entity_tagger": "entity_tagger",
    "faction_radio": "faction_radio",
    "data_card": "data_card",
    "data_card_active": "data_card.active",
    "data_card_master_blueprint": "data_card.master_blueprint",
    "rocket_configurer": "rocket_config",
    "shape_mold_target": "shape.mold.target",
    "cargo_drone_basic": "cargo_drone.basic",
    "cargo_drone_advanced": "cargo_drone.advanced",
    "location_card": "location_card",
    "cargo_drone_elite": "cargo_drone.elite",
    "jet_wingpack": "jet_wingpack",
}


def draw_tungsten_electrode() -> Image.Image:
    image = Image.new("RGBA", (32, 32))
    draw = ImageDraw.Draw(image)
    draw.polygon([(13, 3), (19, 3), (22, 25), (10, 25)], fill=(65, 79, 92, 255))
    draw.polygon([(14, 4), (17, 4), (19, 24), (13, 24)], fill=(173, 188, 198, 255))
    draw.rectangle((9, 25, 23, 29), fill=(55, 61, 69, 255))
    draw.rectangle((11, 26, 21, 27), fill=(189, 199, 205, 255))
    return image


def draw_padding_cloth() -> Image.Image:
    image = Image.new("RGBA", (32, 32))
    draw = ImageDraw.Draw(image)
    draw.rounded_rectangle((4, 5, 27, 27), radius=3, fill=(200, 191, 167, 255), outline=(107, 97, 82, 255))
    for offset in range(7, 27, 5):
        draw.line((offset, 6, offset - 5, 26), fill=(160, 150, 129, 255))
        draw.line((6, offset, 26, offset - 5), fill=(226, 217, 190, 255))
    return image


TARGET.mkdir(parents=True, exist_ok=True)
for target, source in TEXTURES.items():
    Image.open(SOURCE / f"{source}.png").convert("RGBA").save(TARGET / f"{target}.png")

draw_tungsten_electrode().save(TARGET / "tungsten_electrode.png")
draw_padding_cloth().save(TARGET / "padding_cloth.png")
