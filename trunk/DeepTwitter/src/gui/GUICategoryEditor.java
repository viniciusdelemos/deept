package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import gui.visualizations.CategoryEditorView;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import controller.CategoryManager;

import prefuse.Visualization;
import prefuse.controls.ControlAdapter;
import prefuse.data.Node;
import prefuse.util.FontLib;
import prefuse.util.ui.JFastLabel;
import prefuse.visual.VisualItem;

public class GUICategoryEditor {

	private static CategoryEditorView categoryEditor;
	private static JFrame frame;
	private static CategoryManager cManager = CategoryManager.getInstance();

	private GUICategoryEditor() {

	}
	
	public static void main(String args[]){
		
		GUICategoryEditor.openFrame();
		
	}

	public static void openFrame() {

		if (frame == null) {
			try{ 
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			Color BACKGROUND = Color.WHITE;
			Color FOREGROUND = Color.BLACK;
			categoryEditor = new CategoryEditorView();
			categoryEditor.setBackground(BACKGROUND);
			categoryEditor.setForeground(FOREGROUND);

			// create a search panel for the tree map
			/*
			 * JSearchPanel search = new JSearchPanel(categoryEdit
			 * .getVisualization(), treeNodes, Visualization.SEARCH_ITEMS,
			 * m_label, true, true); search.setShowResultCount(true);
			 * search.setBorder(BorderFactory.createEmptyBorder(5, 5, 4, 0));
			 * search.setFont(FontLib.getFont("Tahoma", Font.PLAIN, 11));
			 * search.setBackground(BACKGROUND);
			 * search.setForeground(FOREGROUND);
			 */

			final JFastLabel title = new JFastLabel("                 ");
			title.setPreferredSize(new Dimension(350, 20));
			title.setVerticalAlignment(SwingConstants.BOTTOM);
			title.setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 0));
			title.setFont(FontLib.getFont("Tahoma", Font.PLAIN, 16));
			title.setBackground(BACKGROUND);
			title.setForeground(FOREGROUND);

			categoryEditor.addControlListener(new ControlAdapter() {
				public void itemEntered(VisualItem item, MouseEvent e) {
					if (item.canGetString(categoryEditor.c_label()))
						title.setText(item.getString(categoryEditor.c_label()));
				}

				public void itemExited(VisualItem item, MouseEvent e) {
					title.setText(null);
				}
			});

			Box box = new Box(BoxLayout.X_AXIS);
			box.add(Box.createHorizontalStrut(10));
			box.add(title);
			box.add(Box.createHorizontalGlue());
			// box.add(search);
			box.add(Box.createHorizontalStrut(3));
			box.setBackground(BACKGROUND);

			JButton save = new JButton("Save");
			JButton exit = new JButton("Exit");
			save.setBackground(BACKGROUND);
			save.setForeground(FOREGROUND);
			exit.setBackground(BACKGROUND);
			exit.setForeground(FOREGROUND);

			save.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					categoryEditor.setEdited(false);
					// edited = false;
					saveCategories();
					JOptionPane.showMessageDialog(null,
							"As categorias foram salvas com sucesso.",
							"Gerenciador de Categorias",
							JOptionPane.INFORMATION_MESSAGE);
				}
			});

			exit.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					close();
				}
			});

			JPanel panelButtons = new JPanel(new GridLayout(1, 2));
			panelButtons.add(save);
			panelButtons.add(exit);
			panelButtons.setBackground(BACKGROUND);
			panelButtons.setForeground(FOREGROUND);

			JPanel panel2 = new JPanel(new BorderLayout());
			panel2.add(box, BorderLayout.CENTER);
			panel2.add(panelButtons, BorderLayout.EAST);
			panel2.setBackground(BACKGROUND);
			panel2.setForeground(FOREGROUND);

			JPanel panel = new JPanel(new BorderLayout());
			panel.setBackground(BACKGROUND);
			panel.setForeground(FOREGROUND);
			panel.add(categoryEditor, BorderLayout.CENTER);
			panel.add(panel2, BorderLayout.SOUTH);
			panel.setBackground(BACKGROUND);
			panel.setForeground(FOREGROUND);
			
			

			frame = new JFrame("DeepTwitter - Management Categories");
			frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			frame.setContentPane(panel);
			frame.pack();

			frame.addWindowListener(new WindowListener() {

				@Override
				public void windowActivated(WindowEvent e) {
					// TODO Auto-generated method stub

				}

				@Override
				public void windowClosed(WindowEvent e) {

				}

				@Override
				public void windowClosing(WindowEvent e) {
					close();

				}

				@Override
				public void windowDeactivated(WindowEvent e) {
					// TODO Auto-generated method stub

				}

				@Override
				public void windowDeiconified(WindowEvent e) {
					// TODO Auto-generated method stub

				}

				@Override
				public void windowIconified(WindowEvent e) {
					// TODO Auto-generated method stub

				}

				@Override
				public void windowOpened(WindowEvent e) {
					// TODO Auto-generated method stub

				}

			});
			System.out.println("Instanciou nova tela");

		}
		////categoryEditor.getTreeLayout().setLayoutAnchor(new Point2D.Double(300, 300));
		// treeLayout.setLayoutAnchor(new Point2D.Double(150, 300));
		frame.setVisible(true);
		frame.setLocationRelativeTo(null);
		////categoryEditor.getTreeLayout().setLayoutAnchor(new Point2D.Double(-200, -25));
		// treeLayout.setLayoutAnchor(new Point2D.Double(-200, -25));

	}

	private static void close() {

		if (categoryEditor.isEdited() == true) {
			String message = "Você editou as categorias e não as salvou. Tem certeza que deseja sair?";

			Object[] options = { "Sim", "Não" };
			int r = JOptionPane.showOptionDialog(frame, message,
					"Gerenciador de Categorias", JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE, null, options, options[1]);

			if (r != JOptionPane.YES_OPTION)
				return;

		}

		categoryEditor.setEdited(false);
		// edited = false;
		frame.setVisible(false);
		categoryEditor.getVisualization().cancel("filter");
		categoryEditor.reset();
		categoryEditor.setVisualization(new Visualization());
		categoryEditor = null;
		frame.dispose();
		frame = null;

	}

	private static void saveCategories() {

		cManager.removeAllCategoriesAndWords();
		cManager.getUsedCategories().clear();

		Iterator c = categoryEditor.getTree().getRoot().children();
		while (c.hasNext()) {
			Node category = (Node) c.next();

			List<String> words = new ArrayList<String>();

			Iterator w = category.children();
			while (w.hasNext()) {
				Node word = (Node) w.next();

				words.add(word.getString(categoryEditor.c_label()));
			}

			cManager.addCategory(category.getString(categoryEditor.c_label()),
					words);
		}

		// Salva categorias em arquivo xml
		cManager.saveCategories();

	}

}
