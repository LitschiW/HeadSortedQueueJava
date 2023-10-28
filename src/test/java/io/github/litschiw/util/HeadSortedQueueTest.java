package io.github.litschiw.util;

import org.junit.jupiter.api.RepeatedTest;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class HeadSortedQueueTest {

    record TestBuckatable(double priority) implements Bucketable, Comparable<TestBuckatable> {

        @Override
        public int compareTo(TestBuckatable o) {
            return Double.compare(priority, o.priority);
        }

        @Override
        public String toString() {
            return String.valueOf(priority);
        }
    }


    @RepeatedTest(20)
    void calculatesSizeCorrectly() {
        var random = new Random();
        var seed = random.nextLong();
        System.out.println("Seed: " + seed);
        random = new Random(seed);

        var expected_size = random.nextInt(300000);

        var input = random.doubles(expected_size)
                          .mapToObj(TestBuckatable::new)
                          .toArray(TestBuckatable[]::new);

        var queue = new HeadSortedQueue<TestBuckatable>(10);
        Collections.addAll(queue, input);

        assertThat(queue.size()).isEqualTo(expected_size);
    }

    @RepeatedTest(20)
    void sortsDoublesCorrectly() {
        var random = new Random();
        var seed = random.nextLong();
        System.out.println("Seed: " + seed);
        random = new Random(seed);

        var input = random.doubles(random.nextInt(300000))
                          .mapToObj(TestBuckatable::new)
                          .toArray(TestBuckatable[]::new);


        var queue = new HeadSortedQueue<TestBuckatable>(0.1);
        Collections.addAll(queue, input);

        var sortedList = new ArrayList<TestBuckatable>(queue.size());
        while (!queue.isEmpty()) {
            sortedList.add(queue.poll());
        }
        //check if queue is sorted with assertJ
        assertThat(sortedList).isSortedAccordingTo(Comparator.comparingDouble(TestBuckatable::priority));
    }

    @RepeatedTest(20)
    void sortsIntsCorrectly() {
        var random = new Random();
        var seed = random.nextLong();
        System.out.println("Seed: " + seed);
        random = new Random(seed);

        var input = random.ints(random.nextInt(300000))
                          .mapToObj(TestBuckatable::new)
                          .toArray(TestBuckatable[]::new);


        var queue = new HeadSortedQueue<TestBuckatable>(40000000); //~int range / 10
        Collections.addAll(queue, input);

        var sortedList = new ArrayList<TestBuckatable>(queue.size());
        while (!queue.isEmpty()) {
            sortedList.add(queue.poll());
        }
        //check if queue is sorted with assertJ
        assertThat(sortedList).isSortedAccordingTo(Comparator.comparingDouble(TestBuckatable::priority));
    }
}
