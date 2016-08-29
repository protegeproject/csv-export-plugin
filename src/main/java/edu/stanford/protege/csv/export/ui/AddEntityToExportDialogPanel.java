package edu.stanford.protege.csv.export.ui;

import org.protege.editor.core.ui.util.InputVerificationStatusChangedListener;
import org.protege.editor.core.ui.util.JOptionPaneEx;
import org.protege.editor.core.ui.util.VerifiedInputEditor;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.find.OWLEntityFinder;
import org.protege.editor.owl.ui.renderer.OWLCellRenderer;
import org.semanticweb.owlapi.model.OWLEntity;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.*;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Rafael Gonçalves <br>
 * Center for Biomedical Informatics Research <br>
 * Stanford University
 */
public class AddEntityToExportDialogPanel extends JPanel implements VerifiedInputEditor {
    private static final long serialVersionUID = -4995057409997442476L;
    private List<InputVerificationStatusChangedListener> listeners = new ArrayList<>();
    private OWLEditorKit editorKit;
    private JLabel filterLbl, entitiesLbl, entitySelectionLbl;
    private JTextField filterTextField;
    private JList<OWLEntity> entitiesList;
    private List<OWLEntity> allEntitiesList, filteredEntitiesList;
    private boolean currentlyValid = false;
    private List<OWLEntity> selectedEntities, entitiesToExclude;
    private SortedListModel<OWLEntity> listModel = new SortedListModel<>();

    /**
     * Constructor
     *
     * @param editorKit OWL Editor Kit
     * @param entitiesToExclude  List of OWL entities to exclude (as they are already added)
     */
    public AddEntityToExportDialogPanel(OWLEditorKit editorKit, List<OWLEntity> entitiesToExclude) {
        this.editorKit = checkNotNull(editorKit);
        this.entitiesToExclude = checkNotNull(entitiesToExclude);
        initUi();
    }

    private void initUi() {
        setLayout(new GridBagLayout());
        setupList();

        filterLbl = new JLabel("Filter:");
        entitiesLbl = new JLabel("Entities:");
        entitySelectionLbl = new JLabel();

        filterTextField = new JTextField();
        filterTextField.getDocument().addDocumentListener(filterTextListener);

        JScrollPane propertiesScrollpane = new JScrollPane(entitiesList);
        propertiesScrollpane.setBorder(UiUtils.MATTE_BORDER);
        propertiesScrollpane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        int widest = UiUtils.getWidestEntityStringRendering(editorKit, allEntitiesList, getFontMetrics(getFont()));
        propertiesScrollpane.setPreferredSize(new Dimension(widest, 250));

        Insets insets = new Insets(2, 2, 2, 2);
        int rowIndex = 0;
        add(entitiesLbl, new GridBagConstraints(0, rowIndex, 1, 1, 0.0, 0.0, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, insets, 0, 0));
        add(entitySelectionLbl, new GridBagConstraints(1, rowIndex, 1, 1, 1.0, 0.0, GridBagConstraints.BASELINE_TRAILING, GridBagConstraints.NONE, insets, 0, 0));
        rowIndex++;
        add(propertiesScrollpane, new GridBagConstraints(0, rowIndex, 2, 1, 1.0, 1.0, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.BOTH, insets, 0, 0));
        rowIndex++;
        add(filterLbl, new GridBagConstraints(0, rowIndex, 1, 1, 0.0, 0.0, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(6, 2, 2, 2), 0, 0));
        add(filterTextField, new GridBagConstraints(1, rowIndex, 1, 1, 1.0, 0.0, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, new Insets(6, 2, 2, 2), 0, 0));
    }

    private void setupList() {
        entitiesList = new JList<>();
        entitiesList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        entitiesList.addListSelectionListener(listSelectionListener);
        entitiesList.setCellRenderer(new OWLCellRenderer(editorKit));
        entitiesList.setModel(listModel);
        entitiesList.setBorder(new EmptyBorder(2, 2, 0, 2));

        allEntitiesList = new ArrayList<>(editorKit.getModelManager().getActiveOntology().getSignature());
        if(!entitiesToExclude.isEmpty()) {
            allEntitiesList.removeAll(entitiesToExclude);
        }
        listModel.addAll(allEntitiesList);
    }

    private ListSelectionListener listSelectionListener = e -> {
        selectedEntities = entitiesList.getSelectedValuesList();
        entitySelectionLbl.setText("(" + selectedEntities.size() + " selected)");
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

    public List<OWLEntity> getSelectedEntities() {
        return selectedEntities;
    }

    private void checkInputs() {
        boolean allValid = true;
        if (selectedEntities == null || selectedEntities.isEmpty()) {
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
        String toMatch = filterTextField.getText();
        if(toMatch.isEmpty()) {
            listModel.clear();
            listModel.addAll(allEntitiesList);
            return;
        }
        OWLEntityFinder finder = editorKit.getModelManager().getOWLEntityFinder();
        List<OWLEntity> output = new ArrayList<>();
        Set<OWLEntity> entities = finder.getMatchingOWLEntities(toMatch);
        for(OWLEntity e : entities) {
            if (allEntitiesList.contains(e)) {
                output.add(e);
            }
        }
        filteredEntitiesList = new ArrayList<>(output);
        Collections.sort(filteredEntitiesList);
        listModel.clear();
        listModel.addAll(filteredEntitiesList);
    }

    public static Optional<List<OWLEntity>> showDialog(OWLEditorKit editorKit, List<OWLEntity> entitiesToExlude) {
        AddEntityToExportDialogPanel panel = new AddEntityToExportDialogPanel(editorKit, entitiesToExlude);
        int response = JOptionPaneEx.showValidatingConfirmDialog(
                editorKit.getOWLWorkspace(), "Choose entities to export", panel, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null);
        if (response == JOptionPane.OK_OPTION) {
            return Optional.ofNullable(panel.getSelectedEntities());
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
