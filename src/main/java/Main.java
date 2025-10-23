import java.util.*;
import java.io.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("🚀 CITY TRANSPORTATION NETWORK OPTIMIZATION");
        System.out.println("   Minimum Spanning Tree Algorithms\n");

        try {
            // Демонстрация OOP дизайна графа
            demonstrateOOPGraphDesign();

            // Чтение и обработка всех графов из input.json
            processAllGraphs();

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void demonstrateOOPGraphDesign() {
        System.out.println("\n🎯 DEMONSTRATING OOP GRAPH DESIGN");
        System.out.println("=================================");

        // Create graph using current implementation
        Graph cityNetwork = JSONProcessor.createDemoGraph();

        System.out.println("Created Graph Analysis:");
        System.out.println("  Vertices: " + cityNetwork.getVerticesCount());
        System.out.println("  Edges: " + cityNetwork.getEdgesCount());
        System.out.println("  Connected: " + cityNetwork.isConnected());
        System.out.println("  Adjacent vertices of 2: " + getAdjacentVertices(cityNetwork, 2));

        // Test MST algorithms with new graph
        PrimMST prim = new PrimMST();
        KruskalMST kruskal = new KruskalMST();

        MSTResult primResult = prim.findMST(cityNetwork);
        MSTResult kruskalResult = kruskal.findMST(cityNetwork);

        System.out.println("\nMST Algorithm Results:");
        System.out.println("  Prim MST Cost: " + primResult.getTotalCost());
        System.out.println("  Kruskal MST Cost: " + kruskalResult.getTotalCost());
        System.out.println("  Costs Match: " + (primResult.getTotalCost() == kruskalResult.getTotalCost()));
        System.out.println("  Both Valid: " + (primResult.isValidMST() && kruskalResult.isValidMST()));

        // Генерация визуализации
        try {
            GraphVisualizer.generateGraphImage(cityNetwork, "graph_demo.png");
        } catch (Exception e) {
            System.out.println("⚠️  Graph visualization skipped: " + e.getMessage());
        }

        System.out.println("✅ OOP Graph Design Successfully Integrated with MST Algorithms");
        System.out.println("=================================\n");
    }

    // Вспомогательный метод для получения смежных вершин
    private static List<Integer> getAdjacentVertices(Graph graph, int vertex) {
        List<Integer> adjacent = new ArrayList<>();
        for (Edge edge : graph.getAdjacentEdges(vertex)) {
            int otherVertex = (edge.getSource() == vertex) ? edge.getDestination() : edge.getSource();
            adjacent.add(otherVertex);
        }
        return adjacent;
    }

    private static void processAllGraphs() throws IOException {
        // Чтение графов из JSON
        String inputFile = "src/main/resources/input.json";
        String outputFile = "src/main/resources/output.json";

        System.out.println("📖 Reading graphs from: " + inputFile);
        List<JSONProcessor.GraphData> graphDataList = JSONProcessor.readInputFile(inputFile);

        System.out.println("📊 Found " + graphDataList.size() + " graphs to process\n");

        List<JSONProcessor.OutputResult> results = new ArrayList<>();
        int processed = 0;
        int total = graphDataList.size();

        // Обработка каждого графа
        for (JSONProcessor.GraphData graphData : graphDataList) {
            processed++;
            System.out.printf("🔍 [%d/%d] Processing Graph ID: %d%n",
                    processed, total, graphData.id);
            System.out.println("   Vertices: " + graphData.graph.getVerticesCount());
            System.out.println("   Edges: " + graphData.graph.getEdgesCount());
            System.out.println("   Connected: " + graphData.graph.isConnected());

            if (!graphData.graph.isConnected()) {
                System.out.println("   ⚠️  Skipping disconnected graph");
                continue;
            }

            // Запуск алгоритмов
            PrimMST prim = new PrimMST();
            KruskalMST kruskal = new KruskalMST();

            MSTResult primResult = prim.findMST(graphData.graph);
            MSTResult kruskalResult = kruskal.findMST(graphData.graph);

            // Валидация результатов
            boolean costsMatch = primResult.getTotalCost() == kruskalResult.getTotalCost();
            boolean bothValid = primResult.isValidMST() && kruskalResult.isValidMST();

            System.out.printf("   ✅ Prim: cost=%d, time=%.3fms, ops=%d%n",
                    primResult.getTotalCost(), primResult.getExecutionTime(),
                    primResult.getOperationsCount());
            System.out.printf("   ✅ Kruskal: cost=%d, time=%.3fms, ops=%d%n",
                    kruskalResult.getTotalCost(), kruskalResult.getExecutionTime(),
                    kruskalResult.getOperationsCount());
            System.out.println("   📋 Validation: costsMatch=" + costsMatch +
                    ", bothValid=" + bothValid);

            // Конвертация в выходной формат
            JSONProcessor.OutputResult outputResult = JSONProcessor.convertToOutputResult(
                    graphData.id, graphData.nodeNames, primResult, kruskalResult);

            // Установка правильного количества ребер
            outputResult.input_stats.edges = graphData.graph.getEdgesCount();

            results.add(outputResult);

            // Прогресс каждые 5 графов
            if (processed % 5 == 0) {
                System.out.printf("📈 Progress: %d/%d (%.1f%%)%n%n",
                        processed, total, (processed * 100.0 / total));
            } else {
                System.out.println();
            }
        }

        // Запись результатов
        System.out.println("💾 Writing results to: " + outputFile);
        JSONProcessor.writeOutputFile(outputFile, results);

        printSummary(results);
        printDetailedAnalysis(results);

        // Генерация CSV файлов
        generateCSVFiles(outputFile);
    }

    private static void generateCSVFiles(String outputJsonFile) {
        System.out.println("📈 Generating CSV analysis...");
        try {
            CSVGenerator.generateCSV(
                    outputJsonFile,
                    "src/main/resources/results_analysis.csv",
                    "src/main/resources/summary_statistics.csv",
                    "src/main/resources/chart_data.csv"
            );
            System.out.println("✅ CSV analysis files created!");
        } catch (Exception e) {
            System.err.println("❌ CSV generation failed: " + e.getMessage());
        }
    }

    private static void printSummary(List<JSONProcessor.OutputResult> results) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("PROCESSING SUMMARY");
        System.out.println("=".repeat(60));

        int totalGraphs = results.size();
        double totalPrimTime = 0;
        double totalKruskalTime = 0;
        int totalPrimOps = 0;
        int totalKruskalOps = 0;
        int primWins = 0;
        int kruskalWins = 0;

        for (JSONProcessor.OutputResult result : results) {
            totalPrimTime += result.prim.execution_time_ms;
            totalKruskalTime += result.kruskal.execution_time_ms;
            totalPrimOps += result.prim.operations_count;
            totalKruskalOps += result.kruskal.operations_count;

            // Подсчет побед
            if (result.prim.execution_time_ms < result.kruskal.execution_time_ms) {
                primWins++;
            } else if (result.kruskal.execution_time_ms < result.prim.execution_time_ms) {
                kruskalWins++;
            }
        }

        double avgPrimTime = totalPrimTime / totalGraphs;
        double avgKruskalTime = totalKruskalTime / totalGraphs;

        System.out.println("Total graphs processed: " + totalGraphs);
        System.out.printf("Average Prim time: %.3fms%n", avgPrimTime);
        System.out.printf("Average Kruskal time: %.3fms%n", avgKruskalTime);
        System.out.println("Total Prim operations: " + totalPrimOps);
        System.out.println("Total Kruskal operations: " + totalKruskalOps);
        System.out.printf("Prim wins: %d (%.1f%%)%n", primWins, (primWins * 100.0 / totalGraphs));
        System.out.printf("Kruskal wins: %d (%.1f%%)%n", kruskalWins, (kruskalWins * 100.0 / totalGraphs));

        // Сравнение производительности
        if (avgPrimTime < avgKruskalTime) {
            System.out.println("🎯 Prim was faster overall");
        } else if (avgPrimTime > avgKruskalTime) {
            System.out.println("🎯 Kruskal was faster overall");
        } else {
            System.out.println("🎯 Both algorithms performed similarly");
        }
    }

    private static void printDetailedAnalysis(List<JSONProcessor.OutputResult> results) {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("DETAILED PERFORMANCE ANALYSIS BY GRAPH SIZE");
        System.out.println("=".repeat(70));

        // Группировка по размерам
        Map<String, List<JSONProcessor.OutputResult>> sizeGroups = new HashMap<>();
        sizeGroups.put("Small (5-30)", results.subList(0, 5));
        sizeGroups.put("Medium (50-300)", results.subList(5, 15));
        sizeGroups.put("Large (350-1000)", results.subList(15, 25));
        sizeGroups.put("Extra Large (1200-3000)", results.subList(25, 30));

        for (Map.Entry<String, List<JSONProcessor.OutputResult>> entry : sizeGroups.entrySet()) {
            String size = entry.getKey();
            List<JSONProcessor.OutputResult> groupResults = entry.getValue();

            double avgPrimTime = groupResults.stream()
                    .mapToDouble(r -> r.prim.execution_time_ms)
                    .average().orElse(0);
            double avgKruskalTime = groupResults.stream()
                    .mapToDouble(r -> r.kruskal.execution_time_ms)
                    .average().orElse(0);

            int avgPrimOps = (int) groupResults.stream()
                    .mapToInt(r -> r.prim.operations_count)
                    .average().orElse(0);
            int avgKruskalOps = (int) groupResults.stream()
                    .mapToInt(r -> r.kruskal.operations_count)
                    .average().orElse(0);

            // Подсчет побед в категории
            long primWinsInCategory = groupResults.stream()
                    .filter(r -> r.prim.execution_time_ms < r.kruskal.execution_time_ms)
                    .count();
            long kruskalWinsInCategory = groupResults.size() - primWinsInCategory;

            System.out.printf("%-15s: Prim %.3fms (%d ops) vs Kruskal %.3fms (%d ops)%n",
                    size, avgPrimTime, avgPrimOps, avgKruskalTime, avgKruskalOps);
            System.out.printf("                Wins: Prim %d/%d (%.1f%%) vs Kruskal %d/%d (%.1f%%)%n%n",
                    primWinsInCategory, groupResults.size(), (primWinsInCategory * 100.0 / groupResults.size()),
                    kruskalWinsInCategory, groupResults.size(), (kruskalWinsInCategory * 100.0 / groupResults.size()));
        }

        // Анализ по плотности графов
        printDensityAnalysis(results);
    }

    private static void printDensityAnalysis(List<JSONProcessor.OutputResult> results) {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("PERFORMANCE ANALYSIS BY GRAPH DENSITY");
        System.out.println("=".repeat(70));

        // Группировка по плотности
        Map<String, List<JSONProcessor.OutputResult>> densityGroups = new LinkedHashMap<>();
        densityGroups.put("Very Sparse (<0.1)", new ArrayList<>());
        densityGroups.put("Sparse (0.1-0.3)", new ArrayList<>());
        densityGroups.put("Medium (0.3-0.6)", new ArrayList<>());
        densityGroups.put("Dense (>0.6)", new ArrayList<>());

        for (JSONProcessor.OutputResult result : results) {
            double density = calculateDensity(result.input_stats.vertices, result.input_stats.edges);
            String densityCategory;

            if (density < 0.1) densityCategory = "Very Sparse (<0.1)";
            else if (density < 0.3) densityCategory = "Sparse (0.1-0.3)";
            else if (density < 0.6) densityCategory = "Medium (0.3-0.6)";
            else densityCategory = "Dense (>0.6)";

            densityGroups.get(densityCategory).add(result);
        }

        for (Map.Entry<String, List<JSONProcessor.OutputResult>> entry : densityGroups.entrySet()) {
            String densityCategory = entry.getKey();
            List<JSONProcessor.OutputResult> groupResults = entry.getValue();

            if (groupResults.isEmpty()) continue;

            double avgDensity = groupResults.stream()
                    .mapToDouble(r -> calculateDensity(r.input_stats.vertices, r.input_stats.edges))
                    .average().orElse(0);

            double avgPrimTime = groupResults.stream()
                    .mapToDouble(r -> r.prim.execution_time_ms)
                    .average().orElse(0);
            double avgKruskalTime = groupResults.stream()
                    .mapToDouble(r -> r.kruskal.execution_time_ms)
                    .average().orElse(0);

            long primWins = groupResults.stream()
                    .filter(r -> r.prim.execution_time_ms < r.kruskal.execution_time_ms)
                    .count();

            System.out.printf("%-20s: %2d graphs, density: %.3f%n", densityCategory, groupResults.size(), avgDensity);
            System.out.printf("                      Prim: %.3fms, Kruskal: %.3fms, Prim wins: %d/%d (%.1f%%)%n%n",
                    avgPrimTime, avgKruskalTime, primWins, groupResults.size(), (primWins * 100.0 / groupResults.size()));
        }
    }

    private static double calculateDensity(int vertices, int edges) {
        if (vertices <= 1) return 0;
        double maxPossibleEdges = vertices * (vertices - 1) / 2.0;
        return edges / maxPossibleEdges;
    }
}