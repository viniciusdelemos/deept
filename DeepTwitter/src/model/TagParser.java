package model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TagParser{ 

	private String regex = null;
	private String source = null;
	private Pattern pattern = null; 
	private Matcher matcher = null;

	public TagParser(String source, String key){
		this.source = source;
		String special="���������������������������������������������������"; 		
		this.regex = "(?i)[^\\w"+special+"]"+key+"[^\\w"+special+"]";
		//this.regex = "(?i)[^\\w"+special+"]*"+key+"[^\\w"+special+"]*";
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
} 