#!/usr/bin/env python3
"""Сохтани файлҳои JSON аз tools/src/*.txt.

Формати хати манбаъ (6 майдон бо ' ;; ' ҷудо шудаанд):
    савол ;; ҷавоби ДУРУСТ ;; нодурусти 1 ;; нодурусти 2 ;; нодурусти 3 ;; шарҳ

Скрипт ҷавоби дурустро ба ҷойи A/B/C/D тавре мегузорад, ки ба таври баробар
тақсим шавад. Барои илова кардани савол ба таври дастӣ ҳамин файлро таҳрир
кунед ё мустақиман JSON-ро дар app/src/main/assets/questions/ тағйир диҳед.
"""
import json
import pathlib
import sys

ROOT = pathlib.Path(__file__).resolve().parent.parent
SRC = ROOT / "tools" / "src"
OUT = ROOT / "app" / "src" / "main" / "assets" / "questions"

# (файли манбаъ/хориҷ, subjectId, префикси ID)
SUBJECTS = [
    ("history_tajikistan", "history"),
    ("economic_geography_demography", "geo"),
    ("foreign_language", "english"),
    ("ecology", "ecology"),
    ("law", "law"),
    ("higher_mathematics", "math"),
    ("political_science", "politics"),
    ("russian_language", "russian"),
]
KEYS = ["A", "B", "C", "D"]


def build(name: str, prefix: str) -> list:
    lines = [l.strip() for l in (SRC / f"{name}.txt").read_text(encoding="utf-8").splitlines() if l.strip()]
    result = []
    for i, line in enumerate(lines):
        parts = [p.strip() for p in line.split(" ;; ")]
        if len(parts) != 6:
            sys.exit(f"{name}.txt, сатри {i + 1}: 6 майдон лозим, {len(parts)} ёфт шуд")
        q, correct, w1, w2, w3, expl = parts
        pos = (i + i // 4) % 4  # ҷойгиршавии ҷавоби дуруст
        wrong = [w1, w2, w3]
        options = []
        for p in range(4):
            options.append(correct if p == pos else wrong.pop(0))
        result.append({
            "id": f"{prefix}_{i + 1:03d}",
            "subjectId": name,
            "question": q,
            "options": dict(zip(KEYS, options)),
            "correctAnswer": KEYS[pos],
            "explanation": expl,
        })
    return result


def main() -> None:
    OUT.mkdir(parents=True, exist_ok=True)
    total = 0
    for name, prefix in SUBJECTS:
        data = build(name, prefix)
        (OUT / f"{name}.json").write_text(
            json.dumps(data, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
        print(f"{name}.json: {len(data)} савол")
        total += len(data)
    print(f"Ҳамагӣ: {total}")


if __name__ == "__main__":
    main()
