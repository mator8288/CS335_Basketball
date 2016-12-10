import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.awt.GLCanvas;
import javax.swing.JFrame;
import com.jogamp.opengl.util.Animator;

public class main extends JFrame {
	static private Animator animator = null;
	
	public main() {
		super( "Basketball" );
		
		setDefaultCloseOperation( EXIT_ON_CLOSE );
		setSize( 640, 480 );
		setVisible( true );
		
		setupJOGL();
	}
	//comment
	
	public static void main( String[] args ) {
		main m = new main();
		m.setVisible( true );
	}
	
	private void setupJOGL() {
		GLCapabilities caps = new GLCapabilities( null );
		caps.setDoubleBuffered( true );
		caps.setHardwareAccelerated( true );
		
		GLCanvas canvas = new GLCanvas( caps ); 
		add( canvas );
		
		BasketGame jgl = new BasketGame();
		canvas.addGLEventListener( jgl ); 
		canvas.addKeyListener( jgl ); 
		canvas.addMouseListener( jgl );
		canvas.addMouseMotionListener( jgl );
		
		animator = new Animator( canvas );
		animator.start();
	}
}