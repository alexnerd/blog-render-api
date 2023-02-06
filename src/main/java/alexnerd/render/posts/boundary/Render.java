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
    private ContentResourceClient client;

    @Inject
    @RegistryType(type = MetricRegistry.Type.APPLICATION)
    private MetricRegistry registry;

    @Inject
    private CompilerJS compilerJS;

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

    private AbstractMap.SimpleEntry<String, String> readJson(JsonValue value) {
        String type = value.asJsonObject().getString("type");
        String content = value.toString();
        return new AbstractMap.SimpleEntry<>(type, content);
    }

    private String render(AbstractMap.SimpleEntry<String, String> entry) {
        String template = switch (ContentType.valueOf(entry.getKey())) {
            case POST, ARTICLE -> RenderTemplate.POST;
            case ARTICLE_TEASER -> RenderTemplate.ARTICLE_TEASER;
            case LAST_ARTICLES -> RenderTemplate.LAST_ARTICLES;
            default -> throw new RenderException(422, "Unsupported content type");
        };
        return this.compilerJS.compile(template, entry.getValue());
    }
}
