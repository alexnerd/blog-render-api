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

package alexnerd.render.posts.control.content;

import alexnerd.render.posts.control.Lang;
import alexnerd.render.posts.control.exception.IntegrationException;
import jakarta.inject.Inject;
import jakarta.json.JsonArray;
import jakarta.json.JsonValue;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.annotation.RegistryType;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;

public class ContentResource {

    @Inject
    @RestClient
    private ContentResourceClient client;

    @Inject
    @RegistryType(type = MetricRegistry.Type.APPLICATION)
    private MetricRegistry registry;

    public Content getContent(Lang lang, ContentType type, String date, String title) {
        try (Response response = client.getContent(lang, type, date, title)) {
            int status = response.getStatus();
            registry.counter("get_content_status_" + status).inc();

            JsonValue json = response.readEntity(JsonValue.class);
            return Content.fromJsonValue(json);

        } catch (RuntimeException ex) {
            throw new IntegrationException(ex.getMessage());
        }
    }

    public List<Content> getLast(Lang lang, ContentType type, int limit) {
        try (Response response = client.getLast(lang, type, limit)) {
            int status = response.getStatus();
            registry.counter("get_last_content_status_" + status).inc();

            JsonArray jsonArray = response.readEntity(JsonArray.class);
            return Content.fromJsonArray(jsonArray);

        } catch (RuntimeException ex) {
            throw new IntegrationException(ex.getMessage());
        }
    }

}