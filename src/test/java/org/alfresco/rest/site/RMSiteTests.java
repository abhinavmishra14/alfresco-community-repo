/*
 * #%L
 * Alfresco Records Management Module
 * %%
 * Copyright (C) 2005 - 2016 Alfresco Software Limited
 * %%
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 * #L%
 */
package org.alfresco.rest.site;

import static org.alfresco.com.site.RMSiteCompliance.DOD5015;
import static org.alfresco.com.site.RMSiteCompliance.STANDARD;
import static org.alfresco.com.site.RMSiteFields.COMPLIANCE;
import static org.alfresco.com.site.RMSiteFields.DESCRIPTION;
import static org.alfresco.com.site.RMSiteFields.TITLE;
import static org.alfresco.rest.TestData.ANOTHER_ADMIN;
import static org.alfresco.rest.TestData.DEFAULT_EMAIL;
import static org.alfresco.rest.TestData.DEFAULT_PASSWORD;
import static org.jglue.fluentjson.JsonBuilderFactory.buildObject;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.social.alfresco.api.entities.Site.Visibility.PUBLIC;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import com.google.gson.JsonObject;

import org.alfresco.dataprep.UserService;
import org.alfresco.rest.BaseRestTest;
import org.alfresco.rest.core.RestWrapper;
import org.alfresco.rest.fileplancomponents.RecordCategoryTest;
import org.alfresco.rest.model.site.RMSite;
import org.alfresco.utility.constants.UserRole;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.UserModel;
import org.alfresco.utility.report.Bug;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

/**
 * FIXME: Document me :)
 * FIXME: Should we use dependent tests or not?
 * They were removed here but there is no guarantee for the test execution order.
 * In {@link RecordCategoryTest} we create a record category first to delete it.
 * Probbaly something to think about again.
 *
 * @author Rodica Sutu
 * @since 1.0
 */
public class RMSiteTests extends BaseRestTest
{
    @Autowired
    private UserService userService;


    /**
     * Given that RM module is installed
     * When I want to create the rm site with specific title, description and compliance
     * Then the RM site is created
     *
     */
    @Test
    (
        description = "Create RM site as admin user with Standard Compliance"
    )
    public void createRMSiteAsAdminUser() throws Exception
    {
        rmSiteAPI.usingRestWrapper().authenticateUser(dataUser.getAdminUser());
        if (siteRMExist())
        {
            // Delete the RM site
            rmSiteAPI.deleteRMSite();
        }
        // Build the RM site properties
        JsonObject rmSiteProperties = buildObject()
                .add(TITLE, RM_TITLE)
                .add(DESCRIPTION, RM_DESCRIPTION)
                .add(COMPLIANCE, STANDARD.toString())
                .getJson();

        // Create the RM site
        RMSite rmSite = rmSiteAPI.createRMSite(rmSiteProperties);

        // Verify the status code
        rmSiteAPI.usingRestWrapper().assertStatusCodeIs(CREATED);

        // Verify the returned file plan component
        assertEquals(rmSite.getId(), RM_ID);
        assertEquals(rmSite.getTitle(), RM_TITLE);
        assertEquals(rmSite.getDescription(), RM_DESCRIPTION);
        assertEquals(rmSite.getCompliance(), STANDARD);
        assertEquals(rmSite.getVisibility(), PUBLIC);
        assertEquals(rmSite.getRole(), UserRole.SiteManager.toString());
    }

    /**
     * Given that RM site exists
     * When I want to  create the RM site
     * Then the response code 409 (Site with the given identifier already exists) is return
     */
    @Test
    (
        description = "Create RM site when site already exist with admin user"
    )
    public void createRMSiteWhenSiteExists() throws Exception
    {
        rmSiteAPI.usingRestWrapper().authenticateUser(dataUser.getAdminUser());
        createRMSiteIfNotExists();
        RestWrapper restWrapper = rmSiteAPI.usingRestWrapper().authenticateUser(dataUser.getAdminUser());

        // Construct new properties
        String newTitle = RM_TITLE + "createRMSiteWhenSiteExists";
        String newDescription = RM_DESCRIPTION + "createRMSiteWhenSiteExists";

        // Build the RM site properties
        JsonObject rmSiteProperties = buildObject()
                .add(TITLE, newTitle)
                .add(DESCRIPTION, newDescription)
                .add(COMPLIANCE, STANDARD.toString())
                .getJson();

        // Create the RM site
        rmSiteAPI.createRMSite(rmSiteProperties);

        // Verify the status code
        restWrapper.assertStatusCodeIs(CONFLICT);
    }

    /**
     * Given that RM site exists
     * When I want to delete the RM site
     * Then RM site is successfully deleted
     */
    @Test
    (
        description = "Delete RM site as admin user"
    )
    public void deleteRMSite() throws Exception
    {
        RestWrapper restWrapper = rmSiteAPI.usingRestWrapper().authenticateUser(dataUser.getAdminUser());

        // Delete the RM site
        rmSiteAPI.deleteRMSite();

        // Verify the status code
        restWrapper.assertStatusCodeIs(NO_CONTENT);
    }

    /**
     * Given that RM site exists
     * When I GET the retrieve the RM site details
     * Then RM site details are returned
     */
    @Test
    (
        description = "GET the RM site as admin user"
    )
    public void getRMSite() throws Exception
    {
        RestWrapper restWrapper = rmSiteAPI.usingRestWrapper().authenticateUser(dataUser.getAdminUser());

        // Get the RM site
        RMSite rmSite = rmSiteAPI.getSite();
        if (!siteRMExist())
        {
            // Verify the status code when rm site  doesn't exist
            restWrapper.assertStatusCodeIs(NOT_FOUND);
            createRMSiteIfNotExists();
        }
        else
        {
            // Verify the status code
            restWrapper.assertStatusCodeIs(OK);
            assertEquals(rmSite.getId(), RM_ID);
            assertEquals(rmSite.getDescription(), RM_DESCRIPTION);
            assertEquals(rmSite.getCompliance(), STANDARD);
            assertEquals(rmSite.getVisibility(), PUBLIC);
        }
    }

    /**
     * Given that an user is created and RM site doesn't exist
     * When the user wants to create a RM site with DOD compliance
     * Then RM site is created
     */
    @Test
    (
        description = "Create RM site with DOD compliance as an another admin user"
    )
    @Bug (id="RM-4289")
    public void createRMSiteAsAnotherAdminUser() throws Exception
    {
        rmSiteAPI.usingRestWrapper().authenticateUser(dataUser.getAdminUser());
        if (siteRMExist())
        {
            //Delete the RM site
            rmSiteAPI.deleteRMSite();
        }
        rmSiteAPI.usingRestWrapper().disconnect();
        userService.create(dataUser.getAdminUser().getUsername(),
                dataUser.getAdminUser().getPassword(),
                ANOTHER_ADMIN,
                DEFAULT_PASSWORD,
                DEFAULT_EMAIL,
                ANOTHER_ADMIN,
                ANOTHER_ADMIN);
        UserModel userModel=new UserModel(ANOTHER_ADMIN,DEFAULT_PASSWORD);
        rmSiteAPI.usingRestWrapper().authenticateUser(userModel);
        // Build the RM site properties
        JsonObject rmSiteProperties = buildObject()
                .add(TITLE, RM_TITLE)
                .add(DESCRIPTION, RM_DESCRIPTION)
                .add(COMPLIANCE, DOD5015.toString())
                .getJson();

        // Create the RM site
        RMSite rmSite = rmSiteAPI.createRMSite(rmSiteProperties);

        // Verify the status code
        rmSiteAPI.usingRestWrapper().assertStatusCodeIs(CREATED);

        // Verify the returned file plan component
        assertEquals(rmSite.getId(), RM_ID);
        assertEquals(rmSite.getTitle(), RM_TITLE);
        assertEquals(rmSite.getDescription(), RM_DESCRIPTION);
        assertEquals(rmSite.getCompliance(), DOD5015);
        assertEquals(rmSite.getVisibility(), PUBLIC);
        assertEquals(rmSite.getRole(), UserRole.SiteManager.toString());
    }

    /**
     * Given that RM site exist
     * When a new created user want to update the RM site details (title or description)
     * Then 403 response status code is return
     * When the admin user wants to update the RM site details (title or description)
     * Then RM site details are updated
     */
    @Test
    public void updateRMSiteDetailsAsAdmin()throws Exception
    {
        String NEW_TITLE = RM_TITLE + RandomData.getRandomAlphanumeric();
        String NEW_DESCRIPTION=RM_DESCRIPTION+ RandomData.getRandomAlphanumeric();

        // Build the RM site properties
        JsonObject rmSiteToUpdate = buildObject()
                .add(TITLE, NEW_TITLE)
                .add(DESCRIPTION, NEW_DESCRIPTION)
                .getJson();

        rmSiteAPI.usingRestWrapper().authenticateUser(dataUser.getAdminUser());
        if (!siteRMExist())
        {
           createRMSiteIfNotExists();
        }

        rmSiteAPI.usingRestWrapper().disconnect();
        UserModel nonRMuser = dataUser.createRandomTestUser("testUser");
        rmSiteAPI.usingRestWrapper().authenticateUser(nonRMuser);

        // Create the RM site
        RMSite rmSite = rmSiteAPI.updateRMSite(rmSiteToUpdate);

        // Verify the status code
        rmSiteAPI.usingRestWrapper().assertStatusCodeIs(FORBIDDEN);

        rmSiteAPI.usingRestWrapper().disconnect();
        rmSiteAPI.usingRestWrapper().authenticateUser(dataUser.getAdminUser());

        // Update the RM Site
        rmSite = rmSiteAPI.updateRMSite(rmSiteToUpdate);

        // Verify the response status code
        rmSiteAPI.usingRestWrapper().assertStatusCodeIs(OK);

        // Verify the returned file plan component
        assertEquals(rmSite.getId(), RM_ID);
        assertEquals(rmSite.getTitle(), NEW_TITLE);
        assertEquals(rmSite.getDescription(), NEW_DESCRIPTION);
        assertNotNull(rmSite.getCompliance());
        assertEquals(rmSite.getVisibility(), PUBLIC);

    }


    /**
     * Given that RM site exist
     * When the admin user wants to update the RM site compliance
     * Then RM site compliance is not updated
     */
    @Test
    public void updateRMSiteComplianceAsAdmin() throws Exception
    {
        rmSiteAPI.usingRestWrapper().authenticateUser(dataUser.getAdminUser());
        if (!siteRMExist())
        {
            createRMSiteIfNotExists();
        }
        // Build the RM site properties
        JsonObject rmSiteToUpdate = buildObject()
                .add(COMPLIANCE, DOD5015.toString())
                .getJson();
        // Update the RM site
        RMSite rmSite = rmSiteAPI.updateRMSite(rmSiteToUpdate);

        // Verify the response status code
        rmSiteAPI.usingRestWrapper().assertStatusCodeIs(BAD_REQUEST);
    }
   }
