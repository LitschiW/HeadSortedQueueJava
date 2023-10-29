package io.github.litschiw.util;

import java.util.*;

public class HeadSortedQueue<T extends Bucketable> extends AbstractQueue<T> {

    private final Comparator<Bucketable> bucketComparator = Comparator.comparingDouble(Bucketable::priority);

    private final double bucketSize;
    private double headUpperBound;

    private final PriorityQueue<T> headQueue = new PriorityQueue<>(bucketComparator);

    private final TreeMap<Integer, HashSet<T>> tailMap = new TreeMap<>();

    private int size = 0;

    public HeadSortedQueue(double bucketSize) {
        this.bucketSize = bucketSize;
        this.headUpperBound = bucketSize;
    }


    @Override
    public Iterator<T> iterator() {
        PriorityQueue<T> acc = new PriorityQueue<>(size(), bucketComparator);
        acc.addAll(headQueue);
        tailMap.values().forEach(acc::addAll);
        return acc.iterator();
    }

    @Override
    public int size() {
        return size;
    }


    @Override
    public T peek() {
        return headQueue.peek();
    }

    @Override
    public T poll() {
        if (size == 0) {
            return null;
        }

        T polled = headQueue.poll();

        if (headQueue.isEmpty() || polled == null) {
            //if the head queue was empty or we pulled the last element, we need to refill the head queue with the next bucket
            Map.Entry<Integer, HashSet<T>> entry = tailMap.pollFirstEntry();
            if (entry != null) {
                headQueue.addAll(entry.getValue());
                headUpperBound = entry.getKey() * bucketSize;
            }
        }

        if (polled != null) {
            size--;
        }

        return polled;
    }

    @Override
    public boolean offer(T element) {
        Objects.requireNonNull(element);

        double priority = element.priority();
        boolean added;

        if (priority < headUpperBound) {
            added = headQueue.offer(element);
        } else {
            int bucket_id = (int) (priority / bucketSize);
            Collection<T> bucket = tailMap.computeIfAbsent(bucket_id, k -> new HashSet<>());
            added = bucket.add(element);
        }

        if (added) {
            size++;
        }

        return added;
    }

    public List<T> toList() {
        List<T> acc = new ArrayList<>(size());
        acc.addAll(this);
        return acc;
    }

    public Set<T> toSet() {
        Set<T> acc = new HashSet<>(size());
        acc.addAll(this);
        return acc;
    }

    @Override
    public boolean add(T element) {
        return this.offer(element);
    }

    @Override
    public boolean contains(Object o) {
        if (o == null) {
            return false;
        }

        for (T t : this) {
            if (o.equals(t)) {
                return true;
            }
        }

        return false;
    }
}
