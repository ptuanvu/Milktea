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
package org.joeffice.desktop.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractAction;
import javax.swing.JEditorPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.text.DataEditorSupport;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;

/**
 * Change the font style to italic.
 *
 * @author Anthony Goubard - Japplis
 */
@ActionID(
        category = "Edit/Office",
        id = "org.joeffice.desktop.actions.ItalicAction")
@ActionRegistration(
        iconBase = "org/joeffice/desktop/actions/text_italic.png",
        displayName = "#CTL_ItalicAction")
@ActionReferences({
    @ActionReference(path = "Menu/Edit", position = 1530),
    @ActionReference(path = "Toolbars/Font", position = 3200),
    @ActionReference(path = "Shortcuts", name = "D-I")
})
@Messages("CTL_ItalicAction=Italic")
public final class ItalicAction extends AbstractAction {

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO implement action body
        DataEditorSupport editorSupport = Lookup.getDefault().lookup(DataEditorSupport.class);
        JEditorPane[] openedPanes = editorSupport.getOpenedPanes();
        for (JEditorPane openedPane : openedPanes) {
            int startSelection = openedPane.getSelectionStart();
            int endSelection = openedPane.getSelectionEnd();
            int selectionLength = endSelection - startSelection;
            if (selectionLength > 0) {
                SimpleAttributeSet italicSet = new SimpleAttributeSet();
                StyleConstants.setItalic(italicSet, true);
                editorSupport.getDocument().setCharacterAttributes(startSelection, endSelection - startSelection, italicSet, false);
            } else {
                // todo
            }
        }
    }
}
