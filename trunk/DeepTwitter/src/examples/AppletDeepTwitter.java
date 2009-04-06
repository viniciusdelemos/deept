package examples;

import javax.swing.JSplitPane;
import model.GraphicManager;
import prefuse.Display;
import prefuse.util.force.ForceSimulator;
import prefuse.util.ui.JForcePanel;
import prefuse.util.ui.JPrefuseApplet;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

public class AppletDeepTwitter extends JPrefuseApplet {	
	private static GraphicManager gManager = null;
	public static Twitter twitter;
	
	public void init() {
		String username = "guilhermerotta";
        String password = "g260405r"; //se a senha for "", o user nao esta logado   
        boolean isTwitterUser = true;
        
        if(password.equals("")) isTwitterUser = false;
        
        try{
        	twitter = new Twitter(username,password);         	
        	gManager = new GraphicManager(isTwitterUser);
        	User mainUser = twitter.getUserDetail(username);
        	gManager.addNode(mainUser);
        }
        catch(TwitterException te)
        {
        	te.printStackTrace();
        }
		
//        JFrame frame = new JFrame("DeepTwitter");
//        frame.getContentPane().add(createMainMenu(gManager));
//        frame.pack();
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setVisible(true);        
        this.getContentPane().add(createMainMenu(gManager));
	}
	
	private static JSplitPane createMainMenu(GraphicManager g)
    {
    	ForceSimulator fsim = gManager.getForceSimulator();
        JForcePanel fpanel = new JForcePanel(fsim);
        
        JSplitPane split = new JSplitPane();
        split.setLeftComponent(fpanel);
        split.setRightComponent((Display)g);
        split.setOneTouchExpandable(true);
        split.setContinuousLayout(false);
        split.setDividerLocation(300);
        
        return split;
    }

}
