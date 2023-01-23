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

import alexnerd.render.posts.control.ContentType;
import alexnerd.render.posts.control.Lang;
import alexnerd.render.posts.control.RenderException;
import jakarta.inject.Inject;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.faulttolerance.Bulkhead;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.metrics.annotation.ConcurrentGauge;

@Path("/")
public class PostsResource {
    @Inject
    Render render;

    @GET
    @Bulkhead(5)
    @ConcurrentGauge
    @Path("post/{date}/{title}")
    @Produces(MediaType.TEXT_HTML)
    public Response findPost(@DefaultValue("ru") @QueryParam("lang") Lang lang,
                             @DefaultValue("POST") @QueryParam("type") ContentType type,
                             @PathParam("date") String date,
                             @PathParam("title") String title)  {
        String content = this.render.renderPost(lang, type, date, title);
        return Response.ok(content).build();
    }

    @GET
    @Bulkhead(5)
    @ConcurrentGauge
    @Path("last")
    @Produces(MediaType.TEXT_HTML)
    public Response findLast(@DefaultValue("ru") @QueryParam("lang") Lang lang,
                             @DefaultValue("POST") @QueryParam("type") ContentType type,
                             @QueryParam("limit") @Min(1) @Max(10) int limit) {
        String content = this.render.renderLast(lang, type, limit);
        return Response.ok(content).build();
    }

}
