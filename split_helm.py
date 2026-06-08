"""
Erzeugt aus Swat Helm.json:
  - swat_helm.json          : Vollmodell (alle 76 Elemente) – für Inventar/Drop
  - swat_helm_g0.json       : Gruppe 0  Basis          (0-45)
  - swat_helm_g1.json       : Gruppe 1  NV unten       (46-56)
  - swat_helm_g2.json       : Gruppe 2  Visier Oben    (57-60)
  - swat_helm_g3.json       : Gruppe 3  NV Oben        (61-71)
  - swat_helm_g4.json       : Gruppe 4  Viser Unten    (72-75)

HelmLayer rendert Gruppen sequenziell über denselben Z-Buffer,
damit Innen­flächen korrekt durch den Helmkorpus verdeckt werden.
Run from project root: python split_helm.py
"""
import json, os, shutil

SRC = "Swat Helm.json"
OUT = "src/main/resources/assets/cgm/models/item"
TEX = "src/main/resources/assets/cgm/textures/items"

with open(SRC, encoding="utf-8") as f:
    data = json.load(f)

data["textures"] = {
    "0":        "cgm:items/swat_helm",
    "1":        "cgm:items/visier",
    "particle": "cgm:items/swat_helm"
}

elements = data["elements"]

GROUPS = {
    "swat_helm_g0": list(range(0,  46)),  # Basis
    "swat_helm_g1": list(range(46, 57)),  # NV unten
    "swat_helm_g2": list(range(57, 61)),  # Visier Oben
    "swat_helm_g3": list(range(61, 72)),  # NV Oben
    "swat_helm_g4": list(range(72, 76)),  # Viser Unten
}

os.makedirs(OUT, exist_ok=True)

# Vollmodell für Inventar
full_model = {
    "credit":       data.get("credit", "Made with Blockbench"),
    "texture_size": data.get("texture_size", [64, 64]),
    "textures":     data["textures"],
    "elements":     elements,
    "display":      data.get("display", {}),
}
with open(os.path.join(OUT, "swat_helm.json"), "w", encoding="utf-8") as f:
    json.dump(full_model, f, indent=2)
print(f"  swat_helm.json  ({len(elements)} elements, full)")

# Atomare Gruppenmodelle
for name, idx in GROUPS.items():
    model = {
        "credit":       data.get("credit", "Made with Blockbench"),
        "texture_size": data.get("texture_size", [64, 64]),
        "textures":     data["textures"],
        "elements":     [elements[i] for i in idx],
        "display":      data.get("display", {}),
    }
    with open(os.path.join(OUT, f"{name}.json"), "w", encoding="utf-8") as f:
        json.dump(model, f, indent=2)
    print(f"  {name}.json  ({len(idx)} elements)")

# Texturen
os.makedirs(TEX, exist_ok=True)
for src, dst in [("Swat Helm.png", "swat_helm.png"), ("Visier.png", "visier.png")]:
    shutil.copy2(src, os.path.join(TEX, dst))
    print(f"  copied {src} -> {TEX}/{dst}")

print("Done.")
