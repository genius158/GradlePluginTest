package com.yan.asmlocal;

import com.android.build.gradle.AppExtension;
import java.util.Collections;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class AsmLocalPlugin implements Plugin<Project> {

  @SuppressWarnings("NullableProblems")
  @Override
  public void apply(Project project) {
    AppExtension appExtension = (AppExtension) project.getProperties().get("android");
    appExtension.registerTransform(new AsmLocalTransform(project), Collections.EMPTY_LIST);
  }

}
