import com.jogamp.opengl.*;
import com.jogamp.opengl.util.gl2.GLUT;

public class Player{
	GLUT glut = new GLUT();
	Vector3f centerVec = new Vector3f(8.0f, 0.0f, 2.0f);
	Vector3f eyeVec = new Vector3f(0.0f, 0.0f, 10.0f);
	Vector3f ballPos = new Vector3f(0.0f, -9.0f, 2.0f);
	Vector3f lastPos = new Vector3f(0.0f, -9.0f, 2.0f);
	boolean last_throw_score = false;
	Ball ball = new Ball(ballPos);
	int score = 0;
	int nThrows = 0;
	int mode = 0;
	private final int REGULARTHROW = 1;
	//private final int 
	
	public Player() {}
	
	public Vector3f lookatCenter() {
		return centerVec;
	}
	
	public Vector3f lookatEye() {
		return eyeVec;
	}
	
	public void drawPlayer(GL2 gl) {
		gl.glPushMatrix();
		gl.glColor3f(1.0f, 0.0f, 0.0f);
		gl.glTranslatef(lastPos.getValue(0), lastPos.getValue(1), lastPos.getValue(2));
		glut.glutSolidSphere(0.2, 40, 40);
		gl.glPopMatrix();
		ball.drawBall(lookatEye(), lookatCenter(), gl);
		if (mode == REGULARTHROW && ball.is_thrown()) {
			ball.setThrow(1);
		}
		if (mode == REGULARTHROW && ball.is_thrown() && ball.getScored()) {
			incScore();
		}
	}
	
	public void throwBall() {
		nThrows++;
		lastPos.setVector(ball.getPos());
		ball.throwBall(lookatCenter());
		ball.setPhase(0);
	}
	
	public void replayBall() {
		ball.replay();
	}
	
	public void resetBall(GL2 gl) {
		ball.reset();
		ball.drawBall(lookatEye(), lookatCenter(), gl);
	}
	
	public void setPhase(int phaseno) {
		ball.setPhase(phaseno);
	}
	
	public int getPhase() {
		return ball.getPhase();
	}
	
	public Vector3f getPos() {
		return ball.getPos();
	}
	
	public float calcDistance() {
		float x = lookatCenter().getValue(0) - lookatEye().getValue(0);
		float z = 
	}
	
	public void incScore() {
		score++;
	}
}