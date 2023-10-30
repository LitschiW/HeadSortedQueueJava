package io.github.litschiw.util;

public record TestElement(double priority) implements Bucketable, Comparable<TestElement> {

    @Override
    public int compareTo(TestElement o) {
        return Double.compare(priority, o.priority);
    }

    @Override
    public String toString() {
        return String.valueOf(priority);
    }
}
