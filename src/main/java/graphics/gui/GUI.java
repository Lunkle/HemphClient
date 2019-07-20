package graphics.gui;

import game.Visual;
import graphics.model.VAOBuilder;
import graphics.transformation.ModelTransformation;
import graphics.transformation.Transformation;

public class GUI {

	// The quad is static because it is going to be the same for every GUI
	private static final float[] QUAD_VERTICES = { 0, 0, 0, -1, 1, 0, 1, -1 };
	private static int guiVaoID;

	private ModelTransformation modelTransformation;

	static {
		guiVaoID = VAOBuilder.newInstance().addPositions(2, QUAD_VERTICES).create().getVaoID();
	}

	protected GUI() {
	}

	protected void setDimensions(float posX, float posY, float width, float height) {
		modelTransformation = new ModelTransformation(2.0f * posX / Visual.getWindowWidth() - 1.0f, 1.0f - 2.0f * posY / Visual.getWindowHeight(), 0, 0, 0, 0, 2.0f * width / Visual.getWindowWidth(), 2.0f * height / Visual.getWindowHeight(), 1);
	}

	public Transformation getModelTransformation() {
		return modelTransformation;
	}

	public static int getGuiVaoID() {
		return guiVaoID;
	}

}
