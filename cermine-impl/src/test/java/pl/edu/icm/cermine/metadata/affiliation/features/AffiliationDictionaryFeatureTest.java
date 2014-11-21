/**
 * This file is part of CERMINE project.
 * Copyright (c) 2011-2013 ICM-UW
 *
 * CERMINE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CERMINE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with CERMINE. If not, see <http://www.gnu.org/licenses/>.
 */

package pl.edu.icm.cermine.metadata.affiliation.features;

import java.util.List;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.metadata.affiliation.tools.AffiliationTokenizer;
import pl.edu.icm.cermine.metadata.model.AffiliationLabel;
import pl.edu.icm.cermine.metadata.tools.MetadataTools;
import pl.edu.icm.cermine.parsing.model.Token;

public class AffiliationDictionaryFeatureTest {

	private static final AffiliationDictionaryFeature featureCaseSensitive;
	private static final AffiliationDictionaryFeature featureIgnoreCase;
	
	static {
		try {
			featureCaseSensitive = new AffiliationDictionaryFeature("HIT", "mock-dictionary.txt",
					true);
			featureIgnoreCase = new AffiliationDictionaryFeature("HIT", "mock-dictionary.txt",
					false);
		} catch (AnalysisException e) {
			throw new RuntimeException("Failed to initialize dictionary features");
		}
	}
			
        /* mock-dictionary.txt content:
                Elo Melo 320
                Hejka ziomeczku, :-P
                W chrząszczu Szczebrzeszyn.
         */
	
	@Test
	public void testAddFeatures() {
		String text = "elo meLo  320, Elo Melo 32.0. Hejka ziomeczku,:-P. W W chrzaszczu szczebrzeszyn ..";
		//            "n...n.....n..n.n...n....n.nnn.y.....y........yyyyn.n.n.n..........n.............nn"
		int expectFeature[] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0};
		List<Token<AffiliationLabel>> tokens = 
				new AffiliationTokenizer().tokenize(MetadataTools.cleanAndNormalize(text));
		assertEquals(expectFeature.length, tokens.size());
		featureCaseSensitive.calculateDictionaryFeatures(tokens);
		
		for (int i = 0; i < expectFeature.length; i++) {
			assertEquals("Token: " + i + " " + tokens.get(i).getText(), expectFeature[i],
					tokens.get(i).getFeatures().size());
		}
		
		text = "elo meLo  320, Elo Melo 32.0. Hejka ziómeczku,:-P. W W chrzaszczu szczebrzeszyn ..";
		//     "y...y.....y..n.n...n....n.nnn.y.....y........yyyyn.n.y.y..........y.............yn"
		int expectFeature2[] = {1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 0};
		tokens = new AffiliationTokenizer().tokenize(MetadataTools.cleanAndNormalize(text));
		assertEquals(expectFeature2.length, tokens.size());
		featureIgnoreCase.calculateDictionaryFeatures(tokens);
		
		for (int i = 0; i < expectFeature2.length; i++) {
			assertEquals("Token: " + i + " " + tokens.get(i).getText(), expectFeature2[i],
					tokens.get(i).getFeatures().size());
		}
	}

}