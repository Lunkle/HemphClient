package graphics.framebuffer;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;

import graphics.texture.FramebufferTexture;
import loading.framebuffer.EmptyColourAttachmentRawData;
import loading.framebuffer.EmptyDepthAttachmentRawData;
import loading.framebuffer.EmptyTextureRawData;

public class FBO {

	private static List<Integer> fbos = new ArrayList<>();

	private FramebufferTexture colourTexture;
	private FramebufferTexture depthTexture;

	private int fboID;
	private int frameWidth;
	private int frameHeight;

	public FBO(int frameWidth, int frameHeight) {
		this.frameWidth = frameWidth;
		this.frameHeight = frameHeight;
		fboID = GL30.glGenFramebuffers();
		bindFBO();
		attachColourTexture();
		attachDepthTexture();
		bindDefaultFBO();
	}

	private void attachColourTexture() {
		colourTexture = new FramebufferTexture();
		EmptyTextureRawData emptyColourAttachmentRawData = new EmptyColourAttachmentRawData(getFrameWidth(), getFrameHeight());
		colourTexture.interpret(emptyColourAttachmentRawData);
		GL32.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, colourTexture.getID(), 0);
	}

	private void attachDepthTexture() {
		depthTexture = new FramebufferTexture();
		EmptyTextureRawData emptyDepthAttachmentRawData = new EmptyDepthAttachmentRawData(getFrameWidth(), getFrameHeight());
		depthTexture.interpret(emptyDepthAttachmentRawData);
		GL32.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL11.GL_TEXTURE_2D, depthTexture.getID(), 0);
	}

	public void bindFBO() {
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fboID);
	}

	/**
	 * Binds the default FBO, which has ID of 0.
	 */
	public static void bindDefaultFBO() {
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
	}

	public int getFrameWidth() {
		return frameWidth;
	}

	public int getFrameHeight() {
		return frameHeight;
	}

	public FramebufferTexture getColourTexture() {
		return colourTexture;
	}

	public FramebufferTexture getDepthTexture() {
		return depthTexture;
	}

	public void deleteFBO() {
		GL30.glDeleteFramebuffers(fboID);
		fbos.remove(new Integer(fboID));
	}

	public static void cleanUp() {
		for (int fbo : fbos) {
			GL30.glDeleteFramebuffers(fbo);
		}
	}

	/**
	 * Wipes everything from the currently bound framebuffer object. This includes
	 * colour and depth values.
	 */
	public static void clearCurrentFBOData() {
		GL11.glClearColor(0, 0, 0, 1);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
	}

}
