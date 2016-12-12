/* Names: Aaron Mueller; Thomas Jones
 * Course: CS335
 * Date: 12 December 2016
 * Assignment: Final Project
 * Purpose: To run the project
 */

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFrame;

import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.Animator;

public class main extends JFrame {
	static private Animator animator = null;
	
	public main() {
		super( "Island Baskets" );
		
		setDefaultCloseOperation( EXIT_ON_CLOSE );
		setSize( 640, 480 );
		setVisible( true );
		
		setupJOGL();
	}
	
	public static void main( String[] args ) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		main m = new main();
		m.setVisible( true );
		AudioInputStream audio = AudioSystem.getAudioInputStream(
				new File("sounds/music.wav"));
		Clip clip = AudioSystem.getClip();
		clip.open(audio);
		FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
		gainControl.setValue(-18.0f);
		clip.start();
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