import com.jogamp.opengl.*;
import java.util.*;

public class Ring {

	int faces;
	Vector vertexList;
	Vector normalList;
	final int X = 0;
	final int Y = 1;
	final int Z = 2;
	int [][] faceLocation;
	
	public Ring (float bigR, float smallR, int fa) {
	    float uAngle = 0.0f, vAngle = 0.0f;
	    float angleChange = (float) 360 / (float) faces;
	    this.faces = fa*fa;
	
	    vertexList = new Vector(faces * faces);
	    normalList = new Vector(faces * faces);
	    for (int i = 0; i < faces; i++) {
	        for (int j = 0; j < faces; j++) {
	            vertexList.set(j + i * faces, new Vector3f(
	            		(bigR + smallR * (float) Math.cos(vAngle * (float) Math.PI / 180)) * (float) Math.cos(uAngle * (float) Math.PI / 180),
	            		(float) (bigR + smallR * Math.cos(vAngle * Math.PI / 180)) * (float) Math.sin(uAngle * Math.PI / 180),
	            		smallR * (float) Math.sin(vAngle * Math.PI / 180)));
	            vAngle += angleChange;
	        }
	        vAngle = 0;
	        uAngle += angleChange;
	    }
	
	    faceLocation = new int[faces][];
	    for (int i = 0; i < faces; i++) faceLocation[i] = new int[4];
	
	    for (int i = 0; i < faces; i++) {
	        for (int j = 0; j < faces; j++) {
	            faceLocation[j + i * faces][0] = (j + i * faces) % (faces);
	            faceLocation[j + i * faces][1] = ((j + 1) % faces + i * faces) % (faces);
	            faceLocation[j + i * faces][2] = ((j + 1) % faces + faces + i * faces) % (faces);
	            faceLocation[j + i * faces][3] = (j + faces + i * faces) % (faces);
	        }
	    }
	
	    //Normalize
	    for (int i = 0; i < faces * faces; i++) {
	        Vector3f vec1 = new Vector3f(((Vector3f) vertexList.get(faceLocation[i][0])).getValue(X) - ((Vector3f) vertexList.get(faceLocation[i][1])).getValue(X),
	                      ((Vector3f) vertexList.get(faceLocation[i][0])).getValue(Y) - ((Vector3f) vertexList.get(faceLocation[i][1])).getValue(Y),
	                      ((Vector3f) vertexList.get(faceLocation[i][0])).getValue(Z) - ((Vector3f) vertexList.get(faceLocation[i][1])).getValue(Z));
	        
	        Vector3f vec2 = new Vector3f(((Vector3f) vertexList.get(faceLocation[i][0])).getValue(X) - ((Vector3f) vertexList.get(faceLocation[i][2])).getValue(X),
	                      ((Vector3f) vertexList.get(faceLocation[i][0])).getValue(Y) - ((Vector3f) vertexList.get(faceLocation[i][2])).getValue(Y),
	                      ((Vector3f) vertexList.get(faceLocation[i][0])).getValue(Z) - ((Vector3f) vertexList.get(faceLocation[i][2])).getValue(Z));
	        normalList.set(i, new Vector3f(vec1.getValue(Y) * vec2.getValue(Z) - vec1.getValue(Z) * vec2.getValue(Y),
	                                      vec1.getValue(Z) * vec2.getValue(X) - vec1.getValue(X) * vec2.getValue(Z),
	                                      vec1.getValue(X) * vec2.getValue(Y) - vec1.getValue(Y) * vec2.getValue(X)));
	        ((Vector3f) normalList.get(i)).normalize();
	    }
	
	}
	
	public void drawRing(GL2 gl) {
	    gl.glPushMatrix();
	    gl.glBegin(GL2.GL_QUADS);
	    for (int i = 0; i < faces; i++) {
	        gl.glNormal3f(((Vector3f) normalList.get(i)).getValue(X), ((Vector3f) normalList.get(i)).getValue(Y), ((Vector3f) normalList.get(i)).getValue(Z));
	        gl.glVertex3f(((Vector3f) vertexList.get(faceLocation[i][0])).getValue(X), ((Vector3f) vertexList.get(faceLocation[i][0])).getValue(Y), ((Vector3f) vertexList.get(faceLocation[i][0])).getValue(Z));
	        gl.glVertex3f(((Vector3f) vertexList.get(faceLocation[i][1])).getValue(X), ((Vector3f) vertexList.get(faceLocation[i][1])).getValue(Y), ((Vector3f) vertexList.get(faceLocation[i][1])).getValue(Z));
	        gl.glVertex3f(((Vector3f) vertexList.get(faceLocation[i][2])).getValue(X), ((Vector3f) vertexList.get(faceLocation[i][2])).getValue(Y), ((Vector3f) vertexList.get(faceLocation[i][2])).getValue(Z));
	        gl.glVertex3f(((Vector3f) vertexList.get(faceLocation[i][3])).getValue(X), ((Vector3f) vertexList.get(faceLocation[i][3])).getValue(Y), ((Vector3f) vertexList.get(faceLocation[i][3])).getValue(Z));
	    }
	    gl.glEnd();
	    gl.glColor3d(1.0, 1.0, 1.0);
	    gl.glPopMatrix();
	}
	
	public int getfaces() {
	    return faces;
	}
	
	public Vector3f getNormalAt(int pos) {
	    return (Vector3f) normalList.get(pos);
	}
	
	public Vector3f getVertexAt(int pos) {
	    return (Vector3f) vertexList.get(pos);
	}
}
