package org.opensource.dbpedia;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DbNlpRelation{

	/***
	 * This contain the Prefix Category : ontology | property | elements | terms
	 * | rdf | owl | foaf. URL :
	 * https://docs.google.com/a/pearson.com/document/d
	 * /13Qu9hyrDT1fuAayFTvy1W_T-KEKSrlMSyhZ7MCrjbw8/edit
	 * http://dbpedia.org/resource/
	 */
	public HashMap<String, List<String>> getConceptRelation(ArrayList<CoreLabel> input) {
		DbpQueryPrefix j = new DbpQueryPrefix();
		WordCustomizeUtil d = new WordCustomizeUtil();

		/**
		 * This contain the word with related concepts
		 */
		HashMap<String, List<String>> listOfFinalSet = new HashMap<String, List<String>>();

		/**
		 * Add the searched word with related concepts(Category)
		 */
		for (CoreLabel coreLabel : input) {
			/**
			 * Use to add the Capitalized and replace space with _
			 */
			String result = d.addUnderscore(coreLabel.word());
			/**
			 * Create Temporary list for Categories
			 */
			List<String> tmpList = new ArrayList<String>();

			for (String value : j.retrieveFromDbPedia("skos", "broader", result)) {
				/**
				 * Remove the DbPedia Link from the URL
				 */
				String compressWord = value.replace("http://dbpedia.org/resource/Category:", "");
				tmpList.add(compressWord);
			}
			if (tmpList.size() != 0) {
				listOfFinalSet.put(result, tmpList);
			}
		}

		return listOfFinalSet;
	}
}