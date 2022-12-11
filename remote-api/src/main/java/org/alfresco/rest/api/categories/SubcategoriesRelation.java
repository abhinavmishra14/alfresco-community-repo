/*
 * #%L
 * Alfresco Remote API
 * %%
 * Copyright (C) 2005 - 2022 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software.
 * If the software was purchased under a paid Alfresco license, the terms of
 * the paid license agreement will prevail.  Otherwise, the software is
 * provided under the following open source license terms:
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

package org.alfresco.rest.api.categories;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.alfresco.rest.api.Categories;
import org.alfresco.rest.api.model.Category;
import org.alfresco.rest.framework.WebApiDescription;
import org.alfresco.rest.framework.resource.RelationshipResource;
import org.alfresco.rest.framework.resource.actions.interfaces.RelationshipResourceAction;
import org.alfresco.rest.framework.resource.parameters.Parameters;

@RelationshipResource(name = "subcategories",  entityResource = CategoriesEntityResource.class, title = "Subcategories")
public class SubcategoriesRelation implements RelationshipResourceAction.Create<Category>
{

    private final Categories categories;

    public SubcategoriesRelation(Categories categories)
    {
        this.categories = categories;
    }

    @WebApiDescription(title = "Create a category",
            description = "Creates one or more categories under a parent category",
            successStatus = HttpServletResponse.SC_CREATED)
    @Override
    public List<Category> create(String parentCategoryId, List<Category> categoryList, Parameters parameters)
    {
        return categories.createSubcategories(parentCategoryId, categoryList, parameters);
    }
}