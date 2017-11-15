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
package gradle_clojure.plugin;


import com.android.build.gradle.AppExtension;
import com.android.build.gradle.api.*;
import com.android.builder.model.ProductFlavor;
import com.android.builder.model.SourceProvider;
import gradle_clojure.plugin.internal.DefaultClojureSourceSet;
import gradle_clojure.plugin.tasks.ClojureCompile;
import gradle_clojure.plugin.tasks.ClojureSourceSet;
import org.gradle.api.*;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.ConfigurableFileTree;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.internal.HasConvention;
import org.gradle.api.internal.file.SourceDirectorySetFactory;
import org.gradle.api.internal.plugins.DslObject;
import org.gradle.api.internal.tasks.DefaultSourceSetOutput;
import org.gradle.api.internal.tasks.TaskDependencyResolveContext;
import org.gradle.api.plugins.Convention;
import org.gradle.api.plugins.JavaBasePlugin;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.plugins.internal.SourceSetUtil;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.compile.AbstractCompile;
import org.gradle.api.tasks.compile.JavaCompile;
import org.gradle.internal.Cast;

import javax.inject.Inject;
import java.io.File;
import java.util.Set;
import java.util.concurrent.Callable;

public class ClojureBaseAndroidPlugin implements Plugin<Project> {
  private final SourceDirectorySetFactory sourceDirectorySetFactory;

  @Inject
  public ClojureBaseAndroidPlugin(SourceDirectorySetFactory sourceDirectorySetFactory) {
    this.sourceDirectorySetFactory = sourceDirectorySetFactory;
  }

  @Override
  public void apply(Project project) {
    project.getPluginManager().apply(JavaBasePlugin.class);

    configureSourceSetDefaults(project);
  }

  // iterate with sourceSets
  private void configureSourceSetDefaults(Project project) {

    AppExtension app = project.getConvention().getByType(AppExtension.class);

    // sourceSetsは、full, fullDebug, andoridTestFullなど、flavorもbuildTypeも別々のも組み合わせのも取れる
    app.getSourceSets().all(sourceSets -> {

      //System.out.printf("sourceSet %s\n", sourceSets.getName());

      File sourceSetPath = project.file("src/" + sourceSets.getName() + "/clojure");
      if (!sourceSetPath.exists()) {
        return;
      }


      ClojureSourceSet clojureSourceSet = new DefaultClojureSourceSet("clojure", sourceDirectorySetFactory);
      //new DslObject(sourceSet).getConvention().getPlugins().put("clojure", clojureSourceSet);
      clojureSourceSet.getClojure().srcDir("src/" + sourceSets.getName() + "/clojure");
      //System.out.printf("srcdir %s\n", "src/" + sourceSets.getName() + "/clojure");

      ((HasConvention)sourceSets).getConvention().getPlugins().put("clojure", clojureSourceSet);

      // add so Android Studio will recognize Clojure files can see these
      //sourceSets.getJava().srcDir(sourceSetPath);
    });

    project.afterEvaluate(project1 -> {
      app.getApplicationVariants().all(variant -> {

        //System.out.printf("variant %s\n", variant.getName());

        JavaCompile javaCompile = variant.getJavaCompile();
        if (javaCompile == null) {
          return;
        }

        String compileTaskName = javaCompile.getName().replaceFirst("Java.*$", "Clojure");
        ClojureCompile compile = project.getTasks().create(compileTaskName, ClojureCompile.class);
        compile.setDescription(String.format("Compiles the %s Clojure source.", variant.getName()));

        compile.setClasspath(variant.getJavaCompile().getClasspath()
          .plus(project.files(app.getBootClasspath()))
          .plus(project.files(javaCompile.getDestinationDir()))
        );

        // TODO presumably at some point this will allow providers, so we should switch to that
        // instead of convention mapping
        //compile.getConventionMapping().map("classpath", variant.getJavaCompile()::getClasspath);
        // TODO switch to provider
        compile.getConventionMapping().map("namespaces", compile::findNamespaces);

        compile.getOptions().setAotCompile(true);

        compile.setDestinationDir(new File(project.getBuildDir() + "/intermediates/classes_clojure/" + variant.getName()));

        for (SourceProvider provider : variant.getSourceSets()) {
          ClojureSourceSet sourceSet = (ClojureSourceSet) ((HasConvention)provider).getConvention().getPlugins().get("clojure");
          if (sourceSet == null) {
            continue;
          }
          compile.setSource(sourceSet.getClojure());
        }

        variant.getJavaCompiler().finalizedBy(compile);
        variant.registerPostJavacGeneratedBytecode(compile.getOutputs().getFiles());
      });
    });
  }

  // iterate with sourceSets
  private void configureSourceSetDefaults_test(Project project) {

    AppExtension app = project.getConvention().getByType(AppExtension.class);

    // sourceSetsは、full, fullDebug, andoridTestFullなど、flavorもbuildTypeも別々のも組み合わせのも取れる
    app.getSourceSets().all(sourceSets -> {

      System.out.printf("sets.name %s\n", sourceSets.getName()); // "testMinApi24DemoDebug"
      System.out.printf("sets.java.name %s\n", sourceSets.getJava().getName()); // "test min api21 demo debug Java source" // 何用？flavorは"minApi24"なのにわけられているし。
      //System.out.printf("sets.apiconfigname %s\n", sourceSets.getApiConfigurationName());
      //System.out.printf("sets.compileonlyconfigname %s\n", sourceSets.getCompileOnlyConfigurationName());
      AndroidSourceDirectorySet java = sourceSets.getJava();
      for (File f : java.getSrcDirs()) {
        System.out.printf(" src: %s\n", f.getAbsolutePath());
      }

    });

    // variantは、組み合わせたものが取れる。
    // - demo-debug
    // - demo-release
    // - full-debug
    // - full-release
    app.getApplicationVariants().all(variant -> {


      System.out.printf("var.basename %s\n", variant.getBaseName()); // "demo-debug", "minApi24-demo-debug"
      System.out.printf("var.name %s\n", variant.getName()); // "demoDebug", "minApi24DemoDebug"
      System.out.printf("var.flavorname %s\n", variant.getFlavorName()); // "full", "minApi24Demo" // flavorのdimensionは結合されている
      System.out.printf("var.flavors\n");
      for (ProductFlavor flavor : variant.getProductFlavors()) { // "minApi21", "full"
        System.out.printf("  %s\n", flavor.getName());
      }
      System.out.printf("var.mergedflavor %s\n", variant.getMergedFlavor().getName()); // "" 空？
      System.out.printf("var.buildtype %s\n", variant.getBuildType().getName()); // "release" // builttype名
      System.out.printf("var.dirname %s\n", variant.getDirName()); // "demo/debug", "minAPi24Demo/debug" // classファイルの出力先と一致。flavorのdimensionは結合されている
      System.out.printf("var.versionname %s\n", variant.getVersionName());
      System.out.printf("var.assemble.name %s\n", variant.getAssemble().getName()); // "assembleFullDebug"
      System.out.printf("proj.builddir.path %s\n", project.getBuildDir().getAbsolutePath()); // "<snip>/MyApplication/app/build"

      System.out.printf("var.getsrcfolders\n");
      for (ConfigurableFileTree f : variant.getSourceFolders(SourceKind.JAVA)) {
        System.out.printf("  %s\n", f.getDir().getAbsolutePath());
      }

      // crashes with following error
      // > Failed to notify project evaluation listener.
      //    > com.android.build.gradle.api.ApplicationVariant.getOutputs()Lorg/gradle/api/DomainObjectCollection;
      System.out.printf("var.getoutputs\n");
      for (BaseVariantOutput out : variant.getOutputs()) {
        System.out.printf("  name %s\n", out.getName());
        System.out.printf("  dirname %s\n", out.getDirName());
        System.out.printf("  basename %s\n", out.getBaseName());
      }

      ClojureSourceSet clojureSourceSet = new DefaultClojureSourceSet("clojure", sourceDirectorySetFactory);
      //new DslObject(sourceSet).getConvention().getPlugins().put("clojure", clojureSourceSet);

      clojureSourceSet.getClojure().srcDir(String.format("src/%s/clojure", variant.getName()));
      // in case the clojure source overlaps with the resources source, exclude any clojure code
      // from resources
      //sourceSet.getResources().getFilter().exclude(element -> clojureSourceSet.getClojure().contains(element.getFile()));
      //sourceSet.getJava().setSrcDirs(clojureSourceSet.getClojure());

      String compileTaskName = variant.getName() + "Clojure";
      ClojureCompile compile = project.getTasks().create(compileTaskName, ClojureCompile.class);
      compile.setDescription(String.format("Compiles the %s Clojure source.", variant.getName()));
      compile.setSource(clojureSourceSet.getClojure());

      // そのvariantでビルドする全*.javaファイル
      System.out.printf("var.java.src\n");
      for (File f: variant.getJavaCompile().getSource().getFiles()) {
        System.out.printf(" %s\n", f.getAbsolutePath());
      }
      // そのvariantの*.javaをビルドするときに使うclasspath。
      // debug/hoge.javaでも、variantによってクラスパスが違うかもしれない。
      // よって、clojureのビルドの設定も、variantごとに行う必要がある。(fullDebugAssemble用のclojureのコンパイル、などのように)
      System.out.printf("var.java.classpath\n");
      for (File f : variant.getJavaCompile().getClasspath().getFiles()) {
        System.out.printf(" %s\n", f.getAbsolutePath());
      }

      // TODO presumably at some point this will allow providers, so we should switch to that
      // instead of convention mapping
      compile.getConventionMapping().map("classpath", variant.getJavaCompile()::getClasspath);
      // TODO switch to provider
      compile.getConventionMapping().map("namespaces", compile::findNamespaces);

      //clojureSourceSet.getClojure().setOutputDir();
      compile.setDestinationDir(new File(project.getBuildDir() + "/classes/" + variant.getName()));

      compile.dependsOn(variant.getJavaCompiler());
      variant.getAssemble().dependsOn(compile);
    });
  }
}
