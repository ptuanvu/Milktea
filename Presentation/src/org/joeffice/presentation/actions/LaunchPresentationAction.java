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
package org.joeffice.presentation.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.joeffice.desktop.ui.OfficeTopComponent;

import org.joeffice.presentation.FullScreenFrame;
import org.joeffice.presentation.SlidesTopComponent;

import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

/**
 * Action that launch the presentation in full screen mode when invoked.
 *
 * @author Anthony Goubard - Japplis
 */
@ActionID(
        category = "View/Office/Presentation",
        id = "org.joeffice.presentation.actions.LaunchPresentationAction")
@ActionRegistration(
        iconBase = "org/joeffice/presentation/actions/picture_empty.png",
        displayName = "#CTL_LaunchPresentationAction")
@ActionReferences(value = {
    @ActionReference(path = "Office/Presentation/Toolbar", position = 500)})
@Messages("CTL_LaunchPresentationAction=Launch Presentation")
public final class LaunchPresentationAction extends AbstractAction {

    @Override
    public void actionPerformed(ActionEvent ae) {
        SlidesTopComponent currentTopComponent = OfficeTopComponent.getSelectedComponent(SlidesTopComponent.class);
        if (currentTopComponent != null) {
            XMLSlideShow currentPresentation = currentTopComponent.getPresentation();
            FullScreenFrame presentationFrame = new FullScreenFrame();
            presentationFrame.showSlides(currentPresentation);
        }
    }
}
