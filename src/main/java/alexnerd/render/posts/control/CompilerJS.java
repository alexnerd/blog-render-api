package alexnerd.render.posts.control;

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
    MetricRegistry registry;

    Source handleBars;

    @PostConstruct
    public void init() {
        try {
            this.handleBars = Source.newBuilder("js", this.loadHandlebars(), "Handlebars").build();
        } catch (IOException ex) {
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
            throw new IllegalStateException("Compiling error: " + ex.getMessage(), ex);
        }
    }

    String getCompileLogic() {
        return """
                const postAsJSON = JSON.parse(postContent);
                const compiledTemplate = Handlebars.compile(templateContent);
                compiledTemplate(postAsJSON);
                """;
    }
    Reader loadHandlebars() {
        InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("handlebars-v4.7.7.js");
        return new InputStreamReader(stream);
    }
}
