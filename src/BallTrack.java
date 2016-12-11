import com.jogamp.opengl.*;
import java.util.*;

public class BallTrack{
	final int X = 0;
	final int Y = 1;
	final int Z = 2;
	private boolean scored = false;
	private int scoredLoc = -1;
	private int collidePoint = 0;
	float step;
	float xChange, yChange;
	float f;
	float xStep, yStep, zStep;
	Vector3f curPos, accel, speed;
	private float diameter = 0.4f;
	private Vector route;
	
	
	public BallTrack(Vector3f curLoc, float strength, float angle, Vector3f cenLoc, float level) {
		step = strength / 900;
		xChange = cenLoc.getValue(X) - curLoc.getValue(X);
		yChange = cenLoc.getValue(Y) - curLoc.getValue(Y);
		f = (float) Math.sqrt(xChange * xChange + yChange * yChange);
		xStep = (step * xChange / f) * (float) Math.cos(angle * Math.PI / 180);
		yStep = (step * yChange / f) * (float) Math.cos(angle * Math.PI / 180);
		zStep = step / (float) Math.cos(angle * Math.PI / 180);
		curPos = new Vector3f(curLoc);
		accel = new Vector3f(0.0f, -0.002f, 0.0f);
		speed = new Vector3f(xStep, yStep, zStep);
		
		while (route.size() < 350) {
			route.add(new Vector3f(curPos));
			String info;
			info = "";
			speed.add(accel);
			curPos.add(speed);
			if (curPos.getValue(Y) < 1.8 && curPos.getValue(Y) > 1.3)
		}
	}
}