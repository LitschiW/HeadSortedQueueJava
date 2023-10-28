package io.github.litschiw.util;

import java.util.*;

public class HeadSortedQueue<T extends Bucketable> extends AbstractQueue<T> {

    private final Comparator<Bucketable> bucketComparator = Comparator.comparingDouble(Bucketable::priority);

    private final double bucketSize;
    private double headUpperBound;

    private final PriorityQueue<T> headQueue = new PriorityQueue<>(bucketComparator);

    private final TreeMap<Integer, ArrayList<T>> tailMap = new TreeMap<>();

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
        return headQueue.size() + tailMap.values().stream().mapToInt(List::size).sum();
    }


    @Override
    public T peek() {
        return headQueue.peek();
    }

    @Override
    public T poll() {
        T polled = headQueue.poll();

        if (headQueue.isEmpty() || polled == null) {
            //if the head queue was empty or we pulled the last element, we need to refill the head queue with the next bucket
            Map.Entry<Integer, ArrayList<T>> entry = tailMap.firstEntry();
            if (entry != null) {
                tailMap.remove(entry.getKey());
                headQueue.addAll(entry.getValue());
                headUpperBound = entry.getKey() * bucketSize;
            }

            //if the head queue was empty we poll the next element, also triggering an empty check again
            if (polled == null) {
                polled = poll();
            }
        }

        return polled;
    }

    @Override
    public boolean offer(T t) {
        double priority = t.priority();
        if (priority < headUpperBound) {
            return headQueue.offer(t);
        } else {
            int bucket = (int) (priority / bucketSize);
            ArrayList<T> bucketList = tailMap.computeIfAbsent(bucket, k -> new ArrayList<>());
            return bucketList.add(t);
        }
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


}
