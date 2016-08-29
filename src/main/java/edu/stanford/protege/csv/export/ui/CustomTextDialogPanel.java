package edu.stanford.protege.csv.export.ui;

import org.protege.editor.core.ui.util.InputVerificationStatusChangedListener;
import org.protege.editor.core.ui.util.JOptionPaneEx;
import org.protege.editor.core.ui.util.VerifiedInputEditor;
import org.protege.editor.owl.OWLEditorKit;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Rafael Gonçalves <br>
 * Center for Biomedical Informatics Research <br>
 * Stanford University
 */
public class CustomTextDialogPanel extends JPanel implements VerifiedInputEditor {
    private static final long serialVersionUID = 1536491323885451553L;
    private List<InputVerificationStatusChangedListener> listeners = new ArrayList<>();
    private final String initialString;
    private boolean currentlyValid;
    private JTextArea textArea;

    /**
     * Constructor
     *
     * @param initialString Initial string to be contained in the text area
     */
    public CustomTextDialogPanel(String initialString) {
        this.initialString = checkNotNull(initialString);
        initUi();
    }

    private void initUi() {
        setLayout(new BorderLayout());
        textArea = new JTextArea(initialString, 10, 1);
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
        textArea.setBorder(UiUtils.EMPTY_BORDER);
        textArea.getDocument().addDocumentListener(textAreaListener);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(UiUtils.EMPTY_BORDER);
        add(scrollPane, BorderLayout.CENTER);
    }

    private DocumentListener textAreaListener = new DocumentListener() {
        @Override
        public void insertUpdate(DocumentEvent e) {
            checkInputs();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            checkInputs();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            checkInputs();
        }
    };

    public String getText() {
        return textArea.getText();
    }

    public static String showDialog(OWLEditorKit editorKit, String initialString) {
        CustomTextDialogPanel panel = new CustomTextDialogPanel(initialString);
        int response = JOptionPaneEx.showValidatingConfirmDialog(
                editorKit.getOWLWorkspace(), "Set the custom text", panel, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null);
        if (response == JOptionPane.OK_OPTION) {
            return panel.getText();
        } else {
            return initialString;
        }
    }

    private void checkInputs() {
        boolean allValid = true;
        if (getText().equals(initialString)) {
            allValid = false;
        } else if(getText().isEmpty()) {
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
