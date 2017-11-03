package de.unistuttgart.ims.drama.Main;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.apache.uima.UIMAException;
import org.apache.uima.cas.impl.XmiCasSerializer;
import org.apache.uima.fit.factory.AnnotationFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.google.common.collect.Iterators;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

public class TestCleanup {
	@Test
	public void testCleanUp() throws UIMAException, FileNotFoundException, SAXException {
		JCas jcas = JCasFactory.createText("The dog barks.");
		AnnotationFactory.createAnnotation(jcas, 0, 3, Token.class);
		AnnotationFactory.createAnnotation(jcas, 4, 7, Token.class);
		AnnotationFactory.createAnnotation(jcas, 8, 12, Token.class);

		assertEquals(1, Iterators.size(jcas.getViewIterator()));

		JCas view2 = jcas.createView("tmp:view2");
		view2.setDocumentText("Second view");
		AnnotationFactory.createAnnotation(view2, 0, 3, Token.class);

		assertEquals(2, Iterators.size(jcas.getViewIterator()));
		XmiCasSerializer.serialize(jcas.getCas(), new FileOutputStream(new File("target/test1.xmi")));

		view2.getSofa().removeFromIndexes();
		XmiCasSerializer.serialize(jcas.getCas(), new FileOutputStream(new File("target/test2.xmi")));
		assertEquals(1, Iterators.size(jcas.getViewIterator()));

	}
}
