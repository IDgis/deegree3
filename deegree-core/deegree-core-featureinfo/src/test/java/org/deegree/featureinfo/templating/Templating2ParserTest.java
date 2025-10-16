/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2012 by:
 - Department of Geography, University of Bonn -
 and
 - lat/lon GmbH -
 and
 - Occam Labs UG (haftungsbeschränkt) -

 This library is free software; you can redistribute it and/or modify it under
 the terms of the GNU Lesser General Public License as published by the Free
 Software Foundation; either version 2.1 of the License, or (at your option)
 any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT
 ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 details.
 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation, Inc.,
 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

 Contact information:

 lat/lon GmbH
 Aennchenstr. 19, 53177 Bonn
 Germany
 http://lat-lon.de/

 Department of Geography, University of Bonn
 Prof. Dr. Klaus Greve
 Postfach 1147, 53001 Bonn
 Germany
 http://www.geographie.uni-bonn.de/deegree/

 Occam Labs UG (haftungsbeschränkt)
 Godesberger Allee 139, 53175 Bonn
 Germany

 e-mail: info@deegree.org
 ----------------------------------------------------------------------------*/
package org.deegree.featureinfo.templating;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.deegree.featureinfo.templating.lang.*;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * TODO add class documentation here
 *
 * @author <a href="mailto:schmitz@occamlabs.de">Andreas Schmitz</a>
 */
public class Templating2ParserTest {

	@Test
	public void testMapDefinition() throws IOException, RecognitionException {
		Templating2Parser parser = getParser("map.gfi");
		Map<String, Definition> defs = parser.definitions();
		assertEquals(2, defs.size());
		assertEquals(0, parser.getNumberOfSyntaxErrors());
		assertEquals(Set.of("props", "ftname"), defs.keySet());

		Definition propsDefinition = defs.get("props");
		assertNotNull(propsDefinition);
		assertEquals("props", propsDefinition.name);
		assertTrue(propsDefinition instanceof MapDefinition);

		MapDefinition propsMapDefinition = (MapDefinition) propsDefinition;
		assertEquals("Name", propsMapDefinition.map.get("NAME"));
		assertEquals("Population", propsMapDefinition.map.get("POP_2000"));
		assertEquals("State", propsMapDefinition.map.get("STATE"));

		Definition ftnameDefinition = defs.get("ftname");
		assertNotNull(ftnameDefinition);
		assertEquals("ftname", ftnameDefinition.name);
		assertTrue(ftnameDefinition instanceof MapDefinition);

		MapDefinition ftnameMapDefinition = (MapDefinition) ftnameDefinition;
		assertEquals("Cities in Utah County", ftnameMapDefinition.map.get("SGID93_LOCATION_UDOTMap_CityLocations"));
	}

	@Test
	public void testMapDefinitionUmlauts() throws IOException, RecognitionException {
		Templating2Parser parser = getParser("mapumlauts.gfi");
		Map<String, Definition> defs = parser.definitions();
		assertEquals(2, defs.size());
		assertEquals(0, parser.getNumberOfSyntaxErrors());
		assertEquals(Set.of("props", "ftname"), defs.keySet());

		Definition propsDefinition = defs.get("props");
		assertNotNull(propsDefinition);
		assertEquals("props", propsDefinition.name);
		assertTrue(propsDefinition instanceof MapDefinition);

		MapDefinition propsMapDefinition = (MapDefinition) propsDefinition;
		assertEquals("Name", propsMapDefinition.map.get("NAME"));
		assertEquals("Population", propsMapDefinition.map.get("POP_2000"));
		assertEquals("State", propsMapDefinition.map.get("STATE"));

		Definition ftnameDefinition = defs.get("ftname");
		assertNotNull(ftnameDefinition);
		assertEquals("ftname", ftnameDefinition.name);
		assertTrue(ftnameDefinition instanceof MapDefinition);

		MapDefinition ftnameMapDefinition = (MapDefinition) ftnameDefinition;
		assertEquals("Städte in Utah", ftnameMapDefinition.map.get("SGID93_LOCATION_UDOTMap_CityLocations"));
	}

	@Test
	public void testError() throws IOException, RecognitionException {
		Templating2Parser parser = getParser("error.gfi");
		Map<String, Definition> defs = parser.definitions();
		assertEquals(1, defs.size());
		assertEquals(1, parser.getNumberOfSyntaxErrors());
	}

	@Test
	public void testUtah1() throws IOException, RecognitionException {
		Templating2Parser parser = getParser("utahdemo.gfi");
		Map<String, Definition> defs = parser.definitions();
		assertEquals(3, defs.size());
		assertEquals(0, parser.getNumberOfSyntaxErrors());
		assertEquals(Set.of("start", "myfeaturetemplate", "mypropertytemplate"), defs.keySet());

		Definition startDefinition = defs.get("start");
		assertNotNull(startDefinition);
		assertEquals("start", startDefinition.name);
		assertTrue(startDefinition instanceof TemplateDefinition);

		TemplateDefinition startTemplateDefinition = (TemplateDefinition) startDefinition;
		List<Object> startTemplateBody = startTemplateDefinition.body.stream()
			.filter(item -> !(item instanceof String))
			.toList();
		assertEquals(8, startTemplateBody.size());

		Object startTemplateBody0 = startTemplateBody.get(0);
		assertNotNull(startTemplateBody0);
		assertTrue(startTemplateBody0 instanceof FeatureTemplateCall);

		FeatureTemplateCall featureTemplateCall0 = (FeatureTemplateCall) startTemplateBody0;
		assertEquals("myfeaturetemplate", featureTemplateCall0.getName());
		assertEquals(List.of("*"), featureTemplateCall0.getPatterns());
		assertFalse(featureTemplateCall0.isNegate());

		Object startTemplateBody1 = startTemplateBody.get(1);
		assertNotNull(startTemplateBody1);
		assertTrue(startTemplateBody1 instanceof FeatureTemplateCall);

		FeatureTemplateCall featureTemplateCall1 = (FeatureTemplateCall) startTemplateBody1;
		assertEquals("myfeaturetemplate", featureTemplateCall1.getName());
		assertEquals(List.of("test*"), featureTemplateCall1.getPatterns());
		assertFalse(featureTemplateCall1.isNegate());

		Object startTemplateBody2 = startTemplateBody.get(2);
		assertNotNull(startTemplateBody2);
		assertTrue(startTemplateBody2 instanceof FeatureTemplateCall);

		FeatureTemplateCall featureTemplateCall2 = (FeatureTemplateCall) startTemplateBody2;
		assertEquals("myfeaturetemplate", featureTemplateCall2.getName());
		assertEquals(List.of("*test"), featureTemplateCall2.getPatterns());
		assertFalse(featureTemplateCall2.isNegate());

		Object startTemplateBody3 = startTemplateBody.get(3);
		assertNotNull(startTemplateBody3);
		assertTrue(startTemplateBody3 instanceof FeatureTemplateCall);

		FeatureTemplateCall featureTemplateCall3 = (FeatureTemplateCall) startTemplateBody3;
		assertEquals("myfeaturetemplate", featureTemplateCall3.getName());
		assertEquals(List.of("test*", "*test"), featureTemplateCall3.getPatterns());
		assertFalse(featureTemplateCall3.isNegate());

		Object startTemplateBody4 = startTemplateBody.get(4);
		assertNotNull(startTemplateBody4);
		assertTrue(startTemplateBody4 instanceof Link);

		Link link0 = (Link) startTemplateBody4;
		assertEquals("http://something/else", link0.getPrefix());
		assertEquals("Dokument herunterladen", link0.getText());

		Object startTemplateBody5 = startTemplateBody.get(5);
		assertNotNull(startTemplateBody5);
		assertTrue(startTemplateBody5 instanceof Link);

		Link link1 = (Link) startTemplateBody5;
		assertEquals("http://something:8080/else", link1.getPrefix());
		assertEquals("Dokument herunterladen", link1.getText());

		Object startTemplateBody6 = startTemplateBody.get(6);
		assertNotNull(startTemplateBody6);
		assertTrue(startTemplateBody6 instanceof Link);

		Link link2 = (Link) startTemplateBody6;
		assertEquals("http://something/else", link2.getPrefix());
		assertNull(link2.getText());

		Object startTemplateBody7 = startTemplateBody.get(7);
		assertNotNull(startTemplateBody7);
		assertTrue(startTemplateBody7 instanceof Link);

		Link link3 = (Link) startTemplateBody7;
		assertEquals("http://something:8080/else", link3.getPrefix());
		assertNull(link3.getText());

		Definition myfeaturetemplateDefinition = defs.get("myfeaturetemplate");
		assertNotNull(myfeaturetemplateDefinition);
		assertEquals("myfeaturetemplate", myfeaturetemplateDefinition.name);
		assertTrue(myfeaturetemplateDefinition instanceof TemplateDefinition);

		TemplateDefinition myfeaturetemplateTemplateDefinition = (TemplateDefinition) myfeaturetemplateDefinition;
		List<Object> myfeaturetemplateTemplateBody = myfeaturetemplateTemplateDefinition.body.stream()
			.filter(item -> !(item instanceof String))
			.toList();
		assertEquals(2, myfeaturetemplateTemplateBody.size());

		Object myfeaturetemplateTemplateBody0 = myfeaturetemplateTemplateBody.get(0);
		assertNotNull(myfeaturetemplateTemplateBody0);
		assertTrue(myfeaturetemplateTemplateBody0 instanceof Name);

		Object myfeaturetemplateTemplateBody1 = myfeaturetemplateTemplateBody.get(1);
		assertNotNull(myfeaturetemplateTemplateBody1);
		assertTrue(myfeaturetemplateTemplateBody1 instanceof PropertyTemplateCall);

		PropertyTemplateCall propertyTemplateCall = (PropertyTemplateCall) myfeaturetemplateTemplateBody1;
		assertEquals("mypropertytemplate", propertyTemplateCall.getName());
		assertEquals(List.of("*"), propertyTemplateCall.getPatterns());
		assertFalse(propertyTemplateCall.isNegate());

		Definition mypropertytemplateDefinition = defs.get("mypropertytemplate");
		assertNotNull(mypropertytemplateDefinition);
		assertEquals("mypropertytemplate", mypropertytemplateDefinition.name);
		assertTrue(mypropertytemplateDefinition instanceof TemplateDefinition);

		TemplateDefinition mypropertytemplateTemplateDefinition = (TemplateDefinition) mypropertytemplateDefinition;
		assertTrue(mypropertytemplateTemplateDefinition.body.get(0) instanceof String);
		assertTrue(mypropertytemplateTemplateDefinition.body.get(1) instanceof Name);
		assertEquals("=", mypropertytemplateTemplateDefinition.body.get(2));
		assertTrue(mypropertytemplateTemplateDefinition.body.get(3) instanceof Value);
		assertTrue(mypropertytemplateTemplateDefinition.body.get(4) instanceof String);
	}

	@Test
	public void testUtah2() throws IOException, RecognitionException {
		Templating2Parser parser = getParser("utahdemo2.gfi");
		Map<String, Definition> defs = parser.definitions();
		assertEquals(6, defs.size());
		assertEquals(0, parser.getNumberOfSyntaxErrors());
	}

	@Test
	public void testStandardTemplate() throws IOException, RecognitionException {
		Templating2Parser parser = getParser("../html.gfi");
		Map<String, Definition> defs = parser.definitions();
		assertEquals(4, defs.size());
		assertEquals(0, parser.getNumberOfSyntaxErrors());
	}

	private static Templating2Parser getParser(String name) throws IOException {
		InputStream inputStream = Templating2ParserTest.class.getResourceAsStream(name);
		assertNotNull(String.format("Failed to read resource: %s", name), inputStream);
		CharStream input = new ANTLRInputStream(inputStream);
		Templating2Lexer lexer = new Templating2Lexer(input);
		CommonTokenStream cts = new CommonTokenStream(lexer);
		return new Templating2Parser(cts);
	}

}
