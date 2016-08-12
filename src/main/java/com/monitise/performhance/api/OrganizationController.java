package com.monitise.performhance.api;

import com.monitise.performhance.BaseException;
import com.monitise.performhance.api.model.OrganizationResponse;
import com.monitise.performhance.api.model.Response;
import com.monitise.performhance.api.model.ResponseCode;
import com.monitise.performhance.api.model.Role;
import com.monitise.performhance.api.model.SimplifiedOrganizationResponse;
import com.monitise.performhance.entity.Organization;
import com.monitise.performhance.entity.User;
import com.monitise.performhance.services.OrganizationService;
import com.monitise.performhance.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/organizations")
public class OrganizationController {

    @Autowired
    private OrganizationService organizationService;
    @Autowired
    private UserService userService;

    // TODO: Only available to admin
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public Response<List<SimplifiedOrganizationResponse>> getAll() {
        List<Organization> entityList = organizationService.getAll();
        List<SimplifiedOrganizationResponse> responseList;
        responseList = SimplifiedOrganizationResponse.fromOrganizationList(entityList);
        Response<List<SimplifiedOrganizationResponse>> response = new Response<>();
        response.setSuccess(true);
        response.setData(responseList);
        return response;
    }

    @RequestMapping(value = "/{organizationId}", method = RequestMethod.GET)
    public Response<OrganizationResponse> get(@PathVariable int organizationId) throws BaseException {
        Organization fetchedOrganization = organizationService.get(organizationId);
        OrganizationResponse responseOrganization = OrganizationResponse.fromOrganization(fetchedOrganization);
        Response<OrganizationResponse> response = new Response<>();
        response.setSuccess(true);
        response.setData(responseOrganization);
        return response;
    }

    @Secured("ROLE_ADMIN")
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public Response<OrganizationResponse> add(@RequestBody Organization organization) throws BaseException {
        validateName(organization.getName());
        Organization addedOrganization = organizationService.add(organization);

        // Create management user for the organization.
        User manager = new User(addedOrganization.getName(), "Manager", addedOrganization, Role.MANAGER);
        // TODO: Change the way how manager account is created. It is fixed for now for test purposes.
        manager.setUsername(addedOrganization.getName().toLowerCase());
        manager.setPassword("admin");
        userService.addManager(manager);
        addedOrganization.setManager(manager);
        // TODO: Add set employee method.
        // Update organization with manager ID.
        addedOrganization = organizationService.update(addedOrganization);

        OrganizationResponse responseOrganization = OrganizationResponse.fromOrganization(addedOrganization);

        Response<OrganizationResponse> response = new Response<>();
        response.setSuccess(true);
        response.setData(responseOrganization);
        return response;
    }

    // region Helper Methods

    private void validateName(String name) throws BaseException {

        if (name == null || name.trim().equals("")) {
            throw new BaseException(ResponseCode.ORGANIZATION_NAME_INVALID, "Empty organization name is not allowed.");
        } else if (doesNameExists(name)) {
            throw new BaseException(ResponseCode.ORGANIZATION_NAME_EXISTS,
                    "Given name is used by another organization.");
        }
    }

    private boolean doesNameExists(String name) {
        try {
            organizationService.getByName(name);
        } catch (BaseException exception) {
            if (exception.getCode() == ResponseCode.ORGANIZATION_NAME_DOES_NOT_EXIST) {
                return false;
            }
        }

        return true;
    }

    // endregion

}