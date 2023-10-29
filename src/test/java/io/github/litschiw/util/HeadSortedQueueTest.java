package io.github.litschiw.util;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

        var input = random.ints(random.nextInt(300000))
                          .distinct()
                          .mapToObj(TestBuckatable::new)
                          .toArray(TestBuckatable[]::new);

        var queue = new HeadSortedQueue<TestBuckatable>(10);
        Collections.addAll(queue, input);

        var polled_count = random.nextInt(input.length);
        for (int i = 0; i < polled_count; i++) {
            queue.poll();
        }

        assertThat(queue.size()).isEqualTo(input.length - polled_count);
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
        var seed = 5568966347171102795L;//random.nextLong();
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


    @Test
    void doesCheckContainCorrectly(){
        var queue = new HeadSortedQueue<TestBuckatable>(1);
        for (int i = 0; i < 10; i++) {
            queue.add(new TestBuckatable(i));
        }

        for (int i = 0; i < 10; i++) {
            assertThat(queue.contains(new TestBuckatable(i))).isTrue();
        }

        assertThat(queue.contains(new TestBuckatable(10))).isFalse();
        assertThat(queue.contains(new TestBuckatable(-1))).isFalse();
    }

    @Test
    void cannotAddNull() {
        var queue = new HeadSortedQueue<TestBuckatable>(1);
        assertThatThrownBy(() -> queue.add(null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    void returnsNullIfEmpty() {
        var queue = new HeadSortedQueue<TestBuckatable>(1);
        assertThat(queue.poll()).isNull();
    }

    @Test
    void returnsFalsIfContainsArgumentIsNull() {
        var queue = new HeadSortedQueue<TestBuckatable>(1);
        assertThat(queue.contains(null)).isFalse();
    }

    @Test
    void canPeekCorrectly() {
        var queue = new HeadSortedQueue<TestBuckatable>(1);
        var elements = new TestBuckatable[]{new TestBuckatable(1), new TestBuckatable(2), new TestBuckatable(-3)};
        Collections.addAll(queue, elements);

        assertThat(queue.peek()).isEqualTo(elements[2]);
    }
}
