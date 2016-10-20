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
package org.alfresco.rest.fileplancomponents;

import java.util.UUID;

import org.alfresco.rest.core.RestWrapper;
import org.alfresco.rest.ig.IgRestTest;
import org.alfresco.rest.model.RestFilePlanComponentModel;
import org.alfresco.rest.requests.RestFilePlanComponentApi;
import org.alfresco.utility.data.DataUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertNotNull;
import static org.alfresco.rest.model.FileplanComponentType.CATEGORY;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.NO_CONTENT;


/**
 *
 * @author Kristijan Conkas
 * @since
 */
public class RecordCategoryComponentsTest extends IgRestTest
{
    @Autowired
    private RestFilePlanComponentApi filePlanComponentApi;

    @Autowired
    private DataUser dataUser;
    
    /** new category name */
    private String categoryName = "cat " + UUID.randomUUID().toString().substring(0, 8);
    
    private String newCategoryId = null;
    
    @Test
    (
        description = "Create category as authorised user"
    )
    public void createCategoryAsAuthorisedUser() throws Exception
    {
        // create category
        RestWrapper restWrapper = filePlanComponentApi.usingRestWrapper();
        restWrapper.authenticateUser(dataUser.getAdminUser());
        RestFilePlanComponentModel filePlanComponent = 
            filePlanComponentApi.createFilePlanComponent("-filePlan-", categoryName, CATEGORY, null);
        
        // verify returned object
        restWrapper.assertStatusCodeIs(CREATED);
        assertTrue(filePlanComponent.isIsCategory());
        assertEquals(filePlanComponent.getName(), categoryName);
        assertEquals(filePlanComponent.getNodeType(), CATEGORY.toString());
        newCategoryId = filePlanComponent.getId();
    }
    
    @Test
    (
        description = "Rename category as authorised user", 
        dependsOnMethods= { "createCategoryAsAuthorisedUser" }
    )
    public void renameCategoryAsAuthorisedUser() throws Exception
    {
        assertNotNull(newCategoryId);
        String newName = "renamed " + categoryName;
        
        // rename
        RestWrapper restWrapper = filePlanComponentApi.usingRestWrapper();
        restWrapper.authenticateUser(dataUser.getAdminUser());
        RestFilePlanComponentModel filePlanComponent = filePlanComponentApi.renameFilePlanComponent(newCategoryId, newName);
        
        // verify returned object
        restWrapper.assertStatusCodeIs(OK);
        assertEquals(filePlanComponent.getName(), newName);
    }
    
    @Test
    (
        description = "Rename category as authorised user", 
        dependsOnMethods= { "renameCategoryAsAuthorisedUser" }
    )
    public void deleteCategoryAsAuthorisedUser() throws Exception
    {
        // delete
        RestWrapper restWrapper = filePlanComponentApi.usingRestWrapper();
        restWrapper.authenticateUser(dataUser.getAdminUser());
        filePlanComponentApi.deleteFilePlanComponent(newCategoryId, true);
        
        // verify deletion
        restWrapper.assertStatusCodeIs(NO_CONTENT);
        // TODO: verify we can't get an object with this ID again
        // TODO: can we verify that deletion with deletePermanently=false indeed ended up in trashcan?
    }
}
