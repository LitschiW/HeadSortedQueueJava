package io.github.litschiw.util.performance;

import io.github.litschiw.util.HeadSortedQueue;
import io.github.litschiw.util.TestElement;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.RepeatedTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

class ArrayListPerformanceTest extends CollectionPerformanceTest {

    public static Map<String, PerformanceTestResultsAggregator> aggregators = new HashMap<>();

    public ArrayListPerformanceTest() {
        super(ArrayList::new);
    }

    @RepeatedTest(10)
    void testAdding() {
        var result = aggregators.computeIfAbsent("addArrayList", PerformanceTestResultsAggregator::new);
        testAddPerformance(result);
    }

    @AfterAll
    static void afterAll() {
        aggregators.values().forEach(PerformanceTestResultsAggregator::printToCSV);
    }
}
