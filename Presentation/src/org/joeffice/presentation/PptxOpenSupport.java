package org.joeffice.presentation;

import org.openide.cookies.CloseCookie;
import org.openide.cookies.OpenCookie;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.OpenSupport;
import org.openide.windows.CloneableTopComponent;

/**
 * Cookie for opening (and closing) the pptx files.
 * In this file the data object is associated with the top element used to visualize the document.
 *
 * @author Anthony Goubard - Japplis
 */
public class PptxOpenSupport extends OpenSupport implements OpenCookie, CloseCookie {

    public PptxOpenSupport(MultiDataObject.Entry docxEntry) {
        super(docxEntry);
    }

    @Override
    protected CloneableTopComponent createCloneableTopComponent() {
        return new SlidesTopComponent((PptxDataObject) entry.getDataObject());
    }
}
