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

package com.wipro.ats.bdre.datagen.mr;

import com.wipro.ats.bdre.ResolvePath;
import com.wipro.ats.bdre.datagen.Table;
import com.wipro.ats.bdre.datagen.util.Config;
import com.wipro.ats.bdre.datagen.util.TableUtil;
import com.wipro.ats.bdre.md.api.GetGeneralConfig;
import com.wipro.ats.bdre.md.api.GetProcess;
import com.wipro.ats.bdre.md.beans.ProcessInfo;
import com.wipro.ats.bdre.md.beans.RegisterFileInfo;
import com.wipro.ats.bdre.md.beans.table.GeneralConfig;
import com.wipro.ats.bdre.util.OozieUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Properties;


public class Driver extends Configured implements Tool {
    private static final Logger LOGGER = Logger.getLogger(Driver.class);
    /**
     * @param args the cli arguments
     */
    @Override
    public int run(String[] args)
            throws IOException, InterruptedException, ClassNotFoundException {

        Configuration conf = getConf();
        GetGeneralConfig generalConfig = new GetGeneralConfig();
        GeneralConfig gc = generalConfig.byConigGroupAndKey("imconfig", "common.default-fs-name");
        conf.set("fs.defaultFS", gc.getDefaultVal());

        String processId = args[0];
        Path outputDir = new Path(ResolvePath.replaceVars(args[1]));

        Properties dataProps = Config.getDataProperties(processId);
        Properties tableProps = Config.getTableProperties(processId);

        TableUtil tableUtil = new TableUtil();
        Table table = tableUtil.formTableFromConfig(processId);
        FileSystem fs=FileSystem.get(conf);
        LOGGER.info("Default FS ="+conf.get("fs.defaultFS"));
        //set in the conf for mappers to use
        conf.set(Config.SEPARATOR_KEY, tableProps.getProperty("separator"));
        conf.set(Config.PID_KEY,processId);
        conf.setLong(Config.NUM_ROWS_KEY, Long.parseLong(dataProps.getProperty("numRows")));
        conf.setInt(Config.NUM_SPLITS_KEY, Integer.parseInt(dataProps.getProperty("numSplits")));

        Job job = Job.getInstance(conf);
        Path mrOutputPath = new Path(outputDir.toString() + "/MROUT/" + table.getTableName());


        FileOutputFormat.setOutputPath(job, mrOutputPath);
        job.setJobName("Datagen-" + table.getTableName());
        job.setJarByClass(Driver.class);
        job.setMapperClass(RecordGenMapper.class);
        job.setNumReduceTasks(0);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        job.setInputFormatClass(RangeInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);
        job.waitForCompletion(true);

        //merge and create a single file

        Path srcDir = mrOutputPath;
        Path destFile = new Path(outputDir.toString() + "/" + table.getTableName());
        FileUtil.copyMerge(fs, srcDir, fs, destFile, true, conf, "");

        //Return file info oozie params
        RegisterFileInfo registerFileInfo=new RegisterFileInfo();
        registerFileInfo.setBatchId(null);
        registerFileInfo.setCreationTs(new Timestamp(new Date().getTime()));
        registerFileInfo.setFileHash("0");
        registerFileInfo.setFileSize(0L);
        registerFileInfo.setPath(destFile.toString());
        registerFileInfo.setSubProcessId(Integer.parseInt(processId));
        OozieUtil oozieUtil= new OozieUtil();
        oozieUtil.persistBeanData(registerFileInfo,false);

        GetProcess getProcess = new GetProcess();
        ProcessInfo parentProcessInfo = getProcess.getParentProcess(Integer.valueOf(processId));
        Integer workflowTypeId = parentProcessInfo.getWorkflowId();
        Integer parentProcessId = parentProcessInfo.getProcessId();
        LOGGER.info("workflowTypeId is "+ workflowTypeId);
        LOGGER.info("paretProcessId is "+ parentProcessId);

        if(workflowTypeId == 3) {
            try {
                String homeDir = System.getProperty("user.home");
                // String parentProcessId = String.valueOf(Integer.valueOf(processId) - 1);
                String path = homeDir + "/bdre/airflow/" + parentProcessId + "_jobInfo.txt";
                Files.deleteIfExists(Paths.get(path));
                FileWriter fw = new FileWriter(path);
                BufferedWriter bw = new BufferedWriter(fw);

                if (registerFileInfo.getSubProcessId() != null)
                    bw.write("fileInfo.getSubProcessId()::" + registerFileInfo.getSubProcessId().toString() + "\n");
                else
                    bw.write("fileInfo.getSubProcessId()::null\n");

                if (registerFileInfo.getServerId() != null)
                    bw.write("fileInfo.getServerId()::" + registerFileInfo.getServerId().toString() + "\n");
                else
                    bw.write("fileInfo.getServerId()::null\n");

                if (registerFileInfo.getPath() != null)
                    bw.write("fileInfo.getPath()::" + registerFileInfo.getPath() + "\n");
                else
                    bw.write("fileInfo.getPath()::null\n");

                if (registerFileInfo.getFileSize() != null)
                    bw.write("fileInfo.getFileSize()::" + registerFileInfo.getFileSize().toString() + "\n");
                else
                    bw.write("fileInfo.getFileSize()::null\n");

                if (registerFileInfo.getFileHash() != null)
                    bw.write("fileInfo.getFileHash()::" + registerFileInfo.getFileHash().toString() + "\n");
                else
                    bw.write("fileInfo.getFileHash()::null\n");

                if (registerFileInfo.getCreationTs() != null) {
                    String creationTs = registerFileInfo.getCreationTs().toString().replace(" ", "__");//Recovered back in RegisterFile.java.... CreationTs has space(which splits parameter) and ::(creates great problem while creating python dictionaries)
                    LOGGER.info("Creation Ts modified is " + creationTs);
                    bw.write("fileInfo.getCreationTs()::" + creationTs + "\n");
                } else
                    bw.write("fileInfo.getCreationTs()::null\n");

                if (registerFileInfo.getBatchId() != null)
                    bw.write("fileInfo.getBatchId()::" + registerFileInfo.getBatchId().toString() + "\n");
                else
                    bw.write("fileInfo.getBatchId()::null\n");

                if (registerFileInfo.getParentProcessId() != null)
                    bw.write("fileInfo.getParentProcessId()::" + registerFileInfo.getParentProcessId().toString() + "\n");
                else
                    bw.write("fileInfo.getParentProcessId()::null\n");

                if (registerFileInfo.getBatchMarking() != null)
                    bw.write("fileInfo.getBatchMarking()::" + registerFileInfo.getBatchMarking() + "\n");
                else
                    bw.write("fileInfo.getBatchMarking()::null\n");

                bw.close();

            } catch (IOException i) {
                i.printStackTrace();
            }

        }
        return 0;
    }


    public static enum Counters {
        CHECKSUM
    }


}