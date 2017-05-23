/*
 * Copyright 2017 Davide Steduto
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.davidea.gradle;

class VersioningExtension {
    // Public values from user
    int major
    int minor
    String preRelease
    // Private values from properties
    private int propMajor
    private int propMinor
    private int patch
    private int build
    private int code

    private boolean evaluated = false
    protected int increment = 0

    private void evaluateVersions() {
        if (!evaluated) {
            // Always auto-increment Build
            println("INFO - Auto incrementing build number")
            build++
            // Auto-increment Code only in case of release
            if (increment > 0 ) println("INFO - Auto incrementing code version")
            code += increment
            // Auto-increment Patch if Major or Minor do not differ from user
            patch += increment
            // Auto reset Patch in case they differ
            if (major != propMajor || minor != propMinor || isPreRelease()) {
                if (major != propMajor && minor != 0) {
                    println("WARN - Auto resetting minor version")
                    minor = 0
                }
                println("INFO - Auto resetting patch version")
                patch = 0
            } else if (increment > 0 ) {
                println("INFO - Auto incrementing patch version")
            }
            evaluated = true
        }
    }

    protected void loadVersions(Properties versionProps) {
        // Load current values from properties file
        propMajor = Integer.valueOf(versionProps.getProperty(VersionType.MAJOR.toString(), "0"))
        propMinor = Integer.valueOf(versionProps.getProperty(VersionType.MINOR.toString(), "0"))
        patch = Integer.valueOf(versionProps.getProperty(VersionType.PATCH.toString(), "0"))
        build = Integer.valueOf(versionProps.getProperty(VersionType.BUILD.toString(), "0"))
        code = Integer.valueOf(versionProps.getProperty(VersionType.CODE.toString(), "0"))
        println("INFO - Current versioning: " + toString())
    }

    private isPreRelease() {
        return preRelease != null && !preRelease.trim().empty
    }

    int getMajor() {
        // Keep user value
        return major
    }

    int getMinor() {
        // Keep user value
        return minor
    }

    int getPatch() {
        evaluateVersions()
        return patch
    }

    int getBuild() {
        evaluateVersions()
        return build
    }

    int getCode() {
        evaluateVersions()
        return code
    }

    int getVersionCode() {
        return getCode()
    }

    /**
     * @return will output {@code major.minor.patch[-preRelease]}
     */
    String getVersionName() {
        evaluateVersions()
        return (major + "." + minor + "." + patch + (isPreRelease() ? "-" + preRelease : ""))
    }

    /**
     * @return will output {@code major.minor.patch[-preRelease] #build built on date}
     */
    String getFullVersionName() {
        return (getVersionName() + " #" + build + " built on " + getDate())
    }

    static String getDate() {
        return getDate('yyyy.MM.dd')
    }

    static String getDate(String format) {
        Date date = new Date()
        return date.format(format)
    }

    String toString() {
        return (major > 0 ? "u" + major + "." + minor + " - " : "") +
                (propMajor > 0 ? propMajor : major) +
                "." + (propMinor > 0 ? propMinor : minor) +
                "." + patch +
                (preRelease != null ? "-" + preRelease : "") +
                " #" + build +
                " code=" + code
    }

}