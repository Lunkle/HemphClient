package graphics.model;

import graphics.texture.ModelTexture;
import graphics.texture.Texture;
import graphics.vao.VAO;

/**
 * 
 * A model is a VAO with textures
 * 
 * @author Donny
 *
 */
public class Model {

	private VAO mesh;
	private Texture diffuseTexture;
	private Texture specularTexture;

	public VAO getMesh() {
		return mesh;
	}

	protected void setMesh(VAO mesh) {
		this.mesh = mesh;
	}

	protected void setDiffuseTexture(ModelTexture diffuseTexture) {
		diffuseTexture.setAsDiffuseTexture();
		this.diffuseTexture = diffuseTexture;
	}

	protected void setSpecularTexture(ModelTexture specularTexture) {
		specularTexture.setAsSpecularTexture();
		this.specularTexture = specularTexture;
	}

	/**
	 * Activates all the textures.
	 */
	public void activateTextures() {
		diffuseTexture.activateTexture();
		specularTexture.activateTexture();
	}

}
