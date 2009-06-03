package gui.visualizations;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

import model.AggregateLayout;
import model.WeightedForceDirectedLayout;

import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.ColorAction;
import prefuse.action.layout.RandomLayout;
import prefuse.action.layout.graph.ForceDirectedLayout;
import prefuse.activity.Activity;
import prefuse.activity.ActivityAdapter;
import prefuse.activity.ActivityListener;
import prefuse.controls.ControlAdapter;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.data.Schema;
import prefuse.data.tuple.TupleSet;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.LabelRenderer;
import prefuse.util.ColorLib;
import prefuse.util.PrefuseLib;
import prefuse.util.force.DragForce;
import prefuse.util.force.ForceItem;
import prefuse.util.force.ForceSimulator;
import prefuse.util.force.NBodyForce;
import prefuse.util.force.SpringForce;
import prefuse.visual.VisualGraph;
import prefuse.visual.VisualItem;
import twitter4j.User;

public class MostActiveUsers extends Display{
	
	public final static String GRAPH = "data";

	private Graph g;
	private LabelRenderer nodeRenderer;

	private List<Node> nodes = new ArrayList<Node>();

	private JPanel panelButtonsMessages = new JPanel();

	private JPanel panelGlobal;
	private JFrame frame;

	public MostActiveUsers() {
		super(new Visualization());

		g = new Graph(true);
		g.addColumn("userName", String.class);
		g.addColumn("image", String.class);
		g.addColumn("user", User.class);
		g.addColumn("sizes", Sizes.class);

		// add visual data groups
		VisualGraph vg = m_vis.addGraph(GRAPH, g);

		nodeRenderer = new LabelRenderer(null, "image");
		nodeRenderer.setTextField(null);
		nodeRenderer.setVerticalAlignment(Constants.BOTTOM);
		nodeRenderer.setHorizontalPadding(0);
		nodeRenderer.setVerticalPadding(0);
		nodeRenderer.setMaxImageDimensions(100, 100);

		m_vis.setRendererFactory(new DefaultRendererFactory(nodeRenderer));

//		ActionList update = new ActionList();
//		// update.add(new DataMountainSizeAction());
//		update.add(new ColorAction("data", VisualItem.STROKECOLOR) {
//			public int getColor(VisualItem item) {
//				return ColorLib.rgb((item.isHover() ? 255 : 0), 0, 0);
//			}
//		});
//		update.add(new RepaintAction());
//		m_vis.putAction("update", update);
//
//		// we run this to make sure the forces are stabilized
//		ActionList preforce = new ActionList(Activity.INFINITY);
//		preforce.add(new MostActiveUsersForceLayout(true));
//		preforce.add(new RepaintAction());
//		m_vis.putAction("preforce", preforce);
//
//		// this will cause docs to move out of the way when dragging
//		final ForceDirectedLayout fl = new MostActiveUsersForceLayout(false);
//		ActivityListener fReset = new ActivityAdapter() {
//			public void activityCancelled(Activity a) {
//				fl.reset();
//			}
//		};
//		ActionList forces = new ActionList(Activity.INFINITY);
//		forces.add(fl);
//		forces.add(update);
//		forces.addActivityListener(fReset);
//		m_vis.putAction("forces", forces);
		
		ForceDirectedLayout fdl = new ForceDirectedLayout(GRAPH,getForceSimulator(),true);
		
		ActionList layout = new ActionList(ActionList.INFINITY); 
    	layout.add(fdl);
    	layout.add(new RepaintAction());
    	m_vis.putAction("layout", layout);    	

		setSize(800, 600);
		setDamageRedraw(false); // disable due to Java2D image clipping errors
		setBorder(BorderFactory.createEmptyBorder(30, 20, 5, 20));
		// setItemSorter(new DataMountainSorter());
		addControlListener(new MostActiveUsersControl());

		// pre-load images, otherwise they will be loaded asynchronously
		nodeRenderer.getImageFactory().preloadImages(m_vis.items(), "image");

		// initialize and present the interface
		//// m_vis.run("init");
//		m_vis.runAfter("preforce", "update");
//		m_vis.run("preforce");
		m_vis.run("layout");

		panelGlobal = new JPanel();
		panelGlobal.setLayout(new java.awt.BorderLayout());

		panelGlobal.add(this, BorderLayout.CENTER);

		frame = new JFrame("DeepTwitter - Usuários mais ativos");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().add(panelGlobal);
		frame.pack();
	}
	
	public ForceSimulator getForceSimulator() {		
		
		ForceSimulator	forceSimulator = new ForceSimulator();

    	float gravConstant = -0.4f;
    	float minDistance = -0.01f;
    	float theta = NBodyForce.DEFAULT_THETA;
    	
    	//float drag = 0.007f;
    	float springCoeff = 1e-5f;
    	float defaultLength = 0f;    	
    	
    	forceSimulator.addForce(new NBodyForce(gravConstant, minDistance, theta));
    	forceSimulator.addForce(new DragForce());
    	forceSimulator.addForce(new SpringForce(springCoeff, defaultLength));
		
		return forceSimulator;
		
//        fsim.addForce(new NBodyForce(-0.4f, 25f, NBodyForce.DEFAULT_THETA));
//        fsim.addForce(new SpringForce(1e-5f,0f));
//        fsim.addForce(new DragForce());
    }

	public void addNode(User u) {
		synchronized (m_vis) {

			Node n = g.addNode();

			n.set("image", u.getProfileImageURL().toString());
			n.set("user", u);
			n.set("userName", u.getScreenName());

			VisualItem vi = getVisualization().getVisualItem(GRAPH, n);

			int distMin = 30;

			double x = distMin
					+ (Math.random() * (this.size().width - (distMin * 2)));
			double y = distMin
					+ (Math.random() * (this.size().height - (distMin * 2)));

			vi.setStartX(0);
			vi.setStartY(0);
			vi.setX(x);
			vi.setY(y);
			vi.setEndX(x);
			vi.setEndY(y);

			System.out.println(u.getScreenName());

			nodes.add(n);
			m_vis.notifyAll();
		}
	}

	public void removeAllNodes() {
		for (Node n : nodes) {
			g.removeNode(n);
		}
	}
	
	
	
	private JLabel labelMessages = new JLabel();
	
	private void changeMessageUser(User u){
		
		labelMessages.setText("<html>"
				+ "<div style=\"text-align: left;\"><small>" + u.getStatusText() + "<br>"
				+ "<span style=\"font-style: italic;\">" + u.getName() + "</span><br>"
				+ "</small></div><br></body></html>");
		
		panelGlobal.updateUI();
		
		
	}
	
	private enum TypeSize{
		followers,
		friends,
		favourites,
		tweets
	}
	
	private void changeSize(TypeSize typeSize){
		
		for(Node n : nodes){
			
			Sizes sizes = (Sizes)n.get("sizes");
			VisualItem v = getVisualization().getVisualItem(GRAPH, n);
			
			if(typeSize == TypeSize.followers)
				v.setSize(sizes.followersSize);
			else if(typeSize == TypeSize.friends)
				v.setSize(sizes.friendsSize);
			else if(typeSize == TypeSize.favourites)
				v.setSize(sizes.favouritesSize);
			else if(typeSize == TypeSize.tweets)
				v.setSize(sizes.statusesSize);
			else
				v.setSize(sizes.proportionalSize);
		}
		panelGlobal.updateUI();
	}
	
	private class UpdateMessage extends Thread{
		
		
		public void run(){
			
			while(true){
				
				for(Node n : nodes){
					
					changeMessageUser((User)n.get("user"));
					
					try{
					Thread.sleep(1500);
					} catch(InterruptedException e){
						
					}
				}
				
			}
		}
	}
	

	private void addButtonsMessages() {

		panelButtonsMessages.setLayout(new java.awt.BorderLayout());

		labelMessages.setText("<html>"
				+ "<div style=\"text-align: left;\"><small>" + "" + "<br>"
				+ "<span style=\"font-style: italic;\">" + "" + "</span><br>"
				+ "</small></div><br></body></html>");
		
		final JPanel panelButtons = new JPanel();
		panelButtons.setLayout(new java.awt.GridLayout(2, 2));

		final JButton amigos = new JButton();
		final JButton seguidores = new JButton();
		final JButton favoritos = new JButton();
		final JButton tweets = new JButton();

		amigos.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				amigos.setBackground(new java.awt.Color(66, 249, 225));
				amigos.setIcon(new javax.swing.ImageIcon("img/amigosSel.png"));
				
				seguidores.setBackground(new java.awt.Color(255, 255, 255));
				seguidores.setIcon(new javax.swing.ImageIcon("img/seguidores.png"));
				
				favoritos.setBackground(new java.awt.Color(255, 255, 255));
				favoritos.setIcon(new javax.swing.ImageIcon("img/favoritos.png"));
				
				tweets.setBackground(new java.awt.Color(255, 255, 255));
				tweets.setIcon(new javax.swing.ImageIcon("img/tweets.png"));
				
				panelButtons.updateUI();
				
				changeSize(TypeSize.friends);


			}

		});

		seguidores.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				amigos.setBackground(new java.awt.Color(255, 255, 225));
				amigos.setIcon(new javax.swing.ImageIcon("img/amigos.png"));
				
				seguidores.setBackground(new java.awt.Color(66, 249, 225));
				seguidores.setIcon(new javax.swing.ImageIcon("img/seguidoresSel.png"));
				
				favoritos.setBackground(new java.awt.Color(255, 255, 255));
				favoritos.setIcon(new javax.swing.ImageIcon("img/favoritos.png"));
				
				tweets.setBackground(new java.awt.Color(255, 255, 255));
				tweets.setIcon(new javax.swing.ImageIcon("img/tweets.png"));
				
				panelButtons.updateUI();
				
				changeSize(TypeSize.followers);

			}

		});

		favoritos.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				amigos.setBackground(new java.awt.Color(255, 255, 225));
				amigos.setIcon(new javax.swing.ImageIcon("img/amigos.png"));
				
				seguidores.setBackground(new java.awt.Color(255, 255, 225));
				seguidores.setIcon(new javax.swing.ImageIcon("img/seguidores.png"));
				
				favoritos.setBackground(new java.awt.Color(66, 249, 225));
				favoritos.setIcon(new javax.swing.ImageIcon("img/favoritosSel.png"));
				
				tweets.setBackground(new java.awt.Color(255, 255, 255));
				tweets.setIcon(new javax.swing.ImageIcon("img/tweets.png"));
				
				panelButtons.updateUI();
				
				changeSize(TypeSize.favourites);

			}

		});

		tweets.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				amigos.setBackground(new java.awt.Color(255, 255, 225));
				amigos.setIcon(new javax.swing.ImageIcon("img/amigos.png"));
				
				seguidores.setBackground(new java.awt.Color(255, 255, 225));
				seguidores.setIcon(new javax.swing.ImageIcon("img/seguidores.png"));
				
				favoritos.setBackground(new java.awt.Color(255, 255, 225));
				favoritos.setIcon(new javax.swing.ImageIcon("img/favoritos.png"));
				
				tweets.setBackground(new java.awt.Color(66, 249, 225));
				tweets.setIcon(new javax.swing.ImageIcon("img/tweetsSel.png"));
				
				panelButtons.updateUI();
				
				changeSize(TypeSize.tweets);

			}

		});

		amigos.setBackground(new java.awt.Color(255, 255, 255));
		amigos.setIcon(new javax.swing.ImageIcon("img/amigos.png"));
		amigos.setToolTipText("Amigos");
		amigos.setSize(97, 24);
		amigos.setAlignmentX(CENTER_ALIGNMENT);
		amigos.setAlignmentY(CENTER_ALIGNMENT);

		seguidores.setBackground(new java.awt.Color(255, 255, 255));
		seguidores.setIcon(new javax.swing.ImageIcon("img/seguidores.png"));
		seguidores.setToolTipText("Seguidores");
		seguidores.setSize(97, 24);
		seguidores.setAlignmentX(CENTER_ALIGNMENT);
		seguidores.setAlignmentY(CENTER_ALIGNMENT);

		favoritos.setBackground(new java.awt.Color(255, 255, 255));
		favoritos.setIcon(new javax.swing.ImageIcon("img/favoritos.png"));
		favoritos.setToolTipText("Favoritos");
		favoritos.setSize(97, 24);
		favoritos.setAlignmentX(CENTER_ALIGNMENT);
		favoritos.setAlignmentY(CENTER_ALIGNMENT);

		tweets.setBackground(new java.awt.Color(255, 255, 255));
		tweets.setIcon(new javax.swing.ImageIcon("img/tweets.png"));
		tweets.setToolTipText("Tweets");
		tweets.setSize(97, 24);
		tweets.setAlignmentX(CENTER_ALIGNMENT);
		tweets.setAlignmentY(CENTER_ALIGNMENT);

		panelButtons.add(tweets);
		panelButtons.add(favoritos);
		panelButtons.add(amigos);
		panelButtons.add(seguidores);

		panelButtonsMessages.add(labelMessages, BorderLayout.CENTER);
		panelButtonsMessages.add(panelButtons, BorderLayout.EAST);

		panelGlobal.add(panelButtonsMessages, BorderLayout.SOUTH);

	}

	public void openFrame() {
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	public void closeFrame() {
		frame.setVisible(false);
	}

	public void calculateSizes() {
		CalculatingSizes calculatingSizes = new CalculatingSizes();
		calculatingSizes.start();
	}

	private class CalculatingSizes extends Thread {

		List<ToCalculateSizes> followersCount = new ArrayList<ToCalculateSizes>();
		List<ToCalculateSizes> friendsCount = new ArrayList<ToCalculateSizes>();
		List<ToCalculateSizes> favouritesCount = new ArrayList<ToCalculateSizes>();
		List<ToCalculateSizes> statusesCount = new ArrayList<ToCalculateSizes>();

		public void run() {

			ProgressBar progressBar = new ProgressBar();
			panelGlobal.add(progressBar, BorderLayout.SOUTH);

			for (int i = 0; i < nodes.size(); i++) {
				User user = (User) nodes.get(i).get("user");
				ToCalculateSizes followers = new ToCalculateSizes(user
						.getFollowersCount(), user);
				ToCalculateSizes friends = new ToCalculateSizes(user
						.getFriendsCount(), user);
				ToCalculateSizes favourites = new ToCalculateSizes(user
						.getFavouritesCount(), user);
				ToCalculateSizes statuses = new ToCalculateSizes(user
						.getStatusesCount(), user);

				followersCount.add(followers);
				friendsCount.add(friends);
				favouritesCount.add(favourites);
				statusesCount.add(statuses);

			}

			// Coloca na ordem
			Collections.sort(followersCount);
			Collections.sort(friendsCount);
			Collections.sort(favouritesCount);
			Collections.sort(statusesCount);

			// Calcula tamanhos
			calculate(followersCount);
			calculate(friendsCount);
			calculate(favouritesCount);
			calculate(statusesCount);

			// Seta tamanho de todas fotos para tamanho proporcional
			this.setSizesTo48x48();

			this.printSizes();

			this.setSizes();

			this.printFinal();

			panelGlobal.remove(progressBar);
			panelGlobal.updateUI();
			
			addButtonsMessages();
			panelGlobal.add(panelButtonsMessages, BorderLayout.SOUTH);
			panelGlobal.updateUI();
			
			UpdateMessage updateMessage = new UpdateMessage();
			updateMessage.start();
		}

		// Atualiza screen com ganbiarra

		private void updateScreen() {

			boolean goodUser = true;

			int times = 0;

			while (goodUser) {

				int selected = (int) Math.random() * nodes.size();

				VisualItem v = null;
				v = getVisualization()
						.getVisualItem(GRAPH, nodes.get(selected));

				if (v.getBounds().getWidth() == 0
						|| v.getBounds().getHeight() == 0) {

					getVisualization().repaint();

					times++;

					if (times % 80 == 0)
						System.out.println("Rodou " + times
								+ "vezes e nao deu certo");

					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
					}

					continue;
				}
				times = 0;
				System.out.println("Sorteu cara bom");

				goodUser = false;

				Visualization vis = v.getVisualization();

				try {
					vis.run("forces");
					// panelGlobal.updateUI();
					updateUI();
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {

				}

				try {
					vis.cancel("forces");
				} catch (Exception e) {
					e.printStackTrace();
				}

				System.out.println("Im here 5");
			}
		}

		// * Coloca todas fotos do mesmo tamanho 48 X 48
		// * respeitando proporções

		private void setSizesTo48x48() {

			// synchronized (m_vis) {

			for (int i = 0; i < nodes.size(); i++) {

				VisualItem v = null;
				v = getVisualization().getVisualItem(GRAPH, nodes.get(i));

				boolean isUnvalidSize = true;

				double width = -1;
				double height = -1;

				User user = (User) nodes.get(i).get("user");

				while (isUnvalidSize) {

					width = v.getBounds().getWidth();
					height = v.getBounds().getHeight();

					if (width > 0 && height > 0) {
						isUnvalidSize = false;

					} else {
						System.out.println("Nao deu: " + user.getName() + " w:"
								+ width + " h:" + height);
						updateUI();
						System.out.println("Vai rodar ganbiarra");
						updateScreen();
						System.out.println("Terminou de rodar ganbiarra");
						updateUI();
						// panelGlobal.updateUI();

						System.out.println("\tDepois Nao deu: "
								+ user.getName() + " w:" + width + " h:"
								+ height);

					}
				}

				double size = v.getSize();
				double newSize = 1;

				if (width != 48 || height != 48) {

					if (width == height) {

						newSize = 48 / width;
						v.setSize(newSize);

						Sizes sizes = new Sizes(newSize);
						nodes.get(i).set("sizes", sizes);

						System.out.println("\tMudou tam igual: "
								+ user.getName() + "sizeAnt:" + size
								+ "sizeDep:" + v.getSize());
						// panelGlobal.updateUI();
						updateUI();

					} else if (width > height) {

						newSize = 48 / width;
						v.setSize(newSize);

						Sizes sizes = new Sizes(newSize);
						nodes.get(i).set("sizes", sizes);

						System.out.println("\tMudou height: " + user.getName()
								+ "sizeAnt:" + size + "sizeDep:" + v.getSize());
						// panelGlobal.updateUI();
						updateUI();
					} else {
						newSize = 48 / height;
						v.setSize(newSize);

						Sizes sizes = new Sizes(newSize);
						nodes.get(i).set("sizes", sizes);

						System.out.println("\tMudou width: " + user.getName()
								+ "sizeAnt:" + size + "sizeDep:" + v.getSize());
						// panelGlobal.updateUI();
						updateUI();

					}
				} else {
					Sizes sizes = new Sizes(1);
					nodes.get(i).set("sizes", sizes);

					System.out.println("\tNao precisou mudar tamanho: "
							+ user.getName());
					// panelGlobal.updateUI();
					updateUI();
				}
			}
			// }
		}

		private void printSizes() {

			System.out.println("FollowersCount");
			for (ToCalculateSizes f : followersCount) {
				System.out.println("\t" + f.count + "\t" + f.size + "\t"
						+ f.user.getScreenName());
			}

			System.out.println("FriendsCount");
			for (ToCalculateSizes f : friendsCount) {
				System.out.println("\t" + f.count + "\t" + f.size + "\t"
						+ f.user.getScreenName());
			}

			System.out.println("FavouritesCount");
			for (ToCalculateSizes f : favouritesCount) {
				System.out.println("\t" + f.count + "\t" + f.size + "\t"
						+ f.user.getScreenName());
			}

			System.out.println("StatusesCount");
			for (ToCalculateSizes f : statusesCount) {
				System.out.println("\t" + f.count + "\t" + f.size + "\t"
						+ f.user.getScreenName());
			}
		}

		private void printFinal() {

			System.out.println("\nInicio Final\n");

			for (int i = 0; i < nodes.size(); i++) {
				User user = null;
				Sizes sizes = null;

				user = (User) nodes.get(i).get("user");
				sizes = (Sizes) nodes.get(i).get("sizes");

				System.out.println(user.getScreenName() + "\t"
						+ sizes.proportionalSize);
				System.out.println("\t\t" + sizes.followersSize);
				System.out.println("\t\t" + sizes.friendsSize);
				System.out.println("\t\t" + sizes.favouritesSize);
				System.out.println("\t\t" + sizes.statusesSize);

			}
			System.out.println("\nFim Final\n");

		}

		// * Salva tamanho de cada tipo de usuarios mais
		// * ativos no nodo

		private void setSizes() {

			synchronized (g) {

				for (int i = 0; i < nodes.size(); i++) {

					Sizes sizes = null;
					User user = null;

					sizes = (Sizes) nodes.get(i).get("sizes");
					user = (User) nodes.get(i).get("user");

					if (sizes == null)
						throw new NullPointerException(
								"Problemao, sizes == null");
					if (user == null)
						throw new NullPointerException(
								"Problemao, user == null");

					for (ToCalculateSizes f : followersCount) {
						if (f.user.getScreenName().equals(user.getScreenName())) {

							sizes.followersSize = (f.size - 1)
									+ sizes.proportionalSize;
							// g.set(i, "sizes", sizes)

							followersCount.remove(f);
							break;
						}
					}

					for (ToCalculateSizes f : friendsCount) {
						if (f.user.getScreenName().equals(user.getScreenName())) {

							sizes.friendsSize = (f.size - 1)
									+ sizes.proportionalSize;
							// g.set(i, "sizes", sizes)

							friendsCount.remove(f);
							break;
						}
					}

					for (ToCalculateSizes f : favouritesCount) {
						if (f.user.getScreenName().equals(user.getScreenName())) {

							sizes.favouritesSize = (f.size - 1)
									+ sizes.proportionalSize;
							// g.set(i, "sizes", sizes)

							favouritesCount.remove(f);
							break;
						}
					}

					for (ToCalculateSizes f : statusesCount) {
						if (f.user.getScreenName().equals(user.getScreenName())) {

							sizes.statusesSize = (f.size - 1)
									+ sizes.proportionalSize;
							// g.set(i, "sizes", sizes)

							statusesCount.remove(f);
							break;
						}
					}
				}
				followersCount = null;
				friendsCount = null;
				favouritesCount = null;
				statusesCount = null;
				updateUI();
			}
		}

		private double interpolacaoLinear(double a, double b, double fa,
				double fb, double x) {

			return (((b - x) * fa) / (b - a)) + (((x - a) * fb) / (b - a));

		}

		private void calculate(List<ToCalculateSizes> list) {

			int min = list.get(0).count;
			int max = list.get(list.size() - 1).count;

			int minSize = 1;
			int maxSize = 3;

			for (ToCalculateSizes p : list) {

				p.size = interpolacaoLinear(min, max, minSize, maxSize, p.count);

			}
		}
	}

	/**
	 * Classe auxiliar para calcular o tamanho de cada nodo para os diferentes
	 * tipos de usuários mais ativos: followersCount, friendsCount,
	 * favoritesCount, statusesCount
	 */
	class ToCalculateSizes implements Comparable<ToCalculateSizes> {

		ToCalculateSizes(int count, User user) {
			this.count = count;
			this.user = user;
		}

		int count;
		double size;
		User user;

		@Override
		public int compareTo(ToCalculateSizes o) {
			return Integer.valueOf(count).compareTo(Integer.valueOf(o.count));
		}

		@Override
		public String toString() {
			return count + "\t" + user.getName() + "\t" + user;
		}
	}

	class Sizes {

		public Sizes(double proportionalSize) {
			this.proportionalSize = proportionalSize;

		}

		double proportionalSize;

		double followersSize;
		double friendsSize;
		double favouritesSize;
		double statusesSize;

	}

	class ProgressBar extends JPanel {

		private JProgressBar pbar;
		private JLabel label;

		public ProgressBar() {

			JPanel panel = new JPanel();

			label = new JLabel();

			label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			label.setText("Calculando usuários mais ativos");

			panel.setLayout(new java.awt.GridLayout(2, 0));

			pbar = new JProgressBar();
			pbar.setIndeterminate(true);

			panel.add(label);
			panel.add(pbar);

			add(panel);
		}

		public void changeText(String text) {
			label.setText(text);
		}

	}

	private static final String ANCHORITEM = "_anchorItem";
	private static final Schema ANCHORITEM_SCHEMA = new Schema();
	static {
		ANCHORITEM_SCHEMA.addColumn(ANCHORITEM, ForceItem.class);
	}

	public class MostActiveUsersForceLayout extends ForceDirectedLayout {

		public MostActiveUsersForceLayout(boolean enforceBounds) {
			super("data", enforceBounds, false);

			ForceSimulator fsim = new ForceSimulator();
			fsim.addForce(new NBodyForce(-0.4f, 50f, NBodyForce.DEFAULT_THETA));
			fsim.addForce(new SpringForce(1e-5f, 0f));
			fsim.addForce(new DragForce());
			setForceSimulator(fsim);

			m_nodeGroup = "data";
			m_edgeGroup = null;
		}

		protected float getMassValue(VisualItem n) {
			return n.isHover() ? 5f : 1f;
		}

		public void reset() {
			Iterator iter = m_vis.visibleItems(m_nodeGroup);
			while (iter.hasNext()) {
				VisualItem item = (VisualItem) iter.next();
				ForceItem aitem = (ForceItem) item.get(ANCHORITEM);
				if (aitem != null) {
					aitem.location[0] = (float) item.getEndX();
					aitem.location[1] = (float) item.getEndY();
				}
			}
			super.reset();
		}

		protected void initSimulator(ForceSimulator fsim) {
			// make sure we have force items to work with
			TupleSet t = (TupleSet) m_vis.getGroup(m_group);
			t.addColumns(ANCHORITEM_SCHEMA);
			t.addColumns(FORCEITEM_SCHEMA);

			Iterator iter = m_vis.visibleItems(m_nodeGroup);
			while (iter.hasNext()) {
				VisualItem item = (VisualItem) iter.next();
				// get force item
				ForceItem fitem = (ForceItem) item.get(FORCEITEM);
				if (fitem == null) {
					fitem = new ForceItem();
					item.set(FORCEITEM, fitem);
				}
				fitem.location[0] = (float) item.getEndX();
				fitem.location[1] = (float) item.getEndY();
				fitem.mass = getMassValue(item);

				// get spring anchor
				ForceItem aitem = (ForceItem) item.get(ANCHORITEM);
				if (aitem == null) {
					aitem = new ForceItem();
					item.set(ANCHORITEM, aitem);
					aitem.location[0] = fitem.location[0];
					aitem.location[1] = fitem.location[1];
				}

				fsim.addItem(fitem);
				fsim.addSpring(fitem, aitem, 0);
			}
		}
	}

	public class MostActiveUsersControl extends ControlAdapter {
		private VisualItem activeItem;
		private Point2D down = new Point2D.Double();
		private Point2D tmp = new Point2D.Double();
		private boolean wasFixed, dragged;
		private boolean repaint = false;

		int count = 0;

		private String getToolTip(VisualItem item) {

			User user = (User) item.get("user");

			int m_maxWidth = 200;

			String value = null;

			String[] m_texts = { "Nome:", "Seguidores:", "Amigos:",
					"Favoritos:", "Tweets:", "Último Tweet:" };
			String[] m_fields = { user.getName(),
					String.valueOf(user.getFollowersCount()),
					String.valueOf(user.getFriendsCount()),
					String.valueOf(user.getFavouritesCount()),
					String.valueOf(user.getStatusesCount()),
					user.getStatusText() };

			StringBuffer m_sbuf = new StringBuffer();

			m_sbuf.delete(0, m_sbuf.length());

			m_sbuf.append("<html><table>");

			for (int i = 0; i < m_fields.length; i++) {
				value = m_fields[i];
				if ((value != null) && (value.length() > 0)) {
					m_sbuf.append("<tr valign='top'><td><b>");
					m_sbuf.append(m_texts[i] + "</b></td><td width="
							+ m_maxWidth + ">");
					m_sbuf.append(value);
				}
				m_sbuf.append("</td></tr>");
			}
			return m_sbuf.toString();
		}

		public void itemEntered(VisualItem item, MouseEvent e) {
			Display d = (Display) e.getSource();
			d.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			d.setToolTipText(getToolTip(item));
			activeItem = item;
			wasFixed = item.isFixed();
		}

		public void itemExited(VisualItem item, MouseEvent e) {
			if (activeItem == item) {
				activeItem = null;
				item.setFixed(wasFixed);
			}
			Display d = (Display) e.getSource();
			d.setToolTipText(null);
			d.setCursor(Cursor.getDefaultCursor());

		}

		public void itemPressed(VisualItem item, MouseEvent e) {
			if (!SwingUtilities.isLeftMouseButton(e))
				return;

			// set the focus to the current node
			Visualization vis = item.getVisualization();
			vis.getFocusGroup(Visualization.FOCUS_ITEMS).setTuple(item);

			item.setFixed(true);
			dragged = false;
			Display d = (Display) e.getComponent();
			down = d.getAbsoluteCoordinate(e.getPoint(), down);

			//vis.run("forces");

		}

		public void itemReleased(VisualItem item, MouseEvent e) {
			if (!SwingUtilities.isLeftMouseButton(e))
				return;
			if (dragged) {
				activeItem = null;
				item.setFixed(wasFixed);
				dragged = false;
			}
			// clear the focus
			Visualization vis = item.getVisualization();
			vis.getFocusGroup(Visualization.FOCUS_ITEMS).clear();

			//vis.cancel("forces");
		}

		/*
		 * public void itemClicked(VisualItem item, MouseEvent e) { if
		 * (!SwingUtilities.isLeftMouseButton(e)) return; if ( e.getClickCount()
		 * == 2 ) { String id = item.getString("id");
		 * BrowserLauncher.showDocument(URL+id); } }
		 */

		public void itemDragged(VisualItem item, MouseEvent e) {
			if (!SwingUtilities.isLeftMouseButton(e))
				return;
			dragged = true;
			Display d = (Display) e.getComponent();
			tmp = d.getAbsoluteCoordinate(e.getPoint(), tmp);
			double dx = tmp.getX() - down.getX();
			double dy = tmp.getY() - down.getY();

			PrefuseLib.setX(item, null, item.getX() + dx);
			PrefuseLib.setY(item, null, item.getY() + dy);
			down.setLocation(tmp);
			if (repaint)
				item.getVisualization().repaint();
		}
	}

}
