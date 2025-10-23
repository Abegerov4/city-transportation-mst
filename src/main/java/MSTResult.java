import java.util.ArrayList;
import java.util.List;

public class MSTResult {
    private final String algorithmName;
    private final List<Edge> mstEdges;
    private final int totalCost;
    private final double executionTimeMs; // Изменено на double для точности
    private final int operationsCount;
    private final int vertices;

    public MSTResult(String algorithmName, List<Edge> mstEdges, int totalCost,
                     double executionTimeMs, int operationsCount, int vertices) {
        this.algorithmName = algorithmName;
        this.mstEdges = new ArrayList<>(mstEdges);
        this.totalCost = totalCost;
        this.executionTimeMs = executionTimeMs;
        this.operationsCount = operationsCount;
        this.vertices = vertices;
    }

    // Getters
    public String getAlgorithmName() { return algorithmName; }
    public List<Edge> getMstEdges() { return new ArrayList<>(mstEdges); }
    public int getTotalCost() { return totalCost; }
    public double getExecutionTime() { return executionTimeMs; } // Изменено на double
    public int getOperationsCount() { return operationsCount; }
    public int getVertices() { return vertices; }
    public int getMstEdgesCount() { return mstEdges.size(); }

    // Validation methods
    public boolean isValidMST() {
        return mstEdges.size() == vertices - 1 && totalCost >= 0;
    }

    public boolean hasCorrectEdgeCount() {
        return mstEdges.size() == vertices - 1;
    }

    @Override
    public String toString() {
        return String.format("%s: Cost=%d, Time=%.3fms, Operations=%d, Edges=%d/%d",
                algorithmName, totalCost, executionTimeMs, operationsCount,
                mstEdges.size(), vertices - 1);
    }

    public String toDetailedString() {
        StringBuilder sb = new StringBuilder();
        sb.append(algorithmName).append(" Analysis:\n");
        sb.append("  Total Cost: ").append(totalCost).append("\n");
        sb.append("  Execution Time: ").append(String.format("%.3f", executionTimeMs)).append(" ms\n");
        sb.append("  Operations Count: ").append(operationsCount).append("\n");
        sb.append("  MST Edges: ").append(mstEdges.size()).append("/").append(vertices - 1).append("\n");
        sb.append("  Valid MST: ").append(isValidMST()).append("\n");

        sb.append("  Selected Edges:\n");
        for (Edge edge : mstEdges) {
            sb.append("    ").append(edge).append("\n");
        }

        return sb.toString();
    }
}