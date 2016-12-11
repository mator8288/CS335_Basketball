import com.jogamp.opengl.*;
import com.jogamp.opengl.util.gl2.GLUT;

public class Ball {
	GLUT glut = new GLUT();
	private Vector3f ballPos;
	private boolean thrown = false;
	private int throw_int = 0;
	private boolean scored = false;
	private float angle;
	private float strength;
	private boolean angleChange = false;
	private boolean strengthChange = false;
	private boolean show_trajectory = true;
	private BallTrack ballPath = null;
	private int phase = 0;
	private final int X = 0;
	private final int Y = 1;
	private final int Z = 2;
	
	public Ball(Vector3f pos) {
		ballPos = pos;
	}
	
	public void reset() {
		thrown = false;
	}
	
	public Vector3f getPos() {
		return ballPos;
	}
	
	public void setPos(Vector3f v) {
		ballPos = v;
	}
	
	public void drawBall(Vector3f pos, Vector3f look, GL2 gl) {
		if (thrown) {
			
			if (throw_int <= 1) {
				thrown = false;
			}
			if (show_trajectory) {
				ballPath.showPath(gl);
			}
			
		} else {
			float changeX = look.getValue(X) - pos.getValue(X);
			float changeY = look.getValue(Y) - pos.getValue(Y);
			float dist = (float) Math.sqrt(changeX * changeX + changeY * changeY);
			float newX = pos.getValue(X) + (2 * changeX) / dist;
			float newY = pos.getValue(Y) + (2 * changeY) / dist;
			ballPos.setParameter(X, newX);
			ballPos.setParameter(Y, newY);
			ballPos.setParameter(Z, 0);
		}
		gl.glPushMatrix();
		gl.glColor3f(0.98f, 0.51f, 0.12f);
		gl.glTranslatef(pos.getValue(X), pos.getValue(Y), pos.getValue(Z));
		gl.glDisable(GL.GL_TEXTURE_2D);
		glut.glutSolidSphere(0.4, 50, 50);
		gl.glEnable(GL.GL_TEXTURE_2D);
		
		if (phase != 0) {
			if (phase == 1) {
				//if (angleChange) {
				//	angle += 0.05;
				//}
				//if (angle >= 80) {
				//	angleChange = false;
				//}
			}
			else {
				if (strengthChange) {
					strength += 0.5;
				} else {
					strength -= 0.5;
				}
				
				if (strength >= 80) {
					strengthChange = false;
				}
				else if (strength <= 1) {
					strengthChange = true;
				}
			}
			
			
			float xDisplay = (pos.getValue(X) - look.getValue(X));
			float yDisplay = (pos.getValue(Y) - look.getValue(Y));
			float distance = (float) Math.sqrt(xDisplay * xDisplay + yDisplay * yDisplay);
			float sine = (float) Math.sin(xDisplay / distance);
			float angleSin = (float) Math.asin(sine) * (180 / (float) Math.PI);
			//TODO: display info somehow
			gl.glRotatef(angleSin, 0, 0, 1);
			gl.glRotatef(180 + angleSin, 1, 0, 0);
			//gl.glColor3f(0.0f, 0.0f, 1.0f);
			//glut.glutSolidCone(0.25, strength / 10, 10, 30);
			
		}
		gl.glPopMatrix();
		
		/*gl.glDisable(GL.GL_CULL_FACE);
		gl.glPushMatrix();
		gl.glTranslatef(ballPos.getValue(X), ballPos.getValue(Y), -1.9f);
		gl.glRotatef(90, 0, 0, 1);
		gl.glRotatef(90, 1, 0, 0);
		gl.glColor3f(0.0f, 0.0f, 0.0f);
		glut.glutSolidCone(0.3, 0.1, 10, 10);
		gl.glPopMatrix();
		gl.glEnable(GL.GL_CULL_FACE);
		*/
	}
	
	public void throwBall(Vector3f pos, float str, float ang, Vector3f cen) {
		ballPath = null;
		ballPath = new BallTrack(pos, str, ang, cen);
		thrown = true;
		throw_int = ballPath.getSteps();
		if (ballPath.scored()) {
			scored = true;
		}
	}
	
	public void replay() {
		if (ballPath != null) {
			throw_int = ballPath.getSteps();
			thrown = true;
		}
	}
	
	public boolean is_thrown() {
		return thrown;
	}
	
	public int getPhase() {
		return phase;
	}
	
	public void setPhase(int phaseno) {
		phase = phaseno;
		if (phaseno == 0) {
			strength = 30;
			angle = 40;
		}
	}
	
	public void setThrow(int progress) {
		if (!((throw_int - progress) > ballPath.getSteps())) {
			throw_int -= progress;
		}
		
		if (throw_int > 1) {
			setPos(ballPath.getTrackPos((ballPath.getSteps()) - throw_int));
		}
		else {
			throw_int = 1;
		}
	}
	
	public int getThrowInt() {
		return throw_int;
	}
	
	public boolean getScored() {
		boolean temp = scored;
		scored = false;
		return temp;
	}
	
	public float getAngle() {
		return angle;
	}
	
	public float getStrength() {
		return strength;
	}
	
	public void showPath() {
		show_trajectory = !show_trajectory;
	}
}