"""
Installiert die 4 manuell erstellten Helm-JSONs:
  NVobenVisobenSwat Helm (1).json   -> swat_helm.json          (default)
  NVuntenVisobenSwat Helm.json      -> swat_helm_nv.json        (NV aktiv)
  NVobenVisuntenSwat Helm (1).json  -> swat_helm_visier.json    (Visier unten)
  NVuntenVisuntenSwat Helm (1).json -> swat_helm_nv_visier.json (beides)

Cleant alte g0-g4 Gruppen-JSONs, passt Texturpfade an.
Run from project root: python install_helm.py
"""
import json, os, glob

OUT = "src/main/resources/assets/cgm/models/item"

MAPPING = {
    "NVobenVisobenSwat Helm (1).json":   "swat_helm.json",
    "NVuntenVisobenSwat Helm.json":      "swat_helm_nv.json",
    "NVobenVisuntenSwat Helm (1).json":  "swat_helm_visier.json",
    "NVuntenVisuntenSwat Helm (1).json": "swat_helm_nv_visier.json",
}

TEXTURES = {
    "0":        "cgm:items/swat_helm",
    "1":        "cgm:items/visier",
    "particle": "cgm:items/swat_helm"
}

# 1. alte Gruppen-JSONs löschen
for pat in ["swat_helm_g*.json", "swat_helm_nv.json",
            "swat_helm_visier.json", "swat_helm_nv_visier.json"]:
    for f in glob.glob(os.path.join(OUT, pat)):
        os.remove(f)
        print(f"  deleted {f}")

# 2. neue JSONs einpflegen
for src, dst in MAPPING.items():
    with open(src, encoding="utf-8") as f:
        data = json.load(f)
    data["textures"] = TEXTURES
    # groups-Metadaten entfernen (Minecraft ignoriert sie, aber sauber)
    data.pop("groups", None)
    out_path = os.path.join(OUT, dst)
    with open(out_path, "w", encoding="utf-8") as f:
        json.dump(data, f, indent=2)
    with open(src, encoding="utf-8") as f:
        elem_count = data["elements"].__len__()
    print(f"  {src} -> {dst}  ({elem_count} elements)")

print("Done.")
