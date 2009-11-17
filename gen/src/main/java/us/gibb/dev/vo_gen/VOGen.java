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

import japa.parser.ASTHelper;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.PackageDeclaration;
import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.ModifierSet;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.body.TypeDeclaration;
import japa.parser.ast.expr.AnnotationExpr;
import japa.parser.ast.expr.NullLiteralExpr;
import japa.parser.ast.stmt.BlockStmt;
import japa.parser.ast.stmt.ReturnStmt;
import japa.parser.ast.type.ClassOrInterfaceType;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.io.FileUtils;

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

    public Log getLog() {
        return log;
    }

    public void setLog(Log log) {
        this.log = log;
    }

    /**
     */
    private String converterPackage;

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

    public String getConverterPackage() {
        return converterPackage;
    }

    public void setConverterPackage(String converterPackage) {
        this.converterPackage = converterPackage;
    }

    @SuppressWarnings("unchecked")
    public void generate() throws GenerationException {

        try {
            log.info("classpath: "+ System.getProperty("java.class.path"));
            for (String packageName : packages) {
                
                File directory = new File(srcDir, packageName.replace('.', '/'));
                log.info("src: "+directory);
                
                CompilationUnit converter = new CompilationUnit();
                converter.setImports(new ArrayList<ImportDeclaration>());
                converter.setPackage(new PackageDeclaration(ASTHelper.createNameExpr(converterPackage)));
                ClassOrInterfaceDeclaration converterType = new ClassOrInterfaceDeclaration(ModifierSet.PUBLIC, false, "VO");
                ASTHelper.addTypeDeclaration(converter, converterType);
                
                Collection<File> files = FileUtils.listFiles(directory, new String[]{"java"}, false);
                for (File file : files) {
                    CompilationUnit cu = japa.parser.JavaParser.parse(file);
                    ArrayList<ImportDeclaration> imports = new ArrayList<ImportDeclaration>();
                    imports.add(new ImportDeclaration(ASTHelper.createNameExpr("java.util.Date"), false, false));
                    imports.add(new ImportDeclaration(ASTHelper.createNameExpr("java.sql.Timestamp"), false, false));
                    cu.setImports(imports);
                    String voFileName = null;
                    String newPackageName = packageName+".vo";
                    cu.setPackage(new PackageDeclaration(ASTHelper.createNameExpr(newPackageName)));
                    if (cu.getTypes() != null && !cu.getTypes().isEmpty()) {
                        TypeDeclaration type = cu.getTypes().get(0);
                        String originalTypeName = type.getName();
                        log.info("javaparse: "+packageName+"."+originalTypeName);
                        type.setAnnotations(new ArrayList<AnnotationExpr>());
                        type.setName(originalTypeName+"VO");
                        voFileName = type.getName()+".java";
                        
                        ArrayList<BodyDeclaration> toRemove = new ArrayList<BodyDeclaration>();
                        for (BodyDeclaration body : type.getMembers()) {
                            body.setAnnotations(new ArrayList<AnnotationExpr>());
                            if (body instanceof MethodDeclaration) {
                                MethodDeclaration method = (MethodDeclaration) body;
                                if (method.getName().equals("methodToIgnore")) {
                                    toRemove.add(method);
                                }
                            }
                        }
                        type.getMembers().removeAll(toRemove);
                        
                        converter.getImports().add(new ImportDeclaration(ASTHelper.createNameExpr(packageName+"."+originalTypeName), false, false));
                        converter.getImports().add(new ImportDeclaration(ASTHelper.createNameExpr(newPackageName+"."+type.getName()), false, false));
                        
                        MethodDeclaration toVO = new MethodDeclaration(ModifierSet.PUBLIC, new ClassOrInterfaceType(type.getName()), "toVO");
                        toVO.setModifiers(ModifierSet.addModifier(toVO.getModifiers(), ModifierSet.STATIC));
                        ASTHelper.addMember(converterType, toVO);

                        Parameter param = ASTHelper.createParameter(ASTHelper.createReferenceType(originalTypeName, 0), "o");
                        ASTHelper.addParameter(toVO, param);
                        
                        BlockStmt block = new BlockStmt();
                        toVO.setBody(block);
                        
                        ASTHelper.addStmt(block, new ReturnStmt(new NullLiteralExpr()));
                        
                        MethodDeclaration fromVO = new MethodDeclaration(ModifierSet.PUBLIC, new ClassOrInterfaceType(originalTypeName), "fromVO");
                        fromVO.setModifiers(ModifierSet.addModifier(fromVO.getModifiers(), ModifierSet.STATIC));
                        ASTHelper.addMember(converterType, fromVO);

                        param = ASTHelper.createParameter(ASTHelper.createReferenceType(type.getName(), 0), "vo");
                        ASTHelper.addParameter(fromVO, param);
                        
                        block = new BlockStmt();
                        fromVO.setBody(block);
                        ASTHelper.addStmt(block, new ReturnStmt(new NullLiteralExpr()));
                    }
                    File pkgDir = new File(outDir, newPackageName.replace('.', '/'));
                    FileUtils.forceMkdir(pkgDir);
                    File javaFile = new File(pkgDir, voFileName);
                    FileUtils.writeStringToFile(javaFile, cu.toString());
                }
                File pkgDir = new File(outDir, converterPackage.toString().replace('.', '/'));
                FileUtils.forceMkdir(pkgDir);
                File javaFile = new File(pkgDir, "VO.java");
                FileUtils.writeStringToFile(javaFile, converter.toString());
            }
        } catch (Exception e) {
            log.error(e);
            throw new GenerationException(e);
        } finally {
            // cleanup as much as we can.
        }
    }
}
