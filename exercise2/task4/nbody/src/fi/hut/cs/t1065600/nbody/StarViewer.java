package fi.hut.cs.t1065600.nbody;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Semaphore;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

public final class StarViewer extends Canvas {

	//The window background is mono-black. The black is half transparent
	//so that the drawn stars will leave a fading 'tail' where they travel
	private final static Color BACKGROUND_COLOR = new Color(0, 0, 0, 135);
	
	//The individual stars are white, with some transparency so that drawing
	//lots of stars on top of each other will intensify the glow
	private final static Color STAR_COLOR = new Color(255, 255, 255, 135);
	
	//Control flag for enabling high quality rendering output
	//The rendering times will likely be 2-10 times longer than standard
	private final static boolean HIGH_QUALITY_GRAPHICS = true;
	
	//Control flag for enabling writing each frame to disk as an image.
	//Combine the images into an animation for smooth viewing
	private final static boolean WRITE_IMAGE_PER_FRAME = false;
	
	//Store the window size
	private final int windowWidth;
	private final int windowHeight;
	private final float scale;
	
	private int frameCounter;
	private int randomID;

	/**
	 * Construct a new GalaxyViewer.
	 * @param width
	 * @param height
	 */
	public StarViewer(final int width, final int height, final float scale) {

		this.windowWidth = width;
		this.windowHeight = height;
		this.scale = scale;
		
		frameCounter = 0;
		randomID = (int) (System.currentTimeMillis() % 1000);
	}
	
	public void init(final Semaphore sem) {
		
		GraphicsEnvironment genv = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gdev = genv.getDefaultScreenDevice();
		GraphicsConfiguration gconfig = gdev.getDefaultConfiguration();

		//Create a frame, a OS level window for the view
		final JFrame container = new JFrame("Galaxy viewer", gconfig);
		
		//If the window's 'close' button is clicked, exit the JVM
		container.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		
		//If the user presses Q or ESC, exit the JVM
		this.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				int kc = e.getKeyCode();
				if(kc == KeyEvent.VK_Q ||kc == KeyEvent.VK_ESCAPE) {
					System.exit(0);
				}
			}
		});
		
		this.requestFocus();
		setBounds(5, 5, windowWidth+5, windowHeight+5);
		

		//Get the content pane of the frame set it up
		JPanel panel = (JPanel) container.getContentPane();
		panel.setPreferredSize(new Dimension(windowWidth, windowHeight));
		panel.setLayout(null);
		panel.add(this);

		//Rendering is custom controlled, ignore the default repaint events
		setIgnoreRepaint(true);

		//Let the components configure themselves
		container.pack();

		//Use a fixed sized window, simplifies rendering
		container.setResizable(false);
		
		//Center the frame on the users screen
		container.setLocationRelativeTo(null);
		
		//Finally turn the window visible
		container.setVisible(true);
		
		//Set how many buffers the graphics system will use
		createBufferStrategy(1);

		//Increment sem value by one, allowing the caller to continue
		sem.release();
		
		//Initially fill the entire window with black
		Graphics2D g = (Graphics2D) getBufferStrategy().getDrawGraphics();
		g.setColor(Color.black);
		g.fillRect(0, 0, windowWidth, windowHeight);
	}

	/**
	 * Render the given Galaxy. 
	 * @param galaxy
	 */
	public void render(StarField galaxy) {
		frameCounter++;

		BufferStrategy bufferStrategy = this.getBufferStrategy();
		
		//Render single frame
		do {
			//The following loop ensures that the contents of the drawing buffer
			//are consistent in case the underlying surface was recreated
			do {
				//Acquire the Graphics2D object which is used to draw to the
				//buffer backing the window frame
				Graphics2D g = (Graphics2D) bufferStrategy.getDrawGraphics();
				
				//Check if the HQ graphics should be enabled
				if(HIGH_QUALITY_GRAPHICS) {
					g.setRenderingHint(
							RenderingHints.KEY_RENDERING, 
							RenderingHints.VALUE_RENDER_QUALITY);
					
					g.setRenderingHint(
							RenderingHints.KEY_ANTIALIASING,
							RenderingHints.VALUE_ANTIALIAS_ON);
					
					g.setRenderingHint(
							RenderingHints.KEY_ALPHA_INTERPOLATION,
							RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
				}

				//Draw the array of stars using the graphics context
				render(g, galaxy.getStars());

				//Dispose of the graphics context
				bufferStrategy.dispose();

				//Repeat the rendering if the drawing buffer contents were restored
			} while (bufferStrategy.contentsRestored());

			// Display the buffer
			bufferStrategy.show();

		// Repeat the rendering if the drawing buffer was lost
		} while (bufferStrategy.contentsLost());
		
		//Render the frame to HD image
		if(WRITE_IMAGE_PER_FRAME) {
			BufferedImage img = new BufferedImage(windowWidth, windowHeight, BufferedImage.TYPE_INT_RGB);
			
			Graphics2D g2 = img.createGraphics();
			g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			
			render(g2, galaxy.getStars());
			
			try {
				ImageIO.write(img, "jpg", new File(randomID + "_frame_" + frameCounter +".jpg"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Perform the actual drawing using supplied and configured the Graphics2D context.
	 * @param g
	 * @param stars
	 */
	private void render(final Graphics2D g, Star[] stars) {
		
		//Fill the background with a monocolor
		g.setColor(BACKGROUND_COLOR);
		g.fillRect(0, 0, windowWidth, windowHeight);
		
		g.setColor(STAR_COLOR);
		
		int x;
		int y;
		
		//Do a for-each loop
		for(Star s : stars) {
			
			//Calculate position in 2D, ignore the Z component
			x = (int) (s.getXposition() * scale + (float)windowWidth / 2.0f);
			y = (int) (s.getYposition() * scale + (float)windowHeight / 2.0f);
			
			//There is no drawPixel so just draw a one pixel line
			g.drawLine(x, y, x, y);
		}
	}
	
	//Randomly generated serial number, just because it's expected of classes
	//extending any serializable class
	private static final long serialVersionUID = 6614583248555976602L;

}
