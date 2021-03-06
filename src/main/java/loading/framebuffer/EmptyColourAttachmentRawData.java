package loading.framebuffer;

import org.lwjgl.opengl.GL11;

import graphics.texture.FramebufferTexture;
import loading.loader.InterpretedData;

public class EmptyColourAttachmentRawData extends EmptyTextureRawData {

	public EmptyColourAttachmentRawData(int textureWidth, int textureHeight) {
		super(textureWidth, textureHeight);
	}

	@Override
	public int getInternalFormat() {
		return GL11.GL_RGB;
	}

	@Override
	public int getFormat() {
		return GL11.GL_RGB;
	}

	@Override
	public int getType() {
		return GL11.GL_UNSIGNED_BYTE;
	}

	@Override
	public InterpretedData newInterpretedData() {
		return new FramebufferTexture();
	}

}
