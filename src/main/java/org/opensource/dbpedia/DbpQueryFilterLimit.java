package org.opensource.dbpedia;

import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

/**
 * DBPediaQueryPrefix class extends from {@link DbpQueryPrefix}. The method has been overloaded with parameters 
 * to minimize the text complicity of the user input.
 * @author Dhanushanth P.
 * @version 1.0
 */

public class DbpQueryFilterLimit extends DbpQueryPrefix {
public  List<String> retrieveFromDbPedia(String textToSearch,String prefixCategory,String prefixType) {
		
		//URL of the specific type search		
		String prefixUrl = "";
		
		switch (prefixCategory) {
		case "ontology":
			prefixUrl = "http://dbpedia.org/ontology/" + prefixType;
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
			prefixUrl = "http://www.w3.org/1999/02/22-rdf-syntax-ns#" + prefixType;
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
		
		List<String> resultString = new ArrayList<String>();
		
		// Main Db pedia Search URL
		String service = "http://dbpedia.org/sparql";

		// SPARQL Query to retrieve the Data from DBpedia
		String query = "SELECT ?data" 
						+ " WHERE {{"
						+ "<http://dbpedia.org/resource/"+ textToSearch + ">" + " <"
						+ prefixUrl 
						+ ">" + " ?data." + "}" + "}";
		
		QueryExecution qe = QueryExecutionFactory.sparqlService(service, query);
		
		try {
			ResultSet results = qe.execSelect();
			for (; results.hasNext();) {
				QuerySolution sol = (QuerySolution) results.next();
				resultString.add(sol.get("?data").toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			qe.close();
		}
		
		return resultString;
	}
}