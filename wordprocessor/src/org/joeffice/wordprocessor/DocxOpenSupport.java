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
package org.joeffice.wordprocessor;

import org.openide.cookies.CloseCookie;
import org.openide.cookies.OpenCookie;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.OpenSupport;
import org.openide.windows.CloneableTopComponent;

/**
 * Cookie for opening (and closing) the docx files.
 * In this file the data object is associated with the top element used to visualize the document.
 *
 * @author Anthony Goubard - Japplis
 */
public class DocxOpenSupport extends OpenSupport implements OpenCookie, CloseCookie {

    public DocxOpenSupport(MultiDataObject.Entry docxEntry) {
        super(docxEntry);
    }

    @Override
    protected CloneableTopComponent createCloneableTopComponent() {
        return new WordpTopComponent((DocxDataObject) entry.getDataObject());
    }
}
