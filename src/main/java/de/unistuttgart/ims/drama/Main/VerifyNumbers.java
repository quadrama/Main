package de.unistuttgart.ims.drama.Main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.component.NoOpAnnotator;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.JCasIterable;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import com.lexicalscope.jewel.cli.CliFactory;
import com.lexicalscope.jewel.cli.Option;

import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiReader;
import de.unistuttgart.ims.drama.api.Act;
import de.unistuttgart.ims.drama.api.Drama;
import de.unistuttgart.ims.drama.api.Figure;
import de.unistuttgart.ims.drama.api.Scene;

public class VerifyNumbers {

	static Map<String, CSVRecord> records = new HashMap<String, CSVRecord>();

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void main(String[] args) throws FileNotFoundException, IOException, ResourceInitializationException {
		MyOptions options = CliFactory.parseArguments(MyOptions.class, args);

		CSVParser parser = new CSVParser(new FileReader(options.getVerificationFile()), CSVFormat.TDF);
		Iterator<CSVRecord> iterator = parser.iterator();
		while (iterator.hasNext()) {
			CSVRecord rec = iterator.next();
			records.put(rec.get(0), rec);
		}
		parser.close();

		CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(XmiReader.class,
				XmiReader.PARAM_SOURCE_LOCATION, options.getInput() + File.separator + "*.xmi");
		JCasIterable iterable = SimplePipeline.iteratePipeline(reader,
				AnalysisEngineFactory.createEngineDescription(NoOpAnnotator.class));

		int entries = 3;
		Class[] annoCl = new Class[entries];
		annoCl[0] = Act.class;
		annoCl[1] = Scene.class;
		annoCl[2] = Figure.class;

		int[] correct = new int[entries];
		int[] total = new int[entries];

		for (JCas jcas : iterable) {
			String id = Drama.get(jcas).getDocumentId();
			if (!id.contains(":"))
				id = "tg:" + id;
			CSVRecord rec = records.get(id);

			int[] given = new int[entries];
			int[] expected = new int[entries];
			for (int i = 0; i < total.length; i++) {
				expected[i] = Integer.valueOf(rec.get(i + 1));
				if (expected[i] >= 0) {
					total[i]++;
					given[i] = JCasUtil.select(jcas, annoCl[i]).size();
					if (expected[i] == given[i])
						correct[i]++;
					else
						System.out.println(id + " " + annoCl[i].getSimpleName() + ": expected " + expected[i]
								+ ", found " + given[i]);
				}
			}

		}

		for (int i = 0; i < total.length; i++) {
			System.out.format("Total %s: %2.2f %% correct%n", annoCl[i].getSimpleName(),
					100 * (correct[i] / (double) total[i]));

		}
	}

	interface MyOptions {
		@Option
		File getVerificationFile();

		@Option
		File getInput();

	}
}
