/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wipro.ats.bdre.md.api;

import com.wipro.ats.bdre.md.beans.ProcessDependencyInfo;
import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

//import org.junit.Ignore;

public class GetProcessDependencyTest {
    private static final Logger LOGGER = Logger.getLogger(GetProcessDependencyTest.class);

    @Ignore
    @Test
    public void testExecute() throws Exception {

        String[] args = {"-p", "10802"};
        GetProcessDependency bs = new GetProcessDependency();
        List<ProcessDependencyInfo> processDependencyInfoList = bs.execute(args);
        for (ProcessDependencyInfo info : processDependencyInfoList) {
            System.out.println("info.getProcessId()+ \",\" + info.getRowType() = " + info.getProcessId() + "," + info.getRowType());
        }
        LOGGER.debug("executed!");
    }
}
