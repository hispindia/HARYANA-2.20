package org.hisp.dhis.birtreports.action;
/*
 * Copyright (c) 2004-2007, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import com.opensymphony.xwork2.Action;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Gaurav
 * @version $Id$
 */
public class ReportsHomePageAction
        implements Action {

    public List<BirtReportTypeObject> birtReportTypeObjectList = new ArrayList<BirtReportTypeObject>();
    public List<BirtReportOptionsStack> birtReportOptionsList = new ArrayList<BirtReportOptionsStack>();

    public String execute() {

        String birtHomeFolder = System.getenv("BIRT_HOME");
        System.out.println("* Birt home points to " + "\"" + birtHomeFolder + "\"");

        File birtFolder = new File(birtHomeFolder);

        if (!birtFolder.exists()) {
            System.out.println("Error: Birt Home Not Defined");
            return ERROR;
        }

        File[] contentFiles = birtFolder.listFiles();
        Integer typeID = new Integer(0);
        System.out.println("-------------------------------------------------------//-------------------------------------------------------------");
        for (File reportTypeFolder : contentFiles) {
            BirtReportTypeObject birtReportTypeObject = new BirtReportTypeObject();
            birtReportTypeObject.setReportTypeName(reportTypeFolder.getName());
            birtReportTypeObject.setReportTypeId(typeID);
            ++typeID;
            birtReportTypeObjectList.add(birtReportTypeObject);
            System.out.println("<option value=" + birtReportTypeObject.getReportTypeId() + ">" + birtReportTypeObject.getReportTypeName() + "</option>");
            if (reportTypeFolder.isDirectory()) {
                File[] categoryFiles = reportTypeFolder.listFiles();
                for (File reportFile : categoryFiles) {
                    BirtReportOptionsStack birtReportOptionsStack = new BirtReportOptionsStack();
                    birtReportOptionsStack.setReportName(reportFile.getName().replace("_", " ").replace(".rptdesign", ""));
                    birtReportOptionsStack.setReportDesignFileName(birtHomeFolder + birtReportTypeObject.getReportTypeName() + File.separator + reportFile.getName());
                    birtReportOptionsStack.setReportClass("sub_" + birtReportTypeObject.getReportTypeId());
                    birtReportOptionsList.add(birtReportOptionsStack);
                    System.out.println("<option value=" + birtReportOptionsStack.getReportDesignFileName() + " class=" + birtReportOptionsStack.getReportClass() + ">" + birtReportOptionsStack.getReportName() + "</option>");
                }
            }
        }
        System.out.println("--------------------------------------------------------//------------------------------------------------------------");

        return SUCCESS;
    }
}