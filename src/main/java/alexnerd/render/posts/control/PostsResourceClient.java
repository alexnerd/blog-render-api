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
