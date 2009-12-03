//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-792 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2009.12.03 at 05:42:08 PM MEZ 
//


package org.deegree.commons.datasource.configuration;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.deegree.commons.datasource.configuration package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _BBoxConstraint_QNAME = new QName("http://www.deegree.org/datasource", "BBoxConstraint");
    private final static QName _WMSDataSource_QNAME = new QName("http://www.deegree.org/datasource", "WMSDataSource");
    private final static QName _DCRecordStore_QNAME = new QName("http://www.deegree.org/datasource", "DCRecordStore");
    private final static QName _RasterFile_QNAME = new QName("http://www.deegree.org/datasource", "RasterFile");
    private final static QName _AbstractConstraint_QNAME = new QName("http://www.deegree.org/datasource", "AbstractConstraint");
    private final static QName _FeatureStore_QNAME = new QName("http://www.deegree.org/datasource", "FeatureStore");
    private final static QName _AbstractWebBasedDataSource_QNAME = new QName("http://www.deegree.org/datasource", "AbstractWebBasedDataSource");
    private final static QName _AbstractRasterDataSource_QNAME = new QName("http://www.deegree.org/datasource", "AbstractRasterDataSource");
    private final static QName _ISORecordStore_QNAME = new QName("http://www.deegree.org/datasource", "ISORecordStore");
    private final static QName _AbstractFile_QNAME = new QName("http://www.deegree.org/datasource", "AbstractFile");
    private final static QName _ShapefileDataSource_QNAME = new QName("http://www.deegree.org/datasource", "ShapefileDataSource");
    private final static QName _GeospatialFileSystemDataSource_QNAME = new QName("http://www.deegree.org/datasource", "GeospatialFileSystemDataSource");
    private final static QName _ConstrainedDatabaseDataSource_QNAME = new QName("http://www.deegree.org/datasource", "ConstrainedDatabaseDataSource");
    private final static QName _AbstractFileSystemDataSource_QNAME = new QName("http://www.deegree.org/datasource", "AbstractFileSystemDataSource");
    private final static QName _PostGISFeatureStore_QNAME = new QName("http://www.deegree.org/datasource", "PostGISFeatureStore");
    private final static QName _File_QNAME = new QName("http://www.deegree.org/datasource", "File");
    private final static QName _FileSet_QNAME = new QName("http://www.deegree.org/datasource", "FileSet");
    private final static QName _RecordStore_QNAME = new QName("http://www.deegree.org/datasource", "RecordStore");
    private final static QName _AbstractFileSet_QNAME = new QName("http://www.deegree.org/datasource", "AbstractFileSet");
    private final static QName _FeatureStoreReference_QNAME = new QName("http://www.deegree.org/datasource", "FeatureStoreReference");
    private final static QName _ScaleConstraint_QNAME = new QName("http://www.deegree.org/datasource", "ScaleConstraint");
    private final static QName _MultiResolutionDataSource_QNAME = new QName("http://www.deegree.org/datasource", "MultiResolutionDataSource");
    private final static QName _RasterDataSource_QNAME = new QName("http://www.deegree.org/datasource", "RasterDataSource");
    private final static QName _RasterDirectory_QNAME = new QName("http://www.deegree.org/datasource", "RasterDirectory");
    private final static QName _MemoryFeatureStore_QNAME = new QName("http://www.deegree.org/datasource", "MemoryFeatureStore");
    private final static QName _Directory_QNAME = new QName("http://www.deegree.org/datasource", "Directory");
    private final static QName _DatabaseDataSource_QNAME = new QName("http://www.deegree.org/datasource", "DatabaseDataSource");
    private final static QName _AbstractGeospatialDataSource_QNAME = new QName("http://www.deegree.org/datasource", "AbstractGeospatialDataSource");
    private final static QName _ElevationModelDataSource_QNAME = new QName("http://www.deegree.org/datasource", "ElevationModelDataSource");
    private final static QName _AbstractDataSource_QNAME = new QName("http://www.deegree.org/datasource", "AbstractDataSource");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.deegree.commons.datasource.configuration
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link FeatureStoreType.NamespaceHint }
     * 
     */
    public FeatureStoreType.NamespaceHint createFeatureStoreTypeNamespaceHint() {
        return new FeatureStoreType.NamespaceHint();
    }

    /**
     * Create an instance of {@link MemoryFeatureStoreType.GMLSchemaFileURL }
     * 
     */
    public MemoryFeatureStoreType.GMLSchemaFileURL createMemoryFeatureStoreTypeGMLSchemaFileURL() {
        return new MemoryFeatureStoreType.GMLSchemaFileURL();
    }

    /**
     * Create an instance of {@link FeatureStoreReferenceType }
     * 
     */
    public FeatureStoreReferenceType createFeatureStoreReferenceType() {
        return new FeatureStoreReferenceType();
    }

    /**
     * Create an instance of {@link MultiResolutionDataSource.Resolution }
     * 
     */
    public MultiResolutionDataSource.Resolution createMultiResolutionDataSourceResolution() {
        return new MultiResolutionDataSource.Resolution();
    }

    /**
     * Create an instance of {@link FileSetType }
     * 
     */
    public FileSetType createFileSetType() {
        return new FileSetType();
    }

    /**
     * Create an instance of {@link MemoryFeatureStoreType.GMLFeatureCollectionFileURL }
     * 
     */
    public MemoryFeatureStoreType.GMLFeatureCollectionFileURL createMemoryFeatureStoreTypeGMLFeatureCollectionFileURL() {
        return new MemoryFeatureStoreType.GMLFeatureCollectionFileURL();
    }

    /**
     * Create an instance of {@link ConstrainedDatabaseDataSourceType }
     * 
     */
    public ConstrainedDatabaseDataSourceType createConstrainedDatabaseDataSourceType() {
        return new ConstrainedDatabaseDataSourceType();
    }

    /**
     * Create an instance of {@link WMSDataSourceType }
     * 
     */
    public WMSDataSourceType createWMSDataSourceType() {
        return new WMSDataSourceType();
    }

    /**
     * Create an instance of {@link WMSDataSourceType.MaxMapDimensions }
     * 
     */
    public WMSDataSourceType.MaxMapDimensions createWMSDataSourceTypeMaxMapDimensions() {
        return new WMSDataSourceType.MaxMapDimensions();
    }

    /**
     * Create an instance of {@link PostGISFeatureStoreType }
     * 
     */
    public PostGISFeatureStoreType createPostGISFeatureStoreType() {
        return new PostGISFeatureStoreType();
    }

    /**
     * Create an instance of {@link ElevationModelDataSource }
     * 
     */
    public ElevationModelDataSource createElevationModelDataSource() {
        return new ElevationModelDataSource();
    }

    /**
     * Create an instance of {@link MultiResolutionDataSource }
     * 
     */
    public MultiResolutionDataSource createMultiResolutionDataSource() {
        return new MultiResolutionDataSource();
    }

    /**
     * Create an instance of {@link ShapefileDataSourceType }
     * 
     */
    public ShapefileDataSourceType createShapefileDataSourceType() {
        return new ShapefileDataSourceType();
    }

    /**
     * Create an instance of {@link RasterFileType }
     * 
     */
    public RasterFileType createRasterFileType() {
        return new RasterFileType();
    }

    /**
     * Create an instance of {@link RasterDataSource }
     * 
     */
    public RasterDataSource createRasterDataSource() {
        return new RasterDataSource();
    }

    /**
     * Create an instance of {@link ISORecordStoreType }
     * 
     */
    public ISORecordStoreType createISORecordStoreType() {
        return new ISORecordStoreType();
    }

    /**
     * Create an instance of {@link DCRecordStoreType }
     * 
     */
    public DCRecordStoreType createDCRecordStoreType() {
        return new DCRecordStoreType();
    }

    /**
     * Create an instance of {@link RasterFileSetType }
     * 
     */
    public RasterFileSetType createRasterFileSetType() {
        return new RasterFileSetType();
    }

    /**
     * Create an instance of {@link ScaleConstraint }
     * 
     */
    public ScaleConstraint createScaleConstraint() {
        return new ScaleConstraint();
    }

    /**
     * Create an instance of {@link PostGISFeatureStoreType.GMLSchemaFileURL }
     * 
     */
    public PostGISFeatureStoreType.GMLSchemaFileURL createPostGISFeatureStoreTypeGMLSchemaFileURL() {
        return new PostGISFeatureStoreType.GMLSchemaFileURL();
    }

    /**
     * Create an instance of {@link GeospatialFileSystemDataSourceType }
     * 
     */
    public GeospatialFileSystemDataSourceType createGeospatialFileSystemDataSourceType() {
        return new GeospatialFileSystemDataSourceType();
    }

    /**
     * Create an instance of {@link FileType }
     * 
     */
    public FileType createFileType() {
        return new FileType();
    }

    /**
     * Create an instance of {@link DatabaseDataSourceType }
     * 
     */
    public DatabaseDataSourceType createDatabaseDataSourceType() {
        return new DatabaseDataSourceType();
    }

    /**
     * Create an instance of {@link MemoryFeatureStoreType }
     * 
     */
    public MemoryFeatureStoreType createMemoryFeatureStoreType() {
        return new MemoryFeatureStoreType();
    }

    /**
     * Create an instance of {@link WMSDataSourceType.RequestedFormat }
     * 
     */
    public WMSDataSourceType.RequestedFormat createWMSDataSourceTypeRequestedFormat() {
        return new WMSDataSourceType.RequestedFormat();
    }

    /**
     * Create an instance of {@link RestrictedRequestParameterType }
     * 
     */
    public RestrictedRequestParameterType createRestrictedRequestParameterType() {
        return new RestrictedRequestParameterType();
    }

    /**
     * Create an instance of {@link FileSystemDataSourceType }
     * 
     */
    public FileSystemDataSourceType createFileSystemDataSourceType() {
        return new FileSystemDataSourceType();
    }

    /**
     * Create an instance of {@link AbstractWebBasedDataSourceType.CapabilitiesDocumentLocation }
     * 
     */
    public AbstractWebBasedDataSourceType.CapabilitiesDocumentLocation createAbstractWebBasedDataSourceTypeCapabilitiesDocumentLocation() {
        return new AbstractWebBasedDataSourceType.CapabilitiesDocumentLocation();
    }

    /**
     * Create an instance of {@link BBoxConstraint }
     * 
     */
    public BBoxConstraint createBBoxConstraint() {
        return new BBoxConstraint();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BBoxConstraint }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.deegree.org/datasource", name = "BBoxConstraint", substitutionHeadNamespace = "http://www.deegree.org/datasource", substitutionHeadName = "AbstractConstraint")
    public JAXBElement<BBoxConstraint> createBBoxConstraint(BBoxConstraint value) {
        return new JAXBElement<BBoxConstraint>(_BBoxConstraint_QNAME, BBoxConstraint.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WMSDataSourceType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.deegree.org/datasource", name = "WMSDataSource", substitutionHeadNamespace = "http://www.deegree.org/datasource", substitutionHeadName = "AbstractWebBasedDataSource")
    public JAXBElement<WMSDataSourceType> createWMSDataSource(WMSDataSourceType value) {
        return new JAXBElement<WMSDataSourceType>(_WMSDataSource_QNAME, WMSDataSourceType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DCRecordStoreType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.deegree.org/datasource", name = "DCRecordStore", substitutionHeadNamespace = "http://www.deegree.org/datasource", substitutionHeadName = "RecordStore")
    public JAXBElement<DCRecordStoreType> createDCRecordStore(DCRecordStoreType value) {
        return new JAXBElement<DCRecordStoreType>(_DCRecordStore_QNAME, DCRecordStoreType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RasterFileType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.deegree.org/datasource", name = "RasterFile", substitutionHeadNamespace = "http://www.deegree.org/datasource", substitutionHeadName = "AbstractFile")
    public JAXBElement<RasterFileType> createRasterFile(RasterFileType value) {
        return new JAXBElement<RasterFileType>(_RasterFile_QNAME, RasterFileType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractConstraintType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.deegree.org/datasource", name = "AbstractConstraint")
    public JAXBElement<AbstractConstraintType> createAbstractConstraint(AbstractConstraintType value) {
        return new JAXBElement<AbstractConstraintType>(_AbstractConstraint_QNAME, AbstractConstraintType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FeatureStoreType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.deegree.org/datasource", name = "FeatureStore", substitutionHeadNamespace = "http://www.deegree.org/datasource", substitutionHeadName = "AbstractDataSource")
    public JAXBElement<FeatureStoreType> createFeatureStore(FeatureStoreType value) {
        return new JAXBElement<FeatureStoreType>(_FeatureStore_QNAME, FeatureStoreType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractWebBasedDataSourceType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.deegree.org/datasource", name = "AbstractWebBasedDataSource", substitutionHeadNamespace = "http://www.deegree.org/datasource", substitutionHeadName = "AbstractGeospatialDataSource")
    public JAXBElement<AbstractWebBasedDataSourceType> createAbstractWebBasedDataSource(AbstractWebBasedDataSourceType value) {
        return new JAXBElement<AbstractWebBasedDataSourceType>(_AbstractWebBasedDataSource_QNAME, AbstractWebBasedDataSourceType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractGeospatialDataSourceType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.deegree.org/datasource", name = "AbstractRasterDataSource", substitutionHeadNamespace = "http://www.deegree.org/datasource", substitutionHeadName = "AbstractGeospatialDataSource")
    public JAXBElement<AbstractGeospatialDataSourceType> createAbstractRasterDataSource(AbstractGeospatialDataSourceType value) {
        return new JAXBElement<AbstractGeospatialDataSourceType>(_AbstractRasterDataSource_QNAME, AbstractGeospatialDataSourceType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ISORecordStoreType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.deegree.org/datasource", name = "ISORecordStore", substitutionHeadNamespace = "http://www.deegree.org/datasource", substitutionHeadName = "RecordStore")
    public JAXBElement<ISORecordStoreType> createISORecordStore(ISORecordStoreType value) {
        return new JAXBElement<ISORecordStoreType>(_ISORecordStore_QNAME, ISORecordStoreType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FileType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.deegree.org/datasource", name = "AbstractFile")
    public JAXBElement<FileType> createAbstractFile(FileType value) {
        return new JAXBElement<FileType>(_AbstractFile_QNAME, FileType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ShapefileDataSourceType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.deegree.org/datasource", name = "ShapefileDataSource", substitutionHeadNamespace = "http://www.deegree.org/datasource", substitutionHeadName = "FeatureStore")
    public JAXBElement<ShapefileDataSourceType> createShapefileDataSource(ShapefileDataSourceType value) {
        return new JAXBElement<ShapefileDataSourceType>(_ShapefileDataSource_QNAME, ShapefileDataSourceType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GeospatialFileSystemDataSourceType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.deegree.org/datasource", name = "GeospatialFileSystemDataSource", substitutionHeadNamespace = "http://www.deegree.org/datasource", substitutionHeadName = "AbstractGeospatialDataSource")
    public JAXBElement<GeospatialFileSystemDataSourceType> createGeospatialFileSystemDataSource(GeospatialFileSystemDataSourceType value) {
        return new JAXBElement<GeospatialFileSystemDataSourceType>(_GeospatialFileSystemDataSource_QNAME, GeospatialFileSystemDataSourceType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ConstrainedDatabaseDataSourceType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.deegree.org/datasource", name = "ConstrainedDatabaseDataSource", substitutionHeadNamespace = "http://www.deegree.org/datasource", substitutionHeadName = "AbstractGeospatialDataSource")
    public JAXBElement<ConstrainedDatabaseDataSourceType> createConstrainedDatabaseDataSource(ConstrainedDatabaseDataSourceType value) {
        return new JAXBElement<ConstrainedDatabaseDataSourceType>(_ConstrainedDatabaseDataSource_QNAME, ConstrainedDatabaseDataSourceType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FileSystemDataSourceType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.deegree.org/datasource", name = "AbstractFileSystemDataSource", substitutionHeadNamespace = "http://www.deegree.org/datasource", substitutionHeadName = "AbstractDataSource")
    public JAXBElement<FileSystemDataSourceType> createAbstractFileSystemDataSource(FileSystemDataSourceType value) {
        return new JAXBElement<FileSystemDataSourceType>(_AbstractFileSystemDataSource_QNAME, FileSystemDataSourceType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PostGISFeatureStoreType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.deegree.org/datasource", name = "PostGISFeatureStore", substitutionHeadNamespace = "http://www.deegree.org/datasource", substitutionHeadName = "FeatureStore")
    public JAXBElement<PostGISFeatureStoreType> createPostGISFeatureStore(PostGISFeatureStoreType value) {
        return new JAXBElement<PostGISFeatureStoreType>(_PostGISFeatureStore_QNAME, PostGISFeatureStoreType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FileType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.deegree.org/datasource", name = "File", substitutionHeadNamespace = "http://www.deegree.org/datasource", substitutionHeadName = "AbstractFile")
    public JAXBElement<FileType> createFile(FileType value) {
        return new JAXBElement<FileType>(_File_QNAME, FileType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FileSetType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.deegree.org/datasource", name = "FileSet", substitutionHeadNamespace = "http://www.deegree.org/datasource", substitutionHeadName = "AbstractFileSet")
    public JAXBElement<FileSetType> createFileSet(FileSetType value) {
        return new JAXBElement<FileSetType>(_FileSet_QNAME, FileSetType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RecordStoreType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.deegree.org/datasource", name = "RecordStore", substitutionHeadNamespace = "http://www.deegree.org/datasource", substitutionHeadName = "AbstractDataSource")
    public JAXBElement<RecordStoreType> createRecordStore(RecordStoreType value) {
        return new JAXBElement<RecordStoreType>(_RecordStore_QNAME, RecordStoreType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FileSetType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.deegree.org/datasource", name = "AbstractFileSet")
    public JAXBElement<FileSetType> createAbstractFileSet(FileSetType value) {
        return new JAXBElement<FileSetType>(_AbstractFileSet_QNAME, FileSetType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FeatureStoreReferenceType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.deegree.org/datasource", name = "FeatureStoreReference", substitutionHeadNamespace = "http://www.deegree.org/datasource", substitutionHeadName = "AbstractDataSource")
    public JAXBElement<FeatureStoreReferenceType> createFeatureStoreReference(FeatureStoreReferenceType value) {
        return new JAXBElement<FeatureStoreReferenceType>(_FeatureStoreReference_QNAME, FeatureStoreReferenceType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ScaleConstraint }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.deegree.org/datasource", name = "ScaleConstraint", substitutionHeadNamespace = "http://www.deegree.org/datasource", substitutionHeadName = "AbstractConstraint")
    public JAXBElement<ScaleConstraint> createScaleConstraint(ScaleConstraint value) {
        return new JAXBElement<ScaleConstraint>(_ScaleConstraint_QNAME, ScaleConstraint.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MultiResolutionDataSource }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.deegree.org/datasource", name = "MultiResolutionDataSource", substitutionHeadNamespace = "http://www.deegree.org/datasource", substitutionHeadName = "AbstractRasterDataSource")
    public JAXBElement<MultiResolutionDataSource> createMultiResolutionDataSource(MultiResolutionDataSource value) {
        return new JAXBElement<MultiResolutionDataSource>(_MultiResolutionDataSource_QNAME, MultiResolutionDataSource.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RasterDataSource }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.deegree.org/datasource", name = "RasterDataSource", substitutionHeadNamespace = "http://www.deegree.org/datasource", substitutionHeadName = "AbstractRasterDataSource")
    public JAXBElement<RasterDataSource> createRasterDataSource(RasterDataSource value) {
        return new JAXBElement<RasterDataSource>(_RasterDataSource_QNAME, RasterDataSource.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RasterFileSetType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.deegree.org/datasource", name = "RasterDirectory", substitutionHeadNamespace = "http://www.deegree.org/datasource", substitutionHeadName = "AbstractFileSet")
    public JAXBElement<RasterFileSetType> createRasterDirectory(RasterFileSetType value) {
        return new JAXBElement<RasterFileSetType>(_RasterDirectory_QNAME, RasterFileSetType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MemoryFeatureStoreType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.deegree.org/datasource", name = "MemoryFeatureStore", substitutionHeadNamespace = "http://www.deegree.org/datasource", substitutionHeadName = "FeatureStore")
    public JAXBElement<MemoryFeatureStoreType> createMemoryFeatureStore(MemoryFeatureStoreType value) {
        return new JAXBElement<MemoryFeatureStoreType>(_MemoryFeatureStore_QNAME, MemoryFeatureStoreType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FileType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.deegree.org/datasource", name = "Directory", substitutionHeadNamespace = "http://www.deegree.org/datasource", substitutionHeadName = "AbstractFile")
    public JAXBElement<FileType> createDirectory(FileType value) {
        return new JAXBElement<FileType>(_Directory_QNAME, FileType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DatabaseDataSourceType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.deegree.org/datasource", name = "DatabaseDataSource", substitutionHeadNamespace = "http://www.deegree.org/datasource", substitutionHeadName = "AbstractDataSource")
    public JAXBElement<DatabaseDataSourceType> createDatabaseDataSource(DatabaseDataSourceType value) {
        return new JAXBElement<DatabaseDataSourceType>(_DatabaseDataSource_QNAME, DatabaseDataSourceType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractGeospatialDataSourceType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.deegree.org/datasource", name = "AbstractGeospatialDataSource", substitutionHeadNamespace = "http://www.deegree.org/datasource", substitutionHeadName = "AbstractDataSource")
    public JAXBElement<AbstractGeospatialDataSourceType> createAbstractGeospatialDataSource(AbstractGeospatialDataSourceType value) {
        return new JAXBElement<AbstractGeospatialDataSourceType>(_AbstractGeospatialDataSource_QNAME, AbstractGeospatialDataSourceType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ElevationModelDataSource }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.deegree.org/datasource", name = "ElevationModelDataSource", substitutionHeadNamespace = "http://www.deegree.org/datasource", substitutionHeadName = "AbstractFileSystemDataSource")
    public JAXBElement<ElevationModelDataSource> createElevationModelDataSource(ElevationModelDataSource value) {
        return new JAXBElement<ElevationModelDataSource>(_ElevationModelDataSource_QNAME, ElevationModelDataSource.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractDataSourceType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.deegree.org/datasource", name = "AbstractDataSource")
    public JAXBElement<AbstractDataSourceType> createAbstractDataSource(AbstractDataSourceType value) {
        return new JAXBElement<AbstractDataSourceType>(_AbstractDataSource_QNAME, AbstractDataSourceType.class, null, value);
    }

}
