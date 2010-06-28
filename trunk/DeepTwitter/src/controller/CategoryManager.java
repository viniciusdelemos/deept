package controller;

import java.awt.Color;
import java.awt.Paint;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.swing.JOptionPane;

import model.Category;
import model.Tag;
import model.ChartColor;
import model.TagParser;
import model.extensions.StatusesDataTable;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import prefuse.visual.VisualItem;
import sun.reflect.ReflectionFactory.GetReflectionFactoryAction;
import twitter4j.Status;
import twitter4j.Tweet;
import twitter4j.TwitterResponse;

public class CategoryManager {
	private Map<String, Category> categoriesMap;
	private TagParser tagParser;
	private int relatedResponsesCount;
	private Paint[] colorArray;
	private int colorIndex;
	private List<Category> usedCategories;

	// TODO adicionar syncrhonized em todos metodos que manipulam categoriesMap

	private CategoryManager() {
		// construtor private previne chamadas nao autorizadas ao construtor.
		categoriesMap = new HashMap<String, Category>();
		usedCategories = new ArrayList<Category>();
		relatedResponsesCount = 0;
		colorArray = ChartColor.createDefaultPaintArray();
		colorIndex = new Random().nextInt(colorArray.length);
		// loadTestCategories();
		loadCategories();
	}

	private static class SingletonHolder {
		private final static CategoryManager INSTANCE = new CategoryManager();
	}

	public static CategoryManager getInstance() {
		return SingletonHolder.INSTANCE;
	}

	public Category getCategory(String name) {
		return categoriesMap.get(name);
	}

	public Category addCategory(String name) {
		if (categoriesMap.containsKey(name))
			return categoriesMap.get(name);
		Category c = new Category(name);
		c.setPaintColor(colorArray[colorIndex]);
		categoriesMap.put(name, c);
		colorIndex++;
		if (colorIndex >= colorArray.length)
			colorIndex = 0;
		return c;
	}

	public Category addCategory(String name, List<String> words) {
		Category c = addCategory(name);
		// sobreescreve todas as palavras da categoria
		c.setTags(words);
		return c;
	}

	public boolean addWord(String category, String tag) {
		Category c = categoriesMap.get(category);
		if (c == null)
			return false;
		return c.addTag(tag);
	}

	public void addWords(String category, List<String> tags) {
		for (String s : tags) {
			addWord(category, s);
		}
	}

	public boolean removeWord(String category, String tag) {
		Category c = categoriesMap.get(category);
		if (c == null)
			return false;
		return c.removeTag(tag);
	}

	public Category removeCategory(String name) {
		return categoriesMap.remove(name);
	}

	public boolean setCategoryName(String oldName, String newName) {
		Category c = categoriesMap.get(oldName);
		if (c != null) {
			c.setName(newName);
			categoriesMap.put(newName, c);
			categoriesMap.remove(oldName);
			return true;
		}
		return false;
	}

	public boolean setCategoryName(Category c, String newName) {
		return setCategoryName(c.getName(), newName);
	}

	public List<Category> getCategories() {
		List<Category> list = new ArrayList<Category>();
		Iterator<String> i = categoriesMap.keySet().iterator();
		while (i.hasNext()) {
			list.add(categoriesMap.get(i.next()));
		}
		return list;
	}
	
	public List<Category> getUsedCategories() {
		return usedCategories;
	}

	public void removeAllCategoriesAndWords() {
		categoriesMap.clear();
	}

	/**
	 * Abre categorias do arquivo de configuracao Nao deleta dados do
	 * categoriesMap, ou seja, o ideia é categoriesMap estar vazio ou esvaziar
	 * ele antes de chamar este método
	 */
	private void loadCategories() {
		SAXBuilder sb = new SAXBuilder();
		Document d = null;

		try {
			d = sb.build(getClass().getResourceAsStream("categories.xml"));
		} catch (JDOMException ex) {
			JOptionPane.showMessageDialog(null, ex, "Problemas",
					JOptionPane.ERROR_MESSAGE);
			// TODO ver quais excecoes podem ocorrer aki
		} catch (IOException ex) {
			JOptionPane
					.showMessageDialog(
							null,
							"Não foi possível encontrar o arquivo de configuração das categorias.",
							"Problemas com configuração",
							JOptionPane.ERROR_MESSAGE);
			// TODO se nao tiver pasta, criar a pasta e arquivo, se nao tiver
			// arquivo, criar apenas ele
			// System.exit(0);
		}

		// Root element
		Element root = d.getRootElement();

		List<Element> categories = root.getChildren("Category");

		// TODO verificar possiveis problemas nas categorias, como duas palavras
		// iguais
		// em uma mesma categoria, duas categorias com o mesmo nome
		for (Element e : categories) {

			List<String> wordsList = new ArrayList<String>();

			List<Element> words = e.getChildren("Tag");
			for (Element w : words) {
				wordsList.add(w.getTextTrim());
			}
			// System.out.println(e.getAttributeValue("name")+ "\t" +
			// wordsList);
			addCategory(e.getAttributeValue("name"), wordsList);
		}

		// System.out.println("Terminou");
		// System.out.println(toString());
	}

	/**
	 * Salva categorias em arquivo de configuracao
	 */
	public void saveCategories() {
		SAXBuilder builder = new SAXBuilder(); // Build a document ...
		Document doc = null; // ... from a file
		XMLOutputter output = new XMLOutputter(); // And output the document ...
		Format format = Format.getPrettyFormat();  
		format.setEncoding("ISO-8859-1");
		output.setFormat(format);

		try {
			doc = builder.build(getClass().getResourceAsStream("categories.xml"));
		} catch (JDOMException ex) {
			JOptionPane.showMessageDialog(null, ex, "Problemas",
					JOptionPane.ERROR_MESSAGE);
			// TODO adicionar modal sobre a tela
			return;
		} catch (IOException ex) {
			JOptionPane
					.showMessageDialog(
							null,
							"Não foi possível encontrar o arquivo de configuração das Categorias para salvar suas alterações",
							"Problemas com configuração",
							JOptionPane.ERROR_MESSAGE);
			// TODO adicionar modal sobre a tela
			return;
		}

		// Root element
		Element root = doc.getRootElement();

		root.removeChildren("Category");

		for (Category c : getCategories()) {
			// sb.append(x.getName()+"\n");
			Element category = new Element("Category");
			category.setAttribute(new Attribute("name", c.getName()));
			for (Tag w : c.getTags()) {
				Element tag = new Element("Tag");
				tag.setText(w.getName());
				category.addContent(tag);
			}
			root.addContent(category);
		}

		FileWriter f = null;
		try {
			f = new FileWriter(new File(getClass().getResource("/categories.xml").toURI()));
		} catch (IOException ex) {
			JOptionPane.showMessageDialog(null, ex, "Problemas",
					JOptionPane.ERROR_MESSAGE);
			// TODO colocar frame, ver problemas
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			output.output(doc, f);
			f.close();
		} catch (IOException ex) {
			JOptionPane.showMessageDialog(null, ex, "Problemas",
					JOptionPane.ERROR_MESSAGE);
			// TODO colocar frame, ver problemas
		}

	}

	public void categorizeResponse(TwitterResponse response, VisualItem item) {
		if (response instanceof Status) {
			Status status = (Status) response;
			String text = status.getText();
			long responseId = status.getId();
			categorize(responseId, text, item);
		} else if (response instanceof Tweet) {
			Tweet t = (Tweet) response;
			String text = t.getText();
			long responseId = Long.parseLong(String.valueOf(t.getId()));			
			categorize(responseId, text, item);
		} else
			throw new IllegalArgumentException(
					"Tipo de objeto inválido para este método. Aceitos: Status, Tweet");
	}

	public void categorize(long responseId, String text, VisualItem item) {
		text = " " + text + " ";
		for (Category c : getCategories()) {
			for (Tag tag : c.getTags()) {
				tagParser = new TagParser(text, tag.getName());
				if (!tag.hasRelatedResponse(responseId)) {					
					if (tagParser.hasTag()) {
						tag.addRelatedResponse(responseId);
						formatItem(item, c);						
						relatedResponsesCount++;
					}
				} else {
					formatItem(item, c);
				}
			}
		}
		if (relatedResponsesCount >= 1000) {
			clearRelatedResponses();
			System.out.println("* cleared related responses");
		}
	}

	public void formatItem(VisualItem item, Category c) {
		if(!usedCategories.contains(c))
			usedCategories.add(c);
		item.setString(StatusesDataTable.ColNames.TWEET.toString(),
				getFormatedText("<b><font color=\"blue\">", "</font></b>"));
		String categories = item
				.getString(StatusesDataTable.ColNames.CATEGORIES.toString());

		if (categories != null){
			String[] aux = categories.split(",");
			for(String s : aux) {				 
				if(s.trim().equals(c.getName())) {
					return;
				}
			}
			categories = categories + ", " + c.getName();
			item.setFillColor(Color.black.getRGB());
			item.setString(StatusesDataTable.ColNames.CATEGORIES.toString(),
					categories);
		} else {
			item.setFillColor(((Color) c.getColor()).getRGB());
			item.setString(StatusesDataTable.ColNames.CATEGORIES.toString(), c
					.getName());
		}
	}

	public String getFormatedText(String startTag, String endTag) {
		return (tagParser!=null ? tagParser.getFormatedText(startTag, endTag) : null); 
	}

	public void clearRelatedResponses() {
		for (Category c : getCategories())
			for (Tag tag : c.getTags())
				tag.clearRelatedResponses();
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Category x : getCategories()) {
			sb.append(x.getName() + "\n");
			for (Tag s : x.getTags()) {
				sb.append("  " + s.getName() + "\n");
			}
		}
		return sb.toString();
	}

}
