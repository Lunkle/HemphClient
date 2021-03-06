package graphics.framebuffer;

import graphics.texture.FramebufferTexture;

public class DoubleBuffer {

	private FBO[] buffers;
	private int index = 0;

	/**
	 * Creates a new double buffer with the specified pixel width and height.
	 * 
	 * @param frameWidth
	 * @param frameHeight
	 */
	public DoubleBuffer(int frameWidth, int frameHeight) {
		buffers = new FBO[2];
		buffers[0] = new FBO(frameWidth, frameHeight);
		buffers[1] = new FBO(frameWidth, frameHeight);
	}

	/**
	 * Sets the current buffer to be the next buffer, binds it and clears the data
	 * off of it.
	 * 
	 * Run time O(1)
	 */
	public void bindNextBuffer() {
		swapIndex();
		FBO fbo = getCurrentBuffer();
		fbo.bindFBO();
	}

	public FBO getCurrentBuffer() {
		return buffers[index];
	}

	/**
	 * Gets the current buffer's colour texture. It will have whatever was rendered
	 * onto it since the last time it was cleared.
	 * 
	 * @return the current buffer's colour texture
	 */
	public FramebufferTexture getColourTexture() {
		return getCurrentBuffer().getColourTexture();
	}

	/**
	 * Gets the current buffer's depth texture. It will have whatever was rendered
	 * onto it since the last time it was cleared.
	 * 
	 * @return the current buffer's depth texture
	 */
	public FramebufferTexture getDepthTexture() {
		return getCurrentBuffer().getDepthTexture();
	}

	/**
	 * Swapping the index so it now points to the other buffer in the list.
	 * 
	 * Run time O(1)
	 */
	private void swapIndex() {
		index = 1 - index;
	}

	/**
	 * Delete the annoying little pieces of crap.
	 * 
	 * Run time O(1)
	 */
	public void cleanUp() {
		buffers[0].deleteFBO();
		buffers[1].deleteFBO();
	}

}
