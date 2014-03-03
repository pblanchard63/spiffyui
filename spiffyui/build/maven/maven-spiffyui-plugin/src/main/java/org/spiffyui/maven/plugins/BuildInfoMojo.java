/*******************************************************************************
 * 
 * Copyright 2011 Spiffy UI Team   
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/
package org.spiffyui.maven.plugins;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.json.JSONObject;

/**
 * Generates a build-info descriptor. It is necessary to use the rev-info mojo
 * prior to invoking this mojo, since the rev-info populates the @{code revision.number}
 * and the @{code revision.date} properties.
 * 
 * @goal build-info
 * @phase generate-resources
 */
public class BuildInfoMojo extends AbstractMojo
{
    /**
     * The maven project.
     * 
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;
    
    /**
     * The character encoding scheme to be applied exporting the JSON document
     *
     * @parameter expression="${encoding}" default-value="UTF-8"
     */
    private String encoding;
    
    /**
     * The character encoding scheme to be applied exporting the JSON document
     *
     * @parameter expression="${dateFormat}" default-value="epoch"
     */
    private String dateFormat;
    
    /**
     * The descriptor file to generate
     * 
     * @parameter expression="${spiffyui.buildinfo.file}"
     *            default-value="${spiffyui.www}/build-info.json"
     * @required
     */
    private File outputFile;
    
    /**
     * The base directory of this project
     * 
     * @parameter expression="${basedir}"
     * @required
     * @readonly
     */
    private File basedir;
    
    @Override
    public void execute()
        throws MojoExecutionException,
            MojoFailureException
    {
        Properties p = project.getProperties();
        
        String date = null;
        
        if ("epoch".equals(dateFormat)) {
            date = "" + new Date().getTime();
        } else {
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
            date = sdf.format(cal.getTime());
        }
        
        List<Dependency> depsList = project.getDependencies();
        Map<String, Dependency> deps = new HashMap<String, Dependency>();
        for (Dependency dep : depsList) {
            deps.put(dep.getArtifactId(), dep);
        }
        
        try {
            File parent = outputFile.getParentFile();
            if (!parent.exists()) {
                FileUtils.forceMkdir(parent);
            }
            
            // Create file
            PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outputFile), encoding));
            
            JSONObject rev = new JSONObject();
            rev.put("number", p.getProperty("revision.number"));
            rev.put("date", p.getProperty("revision.date"));
            
            JSONObject components = new JSONObject();
            Dependency spiffyUiDep = deps.get("spiffyui");
            String spiffyVer = (spiffyUiDep != null) ? spiffyUiDep.getVersion() : "n/a";
            components.put("spiffyui", spiffyVer);

            JSONObject info = new JSONObject();
            info.put("schema", 1);
            info.put("version", project.getVersion());
            info.put("date", date);
            info.put("user", System.getProperties().get("user.name"));
            info.put("components", components);
            info.put("revision", rev);
            info.put("dir", basedir.getAbsolutePath());
 
            out.write(info.toString());
            // Close the output stream
            out.close();
        } catch (Exception e) { // Catch exception if any
            throw new MojoExecutionException(e.getMessage());
        }

    }

}
