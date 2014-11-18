package cz.nuc.ngtablejava.rest;

import cz.nuc.ngtablejava.data.MemberRepository;
import cz.nuc.ngtablejava.model.Member;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.Validator;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.xml.ws.Holder;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * JAX-RS Example
 * <p/>
 * This class produces a RESTful service to read/write the contents of the members table.
 */
@Path("/members")
@RequestScoped
public class MemberResourceRESTService {

    @Inject
    private Logger log;

    @Inject
    private Validator validator;

    @Inject
    private MemberRepository repository;

    @POST
    @Produces("application/json")
    @Consumes("application/json")
    @Path("/filter")
    public Response filter(FilterParam param) {

        removeEmptyConstraints(param);

        Holder<Long> totalCountHolder = new Holder<Long>();
        List<Member> items = repository.filterItems(param, totalCountHolder);

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("items", items);
        result.put("totalCount", totalCountHolder.value);
        return Response.ok().entity(result).build();
    }

    private void removeEmptyConstraints(FilterParam param) {
        // remove empty field constraints
        List<String> keysToRemove = new LinkedList<String>();
        if (param.filterByFields != null) {
            for (Map.Entry<String, String> entry : param.filterByFields.entrySet()) {
                if (entry.getValue().equals("")) {
                    keysToRemove.add(entry.getKey());
                }
            }
            for (String key : keysToRemove) {
                param.filterByFields.remove(key);
            }
        }
    }

}
