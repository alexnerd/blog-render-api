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

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "content")
@Path("/")
public interface PostsResourceClient {
    @GET
    @Retry(maxRetries = 3)
    @Path("post/{date}/{title}")
    @Produces(MediaType.APPLICATION_JSON)
    Response findPost(@DefaultValue("ru") @QueryParam("lang") Lang lang,
                      @DefaultValue("POST") @QueryParam("type") ContentType type,
                      @PathParam("date") String date,
                      @PathParam("title") String title);

    @GET
    @Retry(maxRetries = 3)
    @Path("last")
    @Produces(MediaType.APPLICATION_JSON)
    Response findLast(@QueryParam("lang") Lang lang,
                      @QueryParam("type") ContentType type,
                      @QueryParam("limit") int limit);

}
