package logics;

import org.lwjgl.glfw.GLFW;

import input.Input;
import logics.state.GameStateWrapper;

public class Logic extends Thread {

	private double updatesPerSecond = 30.0;
	private double targetUpdateTime = 1.0 / updatesPerSecond;
	private double previousUpdateDuration = 0.0;
	private double previousUpdateEndTime = 0.0;
	private double accumulator = 0.0;
	private int updateCounter = 1;

	private GameStateWrapper gameStateWrapper;

	private boolean isDone = false;

	public Logic(GameStateWrapper stateWrapper, Input inputs) {
		this.gameStateWrapper = stateWrapper;
	}

	@Override
	public void run() {
		while (!isDone) {
			previousUpdateDuration = GLFW.glfwGetTime() - previousUpdateEndTime;
			accumulator += previousUpdateDuration;
			double averageUpdateDuration = previousUpdateEndTime / updateCounter;
			int numUpdates = (int) ((accumulator + averageUpdateDuration) / targetUpdateTime);
			for (int i = 0; i < numUpdates; i++) {
				gameStateWrapper.getState().update();
				accumulator -= targetUpdateTime;
			}
			updateCounter += numUpdates;
			previousUpdateEndTime += previousUpdateDuration;
		}
		cleanUp();
	}

	/**
	 * ??? What does this do
	 * 
	 * @return some garbage
	 */
	public double getAlpha() {
		return accumulator / targetUpdateTime;
	}

	public void cleanUp() {
		isDone = true;
		synchronized (this) {
			notify();
		}
	}

}
