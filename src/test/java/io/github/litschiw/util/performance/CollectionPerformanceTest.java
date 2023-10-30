package io.github.litschiw.util.performance;

import io.github.litschiw.util.TestElement;

import java.util.Collection;
import java.util.function.Supplier;

public abstract class CollectionPerformanceTest {


    private final Supplier<Collection<TestElement>> collectionSupplier;

    public CollectionPerformanceTest(Supplier<Collection<TestElement>> collectionSupplier) {
        this.collectionSupplier = collectionSupplier;
    }

    public static int getTestSize(int datapoint) {
        return datapoint * datapoint;
    }


    public void testAddPerformance(PerformanceTestResultsAggregator result) {

        Collection<TestElement> warmupCollection = collectionSupplier.get();

        for (int i = 0; i < 1000; i++) {
            warmupCollection.add(new TestElement(i));
        }
        warmupCollection.clear();


        final int maxSteps = 800;

        TestElement[] preAllocated = new TestElement[getTestSize(maxSteps)];
        for (int i = 0; i < preAllocated.length; i++) {
            preAllocated[i] = new TestElement(i);
        }

        for (int j = 1; j <= maxSteps; j++) {
            int testSize = getTestSize(j);
            Collection<TestElement> collection = collectionSupplier.get();

            long start = System.currentTimeMillis();
            for (int x = 0; x < testSize; x++) {
                collection.add(preAllocated[x]);
            }
            long duration = System.currentTimeMillis() - start;
            result.addDataPoint(testSize, duration);
        }

    }
}
