package io.github.litschiw.util.performance;

import io.github.litschiw.util.HeadSortedQueue;
import io.github.litschiw.util.TestElement;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.RepeatedTest;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

class PriorityQueuePerformanceTest extends CollectionPerformanceTest {

    public static Map<String, PerformanceTestResultsAggregator> aggregators = new HashMap<>();

    public PriorityQueuePerformanceTest() {
        super(PriorityQueue::new);
    }

    @RepeatedTest(10)
    void testAdding() {
        var result = aggregators.computeIfAbsent("addPriorityQueue", PerformanceTestResultsAggregator::new);
        testAddPerformance(result);
    }

    @AfterAll
    static void afterAll() {
        aggregators.values().forEach(PerformanceTestResultsAggregator::printToCSV);
    }
}
