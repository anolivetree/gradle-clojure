/*
 * Copyright 2017 the original author or authors.
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
package gradle_clojure.plugin.tasks.clojurescript;

import gradle_clojure.plugin.tasks.clojure.ClojureForkOptions;
import org.gradle.api.Action;
import org.gradle.api.tasks.*;
import org.gradle.api.tasks.Optional;

import java.io.File;
import java.io.Serializable;
import java.util.*;

public final class ClojurescriptCompileOptions implements Serializable {
  private final ClojureForkOptions forkOptions = new ClojureForkOptions();

  private File outputTo;
  private File outputDir;
  private Optimizations optimizations;
  private String main;
  private String assetPath;
  private Object sourceMap;
  private Boolean verbose;
  private Boolean prettyPrint;
  private Target target;
  private List<ForeignLib> foreignLibs = new ArrayList<>();
  private List<String> externs = new ArrayList<>();
  private Map<String, Module> modules = new HashMap<>();
  private List<String> preloads;
  private Map<String, String> npmDeps = new HashMap<>();
  private Boolean installDeps;
  private CheckedArrays checkedArrays;
  private String sourceMapPath;
  private String sourceMapAssetPath;
  private Boolean sourceMapTimestamp;
  private Boolean cacheAnalysis;
  private Boolean recompileDependents;
  private Boolean staticFns;
  private Boolean fnInvokeDirect;

  @OutputFile
  @Optional
  public File getOutputTo() {
    return outputTo;
  }

  public void setOutputTo(File outputTo) {
    this.outputTo = outputTo;
  }

  @Optional
  @OutputDirectory
  public File getOutputDir() {
    return outputDir;
  }

  public void setOutputDir(File outputDir) {
    this.outputDir = outputDir;
  }

  @Input
  @Optional
  public Optimizations getOptimizations() {
    return optimizations;
  }

  public void setOptimizations(Optimizations optimizations) {
    this.optimizations = optimizations;
  }

  @Input
  @Optional
  public String getMain() {
    return main;
  }

  public void setMain(String main) {
    this.main = main;
  }

  @Input
  @Optional
  public String getAssetPath() {
    return assetPath;
  }

  public void setAssetPath(String assetPath) {
    this.assetPath = assetPath;
  }

  @Input
  @Optional
  public Object getSourceMap() {
    return sourceMap;
  }

  public void setSourceMap(Object sourceMap) {
    this.sourceMap = sourceMap;
  }

  @Console
  public Boolean getVerbose() {
    return verbose;
  }

  public void setVerbose(Boolean verbose) {
    this.verbose = verbose;
  }

  @Input
  @Optional
  public Boolean getPrettyPrint() {
    return prettyPrint;
  }

  public void setPrettyPrint(Boolean prettyPrint) {
    this.prettyPrint = prettyPrint;
  }

  @Input
  @Optional
  public Target getTarget() {
    return target;
  }

  public void setTarget(Target target) {
    this.target = target;
  }

  @Input
  @Optional
  public List<ForeignLib> getForeignLibs() {
    return foreignLibs;
  }

  // TODO This doesn't work
  public ForeignLib foreignLib(Action<? super ForeignLib> configureAction) {
    ForeignLib lib = new ForeignLib();
    this.foreignLibs.add(lib);
    configureAction.execute(lib);
    return lib;
  }

  public void setForeignLibs(List<ForeignLib> foreignLibs) {
    this.foreignLibs = foreignLibs;
  }

  @Input
  @Optional
  public List<String> getExterns() {
    return externs;
  }

  public void setExterns(List<String> externs) {
    this.externs = externs;
  }

  @Input
  @Optional
  // TODO Shouldn't it be nested?
  public Map<String, Module> getModules() {
    return modules;
  }

  public void setModules(Map<String, Module> modules) {
    this.modules = modules;
  }

  @Input
  @Optional
  public List<String> getPreloads() {
    return preloads;
  }

  public void setPreloads(List<String> preloads) {
    this.preloads = preloads;
  }

  @Input
  @Optional
  // TODO Shouldn't it be nested?
  public Map<String, String> getNpmDeps() {
    return npmDeps;
  }

  public void setNpmDeps(Map<String, String> npmDeps) {
    this.npmDeps = npmDeps;
  }

  @Input
  @Optional
  public Boolean getInstallDeps() {
    return installDeps;
  }

  public void setInstallDeps(Boolean installDeps) {
    this.installDeps = installDeps;
  }

  @Input
  @Optional
  public CheckedArrays getCheckedArrays() {
    return checkedArrays;
  }

  public void setCheckedArrays(CheckedArrays checkedArrays) {
    this.checkedArrays = checkedArrays;
  }

  @Input
  @Optional
  public String getSourceMapPath() {
    return sourceMapPath;
  }

  public void setSourceMapPath(String sourceMapPath) {
    this.sourceMapPath = sourceMapPath;
  }

  @Input
  @Optional
  public String getSourceMapAssetPath() {
    return sourceMapAssetPath;
  }

  public void setSourceMapAssetPath(String sourceMapAssetPath) {
    this.sourceMapAssetPath = sourceMapAssetPath;
  }

  @Input
  @Optional
  public Boolean getSourceMapTimestamp() {
    return sourceMapTimestamp;
  }

  public void setSourceMapTimestamp(Boolean sourceMapTimestamp) {
    this.sourceMapTimestamp = sourceMapTimestamp;
  }

  @Input
  @Optional
  public Boolean getCacheAnalysis() {
    return cacheAnalysis;
  }

  public void setCacheAnalysis(Boolean cacheAnalysis) {
    this.cacheAnalysis = cacheAnalysis;
  }

  @Input
  @Optional
  public Boolean getRecompileDependents() {
    return recompileDependents;
  }

  public void setRecompileDependents(Boolean recompileDependents) {
    this.recompileDependents = recompileDependents;
  }

  @Input
  @Optional
  public Boolean getStaticFns() {
    return staticFns;
  }

  public void setStaticFns(Boolean staticFns) {
    this.staticFns = staticFns;
  }

  @Input
  @Optional
  public Boolean getFnInvokeDirect() {
    return fnInvokeDirect;
  }

  public void setFnInvokeDirect(Boolean fnInvokeDirect) {
    this.fnInvokeDirect = fnInvokeDirect;
  }

  @Nested
  public ClojureForkOptions getForkOptions() {
    return forkOptions;
  }

  public ClojurescriptCompileOptions forkOptions(Action<? super ClojureForkOptions> configureAction) {
    configureAction.execute(forkOptions);
    return this;
  }
}
