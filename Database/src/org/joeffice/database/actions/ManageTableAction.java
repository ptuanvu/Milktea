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
package org.joeffice.database.actions;

import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.AbstractAction;
import javax.swing.JTabbedPane;
import org.joeffice.database.JDBCTopComponent;
import org.joeffice.database.ManageTableTopComponent;
import org.joeffice.desktop.ui.OfficeTopComponent;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.Mode;
import org.openide.windows.WindowManager;

@ActionID(
        category = "Edit/Office/Database",
        id = "org.joeffice.database.ManageTableAction")
@ActionRegistration(
        iconBase = "org/joeffice/database/actions/database_gear.png",
        displayName = "#CTL_ManageTableAction")
@ActionReferences({
    @ActionReference(path = "Menu/Tools", position = 1460),
    @ActionReference(path = "Loaders/application/h2/Actions", position = 160)
})
@Messages("CTL_ManageTableAction=Manage table")
public final class ManageTableAction extends AbstractAction {

    @Override
    public void actionPerformed(ActionEvent e) {
        JDBCTopComponent currentTopComponent = OfficeTopComponent.getSelectedComponent(JDBCTopComponent.class);
        if (currentTopComponent != null) {
            String tableName = currentTopComponent.getSelectedTableName();
            File h2File = FileUtil.toFile(currentTopComponent.getDataObject().getPrimaryFile());
            ManageTableTopComponent manageTableTopComponent = new ManageTableTopComponent();
            Mode explorerMode = WindowManager.getDefault().findMode("editor");
            explorerMode.dockInto(manageTableTopComponent);
            manageTableTopComponent.open();
            manageTableTopComponent.requestActive();
            manageTableTopComponent.initData(h2File, tableName);
        }
    }
}
