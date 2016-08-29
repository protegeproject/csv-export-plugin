package edu.stanford.protege.csv.export;

import edu.stanford.protege.csv.export.ui.ExportDialogPanel;
import org.protege.editor.core.ui.error.ErrorLogPanel;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.action.ProtegeOWLAction;
import org.semanticweb.owlapi.model.OWLEntity;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Rafael Gonçalves <br>
 * Center for Biomedical Informatics Research <br>
 * Stanford University
 */
public class ExportAction extends ProtegeOWLAction {
    private OWLEditorKit editorKit;

    @Override
    public void initialise() throws Exception {
        this.editorKit = getOWLEditorKit();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO get selection from Entities tab
        List<OWLEntity> entities = new ArrayList<>(editorKit.getModelManager().getActiveOntology().getSignature());
        try {
            ExportDialogPanel.showDialog(editorKit, "Export of entity selection", entities, true);
        } catch (IOException ex) {
            ErrorLogPanel.showErrorDialog(ex);
        }
    }

    @Override
    public void dispose() throws Exception {
        /* do nothing */
    }
}
