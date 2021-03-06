package graphics.transformation;

import math.Vector3f;

public class UnitQuaternion extends Quaternion {

	public UnitQuaternion() {
		reset();
	}

	/**
	 * Creates a unit quaternion with axis and angle as specified.
	 * 
	 */
	public UnitQuaternion(Vector3f axis, float theta) {
		setAngleAxis(axis, theta);
	}

	/**
	 * Sets this quaternion by an angle of rotation about an axis.
	 * 
	 * @param theta
	 * @param axisX
	 * @param axisY
	 * @param axisZ
	 * @return this quaternion
	 */
	public UnitQuaternion setAngleAxis(float axisX, float axisY, float axisZ, float theta) {
		double angle = Math.toRadians(theta);
		this.w = (float) Math.cos(angle);
		Vector3f axis = new Vector3f(axisX, axisY, axisZ);
		axis.normalise();
		axis.scale((float) Math.sin(angle));
		this.x = axis.x;
		this.y = axis.y;
		this.z = axis.z;
		normalize();
		return this;
	}

	public UnitQuaternion(float w, float x, float y, float z) {
		setComponents(w, x, y, z);
	}

	public UnitQuaternion(Quaternion q) {
		setComponents(q.w, q.x, q.y, q.z);
	}

	public UnitQuaternion(UnitQuaternion q) {
		this.w = q.w;
		this.x = q.x;
		this.y = q.y;
		this.z = q.z;
	}

	/**
	 * Sets the unit quaternion through individual components.
	 * 
	 * @param w
	 * @param x
	 * @param y
	 * @param z
	 * @return this quaternion
	 */
	@Override
	public UnitQuaternion setComponents(float w, float x, float y, float z) {
		this.w = w;
		this.x = x;
		this.y = y;
		this.z = z;
		normalize();
		return this;
	}

	public Vector3f getAxis() {
		Vector3f axis = new Vector3f(x, y, z);
		if (axis.lengthSquared() == 0) {
			axis.set(0, 1, 0);
		}
		axis.normalise();
		return axis;
	}

	public float getAngle() {
		return (float) Math.toDegrees(Math.acos(w));
	}

	/**
	 * Sets this quaternion by an angle of rotation about an axis.
	 * 
	 * @param theta
	 * @param axisX
	 * @param axisY
	 * @param axisZ
	 * @return this quaternion
	 */
	public UnitQuaternion setAngleAxis(Vector3f axisOfRotation, float theta) {
		return setAngleAxis(axisOfRotation.x, axisOfRotation.y, axisOfRotation.z, theta);
	}

	@Override
	public UnitQuaternion getConjugate() {
		return new UnitQuaternion(w, -x, -y, -z);
	}

	@Override
	public UnitQuaternion getInverse() {
		return getConjugate();
	}

	@Override
	public UnitQuaternion scale(float scale) {
		w *= scale;
		x *= scale;
		y *= scale;
		z *= scale;
		return this;
	}

	/**
	 * 
	 * Multiplies the corresponding scalar parts and sums the results.
	 * 
	 * @param quaternion
	 * @return the dot product
	 */
	public float dot(UnitQuaternion q) {
		float sum = w * q.w + x * q.x + y * q.y + z * q.z;
		return sum;
	}

	/**
	 * Computes the angular difference between the quaternions.
	 * 
	 * @param quaternion
	 * @return the angle difference
	 */
	public float angleBetween(UnitQuaternion q) {
		float cosTheta = this.dot(q) / (magnitude() * q.magnitude());
		float theta = (float) Math.acos(cosTheta);
		return theta;
	}

	/**
	 * Multiplies this quaternion by the parameter quaternion. What this effectively
	 * does is transforms the current quaternion by the given quaternion. The given
	 * quaternion is treated as a rotation.
	 * 
	 * @param q the rotation quaternion
	 * @return the resultant quaternion
	 */
	public UnitQuaternion multiply(UnitQuaternion q) {
		float s = w * q.w - Vector3f.dot(getV(), q.getV());
		Vector3f saB = q.getV().scale(w);
		Vector3f sbA = getV().scale(q.w);
		Vector3f cross = Vector3f.cross(getV(), q.getV());
		Vector3f v = Vector3f.add(Vector3f.add(saB, sbA), cross);
		return new UnitQuaternion(s, v.x, v.y, v.z);
	}

	public void applyRotation(UnitQuaternion rotation) {
		UnitQuaternion result = this.multiply(rotation);
		this.setComponents(result.w, result.x, result.y, result.z);
	}

	public void applyRotation(Vector3f axisOfRotation, float angle) {
		applyRotation(new UnitQuaternion(axisOfRotation, angle / 2));
	}

	public Vector3f rotateVector3f(Vector3f vector) {
		UnitQuaternion conjugate = this.getConjugate();
		Quaternion pureQuaternion = new Quaternion(0, vector.x, vector.y, vector.z);
		Quaternion resultantQuaternion = this.multiply(pureQuaternion).multiply(conjugate);
		Vector3f result = resultantQuaternion.getV();
		return result;
	}

	@Override
	public String toString() {
		return "UnitQuaternion: [" + w + ", " + x + ", " + y + ", " + z + "]";
	}
}
