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
package org.joeffice.spreadsheet;

import static javax.swing.JLayeredPane.DEFAULT_LAYER;
import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS;
import static javax.swing.ScrollPaneConstants.UPPER_LEFT_CORNER;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.text.DefaultEditorKit;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.joeffice.spreadsheet.actions.ClipboardAction;

import org.joeffice.spreadsheet.renderer.CellRenderer;
import org.joeffice.spreadsheet.renderer.TableColumnAdjuster;
import org.joeffice.spreadsheet.rows.JScrollPaneAdjuster;
import org.joeffice.spreadsheet.rows.JTableRowHeaderResizer;
import org.joeffice.spreadsheet.rows.RowTable;
import org.joeffice.spreadsheet.tablemodel.SheetTableModel;

/**
 * Component that displays one sheet.
 *
 * @author Anthony Goubard - Japplis
 */
public class SheetComponent extends JPanel {

    public static final short EXCEL_COLUMN_WIDTH_FACTOR = 256;
    public static final int UNIT_OFFSET_LENGTH = 7;
    public static final int CELL_HEIGHT_MARGINS = 2;

    private SpreadsheetComponent spreadsheetComponent;

    private JLayeredPane layers;
    private JTable sheetTable;
    private Sheet sheet;

    public SheetComponent(Sheet sheet, SpreadsheetComponent spreadsheetComponent) {
        this.sheet = sheet;
        this.spreadsheetComponent = spreadsheetComponent;
        initComponent();
    }

    private void initComponent() {
        sheetTable = createTable(sheet);
        listenToChanges();
        layers = createSheetLayers(sheetTable);

        JScrollPane scrolling = new JScrollPane(VERTICAL_SCROLLBAR_ALWAYS, HORIZONTAL_SCROLLBAR_ALWAYS);
        scrolling.setViewportView(layers);
        scrolling.setColumnHeaderView(sheetTable.getTableHeader());
        JTable rowHeaders = createRowHeaders(sheetTable);
        scrolling.setRowHeaderView(rowHeaders);
        scrolling.setCorner(UPPER_LEFT_CORNER, rowHeaders.getTableHeader());
        new JTableRowHeaderResizer(scrolling).setEnabled(true);
        new JScrollPaneAdjuster(scrolling);
        scrolling.getVerticalScrollBar().setUnitIncrement(16);

        setLayout(new BorderLayout());
        add(scrolling);
    }

    public JTable createTable(Sheet sheet) {
        SheetTableModel sheetTableModel = new SheetTableModel(sheet);
        JTable table = new SheetTable(sheetTableModel);

        table.setDefaultRenderer(Cell.class, new CellRenderer());
        //TableCellEditor editor = new DefaultCellEditor(new JTextField());
        TableCellEditor editor = new org.joeffice.spreadsheet.editor.CellEditor();
        table.setDefaultEditor(Cell.class, editor);
        int columnsCount = sheetTableModel.getColumnCount();
        for (int i = 0; i < columnsCount; i++) {
            TableColumn tableColumn = table.getColumnModel().getColumn(i);
            tableColumn.setCellRenderer(new CellRenderer());
            tableColumn.setCellEditor(editor);
            int widthUnits = sheet.getColumnWidth(i);
            tableColumn.setPreferredWidth(widthUnitsToPixel(widthUnits));
        }

        int rowCount = sheetTableModel.getRowCount();
        for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row != null) {
                int cellHeight = (int) Math.ceil(sheet.getRow(rowIndex).getHeightInPoints());
                cellHeight += CELL_HEIGHT_MARGINS;
                table.setRowHeight(rowIndex, cellHeight);
            }
        }

        table.setAutoscrolls(true);
        table.setFillsViewportHeight(true);
        JLabel tableHeader = (JLabel) table.getTableHeader().getDefaultRenderer();
        tableHeader.setHorizontalAlignment(SwingConstants.CENTER);

        // XXX This is OK for one block but it doesn't work for 2 blocks, also selecting row no longer works
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        table.setCellSelectionEnabled(true);

        table.setDragEnabled(true);
        table.setDropMode(DropMode.ON_OR_INSERT);

        TableColumnAdjuster tca = new TableColumnAdjuster(table, 20);
        if (sheet.getDefaultColumnWidth() == -1) {
            table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            tca.setOnlyAdjustLarger(true);
            tca.setLeaveEmptyAsIs(true);
            tca.adjustColumns();
        }

        Action cutAction = new ClipboardAction(DefaultEditorKit.cutAction);
        Action copyAction = new ClipboardAction(DefaultEditorKit.copyAction);
        Action pasteAction = new ClipboardAction(DefaultEditorKit.pasteAction);
        table.getActionMap().put(DefaultEditorKit.cutAction, cutAction);
        table.getActionMap().put(DefaultEditorKit.copyAction, copyAction);
        table.getActionMap().put(DefaultEditorKit.pasteAction, pasteAction);

        table.setIntercellSpacing(new Dimension(0, 0));

        if (!sheet.isDisplayGridlines()) {
            table.setShowGrid(false);
        }
        return table;
    }

    // From http://www.chka.de/swing/table/row-headers/RowHeaderTable.java Christian Kaufhold
    public JTable createRowHeaders(JTable sheetTable) {
        JTable rowHeaders = new RowTable(sheetTable);
        return rowHeaders;
    }

    // From http://stackoverflow.com/questions/6663591/jtable-inside-jlayeredpane-inside-jscrollpane-how-do-you-get-it-to-work
    public JLayeredPane createSheetLayers(final JTable table) {
        JLayeredPane layers = new JLayeredPane() {
            @Override
            public Dimension getPreferredSize() {
                return table.getPreferredSize();
            }

            @Override
            public void setSize(int width, int height) {
                super.setSize(width, height);
                table.setSize(width, height);
            }

            @Override
            public void setSize(Dimension d) {
                super.setSize(d);
                table.setSize(d);
            }
        };
        // NB you must use new Integer() - the int version is a different method
        layers.add(table, new Integer(DEFAULT_LAYER), 0);
        return layers;
    }

    // From http://apache-poi.1045710.n5.nabble.com/Excel-Column-Width-Unit-Converter-pixels-excel-column-width-units-td2301481.html
    public static int widthUnitsToPixel(int widthUnits) {
        int pixels = (widthUnits / EXCEL_COLUMN_WIDTH_FACTOR) * UNIT_OFFSET_LENGTH;

        int offsetWidthUnits = widthUnits % EXCEL_COLUMN_WIDTH_FACTOR;
        pixels += Math.round((float) offsetWidthUnits / ((float) EXCEL_COLUMN_WIDTH_FACTOR / UNIT_OFFSET_LENGTH));

        return pixels;
    }

    public void listenToChanges() {
        sheetTable.getModel().addTableModelListener(new TableModelListener() {

            @Override
            public void tableChanged(TableModelEvent e) {
                getSpreadsheetComponent().setModified(true);
            }
        });
    }

    public JTable getTable() {
        return sheetTable;
    }

    public Sheet getSheet() {
        return sheet;
    }

    public SpreadsheetComponent getSpreadsheetComponent() {
        return spreadsheetComponent;
    }
}
