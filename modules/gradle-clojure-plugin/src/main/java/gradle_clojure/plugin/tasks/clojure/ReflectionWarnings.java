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
package gradle_clojure.plugin.tasks.clojure;

import java.io.Serializable;
import org.gradle.api.tasks.Input;

public final class ReflectionWarnings implements Serializable {
  private boolean enabled;
  private boolean projectOnly;
  private boolean asErrors;

  public ReflectionWarnings(boolean enabled, boolean projectOnly, boolean asErrors) {
    this.enabled = enabled;
    this.projectOnly = projectOnly;
    this.asErrors = asErrors;
  }

  @Input
  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  @Input
  public boolean isProjectOnly() {
    return projectOnly;
  }

  public void setProjectOnly(boolean projectOnly) {
    this.projectOnly = projectOnly;
  }

  @Input
  public boolean isAsErrors() {
    return asErrors;
  }

  public void setAsErrors(boolean asErrors) {
    this.asErrors = asErrors;
  }
}
