/**
 * Copyright © 2012 Joe Littlejohn
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
package com.github.joelittlejohn.embedmongo;

import de.flapdoodle.embed.process.extract.ExtractedFileSets;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import de.flapdoodle.embed.mongo.MongodProcess;

/**
 * When invoked, this goal stops an instance of mojo that was started by this
 * plugin.
 * 
 * @goal stop
 * @phase post-integration-test
 */
public class StopEmbeddedMongoMojo extends AbstractMojo {

    /**
     * @parameter expression="${embedmongo.skip}" default-value="false"
     */
    private boolean skip;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (skip) {
            return;
        }
        
        MongodProcess mongod = (MongodProcess)getPluginContext().get(StartEmbeddedMongoMojo
            .MONGOD_CONTEXT_PROPERTY_NAME);

        if (mongod != null) {
            // Preload ExtractedFileSets class to prevent NoClassDefFoundError when
            // de.flapdoodle.embed.process.store.CachingArtifactStore.CacheCleaner thread tries to access the class
            // after the plugin classloader is no longer the context classloader.
            loadClass(ExtractedFileSets.class);
            mongod.stop();
        } else {
            throw new MojoFailureException("No mongod process found, it appears embedmongo:start was not called");
        }
    }

    private void loadClass(Class clazz) {
        try {
            Class.forName(clazz.getName());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to load class: " + clazz.getName());
        }
    }

}
