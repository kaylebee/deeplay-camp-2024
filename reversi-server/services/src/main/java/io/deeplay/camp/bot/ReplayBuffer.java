package io.deeplay.camp.bot;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class ReplayBuffer {
    private final int capacity;
    private final LinkedList<Experience> buffer;
    private final Random random;
    private static final double PRIORITY_ALPHA_BASE  = 0.6;
    private static final double PRIORITY_BETA_BASE  = 0.4;
    private double maxPriority = 1.0;

    public ReplayBuffer(int capacity) {
        this.capacity = capacity;
        this.buffer = new LinkedList<>();
        this.random = new Random();
    }

    // ОПТИМИЗИРОВАТЬ !!!!! \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    public void updatePriority(Experience experience, double newPriority) {
        for (Experience exp : buffer) {
            if (exp.equals(experience)) {
                exp.setPriority(newPriority);
                break;
            }
        }
    }

    public void add(Experience experience) {
        if (buffer.size() >= capacity) {
            buffer.removeFirst();
        }
        experience.setPriority(maxPriority);
        buffer.add(experience);
    }

    public List<Experience> sample(int batchSize) {
        double priorAlpha = proportionalAlpha(size());
        double priorBeta = proportionalBeta(size());

        double totalPriority = buffer.stream().mapToDouble(e -> Math.pow(e.getPriority(), priorAlpha)).sum();

        List<Experience> batch = new ArrayList<>();
        for (int i = 0; i < batchSize; i++) {
            double rand = random.nextDouble() * totalPriority;
            double cumulativePriority = 0;

            for (Experience experience : buffer) {
                cumulativePriority += Math.pow(experience.getPriority(), priorAlpha);
                if (cumulativePriority >= rand) {
                    batch.add(experience);
                    break;
                }
            }
        }

        double maxWeight = Math.pow(totalPriority / buffer.size(), -priorBeta);
        for (Experience exp : batch) {
            double samplingProb = Math.pow(exp.getPriority(), priorAlpha) / totalPriority;
            double weight = Math.pow(samplingProb * buffer.size(), -priorBeta) / maxWeight;
            exp.setImportanceWeight(weight);
        }

        return batch;
    }

    public int size() {
        return buffer.size();
    }

    private double proportionalAlpha(int size) {
        return PRIORITY_ALPHA_BASE - 0.3 * (Math.min(50000, size) / 50000.0);
    }

    private double proportionalBeta(int size) {
        return PRIORITY_BETA_BASE + 0.5 * (Math.min(50000, size) / 50000.0);
    }
}
