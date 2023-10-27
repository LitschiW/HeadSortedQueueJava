package io.github.litschiw.util;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

class HeadSortedQueueTest {

    static class TestBuckatable implements Bucketable, Comparable<TestBuckatable> {
        private final double priority;

        public TestBuckatable(double priority) {
            this.priority = priority;
        }

        @Override
        public double getPriority() {
            return priority;
        }

        @Override
        public int compareTo(TestBuckatable o) {
            return Double.compare(priority, o.priority);
        }

        @Override
        public String toString() {
            return String.valueOf(priority);
        }
    }

    @Test
    void sortsCorrectly() {
        var random = new Random();
        var input = random.doubles(100).mapToObj(TestBuckatable::new).toArray(TestBuckatable[]::new);

        var queue = new HeadSortedQueue<TestBuckatable>(0.1);
        Collections.addAll(queue, input);

        var sortedList = queue.toList();
        //check if queue is sorted with assertJ
        assertThat(sortedList).isSortedAccordingTo(Comparator.comparingDouble(TestBuckatable::getPriority));
    }
}