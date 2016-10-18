package de.unistuttgart.ims.drama.Main;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;

import com.lexicalscope.jewel.cli.CliFactory;

import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiWriter;
import de.tudarmstadt.ukp.dkpro.core.languagetool.LanguageToolSegmenter;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordNamedEntityRecognizer;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordPosTagger;
import de.unistuttgart.quadrama.core.DramaSpeechSegmenter;
import de.unistuttgart.quadrama.core.FigureMentionDetection;
import de.unistuttgart.quadrama.core.FigureReferenceAnnotator;
import de.unistuttgart.quadrama.core.SpeakerIdentifier;
import de.unistuttgart.quadrama.io.tei.textgrid.TextgridTEIUrlReader;

public class TEI2XMI {

	public static void main(String[] args) throws Exception {
		Options options = CliFactory.parseArguments(Options.class, args);

		CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(TextgridTEIUrlReader.class,
				TextgridTEIUrlReader.PARAM_INPUT_DIRECTORY, options.getInputDirectory());

		AggregateBuilder builder = new AggregateBuilder();

		builder.add(DramaSpeechSegmenter.getWrappedSegmenterDescription(LanguageToolSegmenter.class));
		builder.add(createEngineDescription(FigureReferenceAnnotator.class));
		builder.add(
				createEngineDescription(SpeakerIdentifier.class, SpeakerIdentifier.PARAM_CREATE_SPEAKER_FIGURE, true));
		builder.add(createEngineDescription(StanfordPosTagger.class));
		builder.add(createEngineDescription(StanfordNamedEntityRecognizer.class));
		builder.add(createEngineDescription(FigureMentionDetection.class));

		builder.add(createEngineDescription(XmiWriter.class, XmiWriter.PARAM_TARGET_LOCATION,
				options.getOutputDirectory()));

		SimplePipeline.runPipeline(reader, builder.createAggregateDescription());
	}

}
