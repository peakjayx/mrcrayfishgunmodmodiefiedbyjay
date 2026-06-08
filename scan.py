import os
import sys

def scan_java_files(root_dir, search_line):
    search_line = search_line.strip()
    found = False

    for dirpath, _, filenames in os.walk(root_dir):
        for filename in filenames:
            if filename.endswith(".java"):
                filepath = os.path.join(dirpath, filename)
                try:
                    with open(filepath, "r", encoding="utf-8", errors="ignore") as f:
                        for line_number, line in enumerate(f, start=1):
                            if search_line in line.strip():
                                print(f"[{filepath}] Zeile {line_number}: {line.rstrip()}")
                                found = True
                except Exception as e:
                    print(f"Fehler beim Lesen von {filepath}: {e}")

    if not found:
        print("Keine Treffer gefunden.")

if __name__ == "__main__":
    root = input("Root-Verzeichnis (Enter für aktuelles '.'): ").strip() or "."

    if not os.path.isdir(root):
        print(f"Verzeichnis '{root}' nicht gefunden.")
        sys.exit(1)

    search = input("Gesuchte Zeile / Text: ").strip()

    if not search:
        print("Kein Suchbegriff eingegeben.")
        sys.exit(1)

    print(f"\nSuche nach '{search}' in '{os.path.abspath(root)}'...\n")
    scan_java_files(root, search)