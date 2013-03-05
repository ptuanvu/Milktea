/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.joeffice.drawing;

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.net.MalformedURLException;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import org.apache.batik.swing.JSVGCanvas;
import org.joeffice.desktop.file.OfficeDataObject;
import org.joeffice.desktop.ui.OfficeTopComponent;
import org.netbeans.api.settings.ConvertAsProperties;
import org.netbeans.api.visual.widget.Scene;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;
import org.openide.util.Utilities;

/**
 * Top component which displays a drawing.
 */
@ConvertAsProperties(
        dtd = "-//org.joeffice.drawing//Drawing//EN",
        autostore = false)
@TopComponent.Description(
        preferredID = "DrawingTopComponent",
        iconBase="org/joeffice/drawing/drawing-16.png",
        persistenceType = TopComponent.PERSISTENCE_ONLY_OPENED)
@TopComponent.Registration(mode = "explorer", openAtStartup = false)
@ActionID(category = "Window", id = "org.joeffice.drawing.DrawingTopComponent")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_DrawingAction",
        preferredID = "DrawingTopComponent")
@Messages({
    "CTL_DrawingAction=Drawing",
    "CTL_DrawingTopComponent=Drawing Window",
    "HINT_DrawingTopComponent=This is a Drawing window"
})
public final class DrawingTopComponent extends OfficeTopComponent {

    //private Scene scene; // Visual library

    public DrawingTopComponent() {
    }

    public DrawingTopComponent(OfficeDataObject dataObject) {
        super(dataObject);
    }

    @Override
    protected JComponent createMainComponent() {
        //scene = new Scene();
        //JScrollPane scenePane = new JScrollPane(scene.createView());
        JSVGCanvas svgCanvas = new JSVGCanvas();
        return svgCanvas;
    }

    @Override
    protected void loadDocument() {
        JSVGCanvas svgCanvas = (JSVGCanvas) getMainComponent();
        File svgFile = FileUtil.toFile(getDataObject().getPrimaryFile());
        try {
            String uri = Utilities.toURI(svgFile).toURL().toString();
            svgCanvas.setURI(uri);
        } catch (MalformedURLException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }
}