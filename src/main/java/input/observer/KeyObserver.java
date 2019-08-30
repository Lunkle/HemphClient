package input.observer;

import java.util.HashMap;
import java.util.Map;

import input.command.KeyCommand;
import input.information.Actions;
import input.information.InputTypes;
import input.information.Keys;

public class KeyObserver extends BasicObserver {

	private Map<Keys, KeyCommand> commandMap;

	public KeyObserver() {
		super();
		commandMap = new HashMap<>();
	}

	public void addCommand(Keys input, KeyCommand command) {
		commandMap.put(input, command);
	}

	public KeyCommand getCommand(Keys input) {
		return commandMap.get(input);
	}

	@Override
	public void handleEvent(InputTypes type, Keys input, Actions action, float[] data) {
		KeyCommand command = getCommand(input);
		if (command != null) {
			if (action == Actions.PRESS)
				command.onPress.execute();
			else if (action == Actions.RELEASE)
				command.onRelease.execute();
		} else {
			notifyObservers(type, input, action, data);
		}
	}

}