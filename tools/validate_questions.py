#!/usr/bin/env python3
"""Санҷиши файлҳои JSON-и саволҳо (барои Termux ва GitHub Actions).

Истифода:  python3 tools/validate_questions.py
Агар хато ёфт шавад, рамзи баромад 1 мешавад.
"""
import json
import pathlib
import sys

ROOT = pathlib.Path(__file__).resolve().parent.parent
QDIR = ROOT / "app" / "src" / "main" / "assets" / "questions"
EXPECTED = [
    "history_tajikistan", "economic_geography_demography", "foreign_language",
    "ecology", "law", "higher_mathematics", "political_science", "russian_language",
]
KEYS = ["A", "B", "C", "D"]
MIN_PER_SUBJECT = 50
FIELDS = ["id", "subjectId", "question", "options", "correctAnswer", "explanation"]


def check_file(subject: str, seen_ids: dict, errors: list) -> int:
    path = QDIR / f"{subject}.json"
    if not path.exists():
        errors.append(f"{path.name}: файл ёфт нашуд")
        return 0
    try:
        data = json.loads(path.read_text(encoding="utf-8"))
    except json.JSONDecodeError as e:
        errors.append(f"{path.name}: JSON-и вайрон (сатр {e.lineno}, сутун {e.colno}): {e.msg}")
        return 0
    if not isinstance(data, list):
        errors.append(f"{path.name}: реша бояд массив бошад")
        return 0
    positions = set()
    for i, q in enumerate(data):
        loc = f"{path.name} #{i + 1}"
        if not isinstance(q, dict):
            errors.append(f"{loc}: объект нест")
            continue
        for f in FIELDS:
            v = q.get(f)
            if v is None or (isinstance(v, str) and not v.strip()):
                errors.append(f"{loc}: майдони '{f}' нест ё холӣ аст")
        qid = q.get("id")
        if qid:
            if qid in seen_ids:
                errors.append(f"{loc}: ID '{qid}' такрор шудааст (аввал дар {seen_ids[qid]})")
            seen_ids[qid] = loc
        if q.get("subjectId") != subject:
            errors.append(f"{loc}: subjectId бояд '{subject}' бошад, '{q.get('subjectId')}' ёфт шуд")
        opts = q.get("options")
        if not isinstance(opts, dict) or sorted(opts.keys()) != KEYS:
            errors.append(f"{loc}: options бояд маҳз калидҳои A, B, C, D дошта бошад")
        else:
            texts = [str(opts[k]).strip() for k in KEYS]
            if any(not t for t in texts):
                errors.append(f"{loc}: яке аз ҷавобҳо холӣ аст")
            if len(set(texts)) != 4:
                errors.append(f"{loc}: ҷавобҳо бояд гуногун бошанд")
        ca = q.get("correctAnswer")
        if ca not in KEYS:
            errors.append(f"{loc}: correctAnswer бояд яке аз A/B/C/D бошад, '{ca}' ёфт шуд")
        else:
            positions.add(ca)
    if len(data) < MIN_PER_SUBJECT:
        errors.append(f"{path.name}: камаш {MIN_PER_SUBJECT} савол лозим, {len(data)} ҳаст")
    if len(data) >= 8 and len(positions) < 4:
        errors.append(f"{path.name}: ҷавоби дуруст дар ҳамаи ҷойҳо (A–D) наомадааст")
    print(f"  {path.name}: {len(data)} савол")
    return len(data)


def main() -> int:
    errors: list = []
    seen: dict = {}
    total = 0
    print("Санҷиши саволҳо:")
    for s in EXPECTED:
        total += check_file(s, seen, errors)
    print(f"Ҳамагӣ: {total} савол")
    if errors:
        print(f"\nХАТОҲО ({len(errors)}):")
        for e in errors:
            print("  ✗", e)
        return 1
    print("Ҳамаи файлҳо дурустанд ✓")
    return 0


if __name__ == "__main__":
    sys.exit(main())
