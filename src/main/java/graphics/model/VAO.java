package graphics.model;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

// An attribute object stores all the information for each vertex of a mesh
// It stores them in the form of buffer objects
public class VAO {

	private static List<Integer> vaos = new ArrayList<>();

	protected int vaoID;
	protected int vertexCount;

	// Creates a new attribute object
	protected VAO() {
		vaoID = GL30.glGenVertexArrays();
		vaos.add(vaoID);
		bindVAO();
	}

	// Must bind and unbind VAO before attaching VBO/EBOs
	// The VAO is already deafult bound upon creation so do not create any other
	// VAOs until finished attaching VBO and EBOs for the most recently created vao
	protected void attachEBO(int[] data) {
		EBO ebo = new EBO();
		ebo.bindEBO();
		ebo.loadData(data);
		ebo.unbindEBO();
		vertexCount = data.length;
	}

	protected void attachVBO(int attributeIndex, int dimensions, float[] data) {
		VBO vbo = new VBO();
		vbo.bindVBO();
		vbo.loadData(data);
		GL20.glVertexAttribPointer(attributeIndex, dimensions, GL11.GL_FLOAT, false, 0, 0);
		vbo.unbindVBO();
		if (vertexCount == 0)
			vertexCount = data.length;
	}

	// Binding the attribute object so opengl knows to write to it
	protected void bindVAO() {
		GL30.glBindVertexArray(vaoID);
	}

	// Remeber to unbind after finished attaching VBOS
	protected void unbindVAO() {
		GL30.glBindVertexArray(0);
	}

	public int getVaoID() {
		return vaoID;
	}

	public int getVertexCount() {
		return vertexCount;
	}

	// Method for deleting all the attribute objects when program is closing
	// Called in Visual class's cleanUp method
	public static void cleanUp() {
		for (int vao : vaos) {
			GL30.glDeleteVertexArrays(vao);
		}
	}

}
