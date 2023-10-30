package io.github.litschiw.util;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HeadSortedQueueTest {


    enum Operation {
        ADD, POLL, CONTAINS, PEEK, REMOVE;

        public static Operation getRandomOperation(Random random) {
            double randomDouble = random.nextDouble();

            if (randomDouble < .40) {
                return Operation.ADD;
            } else if (randomDouble < .65) {
                return Operation.POLL;
            } else if (randomDouble < .80) {
                return Operation.REMOVE;
            } else if (randomDouble < .95) {
                return Operation.PEEK;
            } else {
                return Operation.CONTAINS;
            }
        }
    }

    @RepeatedTest(20)
    void canBeCreatedFromList() {
        var random = new Random();
        var seed = random.nextLong();
        System.out.println("Seed: " + seed);
        random = new Random(seed);

        var input = random.ints(random.nextInt(10000))
                          .distinct()
                          .mapToObj(TestElement::new)
                          .toArray(TestElement[]::new);
        var listOfElements = Arrays.stream(input).toList();
        var queue = new HeadSortedQueue<>(1, listOfElements);

        assertThat(queue.size()).isEqualTo(listOfElements.size());
        assertThat(queue).containsAll(listOfElements);
    }


    @RepeatedTest(20)
    void calculatesSizeCorrectly() {
        var random = new Random();
        var seed = random.nextLong();
        System.out.println("Seed: " + seed);
        random = new Random(seed);

        var input = random.ints(random.nextInt(300000))
                          .distinct()
                          .mapToObj(TestElement::new)
                          .toArray(TestElement[]::new);

        var queue = new HeadSortedQueue<TestElement>(10);
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
                          .mapToObj(TestElement::new)
                          .toArray(TestElement[]::new);


        var queue = new HeadSortedQueue<TestElement>(0.1);
        Collections.addAll(queue, input);

        var sortedList = new ArrayList<TestElement>(queue.size());
        while (!queue.isEmpty()) {
            sortedList.add(queue.poll());
        }
        //check if queue is sorted with assertJ
        assertThat(sortedList).isSortedAccordingTo(Comparator.comparingDouble(TestElement::priority));
    }

    @RepeatedTest(20)
    void sortsIntsCorrectly() {
        var random = new Random();
        var seed = random.nextLong();
        System.out.println("Seed: " + seed);
        random = new Random(seed);

        var input = random.ints(random.nextInt(300000))
                          .mapToObj(TestElement::new)
                          .toArray(TestElement[]::new);


        var queue = new HeadSortedQueue<TestElement>(40000000); //~int range / 10
        Collections.addAll(queue, input);

        var sortedList = new ArrayList<TestElement>(queue.size());
        while (!queue.isEmpty()) {
            sortedList.add(queue.poll());
        }
        //check if queue is sorted with assertJ
        assertThat(sortedList).isSortedAccordingTo(Comparator.comparingDouble(TestElement::priority));
    }


    @Test
    void doesCheckContainCorrectly() {
        var queue = new HeadSortedQueue<TestElement>(1);
        for (int i = 0; i < 10; i++) {
            queue.add(new TestElement(i));
        }

        for (int i = 0; i < 10; i++) {
            assertThat(queue.contains(new TestElement(i))).isTrue();
        }

        assertThat(queue.contains(new TestElement(10))).isFalse();
        assertThat(queue.contains(new TestElement(-1))).isFalse();
    }

    @Test
    void cannotAddNull() {
        var queue = new HeadSortedQueue<TestElement>(1);
        assertThatThrownBy(() -> queue.add(null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    void returnsNullIfEmpty() {
        var queue = new HeadSortedQueue<TestElement>(1);
        assertThat(queue.poll()).isNull();
    }

    @Test
    void returnsFalseIfContainsArgumentIsNull() {
        var queue = new HeadSortedQueue<TestElement>(1);
        assertThat(queue.contains(null)).isFalse();
    }

    @Test
    void canPeekCorrectly() {
        var queue = new HeadSortedQueue<TestElement>(1);
        var elements = new TestElement[]{new TestElement(1), new TestElement(2), new TestElement(-3)};
        Collections.addAll(queue, elements);

        assertThat(queue.peek()).isEqualTo(elements[2]);
    }

    @Test
    void canProvideEmptyIterator() {
        var queue = new HeadSortedQueue<TestElement>(1);
        assertThat(queue.iterator()).isNotNull();
        assertThat(queue.iterator().hasNext()).isFalse();
    }

    @RepeatedTest(20)
    void supportsRandomIO() {
        var random = new Random();
        var seed = random.nextLong();
        System.out.println("Seed: " + seed);
        random = new Random(seed);


        //create list of 100000 random operations
        Random finalRandom = random;
        var operations = random.ints(100000)
                               .parallel()
                               .mapToObj(i -> Operation.getRandomOperation(finalRandom))
                               .toArray(Operation[]::new);

        var queue = new HeadSortedQueue<TestElement>(0.05);

        for (Operation op : operations) {
            switch (op) {
                case ADD -> queue.add(new TestElement(random.nextDouble()));
                case POLL -> queue.poll();
                case CONTAINS -> queue.contains(new TestElement(random.nextDouble()));
                case PEEK -> queue.peek();
                case REMOVE -> queue.remove(new TestElement(random.nextDouble()));
            }
        }
    }

    @Test
    void canRemoveElementFromRandomLocation() {
        var random = new Random();
        var seed = random.nextLong();
        System.out.println("Seed: " + seed);
        random = new Random(seed);

        var input = random.doubles(random.nextInt(300000))
                          .mapToObj(TestElement::new)
                          .toArray(TestElement[]::new);

        var queue = new HeadSortedQueue<TestElement>(0.1);
        Collections.addAll(queue, input);

        var elementsToRemove = random.ints(1000, 0, input.length)
                                     .mapToObj(i -> input[i])
                                     .toArray(TestElement[]::new);

        for (TestElement element : elementsToRemove) {
            if (!queue.contains(element)) {
                continue;
            }

            assertThat(queue.remove(element)).isTrue();
            assertThat(queue.contains(element)).isFalse();
        }
    }

    @RepeatedTest(20)
    void canRemoveListOfElements() {
        var random = new Random();
        var seed = random.nextLong();
        System.out.println("Seed: " + seed);
        random = new Random(seed);

        var input = random.doubles(random.nextInt(300000))
                          .distinct()
                          .mapToObj(TestElement::new)
                          .toArray(TestElement[]::new);

        var queue = new HeadSortedQueue<TestElement>(0.1);
        Collections.addAll(queue, input);

        var elementsToRemove = random.ints(1000, 0, input.length)
                                     .mapToObj(i -> input[i])
                                     .toArray(TestElement[]::new);

        queue.removeAll(Arrays.asList(elementsToRemove));

        for (TestElement element : elementsToRemove) {
            assertThat(queue.contains(element)).isFalse();
        }
    }

    @RepeatedTest(20)
    void canRetainSome() {
        var random = new Random();
        var seed = random.nextLong();
        System.out.println("Seed: " + seed);
        random = new Random(seed);

        var input = random.doubles(random.nextInt(300000))
                          .mapToObj(TestElement::new)
                          .toArray(TestElement[]::new);

        var queue = new HeadSortedQueue<TestElement>(0.1);
        Collections.addAll(queue, input);

        var elementsToRetain = random.ints(1000, 0, input.length)
                                     .mapToObj(i -> input[i])
                                     .collect(Collectors.toSet());

        queue.retainAll(elementsToRetain);

        assertThat(queue).containsAll(elementsToRetain);
    }


    @Test
    void canClear() {
        var queue = new HeadSortedQueue<TestElement>(1);
        //produce 100000 random doubles
        var random = new Random();
        var input = random.doubles(100000)
                          .mapToObj(TestElement::new)
                          .toArray(TestElement[]::new);
        Collections.addAll(queue, input);

        queue.clear();

        assertThat(queue).isEmpty();
        assertThat(queue.size()).isEqualTo(0);
        assertThat(queue.poll()).isNull();
        assertThat(queue.peek()).isNull();
        assertThat(queue.iterator()).isNotNull();
        assertThat(queue.iterator().hasNext()).isFalse();
    }
}
