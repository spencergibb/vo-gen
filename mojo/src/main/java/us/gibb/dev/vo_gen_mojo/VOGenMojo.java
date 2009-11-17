/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package us.gibb.dev.vo_gen_mojo;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import us.gibb.dev.vo_gen.VOGen;

/**
 * @goal vo-gen
 * @phase generate-sources
 * @description Genterate Value Objects
 * @requiresDependencyResolution compile
 */
public class VOGenMojo extends AbstractMojo {
    /**
     * @parameter expression="${vo.gentestSourceRoot}"
     */
    File testSourceRoot;

    /**
     * Path where the generated sources should be placed
     * 
     * @parameter expression="${vo.gensourceRoot}"
     *            default-value="${project.build.directory}/generated-sources/vo"
     * @required
     */
    File sourceRoot;

    /**
     * @parameter expression="${project.build.outputDirectory}"
     * @required
     */
    String classesDirectory;

    /**
     * @parameter expression="${project}"
     * @required
     */
    MavenProject project;

    /**
     * Use the compile classpath rather than the test classpath for execution useful if the test dependencies
     * clash with those of vogen
     * 
     * @parameter expression="${vo.useCompileClasspath}" default-value="false"
     */
    boolean useCompileClasspath;

    /**
     * @parameter expression="${vo.testOption}" default-value="testOptionDefault"
     */
    String testOption;

    /**
     * A list of 
     * 
     * @parameter
     * @required
     */
    String packages[];

    /**
     * A list of 
     * 
     * @parameter
     */
    String classes[];

    /**
     * @parameter expression="${vo.javaRoot}" default-value="${basedir}/src/main/java"
     */
    File javaRoot;

    /**
     * @parameter expression="${vo.converterPackage}"
     * @required
     */
    String converterPackage;

    /**
     * Directory in which the "DONE" markers are saved that
     * 
     * @parameter expression="${vo.genmarkerDirectory}"
     *            default-value="${project.build.directory}/vo_gen_mojo_markers"
     */
    File markerDirectory;

    /**
     * The local repository taken from Maven's runtime. Typically $HOME/.m2/repository.
     * 
     * @ parameter expression="${localRepository}" @ readonly @ required
     */
    // private ArtifactRepository localRepository;

    /**
     * Artifact factory, needed to create artifacts.
     * 
     * @ component @ readonly @ required
     */
    // private ArtifactFactory artifactFactory;

    /**
     * The remote repositories used as specified in your POM.
     * 
     * @ parameter expression="${project.repositories}" @ readonly @ required
     */
    // private List<?> repositories;

    /**
     * Artifact repository factory component.
     * 
     * @ component @ readonly @ required
     */
    // private ArtifactRepositoryFactory artifactRepositoryFactory;

    /**
     * The Maven session.
     * 
     * @ parameter expression="${session}" @ readonly @ required
     */
    // private MavenSession mavenSession;

    /**
     * @ component @ readonly @ required
     */
    // private ArtifactResolver artifactResolver;

    public void execute() throws MojoExecutionException {
        //ClassLoaderSwitcher classLoaderSwitcher = new ClassLoaderSwitcher(getLog());
        try {
            VOGen gen = new VOGen();
            gen.setClasses(classes);
            gen.setConverterPackage(converterPackage);
            gen.setSrcDir(javaRoot);
            gen.setPackages(packages);
            gen.setOutDir(sourceRoot);
            gen.setTestOption(testOption);
            gen.setLog(new MojoLog(getLog()));
            gen.generate();
        } catch (Exception e) {
            throw new MojoExecutionException(e.getMessage(), e);

        } finally {
            // cleanup as much as we can.
            //classLoaderSwitcher.restoreClassLoader();
        }
        if (project != null && sourceRoot != null && sourceRoot.exists()) {
            project.addCompileSourceRoot(sourceRoot.getAbsolutePath());
        }
        if (project != null && testSourceRoot != null && testSourceRoot.exists()) {
            project.addTestCompileSourceRoot(testSourceRoot.getAbsolutePath());
        }

        System.gc();
    }
}
