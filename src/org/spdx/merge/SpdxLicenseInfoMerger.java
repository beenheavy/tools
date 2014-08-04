/**
 * Copyright (c) 2014 Gang Ling.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
*/
package org.spdx.merge;

import java.util.ArrayList;
import java.util.Arrays;
import org.spdx.compare.LicenseCompareHelper;
import org.spdx.rdfparser.InvalidSPDXAnalysisException;
import org.spdx.rdfparser.SPDXDocument;
import org.spdx.rdfparser.SPDXNonStandardLicense;

/**
 * Application to merge SPDX documents' non-standard license information and return the results to the merge main class
 * The non-standard license information from the master SPDX document will add to the result arraylist directly.
 * The non-standard license information from the child SPDX document will be compared with license information in the result arraylist.
 * Any new license information will add to the result arraylist with replacing the license ID. 
 *  
 * @author Gang Ling
 *
 */
public class SpdxLicenseInfoMerger {
	
	private SPDXDocument master = null;
	public SpdxLicenseInfoMerger(SPDXDocument masterDoc){
		this.master = masterDoc;
	}
    /**	
     * @param mergeDocs
     * @return licInfoResut
     * @throws InvalidSPDXAnalysisException
     */
	public SPDXNonStandardLicense[] mergeNonStdLic(SPDXDocument[] mergeDocs) throws InvalidSPDXAnalysisException{
		
		//an array to hold the non-standard license info from master SPDX document
		SPDXNonStandardLicense[] masterNonStdLicInfo = cloneNonStdLic(master.getExtractedLicenseInfos());
		
		//an arrayList to hold the final result 
		ArrayList<SPDXNonStandardLicense> retval = new ArrayList<SPDXNonStandardLicense>(Arrays.asList(masterNonStdLicInfo));

		//call constructor and pass master document as parameter 
		SpdxLicenseMapper mapper = new SpdxLicenseMapper(master);
		
		//read each child SPDX document
		for(int i = 1; i < mergeDocs.length; i++){
			
			//an array to hold non-standard license info from current child SPDX document and clone the data
			SPDXNonStandardLicense[] subNonStdLicInfo = cloneNonStdLic(mergeDocs[i].getExtractedLicenseInfos());
									
			//compare non-standard license info. Note: the method may run as over-comparison
	        for(int k = 0; k < retval.size(); k++){
	        	boolean foundTextMatch = false;
	        	for(int p = 0; p < subNonStdLicInfo.length; p++){
	        		if(LicenseCompareHelper.isLicenseTextEquivalent(retval.get(k).getText(), subNonStdLicInfo[p].getText())){
	        			foundTextMatch = true;
	           		}
	        		if(!foundTextMatch){
	        	        retval.add((mapper.mappingNonStdLic(mergeDocs[i], subNonStdLicInfo[p])));	        	             			
	        		}
	        	}
	        } 			
		}
		SPDXNonStandardLicense[] nonStdLicMergeResult = new SPDXNonStandardLicense[retval.size()];
		retval.toArray(nonStdLicMergeResult);
		retval.clear();
		return nonStdLicMergeResult;
	}
	
	/**
	 * 
	 * @param orgNonStdLicArray
	 * @return clonedNonStdLicArray
	 */
	public SPDXNonStandardLicense[] cloneNonStdLic(SPDXNonStandardLicense[] orgNonStdLicArray){
		SPDXNonStandardLicense[] clonedNonStdLicArray = new SPDXNonStandardLicense[orgNonStdLicArray.length];
		for(int q = 0; q < orgNonStdLicArray.length; q++){
			clonedNonStdLicArray[q] = (SPDXNonStandardLicense) orgNonStdLicArray[q].clone();
		}
		return clonedNonStdLicArray;
	}
}