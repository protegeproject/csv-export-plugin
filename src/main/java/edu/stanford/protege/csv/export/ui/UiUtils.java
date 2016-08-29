package edu.stanford.protege.csv.export.ui;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.renderer.OWLModelManagerEntityRenderer;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;

import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Rafael Gonçalves <br>
 * Center for Biomedical Informatics Research <br>
 * Stanford University
 */
public class UiUtils {

    public static final Border MATTE_BORDER = new MatteBorder(1, 1, 1, 1, new Color(220, 220, 220));
    public static final Border EMPTY_BORDER = new EmptyBorder(0, 0, 0, 0);

    public static List<OWLEntity> getProperties(OWLEditorKit editorKit) {
        List<OWLEntity> entities = new ArrayList<>();
        OWLOntology ont = editorKit.getModelManager().getActiveOntology();
        entities.addAll(ont.getAnnotationPropertiesInSignature());
        entities.addAll(ont.getObjectPropertiesInSignature());
        entities.addAll(ont.getDataPropertiesInSignature());
        return entities;
    }

    public static int getWidestEntityStringRendering(OWLEditorKit editorKit, Collection<? extends OWLEntity> entities, FontMetrics fontMetrics) {
        int widest = 0;
        OWLModelManagerEntityRenderer renderer = editorKit.getModelManager().getOWLEntityRenderer();
        for(OWLEntity e : entities) {
            String str = renderer.render(e);
            int lineWidth = fontMetrics.stringWidth(str);
            widest = Math.max(widest, lineWidth);
        }
        return widest+60;
    }

}
