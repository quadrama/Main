package de.unistuttgart.ims.drama.Main;

import java.io.File;
import java.io.IOException;

import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.resource.ResourceInitializationException;

import com.lexicalscope.jewel.cli.CliFactory;
import com.lexicalscope.jewel.cli.Option;

import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiReader;
import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiWriter;
import de.unistuttgart.ims.drama.core.ml.gender.ClearTkGenderAnnotator;

public class ProcessDramatisPersonae {

	public static void main(String[] args) throws ResourceInitializationException, UIMAException, IOException {
		MyOptions options = CliFactory.parseArguments(MyOptions.class, args);

		SimplePipeline.runPipeline(
				CollectionReaderFactory.createReaderDescription(XmiReader.class, XmiReader.PARAM_SOURCE_LOCATION,
						options.getInput() + "/*.xmi"),
				ClearTkGenderAnnotator.getEngineDescription(options.getGenderModel().getAbsolutePath()),
				AnalysisEngineFactory.createEngineDescription(XmiWriter.class, XmiWriter.PARAM_TARGET_LOCATION,
						options.getOutput(), XmiWriter.PARAM_USE_DOCUMENT_ID, true));
	}

	interface MyOptions extends Options {
		@Option
		File getGenderModel();
	}
}
