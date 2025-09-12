package you.jass.betterhitreg.util;

import java.util.ArrayDeque;

import java.util.Deque;

public class RegQueue {
    private final int capacity;
    private final Deque<Integer> delayQueue;
    private long delaySum = 0L;
    private final Deque<Boolean> ghostQueue;
    private int ghostCount = 0;

    public RegQueue(int capacity) {
        this.capacity = capacity;
        this.delayQueue = new ArrayDeque<>(capacity);
        this.ghostQueue = new ArrayDeque<>(capacity);
    }

    public void add(int value) {
        if (ghostQueue.size() == capacity && ghostQueue.removeFirst()) ghostCount--;

        boolean isGhost = value == -1;
        ghostQueue.addLast(isGhost);
        if (isGhost) ghostCount++;

        if (!isGhost) {
            if (delayQueue.size() == capacity) delaySum -= delayQueue.removeFirst();
            delayQueue.addLast(value);
            delaySum += value;
        }
    }

    public int getAverageDelay() {
        if (delayQueue.isEmpty()) return 0;
        return (int) (delaySum / delayQueue.size());
    }

    public int getGhostRatio() {
        if (ghostQueue.isEmpty()) return 0;
        return ghostCount / ghostQueue.size();
    }
}

