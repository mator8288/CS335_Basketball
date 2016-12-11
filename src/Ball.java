import com.jogamp.opengl.*;
import com.jogamp.opengl.util.gl2.GLUT;

public class Ball {
	GLUT glut = new GLUT();
	private Vector ballPos;
	private boolean thrown = false;
	private int throw_int = 0;
	private boolean scored = false;
	private float strength;
	private float angle;
	private float level;
	private boolean show_trajectory = false;
	private ballPath trajectory = null;
	private int phase;
	private final int X = 0;
	private final int Y = 1;
	private final int Z = 2;
	
	public Ball(Vector pos, float lev) {
		ballPos = pos;
		level = lev;
	}
	
	public void reset() {
		thrown = false;
	}
	
	public Vector getPos() {
		return ballPos;
	}
	
	public void setPos(Vector v) {
		ballPos = v;
	}
	
	public void drawBall(Vector pos, Vector look, GL2 gl) {
		if (thrown) {
			
			if (throw_int <= 1) {
				thrown = false;
			}
			if (show_trajectory) {
				ballPath.showPath();
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
		gl.glTranslatef(ballPos.getValue(X), ballPos.getValue(Y), ballPos.getValue(Z));
		gl.glDisable(GL.GL_TEXTURE_2D);
		glut.glutSolidSphere(0.4, 50, 50);
		gl.glEnable(GL.GL_TEXTURE_2D);
		
		if ()
	}
}