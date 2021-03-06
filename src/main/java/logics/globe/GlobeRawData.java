package logics.globe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import graphics.primitive.HalfEdge;
import graphics.primitive.Primitive;
import graphics.primitive.Triangle;
import graphics.primitive.Vertex;
import graphics.vao.VAO;
import loading.loader.InterpretedData;
import loading.loader.RawData;
import loading.vao.RawMeshData;
import math.Vector2f;
import math.Vector3f;

public class GlobeRawData extends RawMeshData implements RawData {

	private int subdivisions = 5;
	private int numberOfPlates = 50;
	private int numberOfWaterPlates = 30;

	private float perturbAmount = 0.002f;

	private float plateCollisionAmplitude = 0.1f;
	private int smoothingIterations = 2; // How many times to smooth out the
											// heights
	private float smoothingFactor = 0.6f; // How much to smooth each time

	private double landArchExponent = 0.2;
	private float landArchAmplitude = 0.4f;

	private List<TerrainTriangle> triangles;
	private List<Vertex> vertices;
	private List<Plate> plates;

	public GlobeRawData() {
		super();
		triangles = new ArrayList<>();
		vertices = new ArrayList<>();
	}

	@Override
	public void load(String filePath) {
		generateIcosahedron();
		subdivide(subdivisions);
		perturbVertices(perturbAmount);
		generatePlates();
		generateBasicTopography();
		generatePlateMovement();
		separateTriangles();
		translateToRawData();
	}

	public void generateIcosahedron() {
		Vector2f vector = new Vector2f(1, (float) (2 * Math.sin(Math.toRadians(54))));
		vector.normalise();
		float a = vector.x;
		float b = vector.y;
		// XY plane rectangle
		Vertex p1 = new Vertex(-a, b, 0);
		Vertex p2 = new Vertex(a, b, 0);
		Vertex p3 = new Vertex(-a, -b, 0);
		Vertex p4 = new Vertex(a, -b, 0);
		// YZ plane rectangle
		Vertex p5 = new Vertex(0, -a, b);
		Vertex p6 = new Vertex(0, a, b);
		Vertex p7 = new Vertex(0, -a, -b);
		Vertex p8 = new Vertex(0, a, -b);
		// XZ plane rectangle
		Vertex p9 = new Vertex(b, 0, -a);
		Vertex p10 = new Vertex(b, 0, a);
		Vertex p11 = new Vertex(-b, 0, -a);
		Vertex p12 = new Vertex(-b, 0, a);

		// Add them into the ArrayList;
		p1.setIndex(0);
		vertices.add(p1);
		p2.setIndex(1);
		vertices.add(p2);
		p3.setIndex(2);
		vertices.add(p3);
		p4.setIndex(3);
		vertices.add(p4);
		p5.setIndex(4);
		vertices.add(p5);
		p6.setIndex(5);
		vertices.add(p6);
		p7.setIndex(6);
		vertices.add(p7);
		p8.setIndex(7);
		vertices.add(p8);
		p9.setIndex(8);
		vertices.add(p9);
		p10.setIndex(9);
		vertices.add(p10);
		p11.setIndex(10);
		vertices.add(p11);
		p12.setIndex(11);
		vertices.add(p12);

		// Top five triangles around point 1
		triangles.add(new TerrainTriangle(p1, p11, p12));
		triangles.add(new TerrainTriangle(p1, p12, p6));
		triangles.add(new TerrainTriangle(p1, p6, p2));
		triangles.add(new TerrainTriangle(p1, p2, p8));
		triangles.add(new TerrainTriangle(p1, p8, p11));
		// Middle ten triangles forming a strip
		triangles.add(new TerrainTriangle(p12, p11, p3));
		triangles.add(new TerrainTriangle(p12, p3, p5));
		triangles.add(new TerrainTriangle(p6, p12, p5));
		triangles.add(new TerrainTriangle(p6, p5, p10));
		triangles.add(new TerrainTriangle(p2, p6, p10));
		triangles.add(new TerrainTriangle(p2, p10, p9));
		triangles.add(new TerrainTriangle(p8, p2, p9));
		triangles.add(new TerrainTriangle(p8, p9, p7));
		triangles.add(new TerrainTriangle(p11, p8, p7));
		triangles.add(new TerrainTriangle(p11, p7, p3));
		// Bottom five triangles around point 4
		triangles.add(new TerrainTriangle(p4, p9, p10));
		triangles.add(new TerrainTriangle(p4, p10, p5));
		triangles.add(new TerrainTriangle(p4, p5, p3));
		triangles.add(new TerrainTriangle(p4, p3, p7));
		triangles.add(new TerrainTriangle(p4, p7, p9));
	}

	private void subdivide(int iterations) {
		for (int i = 0; i < iterations; i++) {
			List<TerrainTriangle> oldTriangles = triangles;
			triangles = new ArrayList<>();
			int index = vertices.size();
			for (Triangle triangle : oldTriangles) {
				HalfEdge edge = triangle.getEdge();
				HalfEdge next = edge.getNext();
				HalfEdge previous = next.getNext();
				Vertex p1 = previous.getVertex();
				Vertex p2 = edge.getVertex();
				Vertex p3 = next.getVertex();
				Vertex p4 = Primitive.getMidpointOfHalfEdge(previous).normalizeLength();
				p4.setIndex(index++);
				vertices.add(p4);
				Vertex p5 = Primitive.getMidpointOfHalfEdge(edge).normalizeLength();
				p5.setIndex(index++);
				vertices.add(p5);
				Vertex p6 = Primitive.getMidpointOfHalfEdge(next).normalizeLength();
				p6.setIndex(index++);
				vertices.add(p6);
				triangles.add(new TerrainTriangle(p1, p5, p4));
				triangles.add(new TerrainTriangle(p5, p2, p6));
				triangles.add(new TerrainTriangle(p4, p5, p6));
				triangles.add(new TerrainTriangle(p4, p6, p3));
			}
		}
	}

	private void perturbVertices(float radius) {
		for (Vertex vertex : vertices) {
			Vector3f perturbVector = getRandomVector();
			perturbVector.scale(perturbAmount);
			vertex.setPosition(Vector3f.add(vertex.getPosition(), perturbVector));
			vertex.normalizeLength();
		}
	}

	private void generatePlates() {
		int numberOfTriangles = triangles.size();
		plates = new ArrayList<>();
		List<Integer> waterBiomes = Biomes.getWaterBiomeIndices();
		List<Integer> landBiomes = Biomes.getLandBiomeIndices();
		List<Boolean> plateIsWater = new ArrayList<>();
		for (int i = 0; i < numberOfWaterPlates; i++) {
			plateIsWater.add(true);
		}
		for (int i = 0; i < numberOfPlates - numberOfWaterPlates; i++) {
			plateIsWater.add(false);
		}
		for (int i = 0; i < numberOfPlates; i++) {
			// This is a problem because the same ID might get chosen for more
			// than one
			// plate.
			// TODO
			int triangleID = (int) getRandom(0, numberOfTriangles);
			TerrainTriangle triangle = triangles.get(triangleID);
			int isWaterIndex = (int) getRandom(0, plateIsWater.size());
			boolean isWater = plateIsWater.get(isWaterIndex);
			plateIsWater.remove(isWaterIndex);
			List<Integer> biomeMap = isWater ? waterBiomes : landBiomes;
			int biomeIndex = biomeMap.get((int) getRandom(0, biomeMap.size()));
			plates.add(new Plate(Biomes.getBiome(biomeIndex), triangle));
		}
		int numberOfSetTriangles = numberOfPlates;
		while (numberOfSetTriangles < numberOfTriangles) {
			Plate plate = plates.get((int) getRandom(0, numberOfPlates));
			List<TerrainTriangle> triangles = plate.getAdjacentTriangles();
			boolean success = plate.addTriangle(triangles.get((int) getRandom(0, triangles.size())));
			if (success) {
				numberOfSetTriangles++;
			}
		}
	}

	private void generateBasicTopography() {
		for (Plate plate : plates) {
			if (!plate.getBiome().isWater()) {
				// Calculating nearest boundary for each triangle.
				int numberOfTriangles = plate.getTriangles().size();
				int numberOfSetTriangles = 0;
				List<HalfEdge> currentBoundaries = plate.getBoundaries();
				boolean firstRun = true;
				while (numberOfSetTriangles < numberOfTriangles) {
					List<HalfEdge> newBoundaries = new ArrayList<>();
					for (HalfEdge boundary : currentBoundaries) {
						TerrainTriangle terrainTriangle = (TerrainTriangle) boundary.getTriangle();
						if (terrainTriangle.getClosestBoundary() == null) {
							terrainTriangle.setClosestBoundary(firstRun ? boundary : ((TerrainTriangle) boundary.getPair().getTriangle()).getClosestBoundary());
							for (HalfEdge triangleEdge : Primitive.getEdges(terrainTriangle)) {
								if (!currentBoundaries.contains(triangleEdge)) {
									newBoundaries.add(triangleEdge.getPair());
								}
							}
							numberOfSetTriangles++;
						}
					}
					currentBoundaries = newBoundaries;
					firstRun = false;
				}
				// Increasing the heights.
				Map<TerrainTriangle, Float> increaseAmounts = new HashMap<>();
				for (TerrainTriangle triangle : plate.getTriangles()) {
					Vector3f boundaryMidPoint = triangle.getClosestBoundary().getTriangle().getCentroid();
					Vector3f triangleMidPoint = triangle.getCentroid();
					float distanceSquared = Vector3f.sub(boundaryMidPoint, triangleMidPoint).lengthSquared();
					float increaseFactor = (float) (Math.pow(distanceSquared, landArchExponent) * 0.001f * landArchAmplitude);
					increaseAmounts.put(triangle, increaseFactor);
				}
				for (TerrainTriangle triangle : plate.getTriangles()) {
					HalfEdge edge = triangle.getEdge();
					for (int i = 0; i < 3; i++) {
						edge.getVertex().setPosition(edge.getVertex().getPosition().scale(edge.getVertex().getPosition().length() + increaseAmounts.get(triangle)));
					}
				}
			}
		}
	}

	private void generatePlateMovement() {
		// Generating movement vectors for each triangle for each plate
		for (Plate plate : plates) {
			Vector3f rotationAxis = getRandomVector();
			float factor = (float) getRandom(0, plateCollisionAmplitude);
			for (TerrainTriangle triangle : plate.getTriangles()) {
				triangle.setMovementVector(Vector3f.cross(rotationAxis, triangle.getCentroid()).scale(factor));
			}
		}
		// Raising or lowering the boundaries based on the plate movement
		for (Plate plate : plates) {
			for (HalfEdge boundary : plate.getBoundaries()) {
				if (!Plate.getPlateOfBoundary(boundary.getPair()).getBiome().isWater() && !plate.getBiome().isWater()) {
					Vector3f movement1 = ((TerrainTriangle) boundary.getTriangle()).getMovementVector();
					Vector3f movement2 = ((TerrainTriangle) boundary.getPair().getTriangle()).getMovementVector();
					Vertex vertex1 = boundary.getVertex();
					Vertex vertex2 = boundary.getPair().getVertex();
					Vector3f triangle1ToTriangle2 = Vector3f.sub(vertex2.getPosition(), vertex1.getPosition());
					Vector3f triangle2ToTriangle1 = Vector3f.sub(vertex1.getPosition(), vertex2.getPosition());
					float dot1 = Vector3f.dot(triangle1ToTriangle2, movement1);
					float dot2 = Vector3f.dot(triangle2ToTriangle1, movement2);
					float sum = 10 * (dot1 + dot2);
					vertex1.setPosition(vertex1.getPosition().scale(1 + sum));
					vertex2.setPosition(vertex2.getPosition().scale(1 + sum));
				}
			}
		}
		// Smoothing out the heights
		for (int i = 0; i < smoothingIterations; i++) {
			for (Vertex vertex : vertices) {
				float totalHeight = 0;
				for (HalfEdge edge : vertex.getEmanatingEdges()) {
					totalHeight += edge.getVertex().getPosition().length();
				}
				int numberOfAdjacentVertices = vertex.getEmanatingEdges().size();
				float targetHeight = totalHeight / numberOfAdjacentVertices;
				float currentHeight = vertex.getPosition().length();
				float newHeight = smoothingFactor * (targetHeight - currentHeight) + currentHeight;
				vertex.setPosition(vertex.getPosition().scale(newHeight / currentHeight));
			}
		}
	}

	public void raiseBoundaries() {
		for (Plate plate : plates) {
			for (HalfEdge boundary : plate.getBoundaries()) {
				Vertex vertex1 = boundary.getVertex();
				Vertex vertex2 = boundary.getPair().getVertex();
				vertex1.setPosition(vertex1.getPosition().scale(1.01f));
				vertex2.setPosition(vertex2.getPosition().scale(1.01f));
			}
		}
	}

	private void separateTriangles() {
		vertices = new ArrayList<>();
		int index = 0;
		for (Triangle triangle : triangles) {
			HalfEdge edge = triangle.getEdge();
			for (int i = 0; i < 3; i++) {
				Vertex originalVertex = edge.getVertex();
				Vector3f position = originalVertex.getPosition();
				Vertex copyVertex = new Vertex(position.x, position.y, position.z);
				copyVertex.setIndex(index++);
				vertices.add(copyVertex);
				copyVertex.setEmanatingEdges(originalVertex.getEmanatingEdges());
				edge.setVertex(copyVertex);
				edge = edge.getNext();
			}
		}
	}

	private void translateToRawData() {
		for (Vertex vertex : vertices) {
			Vector3f pointPosition = vertex.getPosition();
			positions.add(pointPosition.x);
			positions.add(pointPosition.y);
			positions.add(pointPosition.z);
			Vector3f pointNormal = vertex.calculateNormal();
			normals.add(pointNormal.x);
			normals.add(pointNormal.y);
			normals.add(pointNormal.z);
		}
		for (TerrainTriangle triangle : triangles) {
			HalfEdge edge = triangle.getEdge();
			for (int i = 0; i < 3; i++) {
				indices.add(edge.getVertex().getIndex());
				edge = edge.getNext();
			}
			for (Vector2f textureCoordinate : triangle.getTextureCoordinates()) {
				textureCoordinates.add(textureCoordinate.x);
				textureCoordinates.add(textureCoordinate.y);
			}
		}
	}

	private double getRandom(double min, double max) {
		return (Math.random() * (max - min)) + min;
	}

	private Vector3f getRandomVector() {
		double theta = Math.toRadians(getRandom(-180, 180));
		double phi = Math.acos(2 * getRandom(0, 1) - 1);
		double sinPhi = Math.sin(phi);
		float x = (float) (Math.cos(theta) * sinPhi);
		float y = (float) (Math.sin(theta) * sinPhi);
		float z = (float) (Math.cos(phi));
		return new Vector3f(x, y, z);
	}

	@Override
	public void accept(InterpretedData data) {
		data.interpret(this);
	}

	@Override
	public InterpretedData newInterpretedData() {
		return new VAO();
	}

	@Override
	public long getSize(String filePath) {
		long size = (long) (20 * Math.pow(4, subdivisions) * numberOfPlates);
		return size;
	}

}
