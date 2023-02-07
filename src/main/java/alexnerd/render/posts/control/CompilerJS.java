/*
 * Copyright 2023 Aleksey Popov <alexnerd.com>
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package alexnerd.render.posts.control;

import alexnerd.render.posts.control.exception.RenderException;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.annotation.RegistryType;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class CompilerJS {

    @Inject
    @RegistryType(type = MetricRegistry.Type.APPLICATION)
    private MetricRegistry registry;

    private Source handleBars;

    @PostConstruct
    public void init() {
        try (Reader handlebarsReader = this.loadHandlebars()) {
            this.handleBars = Source.newBuilder("js", handlebarsReader, "Handlebars").build();
        } catch (Exception ex) {
            this.registry.counter("compile_js_errors").inc();
            throw new IllegalStateException("Cannot load Handlebars", ex);
        }
    }

    public String compile (String templateContent, String postContent) {
        try (Context context = Context.create("js")) {
            Value bindings = context.getBindings("js");
            context.eval(this.handleBars);
            bindings.putMember("templateContent", templateContent);
            bindings.putMember("postContent", postContent);
            return context.eval("js", this.getCompileLogic()).asString();
        } catch (PolyglotException ex) {
            throw new RenderException("Compiling error: " + ex.getMessage(), ex);
        }
    }

    private String getCompileLogic() {
        return """
                const postAsJSON = JSON.parse(postContent);
                const compiledTemplate = Handlebars.compile(templateContent);
                compiledTemplate(postAsJSON);
                """;
    }

    private Reader loadHandlebars() throws IOException {
        try (InputStream stream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("handlebars-v4.7.7.js")) {
            return new InputStreamReader(stream);
        }
    }
}
