import pandas as pd
import numpy as np
import matplotlib.pyplot as plt

csv_filename = "policy.csv"
df = pd.read_csv(csv_filename)

def get_hold_threshold(group):
    hold_rows = group[group['action'] == 'HOLD']
    if not hold_rows.empty:
        return hold_rows['k'].min()
    else:
        return np.nan

policy_threshold = df.groupby(['i', 'j']).apply(get_hold_threshold).reset_index()
policy_threshold.columns = ['i', 'j', 'hold_threshold']

pivot = policy_threshold.pivot(index='i', columns='j', values='hold_threshold')

plt.figure(figsize=(10, 8))

plt.imshow(pivot, origin='lower', aspect='auto', interpolation='nearest', cmap='viridis')
plt.colorbar(label='Hold Threshold (k)')
plt.xlabel("Opponent's Score (j)")
plt.ylabel("Player's Score (i)")
plt.title("Optimal Hold Thresholds for the Pig Game")
plt.tight_layout()

plt.savefig("policy_table.png", dpi=300)

plt.show()