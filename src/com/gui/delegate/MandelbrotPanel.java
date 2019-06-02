package com.gui.delegate;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.gui.model.MandelbrotCalculator;

public class MandelbrotPanel extends JPanel {
	/**
	 * This class contains the functionality of the panel that is used to draw the
	 * GUI
	 */
	private static final long serialVersionUID = 1L;
	// complex pane configuration
	double minReal = -2.0, minImaginary = -1.25;
	double maxReal = 0.7, maxImaginary = 1.25;
	int maxIterations = 50, magnification = 1;
	double radiusSquared = 4.0;
	double realDistance, imaginaryDistance, xDistance, yDistance;
	int xResolution, yResolution;
	String magnificationTextContent;
	boolean panStatus = false, zoomStatus = false, mouseDrag = false, magStatus = false, colorStatus = false;
	int currentVersion = -1;
	List<Double[]> list = new ArrayList<Double[]>();
	BufferedImage image;
	MandelbrotCalculator mCalc = new MandelbrotCalculator();
	int[][] mandelbrotSetArray;
	int counter = 0;
	Point initPoint, finalPoint, draggedPoint;

	/**
	 * This constructor initialises the JPanel that I want to use to output the GUI
	 * 
	 * @param xResolution
	 * @param yResolution
	 */
	MandelbrotPanel(int xResolution, int yResolution) {
		this.xResolution = xResolution;
		this.yResolution = yResolution;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JComponent#paint(java.awt.Graphics)
	 */
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		xResolution=getWidth();
		yResolution=getHeight();
		image = new BufferedImage(xResolution, yResolution, BufferedImage.TYPE_INT_RGB);
		mandelbrotSetArray = mCalc.calcMandelbrotSet(xResolution, yResolution, minReal, maxReal, minImaginary,
				maxImaginary, maxIterations, radiusSquared);
		for (int i = 0; i < mandelbrotSetArray.length; i++) {
			for (int j = 0; j < mandelbrotSetArray[i].length; j++) {
					
				if (mandelbrotSetArray[i][j] != maxIterations && counter % 4 != 0) {
					int colorint = (int) (((double) mandelbrotSetArray[i][j] * 255 / (double) maxIterations));
					if (counter % 4 == 1)
						g.setColor(new Color(colorint, colorint, 255));
					else if (counter % 4 == 2)
						g.setColor(new Color(255, colorint, colorint));
					else if (counter % 4 == 3)
						g.setColor(new Color(colorint, 255, colorint));
					else
						g.setColor(new Color(255, 255, 255));
					g.drawLine(j, i, j, i);

				}

				else if (mandelbrotSetArray[i][j] == maxIterations) {

					g.setColor(new Color(0, 0, 0));
					g.drawLine(j, i, j, i);

				}

			}

		}

		if (magStatus) {
			g.setFont(new Font("TimesRoman", Font.BOLD, 18));
			g.setColor(Color.BLACK);
			g.drawString(("Magnification: x" + calculateMagnification()), 5, 55);
		}
		if (mouseDrag) {
			Graphics2D g2 = (Graphics2D) g.create();
			Rectangle2D selection = new Rectangle2D.Double();
			selection.setFrameFromDiagonal(draggedPoint.getX(), draggedPoint.getY(), initPoint.getX(),
					initPoint.getY());
			g2.setColor(Color.BLACK);
			g2.draw(selection);

		}

	}

	/**
	 * This adds the current configuration to the list of configurations and calls
	 * the mouse events
	 */
	public void configureMandelbrotPanel() {
		addCurrentConfiguration();
		configurePanelMouseEvents();

	}

	/**
	 * This configures the mouse events for zoom and pan functionality
	 */
	private void configurePanelMouseEvents() {

		this.addMouseListener(new MouseListener() {

			public void mouseClicked(MouseEvent m) {
				// TODO Auto-generated method stub
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent e) {
				initPoint = e.getPoint();

			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				realDistance = maxReal - minReal;
				imaginaryDistance = maxImaginary - minImaginary;
				finalPoint = arg0.getPoint();

				if (zoomStatus) {
					configureZoomAction();
					addCurrentConfiguration();
				} else if (panStatus) {
					configurePanAction();
					addCurrentConfiguration();
				}
				mouseDrag = false;
			}

		});

		this.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent e) {
				if (zoomStatus) {
					draggedPoint = e.getPoint();
					mouseDrag = true;
					repaint();
				}
			}

			@Override
			public void mouseMoved(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

		});
	}

	/**
	 * This method configures the zoom action
	 */
	private void configureZoomAction() {

		normalizeZoomAction();
		double newMaxReal = minReal + (finalPoint.getX() / xResolution * realDistance);
		double newMinReal = minReal + (initPoint.getX() / xResolution * realDistance);

		double newMaxImaginary = minImaginary + (finalPoint.getY() / yResolution * imaginaryDistance);
		double newMinImaginary = minImaginary + (initPoint.getY() / yResolution * imaginaryDistance);

		setMinCoordinates(newMinReal, newMinImaginary);
		setMaxCoordinates(newMaxReal, newMaxImaginary);
		this.revalidate();
		this.repaint();
	}

	/**
	 * This method normalizes the zoom space to allow user to select in any
	 * direction
	 */
	private void normalizeZoomAction() {
		double minX = Math.min(initPoint.getX(), finalPoint.getX());
		double minY = Math.min(initPoint.getY(), finalPoint.getY());
		double maxX = Math.max(initPoint.getX(), finalPoint.getX());
		double maxY = Math.max(initPoint.getY(), finalPoint.getY());
		xDistance = Math.abs(maxX - minX);
		yDistance = Math.abs(maxY - minY);
		initPoint = new Point((int) minX, (int) minY);
		finalPoint = new Point((int) maxX, (int) maxY);
	}

	/**
	 * This Method configures the panning action
	 */
	private void configurePanAction() {

		xDistance = finalPoint.getX() - initPoint.getX();
		yDistance = finalPoint.getY() - initPoint.getY();
		double xComplexRatio = (xDistance / xResolution * realDistance);
		double yComplexRatio = (yDistance / yResolution * imaginaryDistance);

		setMinCoordinates((minReal - xComplexRatio), (minImaginary - yComplexRatio));
		setMaxCoordinates((maxReal - xComplexRatio), (maxImaginary - yComplexRatio));
		this.revalidate();
		this.repaint();

	}

	/**
	 * This sets the resolutions variables
	 * 
	 * @param xResolution X-resolution
	 * @param yResolution Y-resolution
	 */
	public void setResolution(int xResolution, int yResolution) {
		this.xResolution = xResolution;
		this.yResolution = yResolution;
	}

	/**
	 * This sets the initial points of complex number plane
	 * 
	 * @param minReal      initial point real number
	 * @param minImaginary initial point imaginary number
	 */
	public void setMinCoordinates(double minReal, double minImaginary) {
		this.minReal = minReal;
		this.minImaginary = minImaginary;
	}

	/**
	 * This sets the final points of complex number plane
	 * 
	 * @param maxReal      final point real number
	 * @param maxImaginary final point imaginary number
	 */
	public void setMaxCoordinates(double maxReal, double maxImaginary) {
		this.maxReal = maxReal;
		this.maxImaginary = maxImaginary;
	}

	/**
	 * This sets the maximum number of iterations
	 * 
	 * @param maxIterations Number of iterations
	 */
	public void setIterations(int maxIterations) {
		this.maxIterations = maxIterations;
	}

	/**
	 * This sets the radius
	 * 
	 * @param radiusSquared radius
	 */
	public void setRadius(double radiusSquared) {
		this.radiusSquared = radiusSquared;
	}

	/**
	 * This calculates the magnification of the current image
	 * 
	 * @return magnification of image
	 */
	public int calculateMagnification() {
		return (int) ((2.7*2.5) / ((maxReal - minReal)*(maxImaginary-minImaginary)));
	}

	/**
	 * This resets the image to initial configuration
	 */
	public void initialiseConfiguration() {
		retrieveConfiguration(0);
		list.clear();
		addCurrentConfiguration();
		currentVersion = 0;
		this.revalidate();
		this.repaint();
	}

	/**
	 * This adds the current configuration to the list of configurations
	 */
	public void addCurrentConfiguration() {

		Double[] currentConfiguration = { minReal, minImaginary, maxReal, maxImaginary, (double) maxIterations,
				(double) counter, (double) currentVersion };
		if (currentVersion < list.size())

			list.subList(currentVersion + 1, list.size()).clear();
		currentVersion++;
		list.add(currentConfiguration);

	}

	/**
	 * This retrieves the number of the previous version
	 */
	public void retrievePreviousVersion() {
		int version;
		if (currentVersion > 0) {
			version = currentVersion - 1;
			currentVersion--;
		} else {
			version = 0;
		}
		retrieveConfiguration(version);

	}

	/**
	 * This retrieves the number of the next version
	 */
	public void retrieveNextVersion() {
		int version;
		if (currentVersion < list.size() - 1) {
			version = currentVersion + 1;
			currentVersion++;
		} else {
			version = list.size() - 1;
		}
		retrieveConfiguration(version);

	}

	/**
	 * This sets the complex elements using the retrieved version number
	 * 
	 * @param version version you want to retrieve
	 */
	public void retrieveConfiguration(int version) {

		Double[] previousConfiguration = list.get(version);
		setMinCoordinates(previousConfiguration[0], previousConfiguration[1]);
		setMaxCoordinates(previousConfiguration[2], previousConfiguration[3]);
		setIterations(previousConfiguration[4].intValue());
		this.counter = previousConfiguration[5].intValue();
		this.revalidate();
		this.repaint();
	}

	public void saveList() throws IOException {
		Double[] tempConfiguration = { minReal, minImaginary, maxReal, maxImaginary, (double) maxIterations,
				(double) counter, (double) currentVersion };
		list.add(tempConfiguration);
		JFileChooser f = new JFileChooser();

		
		FileNameExtensionFilter filter = new FileNameExtensionFilter("List Files (.list)", "list");
		f.setFileFilter(filter);
		f.setAcceptAllFileFilterUsed(false);
		f.addChoosableFileFilter(filter);
		int returnVal = f.showSaveDialog(null);
		if(returnVal==JFileChooser.APPROVE_OPTION)
		{
			String filename= f.getSelectedFile().toString();
			File file = new File(filename);
			if(!filename.contains(".list"))
				file=new File(filename+".list");
			
		FileOutputStream fout = new FileOutputStream(file);
		ObjectOutputStream oos = new ObjectOutputStream(fout);
		oos.writeObject(list);
		fout.close();
		oos.close();
		}
		list.remove(list.size() - 1);
		
	}

	@SuppressWarnings("unchecked")
	public void loadList() throws IOException, ClassNotFoundException {
		JFileChooser fc = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("List Files (.list)", "list");
		fc.setFileFilter(filter);
		fc.setAcceptAllFileFilterUsed(false);
		fc.addChoosableFileFilter(filter);
		int returnVal = fc.showOpenDialog(null);
		FileInputStream fin;
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			fin = new FileInputStream(fc.getSelectedFile());
			ObjectInputStream oos = new ObjectInputStream(fin);
			list.clear();
			list = (List<Double[]>) oos.readObject();
			int currentVersion = list.get(list.size() - 1)[6].intValue();
			list.remove(list.size() - 1);
			retrieveConfiguration(currentVersion);
			fin.close();
			oos.close();
		}
	}
	public void saveImage()
	{
		JFileChooser f = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("PNG Files (.png)", "png");
		f.setFileFilter(filter);
		f.setAcceptAllFileFilterUsed(false);
		f.addChoosableFileFilter(filter);
		int returnVal = f.showSaveDialog(null);
		if(returnVal==JFileChooser.APPROVE_OPTION)
		{
			String filename= f.getSelectedFile().toString();
			File file = new File(filename);
			if(!filename.contains(".png"))
				file=new File(filename+".png");
		
		BufferedImage bi = new BufferedImage(this.getSize().width, this.getSize().height, BufferedImage.TYPE_INT_ARGB); 
		Graphics g = bi.createGraphics();
		this.paint(g);
		g.dispose();
		try{
			ImageIO.write(bi,"png",file);
			}
		catch (Exception e) {
			System.out.println("Image save error: " + e.getMessage());
		
		}
		}
	}

}
