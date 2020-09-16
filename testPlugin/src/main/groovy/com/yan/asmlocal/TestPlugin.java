package com.yan.asmlocal;

import com.android.build.gradle.AppExtension;
import com.android.build.gradle.LibraryExtension;
import com.android.build.gradle.internal.api.BaseVariantImpl;
import com.android.build.gradle.tasks.MergeResources;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.gradle.api.Action;
import org.gradle.api.DomainObjectSet;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.execution.TaskExecutionGraph;
import org.gradle.api.tasks.TaskProvider;

public class TestPlugin implements Plugin<Project> {

  private Config config;

  @SuppressWarnings("NullableProblems")
  @Override
  public void apply(Project project) {

    //check is library or application
    boolean hasAppPlugin = project.getPlugins().hasPlugin("com.android.application");
    DomainObjectSet variants;
    if (hasAppPlugin) {
      variants = ((AppExtension) project.getProperties().get("android")).getApplicationVariants();
    } else {
      variants = ((LibraryExtension) project.getProperties().get("android")).getLibraryVariants();
    }
    project.getExtensions().create("TestPlugin", Config.class);
    config = (Config) project.property("TestPlugin");

    project.getGradle().getTaskGraph().whenReady(new Action<TaskExecutionGraph>() {
      @Override public void execute(TaskExecutionGraph taskExecutionGraph) {
        List<Task> tasks = taskExecutionGraph.getAllTasks();
        boolean isDebug = false;
        for (Task task : tasks) {
          KernelLog.info(" taskExecutionGraph   " + task);

          String name = task.getName();
          if (name.contains("assemble") || name.contains("resguard")
              || name.contains("bundle")) {
            if (name.toLowerCase().endsWith("debug")) {
              isDebug = true;
            }
          }
        }
      }
    });

    project.afterEvaluate(new Action<Project>() {
      @Override public void execute(Project project) {
        variants.all(variant -> {
          BaseVariantImpl bv = (BaseVariantImpl) variant;
          TaskProvider<MergeResources> merge = bv.getMergeResourcesProvider();
          Task picTask = project.task("Compass" + ((BaseVariantImpl) variant).getName());

          picTask.doLast(new Action<Task>() {
            @Override public void execute(Task task) {

            }
          });
          Set<File> files = bv.getAllRawAndroidResources().getFiles();

          ArrayList<File> tempFiles = new ArrayList<>();

          for (File file : files) {
            collectionImg(file, tempFiles);
          }
        });
      }
    });
  }

  private void collectionImg(File file, ArrayList<File> tempFiles) {
    if (file.isDirectory()) {
      for (File f : file.listFiles()) {
        collectionImg(f, tempFiles);
      }
    } else {
      tempFiles.add(file);
      KernelLog.info("BaseVariantImpl    " + "   " + file.getAbsolutePath());
    }
  }
}
