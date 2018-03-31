package edu.stanford.protege.csv.export.ui;

import org.protege.editor.core.ui.util.InputVerificationStatusChangedListener;
import org.protege.editor.core.ui.util.JOptionPaneEx;
import org.protege.editor.core.ui.util.VerifiedInputEditor;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.renderer.OWLCellRenderer;
import org.semanticweb.owlapi.model.OWLEntity;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Rafael Gonçalves <br>
 * Center for Biomedical Informatics Research <br>
 * Stanford University
 */
public class AddPropertyToExportDialogPanel extends JPanel implements VerifiedInputEditor {
    private static final long serialVersionUID = 1338817119909899530L;
    private List<InputVerificationStatusChangedListener> listeners = new ArrayList<>();
    private OWLEditorKit editorKit;
    private JLabel filterLbl, propertiesLbl, propertySelectionLbl;
    private JTextField filterTextField;
    private JList<OWLEntity> propertiesList;
    private List<OWLEntity> allPropertiesList, filteredPropertiesList;
    private boolean currentlyValid = false;
    private List<OWLEntity> selectedProperties, propertiesToExclude;
    private SortedListModel<OWLEntity> listModel = new SortedListModel<>();

    /**
     * Constructor
     *
     * @param editorKit OWL Editor Kit
     * @param propertiesToExclude  List of OWL entities containing properties to exclude as they are already added
     */
    public AddPropertyToExportDialogPanel(OWLEditorKit editorKit, List<OWLEntity> propertiesToExclude) {
        this.editorKit = checkNotNull(editorKit);
        this.propertiesToExclude = checkNotNull(propertiesToExclude);
        initUi();
    }

    private void initUi() {
        setLayout(new GridBagLayout());
        setupList();

        filterLbl = new JLabel("Filter:");
        propertiesLbl = new JLabel("Properties:");
        propertySelectionLbl = new JLabel();

        filterTextField = new JTextField();
        filterTextField.getDocument().addDocumentListener(filterTextListener);

        JScrollPane propertiesScrollpane = new JScrollPane(propertiesList);
        propertiesScrollpane.setBorder(UiUtils.MATTE_BORDER);
        propertiesScrollpane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        int widest = UiUtils.getWidestEntityStringRendering(editorKit, allPropertiesList, getFontMetrics(getFont()));
        propertiesScrollpane.setPreferredSize(new Dimension(widest, 250));

        Insets insets = new Insets(2, 2, 2, 2);
        int rowIndex = 0;
        add(propertiesLbl, new GridBagConstraints(0, rowIndex, 1, 1, 0.0, 0.0, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, insets, 0, 0));
        add(propertySelectionLbl, new GridBagConstraints(1, rowIndex, 1, 1, 1.0, 0.0, GridBagConstraints.BASELINE_TRAILING, GridBagConstraints.NONE, insets, 0, 0));
        rowIndex++;
        add(propertiesScrollpane, new GridBagConstraints(0, rowIndex, 2, 1, 1.0, 1.0, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.BOTH, insets, 0, 0));
        rowIndex++;
        add(filterLbl, new GridBagConstraints(0, rowIndex, 1, 1, 0.0, 0.0, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(6, 2, 2, 2), 0, 0));
        add(filterTextField, new GridBagConstraints(1, rowIndex, 1, 1, 1.0, 0.0, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, new Insets(6, 2, 2, 2), 0, 0));
    }

    private void setupList() {
        propertiesList = new JList<>();
        propertiesList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        propertiesList.addListSelectionListener(listSelectionListener);
        propertiesList.setCellRenderer(new OWLCellRenderer(editorKit));
        propertiesList.setModel(listModel);
        propertiesList.setBorder(new EmptyBorder(2, 2, 0, 2));

        allPropertiesList = UiUtils.getProperties(editorKit);
        if(!propertiesToExclude.isEmpty()) {
            allPropertiesList.removeAll(propertiesToExclude);
        }
        listModel.addAll(allPropertiesList);
    }

    private ListSelectionListener listSelectionListener = e -> {
        selectedProperties = propertiesList.getSelectedValuesList();
        propertySelectionLbl.setText("(" + selectedProperties.size() + " selected)");
        checkInputs();
    };

    private DocumentListener filterTextListener = new DocumentListener() {
        @Override
        public void insertUpdate(DocumentEvent e) {
            filterTextField();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            filterTextField();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            filterTextField();
        }
    };

    public List<OWLEntity> getSelectedProperties() {
        return selectedProperties;
    }

    private void checkInputs() {
        boolean allValid = true;
        if (selectedProperties == null || selectedProperties.isEmpty()) {
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

    private void filterTextField() {
        UiUtils.filterTextField(editorKit, listModel, filterTextField.getText(), allPropertiesList);
    }

    public static Optional<List<OWLEntity>> showDialog(OWLEditorKit editorKit, List<OWLEntity> propertiesToExlude) {
        AddPropertyToExportDialogPanel panel = new AddPropertyToExportDialogPanel(editorKit, propertiesToExlude);
        int response = JOptionPaneEx.showValidatingConfirmDialog(
                editorKit.getOWLWorkspace(), "Choose properties to export", panel, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null);
        if (response == JOptionPane.OK_OPTION) {
            return Optional.ofNullable(panel.getSelectedProperties());
        }
        return Optional.empty();
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
}
