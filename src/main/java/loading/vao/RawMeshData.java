package loading.vao;

import java.util.ArrayList;
import java.util.List;

import loading.loader.InterpretedData;
import loading.loader.RawData;

public abstract class RawMeshData implements RawData {

	protected List<Float> positions = new ArrayList<>();
	protected List<Float> normals = new ArrayList<>();
	protected List<Float> textureCoordinates = new ArrayList<>();
	protected List<Integer> indices = new ArrayList<>();

	public RawMeshData() {
		positions = new ArrayList<>();
		normals = new ArrayList<>();
		textureCoordinates = new ArrayList<>();
		indices = new ArrayList<>();
	}

	public float[] getPositionsArray() {
		return toFloatArray(positions);
	}

	public float[] getNormalsArray() {
		return toFloatArray(normals);
	}

	public float[] getTextureArray() {
		return toFloatArray(textureCoordinates);
	}

	public int[] getIndicesArray() {
		return toIntArray(indices);
	}

	@Override
	public void accept(InterpretedData data) {
		data.interpret(this);
	}

	private float[] toFloatArray(List<Float> floatList) {
		int arraySize = floatList.size();
		float[] floatArray = new float[arraySize];
		for (int i = 0; i < arraySize; i++) {
			floatArray[i] = floatList.get(i);
		}
		return floatArray;
	}

	private int[] toIntArray(List<Integer> floatList) {
		int arraySize = floatList.size();
		int[] floatArray = new int[arraySize];
		for (int i = 0; i < arraySize; i++) {
			floatArray[i] = floatList.get(i);
		}
		return floatArray;
	}

}
