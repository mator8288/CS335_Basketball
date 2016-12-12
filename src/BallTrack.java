import com.jogamp.opengl.*;
import com.jogamp.opengl.util.gl2.GLUT;
import java.util.*;

public class BallTrack{
	GLUT glut = new GLUT();
	final int X = 0;
	final int Y = 1;
	final int Z = 2;
	private boolean scored = false;
	private int scoredLoc = -1;
	private boolean stop_bounce;
	private int collidePoint = 0;
	float step;
	float xChange, yChange;
	float f;
	float xStep, yStep, zStep;
	Vector3f curPos, accel, speed;
	private float diameter = 0.4f;
	private Vector route = new Vector();
	Vector info = new Vector();
	String infostr = "";
	
	
	public BallTrack(Vector3f curLoc, float strength, float angle, float z_angle, Vector3f cenLoc) {
		step = strength / 900;
		stop_bounce = false;
		xChange = cenLoc.getValue(X) - curLoc.getValue(X);
		yChange = cenLoc.getValue(Y) - curLoc.getValue(Y);
		f = (float) Math.sqrt(xChange * xChange + yChange * yChange);
		xStep = (step * xChange / f) * (float) Math.sin(angle);
		yStep = - (step * yChange / f) * (float) Math.cos(angle);
		zStep = step / (float) Math.cos(z_angle);
		curPos = new Vector3f(curLoc);
		accel = new Vector3f(0.0f, 0.0f, -0.002f);
		speed = new Vector3f(xStep, yStep, zStep);
		
		while (route.size() < 350) {
			route.add(new Vector3f(curPos));
			infostr = "";
			speed.add(accel);
			curPos.add(speed);
			
			// collision detection
			// backboard collision
			if ((curPos.getValue(Y) <= -18.3 && curPos.getValue(Y) >= -18.7) &&
					(curPos.getValue(X) >= -3.2 && curPos.getValue(X) <= 3.2) &&
					(curPos.getValue(Z) >= 6.3 && curPos.getValue(Z) <= 9.7)) {
				speed.changeDirection(Y);
				if (curPos.getValue(Y) > -18.3)
					curPos.setParameter(Y, -18.7f);
				else 
					curPos.setParameter(Y, -18.3f);
				
				speed.setParameter(Y, 0.9f * speed.getValue(Y));
				infostr = "Backboard hit!";
			}
			// pole collision
			//else if () {}
			// side wall collision
			
			else if (curPos.getValue(X) < -9.8) {
				infostr = "side wall collision";
				curPos.setParameter(X, -9.75f);
				speed.changeDirection(X);
				speed.setParameter(X, 0.9f * speed.getValue(X));
			}
			else if (curPos.getValue(X) > 9.8) {
				infostr = "side wall collision";
				curPos.setParameter(X, 9.75f);
				speed.changeDirection(X);
				speed.setParameter(X, 0.9f * speed.getValue(X));
			}
			// front wall collision
			else if (curPos.getValue(Y) < -19.8) {
				infostr = "Front wall collision";
				curPos.setParameter(Y, -19.75f);
				speed.changeDirection(Y);
				speed.setParameter(Y, 0.9f * speed.getValue(Y));
			}
			else if (curPos.getValue(Y) > 19.8) {
				infostr = "Back wall collision";
				curPos.setParameter(Y, 19.75f);
				speed.changeDirection(Y);
				speed.setParameter(Y,  0.9f * speed.getValue(Y));
			}
			// floor collision
			else if (curPos.getValue(Z) < 0.2 && !stop_bounce) {
				infostr = "floor collision";
				curPos.setParameter(Z, 0.25f);
				speed.changeDirection(Z);
				speed.setParameter(Z, 0.8f * speed.getValue(Z));
				
				//if speed is too low, stop bouncing
				if (speed.getValue(Z) <= 0.05f) {
					stop_bounce = true;
					speed.setParameter(Z, 0.0f);
					accel.setParameter(Z, 0.0f);
					curPos.setParameter(Z, 0.2f);
				}
			}
			
			
			if (collidePoint == 0) {
				// collides with rim
			} else {
				collidePoint--;
			}
			// score test
			if (curPos.getValue(Z) < 7.2 && curPos.getValue(Z) > 6.8 &&
					curPos.getValue(X) > -0.185 && curPos.getValue(X) < 0.185 &&
					curPos.getValue(Y) > -17.685 && curPos.getValue(Y) < -17.415) {
				infostr = "Score!";
				System.out.println("Score!");
				scored = true;
				if (scoredLoc < 0) {
					scoredLoc = route.size();
				}
			}
			if (infostr == "" && info.size() != 0) {
				infostr = (String) info.get(info.size() - 1);
				info.add(infostr);
			}
		}
	}
	
	public Vector3f getTrackPos(int pos) {
		return (Vector3f) route.get(pos);
	}
	
	public int getSteps() {
		return route.size();
	}
	
	public boolean scored() {
		return scored;
	}
	
	public String getInfo(int pos) {
		return (String) info.get(pos);
	}
	
	public Vector getRoute() {
		return route;
	}
	
	public void showPath(GL2 gl) {
		for (int i = 0; i < getSteps(); i++) {
			gl.glPushMatrix();
			gl.glTranslatef(((Vector3f) route.get(i)).getValue(X), ((Vector3f) route.get(i)).getValue(Y), ((Vector3f) route.get(i)).getValue(Z));
			gl.glColor3f(1.0f, 1.0f, 1.0f);
			glut.glutWireSphere(0.02, 10, 10);
			gl.glPopMatrix();
		}
	}
}