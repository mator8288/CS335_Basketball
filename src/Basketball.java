import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

public class Basketball {
	private float x_pos = 8.1f;
	private float y_pos = 0.0f;
	private float z_pos = 1.0f;
	private final float radius = 0.4f;
	
	protected TextureLoader texture_loader = null;
	private String skybox_name = null;
	
	public void draw(GL2 gl) {
		float px, py, pz;
		float nO = 40, nA = 40;
	    float incO = (float) (2 * Math.PI) / nO;
	    float incA = (float) Math.PI / nA;

	    gl.glBegin(GL.GL_TRIANGLE_STRIP);

	    for (int i = 0 ; i <= nO; i++){
	        for (int j = 0; j <= nA; j++) {
	            pz = (float) Math.cos(Math.PI-(incA*j))*radius;
	            py = (float) (Math.sin(Math.PI-(incA*j))*Math.sin(incO*i)*radius);
	            px = (float) (Math.sin(Math.PI-(incA*j))*Math.cos(incO*i)*radius);

	            gl.glNormal3f(px*5, py*5, pz*5);
	            gl.glVertex3f(px, py, pz);

	            pz = (float) Math.cos(Math.PI-(incA*j))*radius;
	            py = (float) (Math.sin(Math.PI-(incA*j))*Math.sin(incO*(i+1))*radius);
	            px = (float) (Math.sin(Math.PI-(incA*j))*Math.cos(incO*(i+1))*radius);

	            gl.glNormal3f(px*5, py*5, pz*5);
	            gl.glVertex3f(px, py, pz);
	           }
	    }
	    gl.glEnd();
	}
}