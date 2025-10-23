#  Minimum Spanning Tree Algorithms Analysis
### *Prim’s vs Kruskal’s Algorithm for City Transportation Optimization*

---

##  Performance Benchmarks

###  Execution Time (milliseconds)
| Graph Size | Prim | Kruskal |  Winner |
|:-----------:|:----:|:--------:|:---------:|
| Small (5–30) | **0.093** | **0.081** |  Kruskal |
| Medium (50–300) | 0.311 | **0.264** |  Kruskal |
| Large (350–1000) | 0.760 | **0.563** |  Kruskal |
| Extra Large (1200–3000) | **0.555** | 0.875 |  Prim |

---

### ️ Operation Counts
| Graph Size | Prim Ops | Kruskal Ops |  More Efficient |
|:-----------:|:---------:|:-------------:|:----------------:|
| Small | **314** | 494 | Prim |
| Medium | **9,970** | 13,427 | Prim |
| Large | **54,119** | 68,578 | Prim |
| Extra Large | **87,030** | 105,364 | Prim |

---

###  Win Distribution (30 Total Graphs)
| Category | Graphs | Prim Wins | Kruskal Wins |  Dominant |
|:----------:|:------:|:----------:|:--------------:|:------------:|
| Small | 5 | 1 (20%) | 4 (80%) | Kruskal |
| Medium | 10 | 3 (30%) | 7 (70%) | Kruskal |
| Large | 10 | 2 (20%) | 8 (80%) | Kruskal |
| Extra Large | 5 | 5 (100%) | 0 (0%) | Prim |
| **Total** | **30** | **11 (37%)** | **19 (63%)** |  **Kruskal** |

---

## Technical Architecture

###  Core Components
- **Prim’s Algorithm** → Priority Queue + visited set → `O(E log V)`
- **Kruskal’s Algorithm** → Union-Find + sorted edges → `O(E log E)`
- **Graph Representation** → Adjacency List
- **Memory Complexity** → `O(V + E)` for both

---

### ️ Optimization Strategies
-  **Prim:** early MST termination, optimized heap ops
-  **Kruskal:** union-by-rank + path compression
- ️ **Both:** operation counting, object reuse, reduced memory allocations

---

##  Complexity & Performance

###  Theoretical Comparison
| Algorithm | Time Complexity | Space Complexity | Best For |
|:-----------:|:----------------:|:----------------:|:----------:|
| **Prim** | O(E log V) | O(V + E) | Large dense graphs |
| **Kruskal** | O(E log E) | O(V + E) | Small/medium sparse graphs |

---

###  Empirical Summary
- **Total Graphs:** 30
- **Avg Prim Time:** 0.465 ms
- **Avg Kruskal Time:** 0.435 ms
- **Performance Gap:** ≈ 6.5% (Kruskal faster)
- **Prim Operations:** 20–25% more efficient

---

##  Key Findings

###  Algorithm Correctness
- All MSTs valid → *(V−1 edges, no cycles)*
- Identical total costs
- Verified full graph connectivity
- Comprehensive error handling

---

###  Performance Patterns
-  **Kruskal dominates** on graphs < **1000 vertices**
- ️ **Prim dominates** on graphs > **1000 vertices**
-  Crossover point ≈ **1000 vertices**
-  **Prim uses fewer operations overall**

---

###  Scalability Insights
| Factor | Prim | Kruskal |
|:-------|:------|:---------|
| **Operation Growth** | Linear | Logarithmic (sorting) |
| **Memory Usage** | Moderate | Moderate |
| **Graph Size Tolerance** | Excellent | Good |

---

##  Project Structure

```markdown
src/main/java/
├── Main.java                 # Application entry point
├── Graph.java                # Graph data structure  
├── Edge.java                 # Edge representation
├── MSTResult.java            # Result container
├── PrimMST.java              # Prim’s algorithm
├── KruskalMST.java           # Kruskal’s algorithm
├── JSONProcessor.java        # JSON I/O operations
└── CSVGenerator.java         # CSV report generation

src/main/resources/
├── input.json                # Test graphs
├── output.json               # Algorithm results
├── results_analysis.csv      # Detailed performance data
├── summary_statistics.csv    # Statistical summary
└── chart_data.csv            # Visualization data
```

##  Algorithm Selection Guide

###  Choose **Prim** When:
- Graphs **>1000 vertices**
- High-density (urban) networks
- Memory efficiency critical
- Need stable performance

###  Choose **Kruskal** When:
- Graphs **<1000 vertices**
- Sparse (rural) networks
- Sorting operations are cheap
- Simpler implementation preferred

---

##  Performance Insights
-  **Urban Networks (Dense)** → Prefer **Prim**
-  **Rural Networks (Sparse)** → Prefer **Kruskal**
- ️ **Real-Time Systems** → Choose based on ~1000-vertex threshold
-  **Limited Resources** → Prim’s operation efficiency wins

---

##  Quick Start

```bash
# Compile and run complete analysis
mvn compile
mvn exec:java -Dexec.mainClass="Main"

# Generate CSV reports
mvn exec:java -Dexec.mainClass="CSVGenerator"

# Run tests
mvn test
