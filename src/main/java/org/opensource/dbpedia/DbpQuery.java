package org.opensource.dbpedia;

import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

/**
 * DBPediaQuary class contain basic retrieval from DBPedia web service. Basic
 * retrieval has only SELECT Query.
 * 
 * @author Dhanushanth P.
 * @version 1.0
 */
public class DbpQuery {
	/**
	 * @param textToSearch
	 *            is the word that you want to search in DBPedia
	 * @param prefixUrl
	 *            is the URL has prefix category (ontology | property | elements
	 *            | terms | rdf | owl | foaf)
	 * @return is the java.util.List<String> return Type.
	 */
	public List<String> retrieveFromDbPedia(String prefixUrl, String textToSearch) {
		List<String> resultString = new ArrayList<String>();

		String service = "http://dbpedia.org/sparql";

		// SPARQL Query to retrieve the Data from DBpedia
		String query = "SELECT ?data" + " WHERE {{" + "<http://dbpedia.org/resource/" + textToSearch + ">" + " <" + prefixUrl + ">" + " ?data." + "}" + "}";

		System.out.println(query);

		QueryExecution qe = QueryExecutionFactory.sparqlService(service, query);

		try {
			ResultSet results = qe.execSelect();
			for (; results.hasNext();) {
				QuerySolution sol = results.next();
				resultString.add(sol.get("?data").toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Please check the web service connectivity");
		} finally {
			qe.close();
		}

		return resultString;
	}
}