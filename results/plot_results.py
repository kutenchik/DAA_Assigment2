import csv
from pathlib import Path

import matplotlib.pyplot as plt


root = Path(__file__).parent
with (root / "results.csv").open(newline="") as file:
    rows = list(csv.DictReader(file))

plots = root / "plots"
plots.mkdir(exist_ok=True)

for workload in ("W1", "W2", "W3", "W4"):
    selected = [row for row in rows if row["workload"] == workload]
    groups = sorted({(row["structure"], row["variant"]) for row in selected})
    fig, ax = plt.subplots(figsize=(7, 4.5))
    for structure, variant in groups:
        data = sorted(
            (row for row in selected if row["structure"] == structure and row["variant"] == variant),
            key=lambda row: int(row["n"]),
        )
        label = structure if variant == "-" else f"{structure} {variant}"
        ax.plot([int(row["n"]) for row in data], [float(row["time_ms"]) for row in data], marker="o", label=label)
    ax.set_xscale("log")
    ax.set_xlabel("n (elements)")
    ax.set_ylabel("Median time (ms)")
    ax.set_title(f"{workload}: time vs n")
    ax.grid(True, alpha=0.3)
    ax.legend()
    fig.tight_layout()
    fig.savefig(plots / f"{workload}_time.png", dpi=160)
    plt.close(fig)

    fig, axes = plt.subplots(1, 3, figsize=(15, 4.5))
    for ax, metric in zip(axes, ("steps", "moves", "comparisons")):
        for structure, variant in groups:
            data = sorted(
                (row for row in selected if row["structure"] == structure and row["variant"] == variant),
                key=lambda row: int(row["n"]),
            )
            label = structure if variant == "-" else f"{structure} {variant}"
            ax.plot([int(row["n"]) for row in data], [int(row[metric]) for row in data], marker="o", label=label)
        ax.set_xscale("log")
        ax.set_xlabel("n (elements)")
        ax.set_ylabel(f"{metric.capitalize()} (count)")
        ax.set_title(metric.capitalize())
        ax.grid(True, alpha=0.3)
    axes[0].legend(fontsize=8)
    fig.suptitle(f"{workload}: counted operations vs n")
    fig.tight_layout()
    fig.savefig(plots / f"{workload}_operations.png", dpi=160)
    plt.close(fig)
