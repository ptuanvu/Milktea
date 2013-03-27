/*
 * Copyright 2013 Japplis.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.joeffice.wordprocessor;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.rtf.RTFEditorKit;

import org.openide.util.Exceptions;

/**
 * Transfer handler that support rich text.
 * Supported MIME types are plain text, HTML and RTF.
 *
 * @author Anthony Goubard - Japplis
 */
public class RichTextTransferHandler extends TransferHandler {

    private JTextComponent textField;

    /**
     * The paste method used for DnD and clipboard.
     */
    @Override
    public boolean importData(JComponent c, Transferable t) {
        if (canImport(new TransferSupport(c, t))) {
            JTextComponent textField = (JTextComponent) c;
            String rtfText = textFromTransferable(t, TransferableRichText.RTF_FLAVOR);
            if (rtfText != null) {
                System.out.println("RTF: " + rtfText);
                addRichtText(rtfText, textField, new RTFEditorKit());
                return true;
            }
            String htmlText = textFromTransferable(t, TransferableRichText.HTML_FLAVOR);
            if (htmlText != null) {
                System.out.println("HTML: " + htmlText);
                addRichtText(rtfText, textField, new HTMLEditorKit());
                return true;
            }
            String plainText = textFromTransferable(t, DataFlavor.stringFlavor);
            if (plainText != null) {
                System.out.println("Plain: " + plainText);
                try {
                    textField.getDocument().insertString(textField.getSelectionStart(), plainText, null);
                    return true;
                } catch (BadLocationException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
        return false;
    }

    private String textFromTransferable(Transferable t, DataFlavor flavor) {
        if (t.isDataFlavorSupported(flavor)) {
            try {
                Object data = t.getTransferData(flavor);
                if (data instanceof ByteArrayInputStream) {
                    byte[] buffer = new byte[((ByteArrayInputStream) data).available()];
                    ((ByteArrayInputStream) data).read(buffer);
                    return new String(buffer, Charset.defaultCharset());
                } else if (data instanceof StringReader) {
                    /*char[] buffer = new char[((StringReader) data).available()];
                    ((StringReader) data).read(buffer);
                    return new String(buffer);*/
                } else if (data instanceof String) {
                    return (String) data;
                }
            } catch (UnsupportedFlavorException | IOException ex) {
            }
        }
        return null;
    }

    @Override
    public void exportToClipboard(JComponent comp, Clipboard clip, int action) {
        Transferable content = createTransferable(comp);
        clip.setContents(content, null);
        /* Disabled as TransferableRichText.getTransferData is called afterwards by netbeans and the offset have changed
        if (action == MOVE) {
            JTextComponent textField = (JTextComponent) comp;
            try {
                textField.getDocument().remove(textField.getSelectionStart(), textField.getSelectionEnd() - textField.getSelectionStart());
            } catch (BadLocationException ex) {
                Exceptions.printStackTrace(ex);
            }
        }*/
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        textField = (JTextComponent) c;
        int selectionStart = textField.getSelectionStart();
        int selectionLength = textField.getSelectionEnd() - selectionStart;
        return new TransferableRichText(textField.getDocument(), selectionStart, selectionLength);
    }

    @Override
    public int getSourceActions(JComponent c) {
        return COPY_OR_MOVE;
    }

    @Override
    public boolean canImport(TransferHandler.TransferSupport support) {
        return true;
    }

    private void addRichtText(String richText, JTextComponent textField, EditorKit richEditor) {
        try {
            StringReader reader = new StringReader(richText);
            Document doc = richEditor.createDefaultDocument();
            richEditor.read(reader, doc, 0);

            addRichText(textField, doc.getRootElements()[0]);
        } catch (IOException | BadLocationException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private void addRichText(JTextComponent textField, Element... elements) throws BadLocationException {
        for (int i = 0; i < elements.length; i++) {
            Element element = elements[i];
            if (element.isLeaf()) {
                String text = element.getDocument().getText(element.getStartOffset(), element.getEndOffset() - element.getStartOffset());
                textField.getDocument().insertString(textField.getSelectionStart(), text, element.getAttributes());
            } else {
                Element[] children = new Element[element.getElementCount()];
                for (int j = 0; j < children.length; j++) {
                    children[j] = element.getElement(j);
                }
                addRichText(textField, children);
            }
        }
    }

}
