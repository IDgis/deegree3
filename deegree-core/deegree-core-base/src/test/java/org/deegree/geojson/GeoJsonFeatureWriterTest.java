package org.deegree.geojson;

import com.jayway.jsonpath.matchers.JsonPathMatchers;
import org.deegree.cs.coordinatesystems.ICRS;
import org.deegree.cs.persistence.CRSManager;
import org.deegree.feature.Feature;
import org.deegree.gml.GMLInputFactory;
import org.deegree.gml.GMLStreamReader;
import org.deegree.gml.GMLVersion;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasNoJsonPath;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import javax.xml.namespace.QName;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class GeoJsonFeatureWriterTest {

	@Test
	public void testWrite() throws Exception {
		StringWriter featureAsJson = new StringWriter();
		GeoJsonWriter geoJsonFeatureWriter = new GeoJsonWriter(featureAsJson, null);
		Feature cadastralZoning = parseFeature("CadastralZoning.gml");

		geoJsonFeatureWriter.startFeatureCollection();
		geoJsonFeatureWriter.write(cadastralZoning);
		geoJsonFeatureWriter.endFeatureCollection();

		String featureCollection = featureAsJson.toString();

		assertThat(featureCollection, JsonPathMatchers.isJson());
		assertThat(featureCollection, hasJsonPath("$.type", is("FeatureCollection")));
		assertThat(featureCollection, hasJsonPath("$.features.length()", is(1)));
		assertThat(featureCollection, hasJsonPath("$.features[0].type", is("Feature")));
		assertThat(featureCollection, hasNoJsonPath("$.features[0].srsName"));
		assertThat(featureCollection, hasJsonPath("$.features[0].id", is("CP_CADASTRALZONING_Bundesland_02")));
		assertThat(featureCollection, hasJsonPath("$.features[0].geometry"));
		assertThat(featureCollection, hasJsonPath("$.features[0].properties.label", is("02")));
		assertThat(featureCollection, hasJsonPath("$.features[0].properties.originalMapScaleDenominator", is(10)));
		assertThat(featureCollection,
				hasJsonPath("$.features[0].properties.beginLifespanVersion", is("2009-12-15T08:04:54Z")));
		assertThat(featureCollection, hasJsonPath("$.features[0].properties.estimatedAccuracy.uom", is("m")));
		assertThat(featureCollection, hasJsonPath("$.features[0].properties.inspireId.Identifier.localId",
				is("urn:adv:oid:DEHHALKA10000005")));
		assertThat(featureCollection, hasJsonPath(
				"$.features[0].properties.name.GeographicalName.spelling.SpellingOfName.text", is("Hamburg")));
		assertThat(featureCollection,
				hasJsonPath("$.features[0].properties.levelName.LocalisedCharacterString.value", is("Bundesland")));
		assertThat(featureCollection, hasJsonPath("$.features[0].properties.validFrom.nil", is(true)));
		assertThat(featureCollection, hasJsonPath("$.features[0].properties.validFrom.nilReason", is("unpopulated")));
		assertThat(featureCollection, hasJsonPath("$.features[0].properties.validTo.nil", is(true)));
		assertThat(featureCollection, hasJsonPath("$.features[0].properties.validTo.nilReason", is("unpopulated")));
		assertThat(featureCollection, not(hasJsonPath("$.features[0].properties.geometry")));

	}

	@Test
	public void testWrite_skipGeometries() throws Exception {
		StringWriter featureAsJson = new StringWriter();
		GeoJsonWriter geoJsonFeatureWriter = new GeoJsonWriter(featureAsJson, null, true);
		Feature cadastralZoning = parseFeature("CadastralZoning.gml");

		geoJsonFeatureWriter.startFeatureCollection();
		geoJsonFeatureWriter.write(cadastralZoning);
		geoJsonFeatureWriter.endFeatureCollection();

		String featureCollection = featureAsJson.toString();

		assertThat(featureCollection, JsonPathMatchers.isJson());
		assertThat(featureCollection, hasJsonPath("$.type", is("FeatureCollection")));
		assertThat(featureCollection, hasJsonPath("$.features.length()", is(1)));
		assertThat(featureCollection, hasJsonPath("$.features[0].type", is("Feature")));
		assertThat(featureCollection, hasNoJsonPath("$.features[0].srsName"));
		assertThat(featureCollection, hasJsonPath("$.features[0].id", is("CP_CADASTRALZONING_Bundesland_02")));
		assertThat(featureCollection, not(hasJsonPath("$.features[0].geometry")));
		assertThat(featureCollection, hasJsonPath("$.features[0].properties.label", is("02")));
		assertThat(featureCollection, hasJsonPath("$.features[0].properties.originalMapScaleDenominator", is(10)));
		assertThat(featureCollection,
				hasJsonPath("$.features[0].properties.beginLifespanVersion", is("2009-12-15T08:04:54Z")));
		assertThat(featureCollection, hasJsonPath("$.features[0].properties.estimatedAccuracy.uom", is("m")));
		assertThat(featureCollection, hasJsonPath("$.features[0].properties.inspireId.Identifier.localId",
				is("urn:adv:oid:DEHHALKA10000005")));
		assertThat(featureCollection, hasJsonPath(
				"$.features[0].properties.name.GeographicalName.spelling.SpellingOfName.text", is("Hamburg")));
		assertThat(featureCollection,
				hasJsonPath("$.features[0].properties.levelName.LocalisedCharacterString.value", is("Bundesland")));
		assertThat(featureCollection, not(hasJsonPath("$.features[0].properties.geometry")));
	}

	@Test
	public void testWrite_GovServ_skipGeometries() throws Exception {
		StringWriter featureAsJson = new StringWriter();
		GeoJsonWriter geoJsonFeatureWriter = new GeoJsonWriter(featureAsJson, null, true);
		Feature govserv = parseFeature("govserv.xml");

		geoJsonFeatureWriter.startFeatureCollection();
		geoJsonFeatureWriter.write(govserv);
		geoJsonFeatureWriter.endFeatureCollection();

		String featureCollection = featureAsJson.toString();

		assertThat(featureCollection, JsonPathMatchers.isJson());
		assertThat(featureCollection, hasJsonPath("$.type", is("FeatureCollection")));
		assertThat(featureCollection, hasJsonPath("$.features.length()", is(1)));
		assertThat(featureCollection, hasJsonPath("$.features[0].type", is("Feature")));
		assertThat(featureCollection, hasNoJsonPath("$.features[0].srsName"));
		assertThat(featureCollection, hasJsonPath("$.features[0].id", is("schule_3600")));

		assertThat(featureCollection, not(hasJsonPath("$.features[0].geometry")));

		assertThat(featureCollection, hasJsonPath("$.features[0].properties.name", is("Carl-Weyprecht-Schule")));
	}

	@Test
	public void testWrite_GovServ_MultiGeom() throws Exception {
		StringWriter featureAsJson = new StringWriter();
		GeoJsonWriter geoJsonFeatureWriter = new GeoJsonWriter(featureAsJson, null, false);
		Feature govserv = parseFeature("govserv.xml");

		geoJsonFeatureWriter.startFeatureCollection();
		try {
			geoJsonFeatureWriter.write(govserv);
		}
		catch (IOException e) {
			// currently expected to fail if there are multiple geometries
			assertThat(e.getMessage(), containsString("more than one geometry"));
			return;
		}
		geoJsonFeatureWriter.endFeatureCollection();

		String featureCollection = featureAsJson.toString();

		fail("Expected to fail because of multiple geometries");
	}

	@Test
	public void testWrite_GovServ_SingleGeom() throws Exception {
		StringWriter featureAsJson = new StringWriter();
		GeoJsonWriter geoJsonFeatureWriter = new GeoJsonWriter(featureAsJson, null, false);
		Feature govserv = parseFeature("govserv_single-geom.xml");

		geoJsonFeatureWriter.startFeatureCollection();
		geoJsonFeatureWriter.write(govserv);
		geoJsonFeatureWriter.endFeatureCollection();

		String featureCollection = featureAsJson.toString();

		assertThat(featureCollection, JsonPathMatchers.isJson());
		assertThat(featureCollection, hasJsonPath("$.type", is("FeatureCollection")));
		assertThat(featureCollection, hasJsonPath("$.features.length()", is(1)));
		assertThat(featureCollection, hasJsonPath("$.features[0].type", is("Feature")));
		assertThat(featureCollection, hasNoJsonPath("$.features[0].srsName"));
		assertThat(featureCollection, hasJsonPath("$.features[0].id", is("schule_3600")));

		assertThat(featureCollection, hasJsonPath("$.features[0].geometry"));
		assertThat(featureCollection, hasJsonPath("$.features[0].geometry.type", is("Point")));
		assertThat(featureCollection, hasJsonPath("$.features[0].geometry.coordinates"));

		assertThat(featureCollection, hasJsonPath("$.features[0].properties.name", is("Carl-Weyprecht-Schule")));
	}

	@Test
	public void testWrite_SingleFeature() throws Exception {
		StringWriter featureAsJson = new StringWriter();
		GeoJsonWriter geoJsonFeatureWriter = new GeoJsonWriter(featureAsJson, null);
		Feature cadastralZoning = parseFeature("CadastralZoningWithPrimitiveArray.gml");

		geoJsonFeatureWriter.startSingleFeature();
		geoJsonFeatureWriter.writeSingleFeature(cadastralZoning);
		geoJsonFeatureWriter.endSingleFeature();

		String featureCollection = featureAsJson.toString();

		assertThat(featureCollection, JsonPathMatchers.isJson());
		assertThat(featureCollection, hasJsonPath("$.type", is("Feature")));
		assertThat(featureCollection, hasNoJsonPath("$.srsName"));
		assertThat(featureCollection, hasJsonPath("$.id", is("CP_CADASTRALZONING_Bundesland_02")));
		assertThat(featureCollection, hasJsonPath("$.geometry"));
		assertThat(featureCollection, hasJsonPath("$.properties.label", hasItem("01")));
		assertThat(featureCollection, hasJsonPath("$.properties.label", hasItem("02")));
		assertThat(featureCollection, hasJsonPath("$.properties.originalMapScaleDenominator", is(10)));
		assertThat(featureCollection, hasJsonPath("$.properties.beginLifespanVersion", is("2009-12-15T08:04:54Z")));
		assertThat(featureCollection, hasJsonPath("$.properties.estimatedAccuracy.uom", is("m")));
		assertThat(featureCollection,
				hasJsonPath("$.properties.inspireId.Identifier.localId", is("urn:adv:oid:DEHHALKA10000005")));
		assertThat(featureCollection,
				hasJsonPath("$.properties.name.GeographicalName.spelling.SpellingOfName.text", is("Hamburg")));
		assertThat(featureCollection,
				hasJsonPath("$.properties.levelName.LocalisedCharacterString.value", is("Bundesland")));
		assertThat(featureCollection, not(hasJsonPath("$.features[0].properties.geometry")));
	}

	@Test
	public void testWriteWithCrs() throws Exception {
		StringWriter featureAsJson = new StringWriter();
		ICRS crs = CRSManager.lookup("EPSG:25832");
		GeoJsonWriter geoJsonFeatureWriter = new GeoJsonWriter(featureAsJson, crs);
		Feature cadastralZoning = parseFeature("CadastralZoning.gml");

		geoJsonFeatureWriter.startFeatureCollection();
		geoJsonFeatureWriter.write(cadastralZoning);
		geoJsonFeatureWriter.endFeatureCollection();

		String featureCollection = featureAsJson.toString();

		assertThat(featureCollection, JsonPathMatchers.isJson());
		assertThat(featureCollection, hasJsonPath("$.type", is("FeatureCollection")));
		assertThat(featureCollection, hasJsonPath("$.features.length()", is(1)));
		assertThat(featureCollection, hasJsonPath("$.features[0].type", is("Feature")));
		assertThat(featureCollection, JsonPathMatchers.hasJsonPath("$.features[0].srsName", is(crs.getAlias())));
		assertThat(featureCollection, hasJsonPath("$.features[0].id", is("CP_CADASTRALZONING_Bundesland_02")));
		assertThat(featureCollection, hasJsonPath("$.features[0].geometry"));
		assertThat(featureCollection, hasJsonPath("$.features[0].properties.label", is("02")));
		assertThat(featureCollection, hasJsonPath("$.features[0].properties.originalMapScaleDenominator", is(10)));
		assertThat(featureCollection,
				hasJsonPath("$.features[0].properties.beginLifespanVersion", is("2009-12-15T08:04:54Z")));
		assertThat(featureCollection, hasJsonPath("$.features[0].properties.estimatedAccuracy.uom", is("m")));
		assertThat(featureCollection, hasJsonPath("$.features[0].properties.inspireId.Identifier.localId",
				is("urn:adv:oid:DEHHALKA10000005")));
		assertThat(featureCollection, hasJsonPath(
				"$.features[0].properties.name.GeographicalName.spelling.SpellingOfName.text", is("Hamburg")));
		assertThat(featureCollection,
				hasJsonPath("$.features[0].properties.levelName.LocalisedCharacterString.value", is("Bundesland")));
		assertThat(featureCollection, not(hasJsonPath("$.features[0].properties.geometry")));
	}

	@Test
	public void testWrite_FeatureCollectionWithPrimitiveArray() throws Exception {
		StringWriter featureAsJson = new StringWriter();
		GeoJsonWriter geoJsonFeatureWriter = new GeoJsonWriter(featureAsJson, null);
		Feature cadastralZoning = parseFeature("CadastralZoningWithPrimitiveArray.gml");

		geoJsonFeatureWriter.startFeatureCollection();
		geoJsonFeatureWriter.write(cadastralZoning);
		geoJsonFeatureWriter.endFeatureCollection();

		String featureCollection = featureAsJson.toString();

		assertThat(featureCollection, JsonPathMatchers.isJson());
		assertThat(featureCollection, hasJsonPath("$.type", is("FeatureCollection")));
		assertThat(featureCollection, hasJsonPath("$.features.length()", is(1)));
		assertThat(featureCollection, hasJsonPath("$.features[0].type", is("Feature")));
		assertThat(featureCollection, hasNoJsonPath("$.features[0].srsName"));
		assertThat(featureCollection, hasJsonPath("$.features[0].id", is("CP_CADASTRALZONING_Bundesland_02")));
		assertThat(featureCollection, hasJsonPath("$.features[0].geometry"));
		assertThat(featureCollection, hasJsonPath("$.features[0].properties.label", hasItem("01")));
		assertThat(featureCollection, hasJsonPath("$.features[0].properties.label", hasItem("02")));
		assertThat(featureCollection, hasJsonPath("$.features[0].properties.originalMapScaleDenominator", is(10)));
		assertThat(featureCollection,
				hasJsonPath("$.features[0].properties.beginLifespanVersion", is("2009-12-15T08:04:54Z")));
		assertThat(featureCollection, hasJsonPath("$.features[0].properties.estimatedAccuracy.uom", is("m")));
		assertThat(featureCollection, hasJsonPath("$.features[0].properties.inspireId.Identifier.localId",
				is("urn:adv:oid:DEHHALKA10000005")));
		assertThat(featureCollection, hasJsonPath(
				"$.features[0].properties.name.GeographicalName.spelling.SpellingOfName.text", is("Hamburg")));
		assertThat(featureCollection,
				hasJsonPath("$.features[0].properties.levelName.LocalisedCharacterString.value", is("Bundesland")));
		assertThat(featureCollection, not(hasJsonPath("$.features[0].properties.geometry")));
	}

	@Test
	public void testWrite_FeatureCollectionWithArrays() throws Exception {
		StringWriter featureAsJson = new StringWriter();
		GeoJsonWriter geoJsonFeatureWriter = new GeoJsonWriter(featureAsJson, null);
		Feature cadastralZoning = parseFeature("CadastralZoningWithArrays.gml");

		geoJsonFeatureWriter.startFeatureCollection();
		geoJsonFeatureWriter.write(cadastralZoning);
		geoJsonFeatureWriter.endFeatureCollection();

		String featureCollection = featureAsJson.toString();

		assertThat(featureCollection, JsonPathMatchers.isJson());
		assertThat(featureCollection, hasJsonPath("$.type", is("FeatureCollection")));
		assertThat(featureCollection, hasJsonPath("$.features.length()", is(1)));
		assertThat(featureCollection, hasJsonPath("$.features[0].type", is("Feature")));
		assertThat(featureCollection, hasNoJsonPath("$.features[0].srsName"));
		assertThat(featureCollection, hasJsonPath("$.features[0].id", is("CP_CADASTRALZONING_Bundesland_02")));
		assertThat(featureCollection, hasJsonPath("$.features[0].geometry"));
		assertThat(featureCollection, hasJsonPath("$.features[0].properties.label", is("02")));
		assertThat(featureCollection, hasJsonPath("$.features[0].properties.originalMapScaleDenominator", is(10)));
		assertThat(featureCollection,
				hasJsonPath("$.features[0].properties.beginLifespanVersion", is("2009-12-15T08:04:54Z")));
		assertThat(featureCollection, hasJsonPath("$.features[0].properties.estimatedAccuracy.uom", is("m")));
		assertThat(featureCollection, hasJsonPath("$.features[0].properties.inspireId.Identifier.localId",
				is("urn:adv:oid:DEHHALKA10000005")));
		assertThat(featureCollection, hasJsonPath(
				"$.features[0].properties.name[0].GeographicalName.spelling.SpellingOfName.text", is("Hamburg")));
		assertThat(featureCollection, hasJsonPath(
				"$.features[0].properties.name[1].GeographicalName.spelling.SpellingOfName.text", is("HH")));
		assertThat(featureCollection,
				hasJsonPath("$.features[0].properties.levelName[0].LocalisedCharacterString.value", is("Bundesland")));
		assertThat(featureCollection, hasJsonPath(
				"$.features[0].properties.levelName[1].LocalisedCharacterString.value", is("federal state")));
		assertThat(featureCollection, not(hasJsonPath("$.features[0].properties.geometry")));
	}

	@Test
	public void testWrite_complexPropertyWithList() throws Exception {
		StringWriter featureAsJson = new StringWriter();
		GeoJsonWriter geoJsonFeatureWriter = new GeoJsonWriter(featureAsJson, null);
		Feature cadastralZoning = parseFeature("zuwanderung.gml");

		geoJsonFeatureWriter.startFeatureCollection();
		geoJsonFeatureWriter.write(cadastralZoning);
		geoJsonFeatureWriter.endFeatureCollection();

		String featureCollection = featureAsJson.toString();

		assertThat(featureCollection, JsonPathMatchers.isJson());
		assertThat(featureCollection, hasJsonPath("$.type", is("FeatureCollection")));
		assertThat(featureCollection, hasJsonPath("$.features.length()", is(1)));
		assertThat(featureCollection, hasJsonPath("$.features[0].type", is("Feature")));
		assertThat(featureCollection, hasNoJsonPath("$.features[0].srsName"));
		assertThat(featureCollection, hasJsonPath("$.features[0].id", is("ZUWANDERUNG_0")));
		assertThat(featureCollection, hasJsonPath("$.features[0].geometry"));
		assertThat(featureCollection, hasJsonPath("$.features[0].geometry.type", is("Point")));
		assertThat(featureCollection, hasJsonPath("$.features[0].geometry.coordinates[0]", is(10.026188)));
		assertThat(featureCollection, hasJsonPath("$.features[0].geometry.coordinates[1]", is(53.487414)));
		assertThat(featureCollection, hasJsonPath("$.features[0].properties.wohnungslose_jep"));
		assertThat(featureCollection, hasJsonPath("$.features[0].properties.wohnungslose_jep.zeitreihe"));
		assertThat(featureCollection,
				hasJsonPath("$.features[0].properties.wohnungslose_jep.zeitreihe.zeitreihen-element.length()", is(62)));
		assertThat(featureCollection, hasJsonPath(
				"$.features[0].properties.wohnungslose_jep.zeitreihe.zeitreihen-element[0].datum", is("2014")));
		assertThat(featureCollection, hasJsonPath(
				"$.features[0].properties.wohnungslose_jep.zeitreihe.zeitreihen-element[0].country-list.length()",
				is(2)));
		assertThat(featureCollection, hasJsonPath(
				"$.features[0].properties.wohnungslose_jep.zeitreihe.zeitreihen-element[0].country-list[0].country-complex.name",
				is("Myanmar")));
		assertThat(featureCollection, hasJsonPath(
				"$.features[0].properties.wohnungslose_jep.zeitreihe.zeitreihen-element[0].country-list[0].country-complex.name",
				is("Myanmar")));
		assertThat(featureCollection, hasJsonPath(
				"$.features[0].properties.wohnungslose_jep.zeitreihe.zeitreihen-element[0].country-list[1].country-complex.length()",
				is(2)));
		assertThat(featureCollection, hasJsonPath(
				"$.features[0].properties.wohnungslose_jep.zeitreihe.zeitreihen-element[0].country-list[1].country-complex[0].name",
				is("Indien")));
		assertThat(featureCollection, hasJsonPath(
				"$.features[0].properties.wohnungslose_jep.zeitreihe.zeitreihen-element[0].country-list[1].country-complex[1].name",
				is("Nordkorea")));
		assertThat(featureCollection, not(hasJsonPath("$.features[0].properties.test_geom")));
	}

	@Test(expected = IOException.class)
	public void testWrite_multipleGeometries_noTunables() throws Exception {
		StringWriter featureAsJson = new StringWriter();
		GeoJsonWriter geoJsonFeatureWriter = new GeoJsonWriter(featureAsJson, null);
		Feature cadastralZoning = parseFeature("zuwanderung_multipleGeometries.gml");

		geoJsonFeatureWriter.startFeatureCollection();
		geoJsonFeatureWriter.write(cadastralZoning);
		geoJsonFeatureWriter.endFeatureCollection();
	}

	@Test
	public void testWrite_multipleGeometries_tunableGeom() throws Exception {
		StringWriter featureAsJson = new StringWriter();
		GeoJsonWriter geoJsonFeatureWriter = new GeoJsonWriter(featureAsJson, null,
				new QName("http://www.deegree.org/datasource/feature/sql", "test_geom2"), false);
		Feature cadastralZoning = parseFeature("zuwanderung_multipleGeometries.gml");

		geoJsonFeatureWriter.startFeatureCollection();
		geoJsonFeatureWriter.write(cadastralZoning);
		geoJsonFeatureWriter.endFeatureCollection();

		String featureCollection = featureAsJson.toString();
		assertThat(featureCollection, hasJsonPath("$.features[0].geometry.type", is("Point")));
		assertThat(featureCollection, hasJsonPath("$.features[0].geometry.coordinates[0]", is(12.02)));
		assertThat(featureCollection, hasJsonPath("$.features[0].geometry.coordinates[1]", is(52.48)));

		assertThat(featureCollection,
				hasJsonPath("$.features[0].properties.test_geom1", is("POINT (11.020000 51.480000)")));
		assertThat(featureCollection, not(hasJsonPath("$.features[0].properties.test_geom2")));
		assertThat(featureCollection,
				hasJsonPath("$.features[0].properties.test_geom3", is("POINT (13.020000 53.480000)")));
	}

	@Test
	public void testWrite_multipleGeometries_tunableGeomSkipWkt() throws Exception {
		StringWriter featureAsJson = new StringWriter();
		GeoJsonWriter geoJsonFeatureWriter = new GeoJsonWriter(featureAsJson, null,
				new QName("http://www.deegree.org/datasource/feature/sql", "test_geom2"), true);
		Feature cadastralZoning = parseFeature("zuwanderung_multipleGeometries.gml");

		geoJsonFeatureWriter.startFeatureCollection();
		geoJsonFeatureWriter.write(cadastralZoning);
		geoJsonFeatureWriter.endFeatureCollection();

		String featureCollection = featureAsJson.toString();
		assertThat(featureCollection, hasJsonPath("$.features[0].geometry.type", is("Point")));
		assertThat(featureCollection, hasJsonPath("$.features[0].geometry.coordinates[0]", is(12.02)));
		assertThat(featureCollection, hasJsonPath("$.features[0].geometry.coordinates[1]", is(52.48)));

		assertThat(featureCollection, not(hasJsonPath("$.features[0].properties.test_geom1")));
		assertThat(featureCollection, not(hasJsonPath("$.features[0].properties.test_geom2")));
		assertThat(featureCollection, not(hasJsonPath("$.features[0].properties.test_geom3")));
	}

	@Test
	public void testWrite_onlyNilReasonTrue() throws Exception {
		StringWriter featureAsJson = new StringWriter();
		GeoJsonWriter geoJsonFeatureWriter = new GeoJsonWriter(featureAsJson, null);
		Feature cadastralZoning = parseFeature("CadastralZoning_nilReasonTrue.gml");

		geoJsonFeatureWriter.startFeatureCollection();
		geoJsonFeatureWriter.write(cadastralZoning);
		geoJsonFeatureWriter.endFeatureCollection();

		String featureCollection = featureAsJson.toString();

		assertThat(featureCollection, JsonPathMatchers.isJson());
		assertThat(featureCollection, not(hasJsonPath("$.features[0].properties.validFrom")));
		assertThat(featureCollection, not(hasJsonPath("$.features[0].properties.validTo")));
	}

	private Feature parseFeature(String resourceName) throws Exception {
		URL testResource = GeoJsonFeatureWriterTest.class.getResource(resourceName);
		GMLStreamReader reader = GMLInputFactory.createGMLStreamReader(GMLVersion.GML_32, testResource);
		return reader.readFeature();
	}

}
