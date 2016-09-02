package edu.stanford.protege.csv.export.ui;

import com.google.common.base.Objects;
import edu.stanford.protege.csv.export.CsvExporter;
import edu.stanford.protege.csv.export.CsvExporterBuilder;
import org.protege.editor.core.ui.list.MList;
import org.protege.editor.core.ui.list.MListItem;
import org.protege.editor.core.ui.list.MListSectionHeader;
import org.protege.editor.core.ui.util.InputVerificationStatusChangedListener;
import org.protege.editor.core.ui.util.JOptionPaneEx;
import org.protege.editor.core.ui.util.UIUtil;
import org.protege.editor.core.ui.util.VerifiedInputEditor;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.model.OWLEntity;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Rafael Gonçalves <br>
 * Center for Biomedical Informatics Research <br>
 * Stanford University
 */
public class ExportDialogPanel extends JPanel implements VerifiedInputEditor {
    private static final long serialVersionUID = 4808084373916448633L;
    private OWLEditorKit editorKit;
    private final List<OWLEntity> output;
    private JLabel fileLocationLbl, outputLbl, propertiesLbl, fileDelimLbl, propertyValuesDelimLbl;
    private JTextField fileLocationTxtField, fileDelim, propertyValuesDelim;
    private JCheckBox includePropertyNames, includeEntityTypes, useCurrentRendering, includeSuperclasses, includeCustomText;
    private JButton browseBtn, editCustomTextBtn;
    private MList propertiesList, outputEntitiesList;
    private List<InputVerificationStatusChangedListener> listeners = new ArrayList<>();
    private boolean currentlyValid = false, allowOutputModifications = false;
    private OwlEntityListItem selectedPropertyListItem, selectedOutputListItem;
    private File selectedFile;
    private String customText;

    /**
     * Constructor
     *
     * @param editorKit OWL Editor Kit
     * @param customText    Custom text to appear at the end of the CSV file
     * @param output  List of OWL entities that will be exported
     * @param allowOutputModifications  true if modifications to the given list of output entities are allowed, false otherwise
     */
    public ExportDialogPanel(OWLEditorKit editorKit, String customText, List<OWLEntity> output, boolean allowOutputModifications) {
        this.editorKit = checkNotNull(editorKit);
        this.customText = checkNotNull(customText);
        this.output = checkNotNull(output);
        this.allowOutputModifications = checkNotNull(allowOutputModifications);
        initUi();
    }

    private void initUi() {
        initUiComponents();
        setLayout(new GridBagLayout());

        JScrollPane outputScrollpane = null;
        if(allowOutputModifications) {
            setPreferredSize(new Dimension(500, 650));
            outputScrollpane = new JScrollPane(outputEntitiesList);
            outputScrollpane.setBorder(UiUtils.MATTE_BORDER);
        } else {
            setPreferredSize(new Dimension(400, 500));
        }
        JScrollPane propertiesScrollpane = new JScrollPane(propertiesList);
        propertiesScrollpane.setBorder(UiUtils.MATTE_BORDER);

        Insets insets = new Insets(2, 2, 2, 2);
        int rowIndex = 0;
        add(fileLocationLbl, new GridBagConstraints(0, rowIndex, 2, 1, 0.0, 0.0, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(8, 2, 2, 2), 0, 0));
        rowIndex++;
        add(fileLocationTxtField, new GridBagConstraints(0, rowIndex, 1, 1, 1.0, 0.0, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, insets, 0, 0));
        add(browseBtn, new GridBagConstraints(1, rowIndex, 1, 1, 0.0, 0.0, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, insets, 0, 0));
        rowIndex += 2;
        if(allowOutputModifications && outputScrollpane != null) {
            add(outputLbl, new GridBagConstraints(0, rowIndex, 2, 1, 0.0, 0.0, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(15, 2, 2, 2), 0, 0));
            rowIndex++;
            add(outputScrollpane, new GridBagConstraints(0, rowIndex, 2, 1, 1.0, 1.0, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.BOTH, insets, 0, 0));
            rowIndex++;
        }
        add(propertiesLbl, new GridBagConstraints(0, rowIndex, 2, 1, 0.0, 0.0, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(15, 2, 2, 2), 0, 0));
        rowIndex++;
        add(propertiesScrollpane, new GridBagConstraints(0, rowIndex, 2, 1, 1.0, 0.5, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.BOTH, insets, 0, 0));
        rowIndex += 2;
        add(fileDelimLbl, new GridBagConstraints(0, rowIndex, 1, 1, 0.0, 0.0, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(15, 2, 2, 0), 0, 0));
        add(fileDelim, new GridBagConstraints(1, rowIndex, 1, 1, 1.0, 0.0, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, new Insets(15, 0, 2, 2), 0, 0));
        rowIndex++;
        add(propertyValuesDelimLbl, new GridBagConstraints(0, rowIndex, 1, 1, 0.0, 0.0, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(2, 2, 2, 0), 0, 0));
        add(propertyValuesDelim, new GridBagConstraints(1, rowIndex, 1, 1, 1.0, 0.0, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 2, 2), 0, 0));
        rowIndex += 2;
        add(includePropertyNames, new GridBagConstraints(0, rowIndex, 2, 1, 1.0, 0.0, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, new Insets(15, 0, 2, 2), 0, 0));
        rowIndex++;
        add(includeEntityTypes, new GridBagConstraints(0, rowIndex, 2, 1, 1.0, 0.0, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 2, 2), 0, 0));
        rowIndex++;
        add(useCurrentRendering, new GridBagConstraints(0, rowIndex, 2, 1, 1.0, 0.0, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 2, 2), 0, 0));
        rowIndex++;
        add(includeSuperclasses, new GridBagConstraints(0, rowIndex, 2, 1, 1.0, 0.0, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 2, 2), 0, 0));
        rowIndex++;
        add(includeCustomText, new GridBagConstraints(0, rowIndex, 1, 1, 1.0, 0.0, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 2, 2), 0, 0));
        add(editCustomTextBtn, new GridBagConstraints(1, rowIndex, 1, 1, 0.0, 0.0, GridBagConstraints.BASELINE_TRAILING, GridBagConstraints.NONE, new Insets(2, 0, 2, 2), 0, 0));
    }

    private void initUiComponents() {
        setupPropertyList();
        if(allowOutputModifications) {
            setupClassList();
        }
        fileLocationLbl = new JLabel("Export to file:");
        outputLbl = new JLabel("Entities to export:");
        propertiesLbl = new JLabel("Export values of properties:");
        fileDelimLbl = new JLabel("File delimiter:");
        propertyValuesDelimLbl = new JLabel("Property values delimiter:");

        fileLocationTxtField = new JTextField();
        fileDelim = new JTextField(CsvExporterBuilder.FILE_DELIMITER);
        propertyValuesDelim = new JTextField(CsvExporterBuilder.PROPERTY_VALUES_DELIMITER);

        fileDelim.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, Collections.EMPTY_SET);
        fileDelim.addKeyListener(keyListener);
        propertyValuesDelim.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, Collections.EMPTY_SET);
        propertyValuesDelim.addKeyListener(keyListener);

        browseBtn = new JButton("Browse");
        browseBtn.addActionListener(browseBtnListener);
        editCustomTextBtn = new JButton("Edit text");
        editCustomTextBtn.addActionListener(editCustomTextBtnListener);

        includePropertyNames = new JCheckBox("Include headers in first line");
        includeEntityTypes = new JCheckBox("Include column with entity types");
        useCurrentRendering = new JCheckBox("Use current rendering instead of IRIs");
        includeSuperclasses = new JCheckBox("Include column with superclasses");
        includeCustomText = new JCheckBox("Include custom text in last line");
    }

    private void setupClassList() {
        outputEntitiesList = new MList() {
            protected void handleAdd() {
                addOutputEntity();
            }

            protected void handleDelete() {
                deleteOutputEntity();
            }
        };
        outputEntitiesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        outputEntitiesList.addListSelectionListener(outputListSelectionListener);
        outputEntitiesList.setCellRenderer(new OwlEntityListCellRenderer(editorKit));
        outputEntitiesList.addKeyListener(keyAdapter);
        outputEntitiesList.addMouseListener(mouseAdapter);
        outputEntitiesList.setVisibleRowCount(10);
        outputEntitiesList.setBorder(new EmptyBorder(2, 2, 0, 2));

        List<Object> data = new ArrayList<>();
        data.add(new OwlEntityListHeaderItem());
        data.addAll(output.stream().map(OwlEntityListItem::new).collect(Collectors.toList()));
        outputEntitiesList.setListData(data.toArray());
    }

    private void setupPropertyList() {
        propertiesList = new MList() {
            protected void handleAdd() {
                addProperty();
            }

            protected void handleDelete() {
                deleteProperty();
            }
        };
        propertiesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        propertiesList.addListSelectionListener(propertyListSelectionListener);
        propertiesList.setCellRenderer(new OwlEntityListCellRenderer(editorKit));
        propertiesList.addKeyListener(keyAdapter);
        propertiesList.addMouseListener(mouseAdapter);
        propertiesList.setVisibleRowCount(5);
        propertiesList.setBorder(new EmptyBorder(2, 2, 0, 2));

        List<Object> data = new ArrayList<>();
        data.add(new OwlPropertyListHeaderItem());
        propertiesList.setListData(data.toArray());
    }

    private KeyListener keyListener = new KeyAdapter() {
        @Override
        public void keyReleased(KeyEvent e) {
            if(e.getKeyCode() == KeyEvent.VK_TAB) {
                if(e.getSource().equals(fileDelim)) {
                    fileDelim.setText(fileDelim.getText() + "\t");
                } else if(e.getSource().equals(propertyValuesDelim)) {
                    propertyValuesDelim.setText(propertyValuesDelim.getText() + "\t");
                }
            } else {
                super.keyReleased(e);
            }
        }
    };

    private ListSelectionListener propertyListSelectionListener = e -> {
        if(propertiesList.getSelectedValue() != null && !e.getValueIsAdjusting()) {
            if(propertiesList.getSelectedValue() instanceof OwlEntityListItem) {
                selectedPropertyListItem = (OwlEntityListItem) propertiesList.getSelectedValue();
            }
        }
    };

    private ListSelectionListener outputListSelectionListener = e -> {
        if(outputEntitiesList.getSelectedValue() != null && !e.getValueIsAdjusting()) {
            if(outputEntitiesList.getSelectedValue() instanceof OwlEntityListItem) {
                selectedOutputListItem = (OwlEntityListItem) outputEntitiesList.getSelectedValue();
            }
        }
    };

    private KeyAdapter keyAdapter = new KeyAdapter() {
        @Override
        public void keyReleased(KeyEvent e) {
            if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                if(e.getSource().equals(propertiesList) && propertiesList.getSelectedValue() instanceof OwlPropertyListHeaderItem) {
                    addProperty();
                } else if(e.getSource().equals(outputEntitiesList) && outputEntitiesList.getSelectedValue() instanceof OwlEntityListHeaderItem) {
                    addOutputEntity();
                }
            } else if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                if(e.getSource().equals(propertiesList) && propertiesList.getSelectedValue() instanceof OwlEntityListItem) {
                    deleteProperty();
                } else if(e.getSource().equals(outputEntitiesList) && outputEntitiesList.getSelectedValue() instanceof OwlEntityListItem) {
                    deleteOutputEntity();
                }
            }
        }
    };

    private MouseAdapter mouseAdapter = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            if(e.getClickCount() == 2) {
                if(e.getSource().equals(propertiesList) && propertiesList.getSelectedValue() instanceof OwlPropertyListHeaderItem) {
                    addProperty();
                } else if(e.getSource().equals(outputEntitiesList) && outputEntitiesList.getSelectedValue() instanceof OwlEntityListHeaderItem) {
                    addOutputEntity();
                }
            }
        }
    };

    private ActionListener browseBtnListener = e -> {
        selectedFile = UIUtil.saveFile(this, "Choose CSV file location", "CSV file", Collections.singleton("csv"), "lucene-export.csv");
        if(selectedFile != null) {
            String filename;
            if(!selectedFile.getName().endsWith(".csv")) {
                filename = selectedFile.getAbsolutePath() + ".csv";
                selectedFile = new File(filename);
            }
            fileLocationTxtField.setText(selectedFile.getAbsolutePath());
            checkInputs();
        }
    };

    private ActionListener editCustomTextBtnListener = e -> customText = CustomTextDialogPanel.showDialog(editorKit, customText);

    private void checkInputs() {
        boolean allValid = true;
        if (selectedFile == null) {
            allValid = false;
        }
        setValid(allValid);
    }

    private void setValid(boolean valid) {
        currentlyValid = valid;
        for (InputVerificationStatusChangedListener l : listeners) {
            l.verifiedStatusChanged(currentlyValid);
        }
    }

    private void addOutputEntity() {
        AddEntityToExportDialogPanel.showDialog(editorKit, getEntities(outputEntitiesList)).ifPresent(owlEntities -> addEntitiesToList(owlEntities, outputEntitiesList));
    }

    private void addProperty() {
        AddPropertyToExportDialogPanel.showDialog(editorKit, getEntities(propertiesList)).ifPresent(owlEntities -> addEntitiesToList(owlEntities, propertiesList));
    }

    private void addEntitiesToList(List<OWLEntity> entities, JList list) {
        List items = getListItems(list);
        items.addAll(entities.stream().map(OwlEntityListItem::new).collect(Collectors.toList()));
        list.setListData(items.toArray());

    }

    private void deleteOutputEntity() {
        List items = getListItems(outputEntitiesList);
        items.remove(selectedOutputListItem);
        outputEntitiesList.setListData(items.toArray());
    }

    private void deleteProperty() {
        List items = getListItems(propertiesList);
        items.remove(selectedPropertyListItem);
        propertiesList.setListData(items.toArray());
    }

    private void exportToCsv() throws IOException {
        CsvExporter csvExporter = new CsvExporterBuilder(editorKit, selectedFile)
                .setOutputProperties((allowOutputModifications ? getEntities(outputEntitiesList) : output))
                .setProperties(getEntities(propertiesList))
                .setFileDelimiter(fileDelim.getText())
                .setPropertyValuesDelimiter(propertyValuesDelim.getText())
                .setIncludeEntityTypes(includeEntityTypes.isSelected())
                .setIncludeCustomText(includeCustomText.isSelected())
                .setIncludeHeaders(includePropertyNames.isSelected())
                .setIncludeSuperclasses(includeSuperclasses.isSelected())
                .setUseCurrentRendering(useCurrentRendering.isSelected())
                .setCustomText(customText)
                .build();
        csvExporter.export();
    }

    private List<OWLEntity> getEntities(JList list) {
        List<OWLEntity> entities = new ArrayList<>();
        for(Object obj : getListItems(list)) {
            if(obj instanceof OwlEntityListItem) {
                entities.add(((OwlEntityListItem)obj).getEntity());
            }
        }
        return entities;
    }

    private List<?> getListItems(JList list) {
        List<Object> properties = new ArrayList<>();
        ListModel model = list.getModel();
        for(int i = 0; i < model.getSize(); i++) {
            properties.add(model.getElementAt(i));
        }
        return properties;
    }

    public static boolean showDialog(OWLEditorKit editorKit, String customText, List<OWLEntity> results, boolean allowOutputAlterations) throws IOException {
        ExportDialogPanel panel = new ExportDialogPanel(editorKit, customText, results, allowOutputAlterations);
        int response = JOptionPaneEx.showValidatingConfirmDialog(
                editorKit.getOWLWorkspace(), "Export to CSV file", panel, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null);
        if (response == JOptionPane.OK_OPTION) {
            panel.exportToCsv();
            return true;
        }
        return false;
    }

    @Override
    public void addStatusChangedListener(InputVerificationStatusChangedListener listener) {
        listeners.add(listener);
        listener.verifiedStatusChanged(currentlyValid);
    }

    @Override
    public void removeStatusChangedListener(InputVerificationStatusChangedListener listener) {
        listeners.remove(listener);
    }


    /**
     * Property list header item
     */
    public class OwlPropertyListHeaderItem implements MListSectionHeader {

        @Override
        public String getName() {
            return "Properties";
        }

        @Override
        public boolean canAdd() {
            return true;
        }
    }

    /**
     * Entity list header item
     */
    public class OwlEntityListHeaderItem implements MListSectionHeader {

        @Override
        public String getName() {
            return "Entities";
        }

        @Override
        public boolean canAdd() {
            return true;
        }
    }

    /**
     * OWLEntity list item
     */
    public class OwlEntityListItem implements MListItem {
        private OWLEntity entity;

        /**
         * Constructor
         *
         * @param entity OWL entity
         */
        public OwlEntityListItem(OWLEntity entity) {
            this.entity = checkNotNull(entity);
        }

        public OWLEntity getEntity() {
            return entity;
        }

        @Override
        public boolean isEditable() {
            return false;
        }

        @Override
        public void handleEdit() {

        }

        @Override
        public boolean isDeleteable() {
            return true;
        }

        @Override
        public boolean handleDelete() {
            return true;
        }

        @Override
        public String getTooltip() {
            return entity.getIRI().toQuotedString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof OwlEntityListItem)) {
                return false;
            }
            OwlEntityListItem that = (OwlEntityListItem) o;
            return Objects.equal(entity, that.entity);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(entity);
        }
    }
}
