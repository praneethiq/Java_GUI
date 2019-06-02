package com.gui.delegate;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.JToolBar;

public class MandelbrotGenerator extends JFrame {
	/**
	 * This class helps create the JFrame in which all the GUI elements and
	 * functionalities are placed
	 */
	private static final long serialVersionUID = 1L;
	// computer pane configuration
	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	int xResolution=screenSize.width;
	int yResolution=screenSize.height;

	// Swing elements
	MandelbrotPanel panel = new MandelbrotPanel(xResolution, yResolution);
	JToolBar toolbar;
	JRadioButton radioButton1, radioButton2, magniRadioButton1, magniRadioButton2;
	JLabel iterationLabel, actionLabel, magnificationLabel;
	JTextField iterationText, magnificationText;

	JButton iterationButton, resetButton, undoButton, redoButton, colorButton, saveButton, loadButton;

	public MandelbrotGenerator() {
		super("Mandelbrot Set");
		displayMandelbrot();
	}

	/**
	 * This method displays the whole frame along with the toolbar and panel
	 */
	public void displayMandelbrot() {
		
		this.setSize(xResolution, yResolution);
		this.setLayout(new BorderLayout());
		configureMenuBar();
		configureToolbar();
		panel.configureMandelbrotPanel();
		Container contentPane = this.getContentPane();
		contentPane.add(panel, BorderLayout.CENTER);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}

	private void configureMenuBar() {
		JMenuBar menubar = new JMenuBar();
		JMenu menu =new JMenu("File");
		menu.setMnemonic(KeyEvent.VK_F);
		JMenuItem save =new JMenuItem("Save");
		save.setMnemonic(KeyEvent.VK_S);
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					panel.saveList();
				} catch (IOException e1) {
					System.out.println("I/O error");
					e1.printStackTrace();
				}
			}
		});
		JMenuItem saveImage =new JMenuItem("Save As Image");
		saveImage.setMnemonic(KeyEvent.VK_I);
		saveImage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
					panel.saveImage();
			}
		});
		JMenuItem load =new JMenuItem("Load");
		load.setMnemonic(KeyEvent.VK_L);
		load.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					panel.loadList();
				} catch (IOException e1) {
					System.out.println("I/O error");
					e1.printStackTrace();
				} catch (ClassNotFoundException e1) {
					System.out.println("Class error");
					e1.printStackTrace();
				}
			}
		});
		menu.add(save);
		menu.add(saveImage);
		menu.add(load);
		menubar.add(menu);
		this.setJMenuBar(menubar);
	}

	/**
	 * This method creates and configures the toolbar and its elements
	 */
	public void configureToolbar() {
		// create the toolbar with a flow layout
		toolbar = new JToolBar();
		toolbar.setLayout(new FlowLayout());
		// add and configure all the elements of toolbar
		configureResetButton();
		toolbar.addSeparator();
		configureRadioButtons();
		toolbar.addSeparator();
		configureIterationField();
		toolbar.addSeparator();
		configureColorField();
		toolbar.addSeparator();
		configureUndoRedoActions();
		toolbar.addSeparator();
		configureMagnificationAction();
		// add the toolbar to the JFrame
		Container contentPane = this.getContentPane();
		contentPane.add(toolbar, BorderLayout.NORTH);
		toolbar.setBounds(0, 0, xResolution, 50);

	}

	/**
	 * This method configures the magnification toggle to hide/show the
	 * magnification
	 */
	private void configureMagnificationAction() {
		magnificationLabel = new JLabel("Magnification :");
		magniRadioButton1 = new JRadioButton("Yes");
		magniRadioButton1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				panel.magStatus = true;
				reConfigureMandelbrotSet();
			}
		});
		magniRadioButton2 = new JRadioButton("No");
		magniRadioButton2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				panel.magStatus = false;
				reConfigureMandelbrotSet();
			}
		});
		ButtonGroup buttonGroup1 = new ButtonGroup();
		buttonGroup1.add(magniRadioButton1);
		buttonGroup1.add(magniRadioButton2);
		toolbar.add(magnificationLabel);
		toolbar.add(magniRadioButton1);
		toolbar.add(magniRadioButton2);

	}

	/**
	 * This method contains the functionality of the reset button to initialize the
	 * graphic
	 */
	private void configureResetButton() {
		resetButton = new JButton("Reset");
		resetButton.setSize(30, 40);
		resetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				panel.initialiseConfiguration();
				reConfigureMandelbrotSet();
			}
		});
		toolbar.add(resetButton);
	}

	/**
	 * This method contains the functionality to choose between zoom and pan
	 */
	private void configureRadioButtons() {
		actionLabel = new JLabel("Select an Action:");
		radioButton1 = new JRadioButton("Zoom");
		radioButton1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				panel.zoomStatus = true;
				panel.panStatus = false;
			}
		});
		radioButton2 = new JRadioButton("Pan");
		radioButton2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				panel.zoomStatus = false;
				panel.panStatus = true;
			}
		});
		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(radioButton1);
		buttonGroup.add(radioButton2);
		toolbar.add(actionLabel);
		toolbar.add(radioButton1);
		toolbar.add(radioButton2);
	}

	/**
	 * This method contains the functionality to update the max iterations
	 */
	private void configureIterationField() {

		iterationLabel = new JLabel("Enter iterations:");
		toolbar.add(iterationLabel);

		iterationText = new JTextField();
		iterationText.setSize(50, 40);
		iterationText.setText(Integer.toString(panel.maxIterations));
		toolbar.add(iterationText);

		iterationButton = new JButton("Set");
		iterationButton.setSize(30, 40);
		iterationButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				panel.setIterations(Integer.parseInt(iterationText.getText()));
				panel.addCurrentConfiguration();
				reConfigureMandelbrotSet();
			}
		});
		toolbar.add(iterationButton);
		
	}

	/**
	 * This configures the change color button functionality
	 */
	private void configureColorField() {
		colorButton = new JButton("Change Color");
		colorButton.setSize(30, 40);
		colorButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				panel.counter++;
				panel.addCurrentConfiguration();
				reConfigureMandelbrotSet();
			}
		});
		toolbar.add(colorButton);
	}

	/**
	 * This method handles the undo and redo actions
	 */
	private void configureUndoRedoActions() {
		undoButton = new JButton("Undo");
		undoButton.setSize(30, 40);
		undoButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				panel.retrievePreviousVersion();
				reConfigureMandelbrotSet();
			}
		});
		redoButton = new JButton("Redo");
		redoButton.setSize(30, 40);
		redoButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				panel.retrieveNextVersion();
				reConfigureMandelbrotSet();
			}
		});
		toolbar.add(undoButton);
		toolbar.add(redoButton);
	}

	/**
	 * This method is used to repaint the GUI
	 */
	private void reConfigureMandelbrotSet() {

		panel.revalidate();
		panel.repaint();
		iterationText.setText(Integer.toString(panel.maxIterations));

	}
}
