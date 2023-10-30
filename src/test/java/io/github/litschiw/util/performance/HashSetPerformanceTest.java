package io.github.litschiw.util.performance;

import io.github.litschiw.util.TestElement;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.RepeatedTest;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

class HashSetPerformanceTest extends CollectionPerformanceTest {

    public static Map<String, PerformanceTestResultsAggregator> aggregators = new HashMap<>();

    public HashSetPerformanceTest() {
        super(HashSet::new);
    }

    @RepeatedTest(10)
    void testAdding() {
        var result = aggregators.computeIfAbsent("addHashSet", PerformanceTestResultsAggregator::new);
        testAddPerformance(result);
    }

    @AfterAll
    static void afterAll() {
        aggregators.values().forEach(PerformanceTestResultsAggregator::printToCSV);
    }
}
