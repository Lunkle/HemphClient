package input.callback;

import org.lwjgl.glfw.GLFWMouseButtonCallback;

import input.information.Actions;
import input.information.InputTypes;
import input.information.Keys;
import input.observer.InputObservee;
import input.observer.InputObserver;
import input.observer.InputObserverNode;

public class MouseButtonCallback extends GLFWMouseButtonCallback implements InputObservee {

	private InputObserverNode node;

	public MouseButtonCallback() {
		node = getEmptyObserverNode();
	}

	@Override
	public void invoke(long window, int button, int action, int mods) {
		notifyObservers(InputTypes.MOUSE_BUTTON, Keys.getKey(button), Actions.getAction(action), new float[0]);
	}

	@Override
	public void notifyObservers(InputTypes type, Keys input, Actions action, float[] data) {
		if (node != null)
			node.handle(type, input, action, data);
	}

	@Override
	public void addObserver(InputObserver newObserver) {
		InputObserverNode newNode = new InputObserverNode(newObserver);
		newNode.setNextNode(node);
		node = newNode;
	}

	@Override
	public void clearObservers() {
		node = getEmptyObserverNode();
	}

}