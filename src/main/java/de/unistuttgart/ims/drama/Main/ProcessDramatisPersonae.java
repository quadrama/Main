package de.unistuttgart.ims.drama.Main;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.apache.uima.UIMAException;
import org.apache.uima.cas.CAS;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.resource.ResourceInitializationException;
import org.cleartk.ml.jar.GenericJarClassifierFactory;

import com.lexicalscope.jewel.cli.CliFactory;
import com.lexicalscope.jewel.cli.Option;

import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiReader;
import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiWriter;
import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
import de.unistuttgart.ims.drama.api.DramatisPersonae;
import de.unistuttgart.ims.drama.api.Figure;
import de.unistuttgart.ims.drama.api.FigureType;
import de.unistuttgart.ims.drama.core.ml.MapBack;
import de.unistuttgart.ims.drama.core.ml.PrepareClearTk;
import de.unistuttgart.ims.drama.core.ml.gender.ClearTkGenderAnnotator;

public class ProcessDramatisPersonae {

	public static void main(String[] args) throws ResourceInitializationException, UIMAException, IOException {
		String tmpView = "DP";
		MyOptions options = CliFactory.parseArguments(MyOptions.class, args);

		AggregateBuilder b = new AggregateBuilder();

		b.add(AnalysisEngineFactory.createEngineDescription(PrepareClearTk.class, PrepareClearTk.PARAM_VIEW_NAME,
				tmpView, PrepareClearTk.PARAM_ANNOTATION_TYPE, DramatisPersonae.class,
				PrepareClearTk.PARAM_SUBANNOTATIONS, Arrays.asList(Figure.class, FigureType.class)));
		b.add(AnalysisEngineFactory.createEngineDescription(BreakIteratorSegmenter.class), CAS.NAME_DEFAULT_SOFA,
				tmpView);
		b.add(AnalysisEngineFactory.createEngineDescription(ClearTkGenderAnnotator.class,
				GenericJarClassifierFactory.PARAM_CLASSIFIER_JAR_PATH, options.getGenderModel()));
		b.add(AnalysisEngineFactory.createEngineDescription(MapBack.class, MapBack.PARAM_ANNOTATION_TYPE,
				FigureType.class, MapBack.PARAM_VIEW_NAME, tmpView));

		SimplePipeline.runPipeline(
				CollectionReaderFactory.createReaderDescription(XmiReader.class, XmiReader.PARAM_SOURCE_LOCATION,
						options.getInput() + "/*.xmi"),
				b.createAggregateDescription(), AnalysisEngineFactory.createEngineDescription(XmiWriter.class,
						XmiWriter.PARAM_TARGET_LOCATION, options.getOutput(), XmiWriter.PARAM_USE_DOCUMENT_ID, true));
	}

	interface MyOptions extends Options {
		@Option
		File getGenderModel();
	}
}
