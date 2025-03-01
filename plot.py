import matplotlib.pyplot as plt
import numpy as np
import pandas as pd

# Load data
data = pd.read_csv("results.csv")

# Convert to log scale
log_x = np.log(16000)  # log(N), since we insert 16,000 elements
log_y = np.log(data["Execution Time (ms)"])  # log(Time)

# Plot
plt.figure(figsize=(8,6))
plt.scatter([log_x]*len(log_y), log_y, label=data["Heap Type"])
plt.xlabel("log(N) (Insertions)")
plt.ylabel("log(Time in ms)")
plt.title("Heap Performance (Log-Log Plot)")
plt.legend()
plt.show()
