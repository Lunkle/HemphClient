package logics.globe;

import graphics.entity.Entity;
import graphics.rendering.Camera;
import graphics.transformation.Quaternion;
import input.command.Command;
import input.information.Actions;
import input.information.Keys;
import input.mouse.MousePicker;
import input.observer.MouseButtonObserver;
import input.observer.MouseCheck;
import input.observer.MouseMovementObserver;
import math.Vector3f;

public class Globe {

	private Entity globeEntity;
	private Quaternion rotation;
	private boolean selected;
	private Vector3f currentIntersection = null;
	private Vector3f previousIntersection = null;

	public Globe() {
		rotation = new Quaternion();
	}

	public void setGlobeEntity(Entity globeEntity) {
		this.globeEntity = globeEntity;
	}

	public Entity getGlobeEntity() {
		return globeEntity;
	}

	public boolean isSelected() {
		return selected;
	}

	public void selectGlobe() {
		selected = true;
	}

	public void deselectGlobe() {
		selected = false;
	}

	private Vector3f previousOrigin = null;
	private Vector3f previousDirection = null;

	public Vector3f getIntersection(Vector3f origin, Vector3f direction) {
		if (origin.equals(previousOrigin) && direction.equals(previousDirection)) {
			return previousIntersection;
		}
		previousOrigin = origin;
		previousDirection = new Vector3f(direction);
		Vector3f globePosition = globeEntity.getWorldTransformation().getPosition();
		float globeSize = globeEntity.getScaleX();
		Vector3f globeToCamera = Vector3f.sub(origin, globePosition);
		float a = Vector3f.dot(direction, direction);
		float b = 2 * Vector3f.dot(direction, globeToCamera);
		float c = Vector3f.dot(globeToCamera, globeToCamera) - globeSize * globeSize;
		float discriminant = b * b - 4 * a * c;
		if (discriminant >= 0) {
			float sqrtDiscriminant = (float) Math.sqrt(discriminant);
			float t1 = (-b + sqrtDiscriminant) / (2 * a);
			float t2 = (-b - sqrtDiscriminant) / (2 * a);
			if (t1 >= 0 || t2 >= 0) {
				float target = t1 < 0 ? t2 : (t2 < 0 ? t1 : Math.min(t1, t2));
				previousIntersection = currentIntersection;
				currentIntersection = Vector3f.add(origin, direction.scale(target));
				return currentIntersection;
			}
		}
		return null;
	}

	public MouseButtonObserver getGlobeSelectionObserver(Camera camera, MousePicker mousePicker) {
		MouseButtonObserver globeSelectionObserver = new MouseButtonObserver();
		Command selectGlobe = new Command(() -> selectGlobe());
		MouseCheck globeIntersectionCheck = () -> {
			Vector3f intersection = getIntersection(camera.getPosition(), mousePicker.calculateMouseRay());
			return intersection != null;
		};
		globeSelectionObserver.addCheck(Keys.MOUSE_LEFT, Actions.PRESS, globeIntersectionCheck, selectGlobe);
		return globeSelectionObserver;
	}

	public MouseButtonObserver getGlobeDeselectionObserver() {
		MouseButtonObserver globeDeselectionObserver = new MouseButtonObserver();
		Command deselectGlobe = new Command(() -> deselectGlobe());
		MouseCheck releaseCheck = () -> {
			return true;
		};
		globeDeselectionObserver.addCheck(Keys.MOUSE_LEFT, Actions.RELEASE, releaseCheck, deselectGlobe);
		return globeDeselectionObserver;
	}

	public MouseMovementObserver getGlobeRotationObserver(Camera camera, MousePicker mousePicker) {
		MouseMovementObserver globeRotationObserver = new MouseMovementObserver();
		Command rotateGlobe = new Command(() -> rotateGlobe(camera.getPosition(), mousePicker.calculateMouseRay()));
		MouseCheck selectedCheck = () -> {
			return selected;
		};
		globeRotationObserver.addCheck(selectedCheck, rotateGlobe);
		return globeRotationObserver;
	}

	private void rotateGlobe(Vector3f origin, Vector3f direction) {
		getIntersection(origin, direction);
		if (isSelected() && previousIntersection != null) {
			Quaternion previousQuaternion = new Quaternion(0, previousIntersection.x, previousIntersection.y, previousIntersection.z);
			Quaternion currentQuaternion = new Quaternion(0, currentIntersection.x, currentIntersection.y, currentIntersection.z);
			Quaternion rotation = currentQuaternion.multiply(previousQuaternion.inverse());
			globeEntity.increaseRotation(new Vector3f(0, 1, 0), 0.5f);
		}
	}

	public void update() {

//		System.out.println(previousIntersection);
//		globeEntity.setRotation(rotX, rotY, rotZ);
	}

}
