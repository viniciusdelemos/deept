package controller;

import gui.visualizations.StatusesDataTable;

import java.awt.Color;
import java.awt.Paint;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.swing.JOptionPane;

import model.ChartColor;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import prefuse.visual.VisualItem;
import twitter4j.Status;
import twitter4j.Tweet;
import twitter4j.TwitterResponse;

public class CategoryManager {
	private Map<String, Category> categoriesMap;
	private TagParser tagParser;
	private int relatedResponsesCount;
	private Paint[] colorArray;
	private int colorIndex;

	// TODO adicionar syncrhonized em todos metodos que manipulam categoriesMap

	private CategoryManager() {
		// construtor private previne chamadas nao autorizadas ao construtor.
		categoriesMap = new HashMap<String, Category>();
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
		c.setWords(words);
		return c;
	}

	public boolean addWord(String category, String word) {
		Category c = categoriesMap.get(category);
		if (c == null)
			return false;
		return c.addWord(word);
	}

	public void addWords(String category, List<String> words) {
		for (String s : words) {
			addWord(category, s);
		}
	}

	public boolean removeWord(String category, String word) {
		Category c = categoriesMap.get(category);
		if (c == null)
			return false;
		return c.removeWord(word);
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

	public void removeAllCategoriesAndWords() {
		categoriesMap.clear();
	}

	/**
	 * Abre categorias do arquivo de configuracao Nao deleta dados do
	 * categoriesMap, ou seja, o ideia � categoriesMap estar vazio ou esvaziar
	 * ele antes de chamar este m�todo
	 */
	private void loadCategories() {

		File file = new File("config/categories.xml");

		SAXBuilder sb = new SAXBuilder();

		Document d = null;

		try {
			d = sb.build(file);
		} catch (JDOMException ex) {
			JOptionPane.showMessageDialog(null, ex, "Problemas",
					JOptionPane.ERROR_MESSAGE);
			// TODO ver quais excecoes podem ocorrer aki
		} catch (IOException ex) {
			JOptionPane
					.showMessageDialog(
							null,
							"N�o foi poss�vel encontrar o arquivo de configura��o das categorias.",
							"Problemas com configura��o",
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

			List<Element> words = e.getChildren("Word");
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

		try {
			doc = builder.build("config/categories.xml");
		} catch (JDOMException ex) {
			JOptionPane.showMessageDialog(null, ex, "Problemas",
					JOptionPane.ERROR_MESSAGE);
			// TODO adicionar modal sobre a tela
			return;
		} catch (IOException ex) {
			JOptionPane
					.showMessageDialog(
							null,
							"N�o foi poss�vel encontrar o arquivo de configura��o das Categorias para salvar suas altera��es",
							"Problemas com configura��o",
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
			for (CategoryWord w : c.getWords()) {
				Element word = new Element("Word");
				word.setText(w.getName());
				category.addContent(word);
			}
			root.addContent(category);
		}

		FileWriter f = null;
		try {
			f = new FileWriter(new File("config/categories.xml"));
		} catch (IOException ex) {
			JOptionPane.showMessageDialog(null, ex, "Problemas",
					JOptionPane.ERROR_MESSAGE);
			// TODO colocar frame, ver problemas
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

	private void loadTestCategories() {
		Category c = addCategory("Divers�o");
		c.addWord("festa");
		c.addWord("festa");
		c.addWord("amigos");
		c.addWord("amigos");
		c.addWord("rir");

		c = addCategory("Geek");
		c.addWord("windows");
		c.addWord("linUx");
		c.addWord("mAc");

		c = addCategory("VaiSerRemovida");
		c.addWord("lerolero");

		c = addCategory("Sentimentos");
		c.addWord("amor");
		c.addWord("�Dio");
		c.addWord("alegria");
		c.addWord("trIsteza");

		addWord("Divers�o", "sair");
		ArrayList<String> testList = new ArrayList<String>();
		testList.add("amo");
		testList.add("alegria"); // ja tem
		testList.add("odeio");
		addWords("Sentimentos", testList);

		c = addCategory("Ingl�s");
		c.addWord("I");
		c.addWord("are");
		c.addWord("is");
		c.addWord("hi");
		c.addWord("am");
		c.addWord("i am");
		c.addWord("my");

		c = addCategory("Futebol");
		c.addWord("gerAl");
		c.addWord("gr�miO");
		c.addWord("SOCcer");

		c = addCategory("Risada");
		c.addWord("r�");

		c = addCategory("VitorFasano");
		c.addWord("VF");

		c = addCategory("Teste");
		c.addWord("mp");
		c.addWord("Dem");
		c.addWord("nios");

		c = addCategory("Gay");
		c.addWord("VF");
		c.addWord("gay");

		c = removeCategory("VaiSerRemovida");

		// System.out.println(c);
		// System.out.println(this.toString());
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
			long responseId = t.getId();
			categorize(responseId, text, item);
		} else
			throw new IllegalArgumentException(
					"Tipo de objeto inv�lido para este m�todo. Aceitos: Status, Tweet");
	}

	public void categorize(long responseId, String text, VisualItem item) {
		text += " ";
		for (Category c : getCategories()) {
			for (CategoryWord word : c.getWords())
				if (!word.hasRelatedResponse(responseId)) {
					tagParser = new TagParser(text, word.getName());
					if (tagParser.hasTag()) {
						//System.out.println(text);
						//System.out.println("palavra: " + word.getName());
						word.addRelatedResponse(responseId);
						formatItem(item, c);
						// System.out.println("response "+responseId+" categorizada em "+c.getName()+" pela palavra "+word.getName()+" e cor "+c.getColor());
						relatedResponsesCount++;
					}
				} else {
					formatItem(item, c);
				}
		}
		if (relatedResponsesCount >= 1000) {
			clearRelatedResponses();
			System.out.println("* cleared related responses");
		}
	}

	public void formatItem(VisualItem item, Category c) {
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
		if (tagParser != null)
			return tagParser.getFormatedText(startTag, endTag);
		return null;
	}

	public void clearRelatedResponses() {
		for (Category c : getCategories())
			for (CategoryWord word : c.getWords())
				word.clearRelatedResponses();
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Category x : getCategories()) {
			sb.append(x.getName() + "\n");
			for (CategoryWord s : x.getWords()) {
				sb.append("  " + s.getName() + "\n");
			}
		}
		return sb.toString();
	}

}
