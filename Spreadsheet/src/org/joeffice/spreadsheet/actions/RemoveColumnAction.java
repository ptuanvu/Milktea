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
package org.joeffice.spreadsheet.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JTable;
import org.joeffice.desktop.ui.OfficeTopComponent;
import org.joeffice.spreadsheet.SpreadsheetTopComponent;
import org.joeffice.spreadsheet.tablemodel.SheetTableModel;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

/**
 * Removes the first selected column.
 *
 * @author Anthony Goubard - Japplis
 */
@ActionID(
        category = "Edit/Office/Spreadsheet",
        id = "org.joeffice.spreadsheet.actions.RemoveColumnAction")
@ActionRegistration(
        displayName = "#CTL_RemoveColumnAction")
@ActionReferences(value = {
    @ActionReference(path = "Office/Spreadsheet/Toolbar", position = 400)})
@Messages("CTL_RemoveColumnAction=Remove Column")
public final class RemoveColumnAction extends AbstractAction {

    @Override
    public void actionPerformed(ActionEvent e) {
        SpreadsheetTopComponent currentTopComponent = OfficeTopComponent.getSelectedComponent(SpreadsheetTopComponent.class);
        if (currentTopComponent != null) {
            JTable currentTable = currentTopComponent.getSelectedTable();
            int selectedColumn = currentTable.getSelectedColumn();
            ((SheetTableModel) currentTable.getModel()).removeColumn(selectedColumn);
        }
    }
}
