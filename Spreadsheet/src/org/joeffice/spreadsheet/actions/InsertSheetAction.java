/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.joeffice.spreadsheet.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.joeffice.spreadsheet.SpreadsheetComponent;
import org.joeffice.spreadsheet.SpreadsheetTopComponent;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.TopComponent;

/**
 * Insert a sheet after the selected sheet.
 *
 * @author Anthony Goubard - Japplis
 */
@ActionID(
        category = "Edit/Office/Spreadsheet",
        id = "org.joeffice.spreadsheet.actions.InsertSheetAction")
@ActionRegistration(
        iconBase = "org/joeffice/spreadsheet/actions/table_add.png",
        displayName = "#CTL_InsertSheetAction")
@ActionReferences(value = {
    @ActionReference(path = "Office/Spreadsheet/Toolbar", position = 500),
    @ActionReference(path = "Office/Spreadsheet/Tabs/Popup")})
@Messages({"CTL_InsertSheetAction=Insert sheet",
        "MSG_AskSheetName=Enter the sheet name:",
        "MSG_DefaultSheetName=Page"})
public final class InsertSheetAction extends AbstractAction {

    @Override
    public void actionPerformed(ActionEvent e) {
        TopComponent currentTopComponent = TopComponent.getRegistry().getActivated();
        if (currentTopComponent instanceof SpreadsheetTopComponent) {
            SpreadsheetComponent spreadsheet = ((SpreadsheetTopComponent) currentTopComponent).getSpreadsheetComponent();
            String question = NbBundle.getMessage(getClass(), "MSG_AskSheetName");
            String defaultName = NbBundle.getMessage(getClass(), "MSG_DefaultSheetName");
            NotifyDescriptor.InputLine askName = new NotifyDescriptor.InputLine(question, question, NotifyDescriptor.OK_CANCEL_OPTION, NotifyDescriptor.QUESTION_MESSAGE);
            askName.setInputText(defaultName);
            Object dialogResult = DialogDisplayer.getDefault().notify(askName);
            if (dialogResult == NotifyDescriptor.OK_OPTION) {
                String sheetName = askName.getInputText();
                spreadsheet.insertSheet(sheetName);
            }
        }
    }
}
