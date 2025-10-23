import java.io.*;
import java.nio.file.*;
import java.util.*;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CSVGenerator {
    private static final ObjectMapper mapper = new ObjectMapper();

    // Классы для парсинга JSON (дублируем из JSONProcessor)
    public static class OutputData {
        public List<OutputResult> results;
    }

    public static class OutputResult {
        public int graph_id;
        public InputStats input_stats;
        public AlgorithmResult prim;
        public AlgorithmResult kruskal;
    }

    public static class InputStats {
        public int vertices;
        public int edges;
    }

    public static class AlgorithmResult {
        public List<OutputEdge> mst_edges;
        public int total_cost;
        public int operations_count;
        public double execution_time_ms;
    }

    public static class OutputEdge {
        public String from;
        public String to;
        public int weight;
    }

    public static void main(String[] args) {
        try {
            String inputFile = "src/main/resources/output.json";
            String outputFile = "src/main/resources/results_analysis.csv";
            String summaryFile = "src/main/resources/summary_statistics.csv";
            String chartFile = "src/main/resources/chart_data.csv";

            System.out.println("📊 Generating CSV analysis from: " + inputFile);
            generateCSV(inputFile, outputFile, summaryFile, chartFile);
            System.out.println("✅ CSV files created successfully!");

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void generateCSV(String jsonFilePath, String csvFilePath,
                                   String summaryFilePath, String chartFilePath) throws IOException {
        // Read JSON data
        String content = new String(Files.readAllBytes(Paths.get(jsonFilePath)));
        OutputData outputData = mapper.readValue(content, OutputData.class);

        // Generate detailed CSV
        generateDetailedCSV(outputData, csvFilePath);

        // Generate summary statistics
        generateSummaryCSV(outputData, summaryFilePath);

        // Generate chart data
        generateChartData(outputData, chartFilePath);

        printStatistics(outputData);
    }

    private static void generateDetailedCSV(OutputData outputData, String csvFilePath) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(csvFilePath))) {
            // Write CSV header
            writer.println("GraphID,Vertices,Edges,PrimCost,PrimTimeMS,PrimOperations,KruskalCost,KruskalTimeMS,KruskalOperations,CostMatch,TimeDifferenceMS,OperationsDifference,PrimFaster,KruskalFaster,GraphSize,EdgeDensity");

            // Write data rows
            for (OutputResult result : outputData.results) {
                String csvLine = createDetailedCSVLine(result);
                writer.println(csvLine);
            }
        }
        System.out.println("📄 Detailed analysis: " + csvFilePath);
    }

    private static String createDetailedCSVLine(OutputResult result) {
        int graphId = result.graph_id;
        int vertices = result.input_stats.vertices;
        int edges = result.input_stats.edges;

        // Prim metrics
        int primCost = result.prim.total_cost;
        double primTime = result.prim.execution_time_ms;
        int primOps = result.prim.operations_count;

        // Kruskal metrics
        int kruskalCost = result.kruskal.total_cost;
        double kruskalTime = result.kruskal.execution_time_ms;
        int kruskalOps = result.kruskal.operations_count;

        // Analysis metrics
        boolean costMatch = (primCost == kruskalCost);
        double timeDiff = primTime - kruskalTime;
        int opsDiff = primOps - kruskalOps;
        boolean primFaster = primTime < kruskalTime;
        boolean kruskalFaster = kruskalTime < primTime;

        // Graph characteristics
        String sizeCategory = getSizeCategory(vertices);
        double density = calculateDensity(vertices, edges);

        // Create CSV line
        return String.format("%d,%d,%d,%d,%.3f,%d,%d,%.3f,%d,%b,%.3f,%d,%b,%b,%s,%.4f",
                graphId, vertices, edges, primCost, primTime, primOps,
                kruskalCost, kruskalTime, kruskalOps, costMatch, timeDiff,
                opsDiff, primFaster, kruskalFaster, sizeCategory, density);
    }

    private static void generateSummaryCSV(OutputData outputData, String summaryFilePath) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(summaryFilePath))) {
            // Overall summary
            writer.println("SUMMARY STATISTICS");
            writer.println("==================");
            writer.println();

            writer.println("Overall Performance:");
            writer.println("Category,GraphCount,AvgVertices,AvgEdges,AvgPrimTimeMS,AvgKruskalTimeMS,AvgPrimOps,AvgKruskalOps,PrimFasterCount,KruskalFasterCount,PrimWinRate");
            writeCategorySummary(writer, "Overall", outputData.results);
            writer.println();

            // Size category summaries
            writer.println("Performance by Graph Size:");
            writer.println("SizeCategory,GraphCount,AvgVertices,AvgEdges,AvgPrimTimeMS,AvgKruskalTimeMS,AvgPrimOps,AvgKruskalOps,PrimFasterCount,KruskalFasterCount,PrimWinRate");

            Map<String, List<OutputResult>> sizeCategories = new LinkedHashMap<>();
            sizeCategories.put("Small", new ArrayList<>());
            sizeCategories.put("Medium", new ArrayList<>());
            sizeCategories.put("Large", new ArrayList<>());
            sizeCategories.put("Extra Large", new ArrayList<>());

            for (OutputResult result : outputData.results) {
                String category = getSizeCategory(result.input_stats.vertices);
                sizeCategories.get(category).add(result);
            }

            for (Map.Entry<String, List<OutputResult>> entry : sizeCategories.entrySet()) {
                if (!entry.getValue().isEmpty()) {
                    writeCategorySummary(writer, entry.getKey(), entry.getValue());
                }
            }
            writer.println();

            // Density analysis
            writer.println("Performance by Edge Density:");
            writer.println("DensityCategory,GraphCount,AvgDensity,PrimWinRate,AvgTimeAdvantageMS");
            writeDensityAnalysis(writer, outputData.results);
            writer.println();

            // Algorithm comparison
            writer.println("Algorithm Comparison:");
            writer.println("Metric,Prim,Kruskal,Advantage");
            writeAlgorithmComparison(writer, outputData.results);
        }
        System.out.println("📊 Summary statistics: " + summaryFilePath);
    }

    private static void writeCategorySummary(PrintWriter writer, String category, List<OutputResult> results) {
        int count = results.size();
        double avgVertices = results.stream().mapToInt(r -> r.input_stats.vertices).average().orElse(0);
        double avgEdges = results.stream().mapToInt(r -> r.input_stats.edges).average().orElse(0);
        double avgPrimTime = results.stream().mapToDouble(r -> r.prim.execution_time_ms).average().orElse(0);
        double avgKruskalTime = results.stream().mapToDouble(r -> r.kruskal.execution_time_ms).average().orElse(0);
        double avgPrimOps = results.stream().mapToInt(r -> r.prim.operations_count).average().orElse(0);
        double avgKruskalOps = results.stream().mapToInt(r -> r.kruskal.operations_count).average().orElse(0);

        long primFasterCount = results.stream()
                .filter(r -> r.prim.execution_time_ms < r.kruskal.execution_time_ms)
                .count();
        long kruskalFasterCount = count - primFasterCount;
        double primWinRate = (primFasterCount * 100.0) / count;

        writer.printf("%s,%d,%.1f,%.1f,%.3f,%.3f,%.1f,%.1f,%d,%d,%.1f%%%n",
                category, count, avgVertices, avgEdges, avgPrimTime, avgKruskalTime,
                avgPrimOps, avgKruskalOps, primFasterCount, kruskalFasterCount, primWinRate);
    }

    private static void writeDensityAnalysis(PrintWriter writer, List<OutputResult> results) {
        Map<String, List<OutputResult>> densityCategories = new LinkedHashMap<>();
        densityCategories.put("Very Sparse (<0.1)", new ArrayList<>());
        densityCategories.put("Sparse (0.1-0.3)", new ArrayList<>());
        densityCategories.put("Medium (0.3-0.6)", new ArrayList<>());
        densityCategories.put("Dense (>0.6)", new ArrayList<>());

        for (OutputResult result : results) {
            double density = calculateDensity(result.input_stats.vertices, result.input_stats.edges);
            String densityCat;
            if (density < 0.1) densityCat = "Very Sparse (<0.1)";
            else if (density < 0.3) densityCat = "Sparse (0.1-0.3)";
            else if (density < 0.6) densityCat = "Medium (0.3-0.6)";
            else densityCat = "Dense (>0.6)";

            densityCategories.get(densityCat).add(result);
        }

        for (Map.Entry<String, List<OutputResult>> entry : densityCategories.entrySet()) {
            List<OutputResult> categoryResults = entry.getValue();
            if (categoryResults.isEmpty()) continue;

            int count = categoryResults.size();
            double avgDensity = categoryResults.stream()
                    .mapToDouble(r -> calculateDensity(r.input_stats.vertices, r.input_stats.edges))
                    .average().orElse(0);

            long primWins = categoryResults.stream()
                    .filter(r -> r.prim.execution_time_ms < r.kruskal.execution_time_ms)
                    .count();
            double primWinRate = (primWins * 100.0) / count;

            double avgTimeAdvantage = categoryResults.stream()
                    .mapToDouble(r -> r.kruskal.execution_time_ms - r.prim.execution_time_ms)
                    .average().orElse(0);

            writer.printf("%s,%d,%.3f,%.1f%%,%.3f%n",
                    entry.getKey(), count, avgDensity, primWinRate, avgTimeAdvantage);
        }
    }

    private static void writeAlgorithmComparison(PrintWriter writer, List<OutputResult> results) {
        double avgPrimTime = results.stream().mapToDouble(r -> r.prim.execution_time_ms).average().orElse(0);
        double avgKruskalTime = results.stream().mapToDouble(r -> r.kruskal.execution_time_ms).average().orElse(0);
        double avgPrimOps = results.stream().mapToInt(r -> r.prim.operations_count).average().orElse(0);
        double avgKruskalOps = results.stream().mapToInt(r -> r.kruskal.operations_count).average().orElse(0);

        writer.printf("Average Time (ms),%.3f,%.3f,%s%n",
                avgPrimTime, avgKruskalTime,
                avgPrimTime < avgKruskalTime ? "Prim" : "Kruskal");
        writer.printf("Average Operations,%.1f,%.1f,%s%n",
                avgPrimOps, avgKruskalOps,
                avgPrimOps < avgKruskalOps ? "Prim" : "Kruskal");

        long primWins = results.stream()
                .filter(r -> r.prim.execution_time_ms < r.kruskal.execution_time_ms)
                .count();
        double primWinRate = (primWins * 100.0) / results.size();
        writer.printf("Win Rate,%.1f%%,%.1f%%,%s%n",
                primWinRate, 100 - primWinRate,
                primWinRate > 50 ? "Prim" : "Kruskal");
    }

    private static void generateChartData(OutputData outputData, String chartFilePath) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(chartFilePath))) {
            writer.println("GraphSize,PrimTimeMS,KruskalTimeMS,PrimOperations,KruskalOperations,EdgeDensity");

            // Sort by graph size for better charts
            outputData.results.sort(Comparator.comparingInt(r -> r.input_stats.vertices));

            for (OutputResult result : outputData.results) {
                double density = calculateDensity(result.input_stats.vertices, result.input_stats.edges);
                writer.printf("%d,%.3f,%.3f,%d,%d,%.4f%n",
                        result.input_stats.vertices,
                        result.prim.execution_time_ms,
                        result.kruskal.execution_time_ms,
                        result.prim.operations_count,
                        result.kruskal.operations_count,
                        density);
            }
        }
        System.out.println("📈 Chart data: " + chartFilePath);
    }

    private static void printStatistics(OutputData outputData) {
        System.out.println("\n📊 ANALYSIS SUMMARY");
        System.out.println("===================");

        int totalGraphs = outputData.results.size();
        long primWins = outputData.results.stream()
                .filter(r -> r.prim.execution_time_ms < r.kruskal.execution_time_ms)
                .count();
        long kruskalWins = totalGraphs - primWins;

        System.out.printf("Total graphs analyzed: %d%n", totalGraphs);
        System.out.printf("Prim wins: %d (%.1f%%)%n", primWins, (primWins * 100.0 / totalGraphs));
        System.out.printf("Kruskal wins: %d (%.1f%%)%n", kruskalWins, (kruskalWins * 100.0 / totalGraphs));

        // Performance by size
        System.out.println("\nPerformance by Graph Size:");
        Map<String, List<OutputResult>> sizeGroups = new HashMap<>();
        for (OutputResult result : outputData.results) {
            String size = getSizeCategory(result.input_stats.vertices);
            sizeGroups.computeIfAbsent(size, k -> new ArrayList<>()).add(result);
        }

        for (Map.Entry<String, List<OutputResult>> entry : sizeGroups.entrySet()) {
            List<OutputResult> group = entry.getValue();
            long groupPrimWins = group.stream()
                    .filter(r -> r.prim.execution_time_ms < r.kruskal.execution_time_ms)
                    .count();
            System.out.printf("  %s: %d/%d (%.1f%%) for Prim%n",
                    entry.getKey(), groupPrimWins, group.size(),
                    (groupPrimWins * 100.0 / group.size()));
        }
    }

    private static String getSizeCategory(int vertices) {
        if (vertices <= 30) return "Small";
        else if (vertices <= 300) return "Medium";
        else if (vertices <= 1000) return "Large";
        else return "Extra Large";
    }

    private static double calculateDensity(int vertices, int edges) {
        if (vertices <= 1) return 0;
        double maxEdges = vertices * (vertices - 1) / 2.0;
        return edges / maxEdges;
    }
}