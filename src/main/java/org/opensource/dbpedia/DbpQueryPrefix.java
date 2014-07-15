package org.opensource.dbpedia;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

/**
 * DBPediaQueryPrefix class extends from {@link DbpQuery}. The method has been
 * overloaded with parameters to minimize the text complexity of the user input.
 * 
 * @author Dhanushanth P.
 * @version 1.0
 * 
 */

public class DbpQueryPrefix extends DbpQuery {
	// private static final int MAX_LEVEL = 3;
	private static final int MAX_LEVEL = 2;

	/**
	 * This method retrieve the data from DBpedia according to the inputs. Give
	 * the result as String List.
	 * 
	 * @param prefixCategory
	 *            has prefix category (ontology | property | elements | terms |
	 *            rdf | owl | foaf)
	 * @param prefixType
	 *            has the search selection from each category
	 * @param textToSearch
	 *            is the word that you need to search
	 * @return is the java.util.Collection<String> return Type.
	 */
	public Collection<String> retrieveFromDbPedia(String prefixCategory, String prefixType, String textToSearch) {

		// Text processing which support to DBPedia
		WordCustomizeUtil n = new WordCustomizeUtil();
		textToSearch = n.addUnderscore(textToSearch);
		// URL of the specific type search
		String prefixUrl = "";

		/**
		 * This is use to sleet the url according to the number .Because here we
		 * are searching for categories as well. in skos that is searching on
		 * category .
		 */
		String urlSelector = "general";

		switch (prefixCategory) {
		case "ontology":
			prefixUrl = "http://dbpedia.org/ontology/" + prefixType;
			urlSelector = "ontology";
			break;
		case "skos":
			prefixUrl = "http://www.w3.org/2004/02/skos/core#" + prefixType;
			urlSelector = "skos";
			break;
		case "property":
			prefixUrl = "http://dbpedia.org/property/" + prefixType;
			break;
		case "elements":
			prefixUrl = "http://purl.org/dc/elements/1.1/" + prefixType;
			break;
		case "terms":
			prefixUrl = "http://purl.org/dc/terms/" + prefixType;
			break;
		case "rdf":
			prefixUrl = "http://www.w3.org/2000/01/rdf-schema#" + prefixType;
			break;
		case "owl":
			prefixUrl = "http://www.w3.org/2002/07/owl#" + prefixType;
			break;
		case "foaf":
			prefixUrl = "http://xmlns.com/foaf/0.1/" + prefixType;
			break;
		default:
			System.out.println("Please check the Prefix Catagory and the Prefix Type");
			break;
		}

		Collection<String> resultString = new ArrayList<String>();

		// Main Dbpedia Search URL
		String service = "http://dbpedia.org/sparql";

		/**
		 * Here we are deciding is the url related to category or normal
		 * ontology search
		 */
		// SPARQL Query to retrieve the Data from DBpedia
		String query = "";

		if (urlSelector.equals("general")) {
			query = "SELECT ?data" + " WHERE {{" + "<http://dbpedia.org/resource/" + textToSearch + ">" + " <" + prefixUrl + ">" + " ?data." + "}" + "}";
		} else if (urlSelector.equals("skos")) {
			query = "SELECT ?data" + " WHERE {{ ?data " + " <" + prefixUrl + ">" + " <http://dbpedia.org/resource/Category:" + textToSearch + ">}}";
		} else if (urlSelector.equals("ontology")) {
			query = "SELECT ?data" + " WHERE {{ ?data " + " <" + prefixUrl + ">" + " <http://dbpedia.org/resource/" + textToSearch + ">}}";
		}

		QueryExecution qe = QueryExecutionFactory.sparqlService(service, query);

		try {
			ResultSet results = qe.execSelect();
			for (; results.hasNext();) {
				QuerySolution sol = results.next();
				resultString.add(sol.get("?data").toString().replace("http://dbpedia.org/resource/Category:", ""));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			qe.close();
		}

		return resultString;
	}

	// public Collection<String> getRelatedConcepts(String input) {
	// if (isLabel(input)) {
	// Collection<String> phrases = getUremovedPhrases(getSubjects(input));
	//
	// if (phrases.size() == 0) {
	// System.out.println("From Cate");
	// phrases = getUremovedPhrases(getCategories(input));
	// } else {
	// System.out.println("From Sub");
	// }
	//
	// Collection<String> names = getKnownFor(input);
	// phrases.addAll(names);
	//
	// return phrases;
	// }
	// return null;
	// }

	public Map<String, PXMetaTag> getRelatedConcepts(String input) {
		int index = 1;

		System.out.println("getRelatedConcepts(" + input + ")");

		Map<String, PXMetaTag> relatedMetaTags = null;

		if (isLabel(input)) {
			relatedMetaTags = getDbpConcepts(input, index);
		}

		return relatedMetaTags;
	}

	private Map<String, PXMetaTag> getDbpConcepts(String input, int index) {
		System.out.println("getDbpConceptMap(" + input + ", " + index + ")");

		index++;
		Collection<String> subjects = getSubjects(input);
		LogUtil.logCollection(subjects, "reference-phrases", "\nL" + index + " ");
		Map<String, PXMetaTag> relatedMetaTags = new HashMap<String, PXMetaTag>();

		if (subjects != null) {
			for (Iterator<String> iterator = subjects.iterator(); iterator.hasNext();) {
				String string = (String) iterator.next();
				PXMetaTag pxMetaTag = new PXMetaTag(WordCustomizeUtil.removeUnderscore(string).toLowerCase());
				relatedMetaTags.put(string, pxMetaTag);
				
				if (index <= MAX_LEVEL) {
					Map<String, PXMetaTag> categoryConceptMap = getCategoryConcepts(string, index);
					LogUtil.logMetaTags(categoryConceptMap, "reference-phrases", "\nL" + index + " ");

					pxMetaTag.setRelatedMetaTags(categoryConceptMap);
				}
			}
		}

		Collection<String> knowns = getKnownFor(input);
		LogUtil.logCollection(knowns, "reference-phrases", "\nL" + index + " ");

		if (knowns != null) {
			for (Iterator<String> iterator = knowns.iterator(); iterator.hasNext();) {
				String string = (String) iterator.next();
				relatedMetaTags.put(string, new PXMetaTag(WordCustomizeUtil.removeUnderscore(string).toLowerCase()));
			}
		}

		return getUremovedPhrases(relatedMetaTags);
	}

	/**
	 * @param input
	 * @param index
	 * @return
	 */
	private Map<String, PXMetaTag> getCategoryConcepts(String input, int index) {
		Map<String, PXMetaTag> conceptMap = new HashMap<String, PXMetaTag>();
		System.out.println("getCategoryConceptMap(" + input + ", " + index + ")");

		index++;
		Collection<String> categories = getCategories(input);
		LogUtil.logCollection(categories, "reference-phrases", "\nL" + index + " ");

		for (Iterator<String> iterator = categories.iterator(); iterator.hasNext();) {
			String string = (String) iterator.next();

			if (isLabel(string)) {
				PXMetaTag pxMetaTag = new PXMetaTag(WordCustomizeUtil.removeUnderscore(string).toLowerCase());
				conceptMap.put(string, pxMetaTag);

				if (index <= MAX_LEVEL) {
					Map<String, PXMetaTag> categoryConceptMap = getCategoryConcepts(string, index);
					LogUtil.logMetaTags(categoryConceptMap, "reference-phrases", "\nL" + index + " ");

					pxMetaTag.setRelatedMetaTags(categoryConceptMap);
				}
			}
		}

		Collection<String> subjects = getSubjects(input);
		LogUtil.logCollection(subjects, "reference-phrases", "\nL" + index + " ");

		if (subjects != null) {
			for (Iterator<String> iterator = subjects.iterator(); iterator.hasNext();) {
				String string = (String) iterator.next();
				PXMetaTag pxMetaTag = new PXMetaTag(WordCustomizeUtil.removeUnderscore(string).toLowerCase());
				conceptMap.put(string, pxMetaTag);

				if (index <= MAX_LEVEL) {
					Map<String, PXMetaTag> dbpConceptMap = getDbpConcepts(string, index);
					LogUtil.logCollection(dbpConceptMap.keySet(), "reference-phrases", "\nL" + index + " ");

					pxMetaTag.setRelatedMetaTags(dbpConceptMap);
				}
			}
		}

		Collection<String> knowns = getKnownFor(input);
		LogUtil.logCollection(knowns, "reference-phrases", "\nL" + index + " ");

		if (knowns != null) {
			for (Iterator<String> iterator = knowns.iterator(); iterator.hasNext();) {
				String string = (String) iterator.next();
				PXMetaTag pxMetaTag = new PXMetaTag(WordCustomizeUtil.removeUnderscore(string).toLowerCase());
				conceptMap.put(string, pxMetaTag);
			}
		}

		return getUremovedPhrases(conceptMap);
	}

	public boolean isLabel(String input) {
		Collection<String> output = new DbpQueryPrefix().retrieveFromDbPedia("rdf", "label", input);
		boolean result = output.contains(WordCustomizeUtil.removeUnderscore(input) + "@en");
		return result;
	}

	public Collection<String> getCategories(String input) {
		return new DbpQueryPrefix().retrieveFromDbPedia("skos", "broader", input);
	}

	public Collection<String> getSubjects(String input) {
		return new DbpQueryPrefix().retrieveFromDbPedia("terms", "subject", input);
	}

	public Collection<String> getKnownFor(String input) {
		return getKnownForFiltered(new DbpQueryPrefix().retrieveFromDbPedia("ontology", "knownFor", input));
	}

	private static Collection<String> getUremovedPhrases(Collection<String> concepts) {
		Collection<String> output = new ArrayList<String>();

		for (String string : concepts) {
			output.add(WordCustomizeUtil.removeUnderscore(string).toLowerCase());
		}

		return output;
	}

	private static Map<String, PXMetaTag> getUremovedPhrases(Map<String, PXMetaTag> relatedMetaTags) {
		Map<String, PXMetaTag> output = new HashMap<String, PXMetaTag>();

		if (relatedMetaTags != null) {
			for (String string : relatedMetaTags.keySet()) {
				output.put(WordCustomizeUtil.removeUnderscore(string).toLowerCase(), relatedMetaTags.get(string));
			}

		}

		return output;
	}

	private static Collection<String> getKnownForFiltered(Collection<String> collection) {
		Collection<String> output = new ArrayList<String>();

		for (String concept : collection) {
			output.add(concept.replace("http://dbpedia.org/resource/", ""));
		}

		return getUremovedPhrases(output);
	}
}