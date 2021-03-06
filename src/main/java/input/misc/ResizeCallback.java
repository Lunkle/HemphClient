package input.misc;

import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.opengl.GL11;

import graphics.Visual;

public class ResizeCallback extends GLFWFramebufferSizeCallback {

	private Visual visuals;

	public ResizeCallback(Visual visuals) {
		this.visuals = visuals;
	}

	@Override
	public void invoke(long windowID, int width, int height) {
		if (width != 0 && height != 0) {
			visuals.updateWindowDimensions(width, height);
			GL11.glViewport(0, 0, width, height);
		}
	}

}
