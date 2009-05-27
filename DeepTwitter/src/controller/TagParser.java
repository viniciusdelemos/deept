package controller;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TagParser{ 

	private String regex = null;
	private String source = null;
	private String key = null;
	private Pattern pattern = null; 
	private Matcher matcher = null;

	public TagParser(String source, String key){
		this.source = source;
		this.key = key;
		this.regex = "(?i)\\W"+key+"\\W";
		this.pattern = Pattern.compile(regex); 
		this.matcher = pattern.matcher(source);
	}

	public Boolean hasTag(){
		return matcher.find();
	}

	public String getFormatedText(String startTag, String endTag){
		StringBuffer sb = new StringBuffer();
		matcher.reset();
		while (matcher.find()) {
			matcher.appendReplacement(sb, startTag+source.substring(matcher.start(),matcher.end())+endTag);
		}
		matcher.appendTail(sb);
		return sb.toString();
	}

	public static void main(String[] args) {
		TagParser TagParser = new TagParser("lero lero Cu lero lero cU. lero", "cu");

		System.out.println(TagParser.hasTag());

		System.out.println(TagParser.getFormatedText("<b>", "</b>"));		
	}
} 