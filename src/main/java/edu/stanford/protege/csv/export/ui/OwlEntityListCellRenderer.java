package edu.stanford.protege.csv.export.ui;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.renderer.OWLCellRenderer;
import org.semanticweb.owlapi.model.OWLEntity;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.awt.*;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Rafael Gonçalves <br>
 * Center for Biomedical Informatics Research <br>
 * Stanford University
 */
public class OwlEntityListCellRenderer extends DefaultListCellRenderer {
    private static final long serialVersionUID = 5153646254115550535L;
    private OWLCellRenderer owlCellRenderer;

    public OwlEntityListCellRenderer(@Nonnull OWLEditorKit editorKit) {
        owlCellRenderer = new OWLCellRenderer(checkNotNull(editorKit));
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Component label = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if(value instanceof ExportDialogPanel.OwlEntityListItem) {
            OWLEntity entity = ((ExportDialogPanel.OwlEntityListItem) value).getEntity();
            label = owlCellRenderer.getListCellRendererComponent(list, entity, index, isSelected, cellHasFocus);
        }
        return label;
    }
}
