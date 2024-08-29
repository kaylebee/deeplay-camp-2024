package io.deeplay.camp.bot;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class ReplayBuffer {
    private final int capacity;
    private final LinkedList<Experience> buffer;
    private final Random random;

    public ReplayBuffer(int capacity) {
        this.capacity = capacity;
        this.buffer = new LinkedList<>();
        this.random = new Random();
    }

    public void add(Experience experience) {
        if (buffer.size() >= capacity) {
            buffer.removeFirst();
        }
        buffer.add(experience);
    }

    public List<Experience> sample(int batchSize) {
        List<Experience> batch = new LinkedList<>();
        for (int i = 0; i < batchSize; i++) {
            int index = random.nextInt(buffer.size());
            batch.add(buffer.get(index));
        }
        return batch;
    }

    public int size() {
        return buffer.size();
    }
}
