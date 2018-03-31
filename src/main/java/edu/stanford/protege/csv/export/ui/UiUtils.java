package edu.stanford.protege.csv.export.ui;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.find.OWLEntityFinder;
import org.protege.editor.owl.ui.renderer.OWLModelManagerEntityRenderer;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;

import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * @author Rafael Gonçalves <br>
 * Center for Biomedical Informatics Research <br>
 * Stanford University
 */
class UiUtils {

    static final Border MATTE_BORDER = new MatteBorder(1, 1, 1, 1, new Color(220, 220, 220));
    static final Border EMPTY_BORDER = new EmptyBorder(0, 0, 0, 0);

    static List<OWLEntity> getProperties(OWLEditorKit editorKit) {
        List<OWLEntity> entities = new ArrayList<>();
        OWLOntology ont = editorKit.getModelManager().getActiveOntology();
        entities.addAll(ont.getAnnotationPropertiesInSignature());
        entities.addAll(ont.getObjectPropertiesInSignature());
        entities.addAll(ont.getDataPropertiesInSignature());
        return entities;
    }

    static int getWidestEntityStringRendering(OWLEditorKit editorKit, Collection<? extends OWLEntity> entities, FontMetrics fontMetrics) {
        int widest = 0;
        OWLModelManagerEntityRenderer renderer = editorKit.getModelManager().getOWLEntityRenderer();
        for(OWLEntity e : entities) {
            String str = renderer.render(e);
            int lineWidth = fontMetrics.stringWidth(str);
            widest = Math.max(widest, lineWidth);
        }
        return widest+60;
    }

    static void filterTextField(OWLEditorKit editorKit, SortedListModel<OWLEntity> listModel, String toMatch, List<OWLEntity> allEntitiesList) {
        List<OWLEntity> filteredEntities = UiUtils.filterEntityList(editorKit, toMatch, allEntitiesList);
        if(filteredEntities.isEmpty()) {
            listModel.clear();
            listModel.addAll(allEntitiesList);
            return;
        }
        listModel.clear();
        listModel.addAll(filteredEntities);
    }

    static List<OWLEntity> filterEntityList(OWLEditorKit editorKit, String toMatch, List<OWLEntity> allEntitiesList) {
        if(toMatch.isEmpty()) {
            return allEntitiesList;
        }
        OWLEntityFinder finder = editorKit.getModelManager().getOWLEntityFinder();
        List<OWLEntity> output = new ArrayList<>();
        Set<OWLEntity> entities = finder.getMatchingOWLEntities(toMatch);
        for(OWLEntity e : entities) {
            if (allEntitiesList.contains(e)) {
                output.add(e);
            }
        }
        List<OWLEntity> filteredEntitiesList = new ArrayList<>(output);
        Collections.sort(filteredEntitiesList);
        return filteredEntitiesList;
    }

}
