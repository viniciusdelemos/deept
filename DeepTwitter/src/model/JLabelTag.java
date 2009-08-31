package model;

import javax.swing.JLabel;

public class JLabelTag extends JLabel{
	
	public JLabelTag(String tag){
        super(tag);
        this.name = tag;
    }

    /** Tag name */
    private String name = null;

    /** Link associated with the tag */
    private String link = null;

    /** Numerical value associated with the tag */
    private double score = 1.0;

    /** Normalized score value (from 0.0 to 1.0) */
    private double normScore = 0.0;
    
    /**
     * 0 -> from categories
     * 1 -> from trends
     */
    private int type = -1;
    public static int typeCategories = 0;
    public static int typeTrends = 1;
    public static int typeBoth = 2;
    
    

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public double getNormScore() {
		return normScore;
	}

	public void setNormScore(double normScore) {
		this.normScore = normScore;
	}
	
	public String toString(){
		return name;
	}

}
