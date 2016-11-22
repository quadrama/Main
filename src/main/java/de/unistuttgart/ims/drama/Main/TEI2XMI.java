package de.unistuttgart.ims.drama.Main;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import java.io.File;

import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;

import com.lexicalscope.jewel.cli.CliFactory;
import com.lexicalscope.jewel.cli.Option;

import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiWriter;
import de.tudarmstadt.ukp.dkpro.core.matetools.MateLemmatizer;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordNamedEntityRecognizer;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordPosTagger;
import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
import de.unistuttgart.quadrama.core.DramaSpeechSegmenter;
import de.unistuttgart.quadrama.core.FigureDetailsAnnotator;
import de.unistuttgart.quadrama.core.FigureMentionDetection;
import de.unistuttgart.quadrama.core.FigureReferenceAnnotator;
import de.unistuttgart.quadrama.core.ReadDlinaMetadata;
import de.unistuttgart.quadrama.core.SetReferenceDate;
import de.unistuttgart.quadrama.core.SpeakerIdentifier;
import de.unistuttgart.quadrama.io.tei.textgrid.TextgridTEIUrlReader;

public class TEI2XMI {

	public static void main(String[] args) throws Exception {
		MyOptions options = CliFactory.parseArguments(MyOptions.class, args);

		CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(TextgridTEIUrlReader.class,
				TextgridTEIUrlReader.PARAM_INPUT, options.getInput(), TextgridTEIUrlReader.PARAM_CLEANUP, true);

		AggregateBuilder builder = new AggregateBuilder();

		builder.add(DramaSpeechSegmenter.getWrappedSegmenterDescription(BreakIteratorSegmenter.class));
		builder.add(createEngineDescription(FigureReferenceAnnotator.class));
		builder.add(createEngineDescription(FigureDetailsAnnotator.class));
		builder.add(
				createEngineDescription(SpeakerIdentifier.class, SpeakerIdentifier.PARAM_CREATE_SPEAKER_FIGURE, true));
		if (options.getDlinaDirectory() != null) {
			builder.add(createEngineDescription(ReadDlinaMetadata.class, ReadDlinaMetadata.PARAM_DLINA_DIRECTORY,
					options.getDlinaDirectory()));
			builder.add(createEngineDescription(SetReferenceDate.class));
		}
		builder.add(createEngineDescription(StanfordPosTagger.class));
		builder.add(createEngineDescription(MateLemmatizer.class));
		builder.add(createEngineDescription(StanfordNamedEntityRecognizer.class));
		builder.add(createEngineDescription(FigureMentionDetection.class));

		builder.add(createEngineDescription(XmiWriter.class, XmiWriter.PARAM_TARGET_LOCATION, options.getOutput()));

		SimplePipeline.runPipeline(reader, builder.createAggregateDescription());
	}

	interface MyOptions extends Options {
		@Option(defaultToNull = true)
		File getDlinaDirectory();
	}
}
