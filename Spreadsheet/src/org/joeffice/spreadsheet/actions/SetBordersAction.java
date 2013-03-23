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
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellUtil;
import org.joeffice.desktop.ui.OfficeTopComponent;
import org.joeffice.spreadsheet.POIUtils;
import org.joeffice.spreadsheet.SpreadsheetTopComponent;
import org.joeffice.spreadsheet.tablemodel.SheetTableModel;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

/**
 * Set borders to the selected cells.
 *
 * Note that the first version only set a plain black border to the first selected cell.
 *
 * @author Anthony Goubard - Japplis
 */
@ActionID(
        category = "Edit/Office/Spreadsheet",
        id = "org.joeffice.spreadsheet.actions.SetBordersAction")
@ActionRegistration(
        displayName = "#CTL_SetBordersAction")
@ActionReferences(value = {
    @ActionReference(path = "Office/Spreadsheet/Toolbar", position = 600)})
@Messages("CTL_SetBordersAction=Set borders")
public final class SetBordersAction extends AbstractAction {

    @Override
    public void actionPerformed(ActionEvent e) {
        SpreadsheetTopComponent currentTopComponent = OfficeTopComponent.getSelectedComponent(SpreadsheetTopComponent.class);
        if (currentTopComponent != null) {
            JTable currentTable = currentTopComponent.getSelectedTable();
            int selectedRow = currentTable.getSelectedRow();
            int selectedColumn = currentTable.getSelectedColumn();
            Sheet currentSheet = currentTopComponent.getSpreadsheetComponent().getSelectedSheet().getSheet();
            Cell cell = POIUtils.getCell(true, currentSheet, selectedRow, selectedColumn);
            Workbook workbook = cell.getSheet().getWorkbook();
            CellUtil.setCellStyleProperty(cell, workbook, CellUtil.BORDER_TOP, CellStyle.BORDER_MEDIUM);
            CellUtil.setCellStyleProperty(cell, workbook, CellUtil.BORDER_LEFT, CellStyle.BORDER_MEDIUM);
            CellUtil.setCellStyleProperty(cell, workbook, CellUtil.BORDER_BOTTOM, CellStyle.BORDER_MEDIUM);
            CellUtil.setCellStyleProperty(cell, workbook, CellUtil.BORDER_RIGHT, CellStyle.BORDER_MEDIUM);
            ((SheetTableModel) currentTable.getModel()).fireTableCellUpdated(selectedRow, selectedColumn);
        }
    }
}
