package de.unistuttgart.ims.drama.Main.IO;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.impl.XmiCasSerializer;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.JCasIterable;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.xml.sax.SAXException;

import com.lexicalscope.jewel.cli.CliFactory;

import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiReader;
import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
import de.unistuttgart.ims.drama.Main.Options;
import de.unistuttgart.ims.drama.api.Act;
import de.unistuttgart.ims.drama.api.ActHeading;
import de.unistuttgart.ims.drama.api.Author;
import de.unistuttgart.ims.drama.api.Date;
import de.unistuttgart.ims.drama.api.Drama;
import de.unistuttgart.ims.drama.api.DramatisPersonae;
import de.unistuttgart.ims.drama.api.Figure;
import de.unistuttgart.ims.drama.api.FigureDescription;
import de.unistuttgart.ims.drama.api.FigureMention;
import de.unistuttgart.ims.drama.api.FigureName;
import de.unistuttgart.ims.drama.api.FrontMatter;
import de.unistuttgart.ims.drama.api.MainMatter;
import de.unistuttgart.ims.drama.api.Scene;
import de.unistuttgart.ims.drama.api.SceneHeading;
import de.unistuttgart.ims.drama.api.Speaker;
import de.unistuttgart.ims.drama.api.SpeakerFigure;
import de.unistuttgart.ims.drama.api.Speech;
import de.unistuttgart.ims.drama.api.StageDirection;
import de.unistuttgart.ims.drama.api.Translator;
import de.unistuttgart.ims.drama.api.Utterance;
import de.unistuttgart.ims.uimautil.ClearAnnotation;
import de.unistuttgart.ims.uimautil.MapAnnotations;
import de.unistuttgart.quadrama.core.api.Origin;

public class ExtractForDPAnnotation {

	public static void main(String[] args)
			throws ResourceInitializationException, UIMAException, IOException, SAXException {
		Options options = CliFactory.parseArguments(Options.class, args);
		AggregateBuilder b = new AggregateBuilder();
		b.add(AnalysisEngineFactory.createEngineDescription(ClearAnnotation.class, ClearAnnotation.PARAM_TYPE,
				Origin.class), CAS.NAME_DEFAULT_SOFA, "Utterances");
		b.add(AnalysisEngineFactory.createEngineDescription(ClearAnnotation.class, ClearAnnotation.PARAM_TYPE,
				Sentence.class), CAS.NAME_DEFAULT_SOFA, "Utterances");
		b.add(AnalysisEngineFactory.createEngineDescription(ClearAnnotation.class, ClearAnnotation.PARAM_TYPE,
				Origin.class), CAS.NAME_DEFAULT_SOFA, "Dramatis Personae");
		b.add(AnalysisEngineFactory.createEngineDescription(ClearAnnotation.class, ClearAnnotation.PARAM_TYPE,
				Sentence.class), CAS.NAME_DEFAULT_SOFA, "Dramatis Personae");
		b.add(AnalysisEngineFactory.createEngineDescription(ClearAnnotation.class, ClearAnnotation.PARAM_TYPE,
				Figure.class), CAS.NAME_DEFAULT_SOFA, "Dramatis Personae");
		JCasIterable iter = SimplePipeline.iteratePipeline(
				CollectionReaderFactory.createReaderDescription(XmiReader.class, XmiReader.PARAM_SOURCE_LOCATION,
						options.getInput() + "/*.xmi", XmiReader.PARAM_LENIENT, true),
				AnalysisEngineFactory.createEngineDescription(ClearAnnotation.class, ClearAnnotation.PARAM_TYPE,
						Sentence.class),
				AnalysisEngineFactory.createEngineDescription(ClearAnnotation.class, ClearAnnotation.PARAM_TYPE,
						Utterance.class),
				AnalysisEngineFactory.createEngineDescription(ClearAnnotation.class, ClearAnnotation.PARAM_TYPE,
						Speaker.class),
				AnalysisEngineFactory.createEngineDescription(ClearAnnotation.class, ClearAnnotation.PARAM_TYPE,
						Author.class),
				AnalysisEngineFactory.createEngineDescription(ClearAnnotation.class, ClearAnnotation.PARAM_TYPE,
						Translator.class),
				AnalysisEngineFactory.createEngineDescription(ClearAnnotation.class, ClearAnnotation.PARAM_TYPE,
						FrontMatter.class),
				AnalysisEngineFactory.createEngineDescription(ClearAnnotation.class, ClearAnnotation.PARAM_TYPE,
						MainMatter.class),
				AnalysisEngineFactory.createEngineDescription(ClearAnnotation.class, ClearAnnotation.PARAM_TYPE,
						Act.class),
				AnalysisEngineFactory.createEngineDescription(ClearAnnotation.class, ClearAnnotation.PARAM_TYPE,
						Scene.class),
				AnalysisEngineFactory.createEngineDescription(ClearAnnotation.class, ClearAnnotation.PARAM_TYPE,
						ActHeading.class),
				AnalysisEngineFactory.createEngineDescription(ClearAnnotation.class, ClearAnnotation.PARAM_TYPE,
						SceneHeading.class),
				AnalysisEngineFactory.createEngineDescription(ClearAnnotation.class, ClearAnnotation.PARAM_TYPE,
						StageDirection.class),
				AnalysisEngineFactory.createEngineDescription(ClearAnnotation.class, ClearAnnotation.PARAM_TYPE,
						FigureMention.class),
				AnalysisEngineFactory.createEngineDescription(ClearAnnotation.class, ClearAnnotation.PARAM_TYPE,
						Speech.class),
				AnalysisEngineFactory.createEngineDescription(ClearAnnotation.class, ClearAnnotation.PARAM_TYPE,
						SpeakerFigure.class),
				AnalysisEngineFactory.createEngineDescription(ClearAnnotation.class, ClearAnnotation.PARAM_TYPE,
						Date.class),
				b.createAggregateDescription(), AnalysisEngineFactory.createEngineDescription(
						BreakIteratorSegmenter.class, BreakIteratorSegmenter.PARAM_WRITE_SENTENCE, false),

				AnalysisEngineFactory.createEngineDescription(ClearAnnotation.class, ClearAnnotation.PARAM_TYPE,
						Origin.class),
				AnalysisEngineFactory.createEngineDescription(MapAnnotations.class, MapAnnotations.PARAM_SOURCE_CLASS,
						DramatisPersonae.class, MapAnnotations.PARAM_TARGET_CLASS, Sentence.class,
						MapAnnotations.PARAM_DELETE_SOURCE, true),
				AnalysisEngineFactory.createEngineDescription(MapAnnotations.class, MapAnnotations.PARAM_SOURCE_CLASS,
						Figure.class, MapAnnotations.PARAM_TARGET_CLASS, webanno.custom.Figure.class,
						MapAnnotations.PARAM_DELETE_SOURCE, true),
				AnalysisEngineFactory.createEngineDescription(ClearAnnotation.class, ClearAnnotation.PARAM_TYPE,
						FigureName.class),
				AnalysisEngineFactory.createEngineDescription(ClearAnnotation.class, ClearAnnotation.PARAM_TYPE,
						FigureDescription.class),
				AnalysisEngineFactory.createEngineDescription(DocumentMetaDataProvider.class));

		for (JCas jcas : iter) {

			String id = DocumentMetaData.get(jcas).getDocumentId();

			OutputStream os = new FileOutputStream(new File(options.getOutput(), id + ".xmi"));
			XmiCasSerializer.serialize(jcas.getCas(), os);
			os.flush();
			os.close();

		}

	}

	public static class DocumentMetaDataProvider extends JCasAnnotator_ImplBase {

		@Override
		public void process(JCas aJCas) throws AnalysisEngineProcessException {
			Collection<Drama> ds = JCasUtil.select(aJCas, Drama.class);
			String id = ds.iterator().next().getDocumentId();
			for (Drama d : ds) {
				d.removeFromIndexes();
			}
			DocumentMetaData dmd = DocumentMetaData.create(aJCas);
			dmd.setDocumentId(id);

			dmd.addToIndexes();
		}

	}

}
