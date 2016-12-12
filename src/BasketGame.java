import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;
import javax.sound.sampled.*;

import javax.imageio.ImageIO;

import com.jogamp.opengl.*;
import com.jogamp.opengl.fixedfunc.GLMatrixFunc;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;
import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.awt.TextRenderer;
import java.text.DecimalFormat;
import java.util.Set;

public class BasketGame implements GLEventListener, KeyListener, MouseListener, MouseMotionListener {
	private int windowWidth, windowHeight;
	
	private TextureLoader texture_loader = null;
	private SkyBox mySkybox = null;
	private final float skybox_size = 1000.0f;
	private final String SkyboxName = "ThickCloudsWater";
	
	private final int skybox_max_textures = 1;
	//private SkyBox[] arrSkyboxes = new SkyBox[ arrSkyboxName.length ];
	int texID[]  = new int[10];
	
	private float xPos = 8.0f;
	private float yPos = 0.0f;
	private float zPos = 2.0f;
	private float xLook = 1.0f;
	private float yLook = 0.0f;
	private float zLook = 0.0f;
	
	
	Vector3f pos = new Vector3f(xPos, yPos, zPos);
	Vector3f look = new Vector3f(xLook, yLook, zLook);
	Vector3f ballPos = new Vector3f(0.0f, -9.0f, 2.0f);
	
	Ball basketball = new Ball(new Vector3f(0.0f, -9.0f, 2.0f));
	Ball lastBall = new Ball(pos);
	boolean last_throw_score = false;
	
	Vector route;
	
	final int MOVEMENT = 0;
	final int STRENGTH = 1;
	final int SHOOT = 2;
	final int REPLAY = 3;
	int phase = MOVEMENT;
	
	int speed = 1;
	float step = 0.0f;
	float step_inc = 2;
	
	GLUT glut = new GLUT();
	
	private int mouse_x0 = 0;
	private int mouse_y0 = 0;
	
	private String buttonType = "";
	
	private float init_Velocity = 0.0f;
	private boolean progDirection = true;
	private boolean shootBar = false;
	private int shootPhase = 0;
	
	private int mouse_mode = 0;
	
	private final int MOUSE_MODE_NONE = 0;
	private final int MOUSE_MODE_ROTATE = 1;

	private boolean[] keys = new boolean[256];
	GLUquadric quadric;
	private GLU glu = new GLU();
	
	public void displayChanged( GLAutoDrawable gLDrawable, boolean modeChanged,
			boolean deviceChanged) { }

	@Override
	public void init( GLAutoDrawable gLDrawable ) {
		
		GL2 gl = gLDrawable.getGL().getGL2();
		gl.glClearColor( 0.0f, 0.0f, 0.0f, 1.0f );
		gl.glColor3f( 1.0f, 1.0f, 1.0f );
		gl.glClearDepth( 1.0f );
		gl.glEnable( GL.GL_DEPTH_TEST );
		gl.glDepthFunc( GL.GL_LEQUAL );
		gl.glEnable( GL.GL_TEXTURE_2D );
		//gl.glEnable(GL2.GL_LIGHTING);
		
		quadric = glu.gluNewQuadric();
		
		texture_loader = new TextureLoader( gl );
		
		/*for ( int i = 0; i < skybox_max_textures; ++i )
			arrSkyboxes[ i ] = new SkyBox( texture_loader, arrSkyboxName[ i ] );
		*/
		mySkybox = new SkyBox( texture_loader, SkyboxName );
		
		try {
			gl.glGenTextures(texID.length, texID, 0);
			texture_loader.loadTexture(texID[0], "textures/grass.jpg");
			gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
			gl.glEnable(GL2.GL_BLEND);
			texture_loader.loadTexture(texID[1], "textures/fence_wire.png");
			gl.glDisable(GL2.GL_BLEND);
			texture_loader.loadTexture(texID[2], "textures/outsidecourt.jpg");
			texture_loader.loadTexture(texID[3], "textures/bboard.jpg");
			texture_loader.loadTexture(texID[4], "textures/pole.jpg");
			texture_loader.loadTexture(texID[5], "textures/asphaltArrow.jpg");
			
			//gl.glBlendFunc(GL2.GL_SRC_ALPHA,GL2.GL_ONE_MINUS_SRC_ALPHA);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		// Initialize the keys.
		for ( int i = 0; i < keys.length; ++i )
			keys[i] = false;
		
		gl.glMatrixMode( GLMatrixFunc.GL_MODELVIEW );
		gl.glLoadIdentity();
	}
	
	@Override
	public void reshape( GLAutoDrawable gLDrawable, int x, int y, int width, int height ) {
		windowWidth = width;
		windowHeight = height > 0 ? height : 1;
		
		final GL2 gl = gLDrawable.getGL().getGL2();
		
		gl.glViewport( 0, 0, width, height );
		gl.glMatrixMode( GL2.GL_PROJECTION );
		gl.glLoadIdentity();
		glu.gluPerspective( 60.0f, (float) windowWidth / windowHeight, 0.1f, skybox_size * (float) Math.sqrt( 3.0 ) / 2.0f );
	}

	@Override
	public void display( GLAutoDrawable gLDrawable ) {
		final GL2 gl = gLDrawable.getGL().getGL2();
		
		gl.glClear( GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT );
		
		gl.glMatrixMode( GL2.GL_MODELVIEW );
		gl.glPushMatrix();
		final float pan = 0.25f;
		
		// Update the camera state.
		if (phase == MOVEMENT) {
			if ( keys[KeyEvent.VK_W] || keys[KeyEvent.VK_S] ) {
				float normxy = (float) Math.sqrt( xLook * xLook + yLook * yLook );
				float multiplier = keys[KeyEvent.VK_W] ? 1.0f : -0.5f;
				xPos += xLook / normxy * pan * multiplier;
				yPos += yLook / normxy * pan * multiplier;
			}
			
			if ( keys[KeyEvent.VK_R] ) {
				zPos += pan;
			} else if ( keys[KeyEvent.VK_F] ) {
				zPos -= pan;
			}
			
			if ( keys[KeyEvent.VK_A] || keys[KeyEvent.VK_D] ) {
				float angle1 = (float) Math.atan2( yLook, xLook );
				float angle2 = (float) Math.acos( zLook );
				
				if ( keys[KeyEvent.VK_A] )
					angle1 += Math.PI / 2.0;
				
				else if ( keys[KeyEvent.VK_D] )
					angle1 -= Math.PI / 2.0;
				
				float strafeHorizontal = (float)( Math.cos( angle1 ) * Math.sin( angle2 ) );
				float strafeVertical = (float)( Math.sin( angle1 ) * Math.sin( angle2 ) );
				float normxy = (float) Math.sqrt( strafeHorizontal * strafeHorizontal + strafeVertical * strafeVertical );
				
				System.out.println(Math.abs(yPos));
				
				xPos += strafeHorizontal / normxy * pan;
				if (yPos > 9.5)
					yPos = 9.5f;
				else if (yPos < -9.5)
					yPos = -9.5f;
				else
					yPos += strafeVertical / normxy * pan;
			}
			
			if (keys[KeyEvent.VK_UP] || buttonType == "lookUp"){
				
				if (zLook < 1.0f){
					zLook += 0.05f;
				}
				else{
					zLook -= 0.05f;
				}
				buttonType = "";
			}
			if (keys[KeyEvent.VK_DOWN] || buttonType == "lookDown"){
				
				if (zLook > -1.0f){
					zLook -= 0.05f;
				}
				else{
					zLook += 0.05f;
				}
				buttonType = "";
			}
			if (keys[KeyEvent.VK_LEFT] || buttonType == "lookLeft"){
				
				if (yLook < 1.0f){
					yLook += 0.1f;
				}
				else{
					yLook -= 0.1f;
				}
				buttonType = "";
			}
			if (keys[KeyEvent.VK_RIGHT] || buttonType == "lookRight"){
		
				if (yLook > -1.0f){
					yLook -= 0.1f;
				}
				else{
					yLook += 0.1f;
				}
				buttonType = "";
			}
			
			if (keys[KeyEvent.VK_COMMA] || buttonType == "moveLeft"){
				
				if (yPos < 9.5)
					yPos += 0.25;
				else
					yPos -=0.25;
				
				buttonType = "";
			}
			if (keys[KeyEvent.VK_PERIOD] || buttonType == "moveRight"){
				
				if (yPos > -9.5)
					yPos -= 0.25;
				else
					yPos +=0.25;
				
				buttonType = "";
			}
		}
		if (phase == MOVEMENT || phase == STRENGTH) {
			if(keys[KeyEvent.VK_SPACE] || buttonType == "strengthBar"){
				if(shootBar) {
					shootBar = false;
					basketball.throwBall(ballPos, init_Velocity * 160, yLook, zLook, pos);
					phase = SHOOT;
					route = basketball.getPath().getRoute();
				}
				else {
					shootBar = true;
					init_Velocity = 0.01f;
					phase = STRENGTH;
				}
				
				buttonType = "";
			}
		}
		
		glu.gluLookAt( xPos, yPos, zPos,
				xPos + xLook, yPos + yLook, zPos + zLook,
				0.0f, 0.0f, 1.0f );
		
		gl.glPushMatrix();
		
		gl.glTranslatef( xPos, yPos, zPos );
		mySkybox.draw( gl, skybox_size );
		gl.glPopMatrix();
		
		gl.glPushMatrix();
		gl.glRotatef(90,0.0f,0.0f,1.0f);
		gl.glTranslatef(0f, 0f, -0.5f);
		drawCourt(gl, 30f);
		drawGround( gl, 150.0f );
		drawBox(gl, 30f);
		
		if (phase == MOVEMENT || phase == STRENGTH) {
			ballPos.setParameter(2, 2.0f + zLook);
			ballPos.setParameter(0, 0.0f + yPos + yLook);
		}
		
		if (phase == SHOOT) {
			step += step_inc;
			int step_down = (int) Math.floor(step);
			int step_up = (int) Math.ceil(step);
			float factor = step - step_down;
			
			if (step_up < route.size()) {
				ballPos.setVector(((Vector3f) route.get(step_down)).getValue(0) + factor * ((Vector3f) route.get(step_up)).getValue(0),
					((Vector3f) route.get(step_down)).getValue(1) + factor * ((Vector3f) route.get(step_up)).getValue(1),
					((Vector3f) route.get(step_down)).getValue(2) + factor * ((Vector3f) route.get(step_up)).getValue(2));
			}
			else {
				ballPos.setVector(((Vector3f) route.get(route.size() - 1)).getValue(0),
						((Vector3f) route.get(route.size() - 1)).getValue(1),
						((Vector3f) route.get(route.size() - 1)).getValue(2));
				phase = REPLAY;
			}
		}
		
		if (phase == REPLAY) {
			step = 0;
			
			
			xPos = 8.0f;
			yPos = 0.0f;
			zPos = 2.0f;
			xLook = 1.0f;
			yLook = 0.0f;
			zLook = 0.0f;
			phase = MOVEMENT;
			ballPos.setVector(0.0f, -9.0f, 2.0f);
		}
		
		basketball.drawBall(ballPos, look, gl);
		
		//Backboard
		gl.glBindTexture(GL.GL_TEXTURE_2D, texID[3]);
		gl.glBegin(GL2.GL_POLYGON);
		gl.glTexCoord2f(0.0f, 0.0f);
		gl.glVertex3f(-3.0f, -18.5f, 6.5f);
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex3f(3.0f, -18.5f, 6.5f);
		gl.glTexCoord2f(1.0f, 1.0f);
		gl.glVertex3f(3.0f, -18.5f, 9.5f);
		gl.glTexCoord2f(0.0f, 1.0f);
		gl.glVertex3f(-3.0f, -18.5f, 9.5f);
		gl.glEnd();
		gl.glDisable(GL.GL_TEXTURE_2D);
		
		//Rim
		gl.glColor3f(1.0f, 0.3f, 0.03f);
		gl.glPushMatrix();
		gl.glTranslatef(0.0f, -17.5f, 7.0f);
		//gl.glRotatef(-5, 1, 0, 0);
		glut.glutSolidTorus(0.045f, 1.0f, 15, 15);
		gl.glPopMatrix();
		

		//Pole
		gl.glColor3f(0.5f, 0.5f, 0.5f);
	    gl.glPushMatrix();
	    //gl.glBindTexture(GL.GL_TEXTURE_2D, texID[4]);
	    //gl.glColor3f(0.5f, 0.5f, 0.5f);
	    //gl.glRotatef(90, 0, 1, 0);  
	    //gl.glRotatef(90, 1, 0, 0);
	    gl.glTranslatef(0.0f, -19.0f, 0.0f);
	    glut.glutSolidCylinder(0.1, 8, 10, 10);
	    gl.glPopMatrix();
		
	    gl.glEnable(GL.GL_TEXTURE_2D);
		//Start of 2D heads-up display
		gl.glPopMatrix();
		gl.glPopMatrix();
		
		drawHud(gl);
		
				
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glPushMatrix();
		gl.glOrtho(0,windowWidth,windowHeight,0,-1,1);
		gl.glLoadIdentity();
		//gl.glOrtho(0,windowWidth,windowHeight,0,-1,1);
				
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glPushMatrix();
		gl.glLoadIdentity();
		
		gl.glDisable(GL2.GL_DEPTH_TEST);
		
		drawHudButtons(gl);
		
		gl.glEnable(GL2.GL_DEPTH_TEST);		
		
		gl.glPopMatrix();
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glPopMatrix();
	}
	
	void drawHudButtons(GL2 gl){
		gl.glBindTexture( GL2.GL_TEXTURE_2D, texID[5] );
		
		//Angle Button 1 - Left
		gl.glBegin(GL2.GL_QUADS);
		gl.glTexCoord2f( 0.0f, 0.0f );
		gl.glVertex2f(-0.75f, -0.6f);
		
		gl.glTexCoord2f( 1.0f, 0.0f );
		gl.glVertex2f(-0.9f, -0.6f);
		
		gl.glTexCoord2f( 1.0f, 1.0f );
		gl.glVertex2f(-0.9f, -0.75f);
		
		gl.glTexCoord2f( 0.0f, 1.0f );
		gl.glVertex2f(-0.75f, -0.75f);
		gl.glEnd();
				
		//Angle Button 2 - Up
		gl.glBegin(GL2.GL_QUADS);
		gl.glTexCoord2f( 1.0f, 0.0f );
		gl.glVertex2f(-0.6f, -0.45f);
		
		gl.glTexCoord2f( 1.0f, 1.0f );
		gl.glVertex2f(-0.75f, -0.45f);
		
		gl.glTexCoord2f( 0.0f, 1.0f );
		gl.glVertex2f(-0.75f, -0.6f);
		
		gl.glTexCoord2f( 0.0f, 0.0f );
		gl.glVertex2f(-0.6f, -0.6f);
		gl.glEnd();

		//Angle Button 3
		gl.glBegin(GL2.GL_QUADS);
		gl.glTexCoord2f( 1.0f, 1.0f );
		gl.glVertex2f(-0.45f, -0.6f);
		
		gl.glTexCoord2f( 0.0f, 1.0f );
		gl.glVertex2f(-0.6f, -0.6f);
		
		gl.glTexCoord2f( 0.0f, 0.0f );
		gl.glVertex2f(-0.6f, -0.75f);
		
		gl.glTexCoord2f( 1.0f, 0.0f );
		gl.glVertex2f(-0.45f, -0.75f);
		gl.glEnd();
		
		//Angle Button 4
		gl.glBegin(GL2.GL_QUADS);
		gl.glTexCoord2f( 0.0f, 1.0f );
		gl.glVertex2f(-0.6f, -0.75f);
		
		gl.glTexCoord2f( 0.0f, 0.0f );
		gl.glVertex2f(-0.75f, -0.75f);
		
		gl.glTexCoord2f( 1.0f, 0.0f );
		gl.glVertex2f(-0.75f, -0.9f);
		
		gl.glTexCoord2f( 1.0f, 1.0f );
		gl.glVertex2f(-0.6f, -0.9f);
		gl.glEnd();
		
		//Position Button 1
		gl.glBegin(GL2.GL_QUADS);
		gl.glTexCoord2f( 0.0f, 0.0f );
		gl.glVertex2f(0.75f, -0.6f);
		
		gl.glTexCoord2f( 1.0f, 0.0f );
		gl.glVertex2f(0.9f, -0.6f);
		
		gl.glTexCoord2f( 1.0f, 1.0f );
		gl.glVertex2f(0.9f, -0.75f);
		
		gl.glTexCoord2f( 0.0f, 1.0f );
		gl.glVertex2f(0.75f, -0.75f);
		gl.glEnd();
				
		//Position Button 2
		gl.glBegin(GL2.GL_QUADS);
		gl.glTexCoord2f( 1.0f, 1.0f );
		gl.glVertex2f(0.45f, -0.6f);
				
		gl.glTexCoord2f( 0.0f, 1.0f );
		gl.glVertex2f(0.6f, -0.6f);
				
		gl.glTexCoord2f( 0.0f, 0.0f );
		gl.glVertex2f(0.6f, -0.75f);
				
		gl.glTexCoord2f( 1.0f, 0.0f );
		gl.glVertex2f(0.45f, -0.75f);
		gl.glEnd();
		
		//Draw Progress Bar
		gl.glBindTexture( GL2.GL_TEXTURE_2D, 0 );
		gl.glBegin(GL2.GL_QUADS);
		gl.glVertex2f(0.3f, -0.6f);
		gl.glVertex2f(-0.3f, -0.6f);
		gl.glVertex2f(-0.3f, -0.75f);
		gl.glVertex2f(0.3f, -0.75f);
		gl.glEnd();
		
		gl.glColor3f(0.0f, 1.0f, 0.0f);
		gl.glBegin(GL2.GL_QUADS);
		gl.glVertex2f(0.15f, -0.6f);
		gl.glVertex2f(0.1f, -0.6f);
		gl.glVertex2f(0.1f, -0.75f);
		gl.glVertex2f(0.15f, -0.75f);
		gl.glEnd();
		
		gl.glTranslatef(0.55f * init_Velocity, 0.0f, 0.0f);
		gl.glColor3f(0.0f, 0.0f, 1.0f);
		gl.glBegin(GL2.GL_QUADS);
		gl.glVertex2f(-0.25f, -0.65f);
		gl.glVertex2f(-0.3f, -0.65f);
		gl.glVertex2f(-0.3f, -0.7f);
		gl.glVertex2f(-0.25f, -0.7f);
		gl.glEnd();
		
		if (shootBar)
		{
			if (init_Velocity < 1.0f && progDirection){
				init_Velocity += 0.02f;
			}
			else
			{
				progDirection = false;
			}
			
			if (init_Velocity > 0.01 && !progDirection){
				init_Velocity -= 0.02;
			}
			else
			{
				progDirection = true;
			}
		}
	
	}
	
	void drawHud(GL2 gl){
		if (phase != REPLAY) {
			TextRenderer hudElements = new TextRenderer(new Font("Helvatica",Font.BOLD,15)); 
	        hudElements.beginRendering(windowWidth, windowHeight);
	        
	        DecimalFormat f = new DecimalFormat("0.00");
	        
	        String v1 = f.format(yPos * -1);
	        String v2 = f.format(yLook * -1);
	        String v3 = f.format(zLook);
	        String v4 = f.format((init_Velocity) * 100.0f);
	        
	        hudElements.setColor(1.0f,1.0f,1.0f,0.8f);
	        hudElements.draw("Player Position: " + v1, 15 , 400);
	        hudElements.draw("Player Horizontal Angle: " + v2, 15 , 385);
	        hudElements.draw("Player Vertical Angle: " + v3, 15 , 370);
	        hudElements.draw("Throw Strength: " + v4 + "%", 450, 400);
	        hudElements.endRendering();
		} else {
			int speed = 1;
			
		}
	}
	
	void drawBox(GL2 gl, float size)
	{
		final float d = (size / 3.0f);
		final float e = d - size;
		gl.glEnable(GL2.GL_BLEND);
		// Front
		gl.glBindTexture( GL2.GL_TEXTURE_2D, texID[1] );
		gl.glBegin( GL2.GL_QUADS );
		
		gl.glTexCoord2f( 0.0f, 1.0f );
		gl.glVertex3f( d, -e, d);
		
		gl.glTexCoord2f( 0.0f, 0.0f );
		gl.glVertex3f( d, -e, -0.5f);
		
		gl.glTexCoord2f( 1.0f, 0.0f );
		gl.glVertex3f( d, e, -0.5f);
		
		gl.glTexCoord2f( 1.0f, 1.0f );
		gl.glVertex3f( d, e, d);
		
		gl.glEnd();
		
		// Back
		gl.glBindTexture( GL2.GL_TEXTURE_2D, texID[1] );
		gl.glBegin( GL2.GL_QUADS );
		
		gl.glTexCoord2f( 0.0f, 1.0f );
		gl.glVertex3f( -d, e, d);
		
		gl.glTexCoord2f( 0.0f, 0.0f );
		gl.glVertex3f( -d, e, -0.5f );
		
		gl.glTexCoord2f( 1.0f, 0.0f );
		gl.glVertex3f( -d, -e, -0.5f );
		
		gl.glTexCoord2f( 1.0f, 1.0f );
		gl.glVertex3f( -d, -e, d );
		
		gl.glEnd();
		
		// Left
		gl.glBindTexture( GL2.GL_TEXTURE_2D, texID[1] );
		gl.glBegin( GL2.GL_QUADS );
		
		gl.glTexCoord2f( 0.0f, 1.0f );
		gl.glVertex3f( d, e, d );
		
		gl.glTexCoord2f( 0.0f, 0.0f );
		gl.glVertex3f( d, e, -0.5f );
		
		gl.glTexCoord2f( 0.5f, 0.0f );
		gl.glVertex3f( -d, e, -0.5f );
		
		gl.glTexCoord2f( 0.5f, 1.0f );
		gl.glVertex3f( -d, e, d );
		
		gl.glEnd();
		
		// Right
		gl.glBindTexture( GL2.GL_TEXTURE_2D, texID[1] );
		gl.glBegin( GL2.GL_QUADS );
		
		gl.glTexCoord2f( 0.0f, 1.0f );
		gl.glVertex3f( -d, -e, d );
		
		gl.glTexCoord2f( 0.0f, 0.0f );
		gl.glVertex3f( -d, -e, -0.5f );
		
		gl.glTexCoord2f( 0.5f, 0.0f );
		gl.glVertex3f( d, -e, -0.5f );
		
		gl.glTexCoord2f( 0.5f, 1.0f );
		gl.glVertex3f( d, -e, d );
		
		gl.glEnd();
		/*
		// Up
		gl.glBindTexture( GL2.GL_TEXTURE_2D, texID[1] );
		gl.glBegin( GL2.GL_QUADS );
		
		gl.glTexCoord2f( 0.0f, 1.0f );
		gl.glVertex3f( -d, -e, d );
		
		gl.glTexCoord2f( 0.0f, 0.0f );
		gl.glVertex3f( d, -e, d );
		
		gl.glTexCoord2f( 1.0f, 0.0f );
		gl.glVertex3f( d, e, d );
		
		gl.glTexCoord2f( 1.0f, 1.0f );
		gl.glVertex3f( -d, e, d );
		
		gl.glEnd();
		*/
		
		/*
		gl.glBindTexture( GL2.GL_TEXTURE_2D, texID[1] );
		gl.glBegin( GL2.GL_QUADS );
		
		gl.glTexCoord2f( 0.0f, 1.0f );
		gl.glVertex3f( d, -e, 0.0f );
		
		gl.glTexCoord2f( 0.0f, 0.0f );
		gl.glVertex3f( -d, -e, -d );
		
		gl.glTexCoord2f( 1.0f, 0.0f );
		gl.glVertex3f( -d, e, -d );
		
		gl.glTexCoord2f( 1.0f, 1.0f );
		gl.glVertex3f( d, e, -d );
		
		gl.glEnd();
		*/
		
		gl.glDisable(GL2.GL_BLEND);
	}
	
	void drawGround( GL2 gl, float size ) {
		final float d = size / 2.0f;
		
		//gl.glDisable( GL2.GL_TEXTURE_2D );
		gl.glBindTexture(GL.GL_TEXTURE_2D, texID[0]);
		gl.glBegin( GL2.GL_QUADS );
		
			gl.glTexCoord2f(10.0f, 10.0f);
			gl.glVertex3f( d, d, 0.0f );
			gl.glTexCoord2f(-1f, 10.0f);
			gl.glVertex3f( -d, d, 0.0f );
			gl.glTexCoord2f(-1f, -1f);
			gl.glVertex3f( -d, -d, 0.0f );
			gl.glTexCoord2f(10.0f, -1f);
			gl.glVertex3f( d, -d, 0.0f );
		
		gl.glEnd();
		//gl.glEnable( GL2.GL_TEXTURE_2D );
	}
	
	void drawCourt( GL2 gl, float size ) {
		final float d = size / 3.0f;
		final float e = d - size;
		
		//gl.glDisable( GL2.GL_TEXTURE_2D );
		gl.glBindTexture( GL2.GL_TEXTURE_2D, texID[2] );
		gl.glBegin( GL2.GL_QUADS );
		
		gl.glTexCoord2f( 0.0f, 1.0f );
		gl.glVertex3f( d, -e, 0.1f );
		
		gl.glTexCoord2f( 0.0f, 0.0f );
		gl.glVertex3f( -d, -e, 0.1f );
		
		gl.glTexCoord2f( 1.0f, 0.0f );
		gl.glVertex3f( -d, e, 0.1f );
		
		gl.glTexCoord2f( 1.0f, 1.0f );
		gl.glVertex3f( d, e, 0.1f );
		
		gl.glEnd();
		//gl.glEnable( GL2.GL_TEXTURE_2D );
	}

	@Override
	public void dispose( GLAutoDrawable arg0 ) {
	}

	@Override
	public void keyTyped( KeyEvent e ) {
		char key = e.getKeyChar();
	}

	@Override
	public void keyPressed( KeyEvent e ) {
		keys[ e.getKeyCode() ] = true;
	}

	@Override
	public void keyReleased( KeyEvent e ) {
		if(keys[ e.getKeyCode() ])
			keys[ e.getKeyCode() ] = false;
		else
			keys[ e.getKeyCode() ] = true;
	}

	@Override
	public void mouseDragged( MouseEvent e ) {
		/*
		int x = e.getX();
		int y = e.getY();
		
		final float throttle_rot = 128.0f;
		
		float dx = ( x - mouse_x0 );
		float dy = ( y - mouse_y0 );
		
		if ( MOUSE_MODE_ROTATE == mouse_mode ) {
			float angle2 = (float) Math.acos( zLook );
			float angle1 = (float) Math.atan2( yLook, xLook );
			
			angle1 -= dx / throttle_rot;
			angle2 += dy / throttle_rot;
			
			if ( angle1 >= Math.PI * 2.0 )
				angle1 -= Math.PI * 2.0;
			else if ( angle1 < 0 )
				angle1 += Math.PI * 2.0;
			
			if ( angle2 > Math.PI - 0.1 )
				angle2 = (float)( Math.PI - 0.1 );
			else if ( angle2 < 0.1f )
				angle2 = 0.1f;
			
			xLook = (float)( Math.cos( angle1 ) * Math.sin( angle2 ) );
			yLook = (float)( Math.sin( angle1 ) * Math.sin( angle2 ) );
			zLook = (float)( Math.cos( angle2 ) );
		}
		
		mouse_x0 = x;
		mouse_y0 = y;
		*/
	}
	
	@Override
	public void mouseMoved( MouseEvent e ) {
	}

	@Override
	public void mouseClicked( MouseEvent e ) {
	}

	@Override
	public void mousePressed( MouseEvent e ) {
		mouse_x0 = e.getX();
		mouse_y0 = e.getY();
		
		System.out.println("X Click: " + mouse_x0);
		System.out.println("Y Click: " + mouse_y0);
		
		if ( MouseEvent.BUTTON2 == e.getButton() ) {
			mouse_mode = MOUSE_MODE_ROTATE;
		} else {
			mouse_mode = MOUSE_MODE_NONE;
		}
		
		//Button Look 1 - Left
		if(mouse_x0 < 75 && mouse_x0 > 30){
			if (mouse_y0 < 385 && mouse_y0 > 350){
				buttonType = "lookLeft";
			}
		}
		//Button Look 2 - Right
		if(mouse_x0 < 170 && mouse_x0 > 125){
			if (mouse_y0 < 385 && mouse_y0 > 350){
				buttonType = "lookRight";
			}
		}
		//Button Look 3 - Up
		if(mouse_x0 < 125 && mouse_x0 > 75){
			if (mouse_y0 < 350 && mouse_y0 > 320){
				buttonType = "lookUp";
			}
		}
		//Button Look 4 - Down
		if(mouse_x0 < 125 && mouse_x0 > 75){
			if (mouse_y0 < 420 && mouse_y0 > 385){
				buttonType = "lookDown";
			}
		}
		
		//Button Move 1 - Left
		if(mouse_x0 < 495 && mouse_x0 > 455){
			if (mouse_y0 < 385 && mouse_y0 > 350){
				buttonType = "moveLeft";
			}
		}
		//Button Move 2 - Right
		if(mouse_x0 < 590 && mouse_x0 > 545){
			if (mouse_y0 < 385 && mouse_y0 > 350){
				buttonType = "moveRight";
			}
		}
		
		//Strength Bar
		if(buttonType == ""){
			buttonType = "strengthBar";
		}

	}

	@Override
	public void mouseReleased( MouseEvent e ) {
	}

	@Override
	public void mouseEntered( MouseEvent e ) {
	}

	@Override
	public void mouseExited( MouseEvent e ) {
	}
}