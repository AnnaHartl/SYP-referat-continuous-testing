package at.htl;

import org.jboss.resteasy.reactive.RestResponse;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/tdd")
public class ExampleResource {
    @Inject
    ExampleService service;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Item> getAll() {
        return service.getAllItems();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Response getOne(@PathParam("id") Long id) {
        return service.getItem(id)
                .map(item -> Response.ok(item).build())
                .orElseGet(() -> Response.status(RestResponse.Status.NOT_FOUND).build());
    }
}