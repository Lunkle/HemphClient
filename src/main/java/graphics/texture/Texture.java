package graphics.texture;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import loading.loader.InterpretedData;

public abstract class Texture implements InterpretedData {

	private static List<Integer> textures = new ArrayList<>();

	private int textureID;
	private int textureUnit;

	public int getTextureUnit() {
		return textureUnit;
	}

	public void setTextureUnit(int textureUnit) {
		this.textureUnit = textureUnit;
	}

	public void activateTexture() {
		GL13.glActiveTexture(getTextureUnit());
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, getTextureID());
	}

	/**
	 * Gets the handle of the texture
	 */
	public int getID() {
		return getTextureID();
	}

	/**
	 * Add to list to be deleted when the program is done
	 * 
	 * @param texture the texture
	 */
	public void addTexture(Texture texture) {
		textures.add(texture.getID());
	}

	public int getTextureID() {
		return textureID;
	}

	public void setTextureID(int textureID) {
		this.textureID = textureID;
	}

	public static void cleanUp() {
		for (int texture : textures) {
			GL11.glDeleteTextures(texture);
		}
	}

}
