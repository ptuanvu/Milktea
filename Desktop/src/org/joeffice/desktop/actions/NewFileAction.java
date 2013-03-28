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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.joeffice.desktop.file.OfficeDataObject;
import org.joeffice.desktop.ui.OfficeTopComponent;

import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.NbBundle.Messages;
import org.openide.util.NbPreferences;
import org.openide.windows.WindowManager;

/**
 * Class used to create a new document. This class is temporary until the annotation requireProject=false is supported.
 *
 * @author Anthony Goubard - Japplis
 */
// Wait for http://netbeans.org/bugzilla/show_bug.cgi?id=186943 to be released
@ActionID(
        category = "File",
        id = "org.joeffice.desktop.actions.NewFileAction")
@ActionRegistration(
        iconBase = "org/joeffice/desktop/actions/add.png",
        displayName = "#CTL_NewFileAction")
@ActionReference(path = "Menu/File", position = 90)
@Messages({"CTL_NewFileAction=New File...", "MSG_ChooseExtension=Please choose or provide a file type"})
public final class NewFileAction extends AbstractAction {

    @Override
    public void actionPerformed(ActionEvent ae) {
        //if (0 == 0) throw new IllegalArgumentException("This is a test for the error reporting URL. If you read this you can remove the issue as it's not a bug but a test from another Netbeans platforma app.");
        JFileChooser newFileChooser = createFileChooser();
        int saveResult = newFileChooser.showSaveDialog(WindowManager.getDefault().getMainWindow());
        if (saveResult == JFileChooser.APPROVE_OPTION) {
            File savedFile = getSelectedFile(newFileChooser);
            FileObject template = getFileTemplate(savedFile);
            if (template == null) {
                showChooseExtensionMessage();
                actionPerformed(ae);
            } else {
                try {
                    FileObject createdFile = createFileFromTemplate(template, savedFile);
                    open(createdFile);
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
    }

    private JFileChooser createFileChooser() {
        JFileChooser newFileChooser = new JFileChooser();
        String defaultLocation = NbPreferences.forModule(NewFileAction.class).get("file.location", System.getProperty("user.home"));
        newFileChooser.setCurrentDirectory(new File(defaultLocation));
        newFileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
        addFileFilters(newFileChooser);
        return newFileChooser;
    }

    private void addFileFilters(JFileChooser chooser) {
        List<DataObject> possibleObjects = findDataObject("Templates/Other");
        for (DataObject dataObject : possibleObjects) {
            if (dataObject instanceof OfficeDataObject) {
                FileFilter filter = new OfficeFileFilter(dataObject);
                chooser.addChoosableFileFilter(filter);
            }
        }
        chooser.setAcceptAllFileFilterUsed(true);
    }

    public List<DataObject> findDataObject(String key) {
        List<DataObject> templates = new ArrayList<>();
        FileObject fo = FileUtil.getConfigFile(key);
        if (fo != null && fo.isValid()) {
            addFileObject(fo, templates);
        }
        return templates;
    }

    private void addFileObject(FileObject fileObject, List<DataObject> templates) {
        if (fileObject.isFolder()) {
            for (FileObject child : fileObject.getChildren()) {
                addFileObject(child, templates);
            }
        } else {
            try {
                DataObject dob = DataObject.find(fileObject);
                templates.add(dob);
            } catch (DataObjectNotFoundException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    public File getSelectedFile(JFileChooser newFileChooser) {
        File savedFile = newFileChooser.getSelectedFile();
        NbPreferences.forModule(NewFileAction.class).put("file.location", savedFile.getParentFile().getAbsolutePath());
        FileFilter filter = newFileChooser.getFileFilter();
        if (!savedFile.getName().contains(".") && filter != null && filter.getDescription().contains(".")) {
            String extension = filter.getDescription().substring(filter.getDescription().indexOf('.'));
            savedFile = new File(savedFile.getAbsolutePath() + extension);
        }
        return savedFile;
    }

    public FileObject getFileTemplate(File savedFile) {
        List<DataObject> possibleObjects = findDataObject("Templates/Other");
        for (final DataObject dataObject : possibleObjects) {
            if (dataObject instanceof OfficeDataObject && savedFile.getName().endsWith(dataObject.getPrimaryFile().getExt())) {
                return dataObject.getPrimaryFile();
            }
        }
        return null;
    }

    private void showChooseExtensionMessage() {
        String provideExtensionMessage = NbBundle.getMessage(NewFileAction.class, "MSG_ChooseExtension");
        NotifyDescriptor provideExtensionDialog =
                new NotifyDescriptor.Message(provideExtensionMessage, NotifyDescriptor.INFORMATION_MESSAGE);
        DialogDisplayer.getDefault().notify(provideExtensionDialog);
    }

    public FileObject createFileFromTemplate(FileObject template, File savedFile) throws IOException {
        try (InputStream input = template.getInputStream();
                FileOutputStream output = new FileOutputStream(savedFile);) {
            FileUtil.copy(input, output);
            FileObject savedFileObject = FileUtil.toFileObject(FileUtil.normalizeFile(savedFile));
            return savedFileObject;
        }
    }

    private void open(FileObject fileToOpen) throws DataObjectNotFoundException {
        DataObject fileDataObject = DataObject.find(fileToOpen);
        OpenCookie openCookie = fileDataObject.getCookie(OpenCookie.class);
        if (openCookie != null) {
            openCookie.open();
        }
    }

    private class OfficeFileFilter extends FileFilter {

        private DataObject dataObject;

        private OfficeFileFilter(DataObject dataObject) {
            this.dataObject = dataObject;
        }

        @Override
        public boolean accept(File file) {
            return file.isDirectory() || file.getName().endsWith(dataObject.getPrimaryFile().getExt());
        }

        @Override
        public String getDescription() {
            return "*." + dataObject.getPrimaryFile().getExt();
        }
    }
}
