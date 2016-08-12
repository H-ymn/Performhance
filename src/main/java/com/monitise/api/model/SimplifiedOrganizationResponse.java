package com.monitise.api.model;

import com.monitise.entity.Organization;

import java.util.ArrayList;
import java.util.List;

public class SimplifiedOrganizationResponse {

    private int id;
    private String name;
    private int numberOfEmployees;

    public SimplifiedOrganizationResponse(Organization organization) {
        id = organization.getId();
        name = organization.getName();
        numberOfEmployees = organization.getNumberOfEmployees();
    }

    public static SimplifiedOrganizationResponse fromOrganization(Organization organization) {
        return new SimplifiedOrganizationResponse(organization);
    }

    public static List<SimplifiedOrganizationResponse> fromOrganizationList(List<Organization> organizations) {
        List<SimplifiedOrganizationResponse> responseList = new ArrayList<>();
        for (Organization organization : organizations) {
            responseList.add(new SimplifiedOrganizationResponse(organization));
        }
        return responseList;
    }

    // region Getters

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getNumberOfEmployees() {
        return numberOfEmployees;
    }

    // endregion

    // region Setters

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNumberOfEmployees(int numberOfEmployees) {
        this.numberOfEmployees = numberOfEmployees;
    }

    // endregion

}