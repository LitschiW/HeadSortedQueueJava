package io.github.litschiw.util.performance;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

public class PerformanceTestResultsAggregator {
    private final String name;

    private final HashMap<Integer, ArrayList<Long>> data = new HashMap<>();

    public PerformanceTestResultsAggregator(String name) {
        this.name = name;
    }

    public void addDataPoint(int x, long y) {
        data.computeIfAbsent(x, k -> new ArrayList<>()).add(y);
    }

    public void printToCSV() {
        File file = new File(name + ".csv");
        TreeMap<Integer, ArrayList<Long>> sortedData = new TreeMap<>();
        sortedData.putAll(data);

        int runs = sortedData.values().stream().mapToInt(ArrayList::size).max().orElse(0);


        try {
            file.delete();

            StringBuilder header = new StringBuilder("size");
            for (int i = 1; i <= runs; i++) {
                header.append(",run").append(i);
            }
            header.append("\n");
            Files.write(file.toPath(), header.toString().getBytes(), StandardOpenOption.CREATE);

            for (var entry : sortedData.entrySet()) {

                String values = String.join(",", entry.getValue().stream().map(Object::toString).toList());
                String line = entry.getKey() + "," + values + "\n";

                Files.write(file.toPath(), line.getBytes(), StandardOpenOption.APPEND);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
