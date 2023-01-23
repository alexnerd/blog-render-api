package alexnerd.render.posts.boundary;

import alexnerd.render.posts.control.*;
import jakarta.inject.Inject;
import jakarta.json.JsonArray;
import jakarta.json.JsonValue;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.annotation.RegistryType;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.AbstractMap;
import java.util.stream.Collectors;

public class Render {

    @Inject
    @RestClient
    PostsResourceClient client;

    @Inject
    @RegistryType(type = MetricRegistry.Type.APPLICATION)
    MetricRegistry registry;

    @Inject
    CompilerJS compilerJS;

    public String renderPost(Lang lang, ContentType type, String date, String title) {
        Response response;
        try {
            response = this.client.findPost(lang, type, date, title);
        } catch (WebApplicationException ex) {
            response = ex.getResponse();
            registry.counter("content_find_post_status_" + response.getStatus()).inc();
            throw new RenderException(response.getStatus(), response.getHeaderString("message"));
        }
        int status = response.getStatus();
        registry.counter("content_find_post_status_" + status).inc();

        JsonValue jsonValue = response.readEntity(JsonValue.class);
        AbstractMap.SimpleEntry<String, String> entry = this.readJson(jsonValue);

        return this.render(entry);
    }

    public String renderLast(Lang lang, ContentType type, int limit) {
        Response response;
        try {
            response = this.client.findLast(lang, type, limit);
        } catch (WebApplicationException ex) {
            response = ex.getResponse();
            registry.counter("content_find_last_status_" + response.getStatus()).inc();
            throw new RenderException(response.getStatus(), response.getHeaderString("message"));
        }
        int status = response.getStatus();
        registry.counter("content_find_last_status_" + status).inc();
        JsonArray jsonValues = response.readEntity(JsonArray.class);
        return jsonValues.stream()
                .parallel()
                .map(this::readJson)
                .map(this::render)
                .collect(Collectors.joining());
    }

    public AbstractMap.SimpleEntry<String, String> readJson(JsonValue value) {
        String type = value.asJsonObject().getString("type");
        String content = value.toString();
        return new AbstractMap.SimpleEntry<>(type, content);
    }

    private String render(AbstractMap.SimpleEntry<String, String> entry) {
        String template = switch (ContentType.valueOf(entry.getKey())) {
            case POST, ARTICLE -> ContentTemplate.POST;
            case ARTICLE_TEASER -> ContentTemplate.ARTICLE_TEASER;
            case LAST_ARTICLES -> ContentTemplate.LAST_ARTICLES;
            default -> throw new IllegalStateException("Unsupported type");
        };
        return this.compilerJS.compile(template, entry.getValue());
    }
}
