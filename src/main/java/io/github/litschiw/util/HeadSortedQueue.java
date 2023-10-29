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
        this(bucketSize, 0); // assuming positive values by default
    }

    public HeadSortedQueue(double bucketSize, Collection<T> elements) {
        this(bucketSize);
        this.addAll(elements);
    }

    public HeadSortedQueue(double bucketSize, double expectedRangeStart) {
        this.bucketSize = bucketSize;
        this.headUpperBound = expectedRangeStart + bucketSize; // assuming positive values by default
    }

    public HeadSortedQueue(double bucketSize, double expectedRangeStart, int preAllocatedBuckets) {
        this(bucketSize, expectedRangeStart);

        //pre fill the tail map with empty buckets
        //expectedRangeStart + bucketSize is the upper border of the head queue, so we step one further
        double currentIdicator = expectedRangeStart + 2 * bucketSize;
        for (int i = 0; i < preAllocatedBuckets; i++) {
            tailMap.putIfAbsent(getBucketId(currentIdicator), new HashSet<>());
            currentIdicator += bucketSize;
        }
    }


    @Override
    public Iterator<T> iterator() {
        if (size() == 0) {
            return Collections.emptyIterator();
        }

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
            int bucket_id = getBucketId(priority);
            Collection<T> bucket = tailMap.computeIfAbsent(bucket_id, k -> new HashSet<>());
            added = bucket.add(element);
        }

        if (added) {
            size++;
        }

        return added;
    }


    @Override
    public boolean remove(Object element) {
        if (!(element instanceof Bucketable bucketable)) {
            return false;
        }

        double priority = bucketable.priority();
        boolean removed = false;

        if (priority < headUpperBound) {
            removed = headQueue.remove(bucketable);
        } else {
            int bucket_id = getBucketId(priority);
            Collection<T> bucket = tailMap.get(bucket_id);
            if (bucket != null) {
                removed = bucket.remove(bucketable);
            }
        }

        if (removed) {
            size--;
        }
        return removed;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        Objects.requireNonNull(c);

        boolean changed = false;
        for (Object o : c) {
            changed |= this.remove(o);
        }
        return changed;
    }


    @Override
    public boolean retainAll(Collection<?> c) {
        Objects.requireNonNull(c);

        boolean changed = false;
        for (T element : this) {
            if (!c.contains(element)) {
                changed |= this.remove(element);
            }
        }
        return changed;
    }

    @Override
    public boolean add(T element) {
        return this.offer(element);
    }

    @Override
    public boolean contains(Object o) {
        if (!(o instanceof Bucketable bucketable)) {
            return false;
        }

        double priority = bucketable.priority();

        if (priority < headUpperBound) {
            return headQueue.contains(o);
        } else {
            int bucket_id = getBucketId(priority);
            Collection<T> bucket = tailMap.get(bucket_id);
            return bucket != null && bucket.contains(o);
        }
    }

    private int getBucketId(double indicator) {
        return (int) (indicator / bucketSize);
    }


}
