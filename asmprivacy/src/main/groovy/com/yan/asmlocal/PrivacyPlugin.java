package com.yan.asmlocal;

import com.android.build.gradle.AppExtension;
import com.android.build.gradle.LibraryExtension;
import com.android.build.gradle.api.BaseVariant;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.gradle.api.Action;
import org.gradle.api.DomainObjectSet;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;

public class PrivacyPlugin implements Plugin<Project> {

  private Config config;

  @SuppressWarnings("NullableProblems")
  @Override
  public void apply(Project project) {
    KernelLog.info("PrivacyPlugin PrivacyPlugin PrivacyPlugin PrivacyPlugin  ");

    //check is library or application
    boolean hasAppPlugin = project.getPlugins().hasPlugin("com.android.application");
    DomainObjectSet variants;
    if (hasAppPlugin) {
      variants = ((AppExtension) project.getProperties().get("android")).getApplicationVariants();
    } else {
      variants = ((LibraryExtension) project.getProperties().get("android")).getLibraryVariants();
    }

    variants.all(o -> {
      BaseVariant bv = (BaseVariant) o;
      bv.getOutputs().all(baseVariantOutput -> {
        KernelLog.info("BaseVariantOutput   " + baseVariantOutput);
        baseVariantOutput.getProcessManifestProvider().get().doLast(task -> {
          KernelLog.info("BaseVariantOutput   " + task);
          File manifest = baseVariantOutput.getProcessManifestProvider()
              .get()
              .getManifestOutputDirectory()
              .get()
              .file("AndroidManifest.xml")
              .getAsFile();

          try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(manifest));

            StringBuilder manifestStr = new StringBuilder();
            bufferedReader.lines().forEach(manifestStr::append);
            bufferedReader.close();
            KernelLog.info("manifestStr  " + manifestStr.toString());
            // <activity
            //      android:name="com.yan.gradle.plugin.TestActivity"
            //      android:label="@string/activity_name"
            //      android:theme="@style/AppTheme.NoActionBar">
            //      <intent-filter>
            //        <action android:name="android.intent.action.MAIN" />
            //
            //        <category android:name="android.intent.category.LAUNCHER" />
            //      </intent-filter>
            //
            //    </activity>
            Pattern pattern = Pattern.compile(
                "<activity[\\W\\w]+android\\.intent\\.action\\.MAIN[\\W\\w]+android\\.intent\\.category\\.LAUNCHER[\\W\\w]+</activity>");

            Matcher matcher = pattern.matcher(manifestStr);

            if (matcher.find()) {
              String startPart = matcher.group();
              KernelLog.info("startPart  " + startPart);

              //android:name="com.yan.gradle.plugin.TestActivity"
              pattern = Pattern.compile("android:name\\s*=\\s*\"[\\w\\d.]+\"");

              matcher = pattern.matcher(startPart);
              if (matcher.find()) {
                replaceClass =
                    matcher.group().replaceAll("android:name\\s*=\\s*", "").trim();
                KernelLog.info("replaceClass  " + replaceClass);

                BufferedWriter writer = new BufferedWriter(new FileWriter(manifest));
                writer.write(manifestStr.toString()
                    .replace(replaceClass, "\"com.yan.gradle.plugin.TestActivity2\""));
                writer.flush();
                writer.close();
              }
            }
          } catch (FileNotFoundException e) {
            e.printStackTrace();
          } catch (IOException e) {
            e.printStackTrace();
          }
        });
      });
    });

    project.afterEvaluate(new Action<Project>() {
      @Override public void execute(Project project) {
        for (Task task : project.getTasks()) {
          if (task.getName().contains("WithJavac")) {
            task.doLast(new Action<Task>() {
              @Override public void execute(Task task) {
                //task.getOutputs().getFiles().getFiles()
                KernelLog.info("privacy do after javac  "+replaceClass);
              }
            });
          }
        }
      }
    });
  }

  private String replaceClass;
}
