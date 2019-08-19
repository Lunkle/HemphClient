package graphics.entity;

import graphics.model.Model;
import graphics.transformation.WorldTransformation;
import math.Matrix4f;

public class Entity {

	private Model model;
	private float textureXOffset;
	private float textureYOffset;
	private WorldTransformation transformation;

	/**
	 * Initializes the entity with default transformation
	 * 
	 * @param model
	 * @param texture
	 */
	public Entity(Model model) {
		this(model, 0, 0, 0, 0, 0, 0, 0, 0, 0);
	}

	/**
	 * Initializes the entity with the specified transformations.
	 * 
	 * @param model
	 * @param texture
	 * @param posX
	 * @param posY
	 * @param posZ
	 * @param rotX
	 * @param rotY
	 * @param rotZ
	 * @param scaleX
	 * @param scaleY
	 * @param scaleZ
	 */
	public Entity(Model model, float posX, float posY, float posZ, float rotX, float rotY, float rotZ, float scaleX, float scaleY, float scaleZ) {
		this.model = model;
		transformation = new WorldTransformation(posX, posY, posZ, rotX, rotY, rotZ, scaleX, scaleY, scaleZ);
	}

	/**
	 * Used to set the texture index if applicable
	 * 
	 * @param textureIndex
	 */
	public void setTextureIndex(int textureIndex) {
//		int column = textureIndex % model.getTexture().getTextureGridSize();
//		textureXOffset = column / (float) model.getTexture().getTextureGridSize();
//		int row = textureIndex / model.getTexture().getTextureGridSize();
//		textureYOffset = row / (float) model.getTexture().getTextureGridSize();
	}

	public void increasePosition(float dx, float dy, float dz) {
		transformation.increasePosition(dx, dy, dz);
	}

	public void increaseRotation(float dx, float dy, float dz) {
		transformation.increaseRotation(dx, dy, dz);
	}

	public float getScaleX() {
		return transformation.getScaleX();
	}

	public float getScaleY() {
		return transformation.getScaleY();
	}

	public float getScaleZ() {
		return transformation.getScaleZ();
	}

	public void setScaleX(float scaleX) {
		transformation.setScaleX(scaleX);
	}

	public void setScaleY(float scaleY) {
		transformation.setScaleY(scaleY);
	}

	public void setScaleZ(float scaleZ) {
		transformation.setScaleZ(scaleZ);
	}

	public float getTextureXOffset() {
		return textureXOffset;
	}

	public float getTextureYOffset() {
		return textureYOffset;
	}

	public Model getModel() {
		return model;
	}

	/**
	 * Activates all the textures.
	 */
	public void activateTextures() {
		model.getDiffuseTexture().activateTexture();
		model.getSpecularTexture().activateTexture();
	}

	public Matrix4f getModelMatrix() {
		return transformation.getMatrix();
	}

}
