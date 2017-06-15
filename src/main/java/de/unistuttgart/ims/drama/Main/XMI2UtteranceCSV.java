package de.unistuttgart.ims.drama.Main;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import java.io.File;
import java.net.URL;

import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;

import com.lexicalscope.jewel.cli.CliFactory;

import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiReader;
import de.unistuttgart.ims.drama.api.Utterance;
import de.unistuttgart.ims.uimautil.CoNLLCasConsumer;

public class XMI2UtteranceCSV {

	public static void main(String[] args) throws Exception {
		URL configURL = ClassLoader.getSystemResource("drama-export.properties");

		Options options = CliFactory.parseArguments(Options.class, args);
		CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(XmiReader.class,
				XmiReader.PARAM_SOURCE_LOCATION, options.getInput() + File.separator + "*.xmi");

		AggregateBuilder builder = new AggregateBuilder();
		builder.add(createEngineDescription(CoNLLCasConsumer.class, CoNLLCasConsumer.PARAM_OUTPUT_FILE,
				options.getOutput(), CoNLLCasConsumer.PARAM_CONFIGURATION_FILE, configURL.toString(),
				CoNLLCasConsumer.PARAM_ANNOTATION_CLASS, Utterance.class));

		SimplePipeline.runPipeline(reader, builder.createAggregateDescription());

	}

}
