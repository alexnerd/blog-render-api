package alexnerd.render.checks;

import alexnerd.render.posts.control.ContentType;
import alexnerd.render.posts.control.Lang;
import alexnerd.render.posts.control.PostsResourceClient;
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
    PostsResourceClient client;

    private final static String INITIAL_TITLE = "JavaNerd blog";

    private final static String INITIAL_DATE = "2016-1-1";

    @Produces
    @Liveness
    public HealthCheck call() {
        return () -> HealthCheckResponse.named("content-availability")
                .status(this.checkContentAvailability())
                .build();
    }

    boolean checkContentAvailability() {
        try {
            Response response = this.client.findPost(Lang.ru, ContentType.POST, INITIAL_DATE, INITIAL_TITLE);
            return response.getStatus() == 200;
        } catch (Exception ex) {
            return false;
        }
    }
}
