package toxi.physics2d;

/**
 * Implements a string which will only enforce its rest length if the current
 * distance is less than its rest length. This is handy if you just want to
 * ensure objects are at least a certain distance from each other, but don't
 * care if it's bigger than the enforced minimum.
 * 
 * @author toxi
 */
public class VerletMinDistanceSpring extends VerletSpring2D {

	public VerletMinDistanceSpring(VerletParticle2D a, VerletParticle2D b,
			float len, float str) {
		super(a, b, len, str);
	}

	public void update() {
		if (b.sub(a).magSquared() < restLength * restLength)
			super.update();
	}
}
