package alexnerd.render.posts.control;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

public class RenderException  extends WebApplicationException {
    public RenderException (int status, String message) {
        super(Response.status(status).header("message", message).build());
    }
}
