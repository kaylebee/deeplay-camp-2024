package io.deeplay.camp.bot;

import org.nd4j.linalg.api.ndarray.INDArray;

/**
 * The {@code Experience} class encapsulates a single experience sample from an agent's interaction with
 * an environment, typically used in reinforcement learning algorithms. An experience consists of the
 * following components:
 * <ul>
 *   <li>{@code state}: The current state of the environment when the action was taken.</li>
 *   <li>{@code action}: The action taken by the agent.</li>
 *   <li>{@code reward}: The reward received after taking the action.</li>
 *   <li>{@code nextState}: The subsequent state of the environment after the action.</li>
 *   <li>{@code done}: A boolean indicating whether the episode has terminated.</li>
 *   <li>{@code priority}: Used for prioritized experience replay, indicating the importance of this experience.</li>
 *   <li>{@code importanceWeight}: The weight used for importance sampling in prioritized experience replay.</li>
 * </ul>
 * This structure is often used in replay buffers to store past experiences, allowing reinforcement learning
 * models to learn from them.
 */
public class Experience {

    /**
     * The current state of the environment when the action was taken.
     */
    private final INDArray state;

    /**
     * The action taken by the agent in the current state.
     */
    private final int action;

    /**
     * The reward received from the environment after the action was taken.
     */
    private final double reward;

    /**
     * The state of the environment after the action was taken.
     */
    private final INDArray nextState;

    /**
     * Indicates whether the episode has ended after the action was taken.
     */
    private final boolean done;

    /**
     * The priority of this experience sample, used in prioritized experience replay.
     */
    private double priority;

    /**
     * The importance sampling weight of this experience, used to correct biased updates.
     */
    private double importanceWeight;

    /**
     * Constructs an instance of an {@code Experience} with specified parameters.
     *
     * @param state  The current state of the environment.
     * @param action  The action taken by the agent.
     * @param reward  The reward received after taking the action.
     * @param nextState  The next state of the environment after the action.
     * @param done  A boolean indicating if the episode is done.
     * @param priority  The initial priority of this experience.
     */
    public Experience(INDArray state, int action, double reward, INDArray nextState, boolean done, double priority) {
        this.state = state;
        this.action = action;
        this.reward = reward;
        this.nextState = nextState;
        this.done = done;
        this.priority = priority;
        this.importanceWeight = 1.0;
    }

    /**
     * Gets the importance sampling weight for this experience.
     *
     * Importance sampling weights are used in prioritized experience replay to
     * adjust the learning process such that it accounts for the non-uniform sampling.
     *
     * @return the importance sampling weight for this experience.
     */
    public double getImportanceWeight() {
        return importanceWeight;
    }

    /**
     * Sets the importance sampling weight for this experience.
     *
     * This weight is adjusted during learning to ensure unbiased updates.
     *
     * @param importanceWeight  The new importance sampling weight for this experience.
     */
    public void setImportanceWeight(double importanceWeight) {
        this.importanceWeight = importanceWeight;
    }

    /**
     * Retrieves the priority of this experience.
     *
     * In prioritized experience replay, experiences with higher priority are more
     * likely to be sampled, allowing the agent to focus on more significant learning updates.
     *
     * @return the priority of this experience.
     */
    public double getPriority() {
        return priority;
    }

    /**
     * Sets the priority of this experience.
     *
     * Adjusting the priority of an experience affects its likelihood of being sampled
     * in future learning iterations.
     *
     * @param priority  The new priority for this experience.
     */
    public void setPriority(double priority) {
        this.priority = priority;
    }

    /**
     * Retrieves the state of the environment when the action was taken.
     *
     * This method allows the agent to review the state aspect of this experience.
     *
     * @return the state of the environment.
     */
    public INDArray getState() {
        return state;
    }

    /**
     * Retrieves the action taken by the agent.
     *
     * Knowing the action part of the experience is crucial for understanding the
     * outcome and future decision-making processes.
     *
     * @return the action taken.
     */
    public int getAction() {
        return action;
    }

    /**
     * Retrieves the reward received by the agent.
     *
     * Rewards indicate the immediate feedback from the environment due to the action.
     * These are fundamental for reinforcement learning updates.
     *
     * @return the reward received.
     */
    public double getReward() {
        return reward;
    }

    /**
     * Retrieves the next state of the environment after the action.
     *
     * This is essential for determining how the environment responded to the action
     * and is used in learning the effects of actions taken.
     *
     * @return the next state of the environment.
     */
    public INDArray getNextState() {
        return nextState;
    }

    /**
     * Indicates whether the episode is done.
     *
     * When the episode is done, it signifies the end of an agent's journey in the
     * environment, necessitating certain learning updates or resetting procedures.
     *
     * @return {@code true} if the episode is done; {@code false} otherwise.
     */
    public boolean isDone() {
        return done;
    }
}