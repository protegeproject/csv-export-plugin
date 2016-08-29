package edu.stanford.protege.csv.export;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.model.OWLEntity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class CsvExporterBuilder {
    public static final String FILE_DELIMITER = ",", PROPERTY_VALUES_DELIMITER = "\t";
    private final OWLEditorKit editorKit;
    private final File outputFile;
    private String fileDelimiter = FILE_DELIMITER;
    private String propertyValuesDelimiter = PROPERTY_VALUES_DELIMITER;
    private List<OWLEntity> output = new ArrayList<>();
    private List<OWLEntity> properties = new ArrayList<>();
    private boolean includeHeaders = false;
    private boolean includeEntityTypes = false;
    private boolean useCurrentRendering = false;
    private boolean includeSuperclasses = false;
    private boolean includeCustomText = false;
    private String customText = "";

    /**
     * Constructor
     *
     * @param editorKit OWL editor kit
     * @param outputFile    Csv output file
     */
    public CsvExporterBuilder(OWLEditorKit editorKit, File outputFile) {
        this.editorKit = checkNotNull(editorKit);
        this.outputFile = checkNotNull(outputFile);
    }

    public CsvExporterBuilder setOutputProperties(List<OWLEntity> output) {
        this.output = output;
        return this;
    }

    public CsvExporterBuilder setProperties(List<OWLEntity> properties) {
        this.properties = properties;
        return this;
    }

    public CsvExporterBuilder setFileDelimiter(String fileDelimiter) {
        this.fileDelimiter = fileDelimiter;
        return this;
    }

    public CsvExporterBuilder setPropertyValuesDelimiter(String propertyValuesDelimiter) {
        this.propertyValuesDelimiter = propertyValuesDelimiter;
        return this;
    }

    public CsvExporterBuilder setIncludeHeaders(boolean includeHeaders) {
        this.includeHeaders = includeHeaders;
        return this;
    }

    public CsvExporterBuilder setIncludeEntityTypes(boolean includeEntityTypes) {
        this.includeEntityTypes = includeEntityTypes;
        return this;
    }

    public CsvExporterBuilder setUseCurrentRendering(boolean useCurrentRendering) {
        this.useCurrentRendering = useCurrentRendering;
        return this;
    }

    public CsvExporterBuilder setIncludeSuperclasses(boolean includeSuperclasses) {
        this.includeSuperclasses = includeSuperclasses;
        return this;
    }

    public CsvExporterBuilder setIncludeCustomText(boolean includeCustomText) {
        this.includeCustomText = includeCustomText;
        return this;
    }

    public CsvExporterBuilder setCustomText(String customText) {
        this.customText = customText;
        return this;
    }

    public CsvExporter build() {
        return new CsvExporter(editorKit, outputFile, output, properties, fileDelimiter, propertyValuesDelimiter, includeHeaders, includeEntityTypes,
                useCurrentRendering, includeSuperclasses, includeCustomText, customText);
    }
}