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

package us.gibb.dev.vo_gen;

import japa.parser.JavaParser;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.TypeDeclaration;
import japa.parser.ast.body.VariableDeclarator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

/**
 */
public class VOGen {
    private Log log = new JavaUtilLog(VOGen.class);
    /**
     * Path where the generated sources should be placed
     */
    private File outDir;

    /**
     */
    private String testOption;

    /**
     * A list of 
     */
    private String packages[];

    /**
     */
    private String classes[];

    /**
     */
    private File srcDir;

    /**
     */
    private String defaultPackage;
    private VelocityEngine ve;
    
    public VOGen() throws Exception {
        ve = new VelocityEngine();
        ve.addProperty(Velocity.RESOURCE_LOADER, "class");
        ve.addProperty("class.resource.loader.description", "Velocity Classpath Resource Loader");
        ve.addProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        ve.init();
    }

    public Log getLog() {
        return log;
    }

    public void setLog(Log log) {
        this.log = log;
    }

    public File getOutDir() {
        return outDir;
    }

    public void setOutDir(File sourceRoot) {
        this.outDir = sourceRoot;
    }

    public String getTestOption() {
        return testOption;
    }

    public void setTestOption(String testOption) {
        this.testOption = testOption;
    }

    public String[] getPackages() {
        return packages;
    }

    public void setPackages(String[] packages) {
        this.packages = packages;
    }

    public String[] getClasses() {
        return classes;
    }

    public void setClasses(String[] classes) {
        this.classes = classes;
    }

    public File getSrcDir() {
        return srcDir;
    }

    public void setSrcDir(File javaRoot) {
        this.srcDir = javaRoot;
    }

    public String getDefaultPackage() {
        return defaultPackage;
    }

    public void setDefaultPackage(String converterPackage) {
        this.defaultPackage = converterPackage;
    }

    public void generate() throws GenerationException {

        try {
            log.info("classpath: "+ System.getProperty("java.class.path"));
            if (outDir.exists()) {
                FileUtils.cleanDirectory(outDir);
            }
            for (String packageName : packages) {
                
                File directory = new File(srcDir, packageName.replace('.', '/'));
                log.info("src: "+directory);
                
                Map<String, Object> ctxt = new HashMap<String, Object>();
                ctxt.put("defaultPackage", defaultPackage);
                
                Collection<File> javaFiles = getJavaFiles(directory);
                LinkedHashMap<String, Map<String, Object>> classInfos = new LinkedHashMap<String, Map<String, Object>>();
                ctxt.put("classes", classInfos);
                
                for (File file : javaFiles) {
                    CompilationUnit cu = JavaParser.parse(file);

                    if (cu.getTypes() != null && !cu.getTypes().isEmpty()) {
                        if (cu.getTypes().size() > 1) {
                            log.warn("Unable to handle {0} types in {1}", cu.getTypes().size(), file.getAbsolutePath());
                        }
                        
                        TypeDeclaration type = cu.getTypes().get(0);
                        
                        Map<String, Object> info = new HashMap<String, Object>();
                        classInfos.put(packageName+"."+type.getName(), info);
                        info.put("name", type.getName());
                        info.put("packageName", packageName);
                        String newPackage = packageName+".vo";
                        info.put("newPackage", newPackage);
                        String newName = type.getName()+"VO";
                        info.put("newName", newName);
                        info.put("newPackageDir", newPackage.replace('.', '/'));
                        info.put("newFileName", newName+".java");
                        
                        LinkedHashMap<String, Map<String, String>> fields = new LinkedHashMap<String, Map<String, String>>();
                        info.put("fields", fields);
                        for (BodyDeclaration member : type.getMembers()) {
                            if (member instanceof FieldDeclaration) {
                                FieldDeclaration fieldDecl = (FieldDeclaration) member;
                                List<VariableDeclarator> variables = fieldDecl.getVariables();
                                for (VariableDeclarator var : variables) {
                                    Map<String, String> field = new HashMap<String, String>();
                                    String name = var.getId().getName();
                                    field.put("name", name);
                                    String fieldType = fieldDecl.getType().toString();
                                    field.put("type", fieldType);
                                    String getter = "get"+StringUtils.capitalize(name);
                                    if (fieldType.equals("boolean")) {
                                        getter = "is"+StringUtils.capitalize(name);
                                    }
                                    field.put("getter", getter);
                                    String setter = "set"+StringUtils.capitalize(name);
                                    field.put("setter", setter);
                                    //TODO: only put if has getter and setter method
                                    boolean hasGetter = hasMethod(getter, type, fieldType, null);
                                    boolean hasSetter = hasMethod(setter, type, "void", fieldType);
                                    if (hasGetter && hasSetter) {
                                        fields.put(name+":"+fieldType, field);
                                    }
                                }
                            }
                        }
                    }
                }
                MapUtils.verbosePrint(System.out, "ctxt", ctxt);
                                
                for (Map<String, Object> classInfo : classInfos.values()) {
                    write("templates/VO.vm", classInfo, getFile((String) classInfo.get("newPackageDir"), (String) classInfo.get("newFileName")));
                }
                
                write("templates/VOs.vm", ctxt, getFile(defaultPackage.replace('.', '/'), "VO.java"));
            }
        } catch (Exception e) {
            log.error(e);
            throw new GenerationException(e);
        } finally {
            // cleanup as much as we can.
        }
    }

    private boolean hasMethod(String methodName, TypeDeclaration type, String returnType, String paramType) {
        for (BodyDeclaration member : type.getMembers()) {
            if (member instanceof MethodDeclaration) {
                MethodDeclaration method = (MethodDeclaration) member;
                if (method.getName().equals(methodName)) {
                    // check return type
                    if (method.getType() == null || !method.getType().toString().equals(returnType)) {
                        continue;
                    }

                    // check param type
                    if (paramType == null && (method.getParameters() == null || method.getParameters().isEmpty())) {
                        return true;
                    }
                    if (paramType != null && method.getParameters() != null && 
                            method.getParameters().size() == 1 &&
                            method.getParameters().get(0).getType().toString().equals(paramType)) 
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private File getFile(String pkgName, String fileName) throws IOException {
        File pkg = new File(outDir, pkgName);
        FileUtils.forceMkdir(pkg);
        File file = new File(pkg, fileName);
        return file;
    }

    private void write(String template, Map<?, ?> ctxt, File out) throws Exception {
        VelocityContext velocityContext = new VelocityContext(ctxt);
        FileWriter writer = new FileWriter(out);
        ve.mergeTemplate(template, "UTF-8", velocityContext, writer);
        writer.close();
    }

    @SuppressWarnings("unchecked")
    private Collection<File> getJavaFiles(File directory) {
        return FileUtils.listFiles(directory, new String[]{"java"}, false);
    }
}
