import os
from pathlib import Path

# Ordner, die beim Auslesen ignoriert werden
EXCLUDE_DIRS = {
    '.git', '.gradle', '.idea', 'build', '.cxx', 
    'captures', '.externalNativeBuild', 'app/build'
}

# Dateiendungen, die exportiert werden sollen
ALLOWED_EXTENSIONS = {'.java', '.kt', '.xml', '.gradle', '.pro'}

def should_ignore(path: Path) -> bool:
    return any(part in EXCLUDE_DIRS for part in path.parts)

def export_android_project(project_dir: str, output_file: str):
    root_path = Path(project_dir).resolve()
    
    if not root_path.exists():
        print(f"Fehler: Pfad '{root_path}' existiert nicht.")
        return

    with open(output_file, 'w', encoding='utf-8') as out:
        # Kopfzeile
        out.write("=" * 80 + "\n")
        out.write(f"PROJECT CODE EXPORT: {root_path.name}\n")
        out.write("=" * 80 + "\n\n")

        # 1. Übersicht der Ordnerstruktur
        out.write("--- VERZEICHNISSTRUKTUR ---\n")
        for path in sorted(root_path.rglob('*')):
            if should_ignore(path):
                continue
            depth = len(path.relative_to(root_path).parts)
            indent = "  " * (depth - 1)
            out.write(f"{indent}{path.name}{'/' if path.is_dir() else ''}\n")
        
        out.write("\n" + "=" * 80 + "\n")
        out.write("--- DATEI-INHALTE ---\n")
        out.write("=" * 80 + "\n\n")

        # 2. Inhalte der Quellcodedateien
        for path in sorted(root_path.rglob('*')):
            if path.is_file() and not should_ignore(path):
                if path.suffix.lower() in ALLOWED_EXTENSIONS:
                    rel_path = path.relative_to(root_path)
                    
                    out.write("\n" + "-" * 80 + "\n")
                    out.write(f"DATEI: {rel_path}\n")
                    out.write("-" * 80 + "\n\n")
                    
                    try:
                        content = path.read_text(encoding='utf-8', errors='replace')
                        out.write(content)
                        out.write("\n")
                    except Exception as e:
                        out.write(f"[Fehler beim Lesen der Datei: {e}]\n")

    print(f"Erfolgreich! Der Code wurde in '{output_file}' gespeichert.")

if __name__ == "__main__":
    # Pfad zum Android-Projekt
    project_path = input("Gib den Pfad zum Android-Projektordner ein: ").strip('"\'')
    output_txt = "android_project_code.txt"
    
    export_android_project(project_path, output_txt)
