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

package alexnerd.render.checks;

import alexnerd.render.posts.control.content.ContentResourceClient;
import alexnerd.render.posts.control.content.ContentType;
import alexnerd.render.posts.control.Lang;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;
import org.eclipse.microprofile.rest.client.inject.RestClient;

public class LivenessCheck {
    @Inject
    @RestClient
    private ContentResourceClient client;

    private final static String INITIAL_TITLE = "JavaNerd blog";

    private final static String INITIAL_DATE = "2016-1-1";

    @Produces
    @Liveness
    public HealthCheck call() {
        return () -> HealthCheckResponse.named("content-availability")
                .status(this.checkContentAvailability())
                .build();
    }

    private boolean checkContentAvailability() {
        try (Response response = this.client.getContent(Lang.ru, ContentType.POST, INITIAL_DATE, INITIAL_TITLE)){
            return response.getStatus() == 200;
        } catch (Exception ex) {
            return false;
        }
    }
}
