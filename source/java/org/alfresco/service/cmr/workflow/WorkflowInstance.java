package org.alfresco.service.cmr.workflow;

import java.io.Serializable;
import java.util.Date;

import org.alfresco.api.AlfrescoPublicApi;
import org.alfresco.service.cmr.repository.NodeRef;


/**
 * Workflow Instance Data Object
 * 
 * Represents an "in-flight" workflow.
 * 
 * @author davidc
 */
@AlfrescoPublicApi
public class WorkflowInstance implements Serializable
{
    private static final long serialVersionUID = 4221926809419223452L;

    /** Workflow Instance unique id */
    @Deprecated
    public String id;

    /** Workflow Instance description */
    @Deprecated
    public String description;

    /** Is this Workflow instance still "in-flight" or has it completed? */
    @Deprecated
    public boolean active;

    /** Initiator (cm:person) - null if System initiated */
    @Deprecated
    public NodeRef initiator;
    
    /** Workflow priority */
    public Integer priority;
    
    /** Workflow Start Date */
    @Deprecated
    public Date startDate;
    
    /** Workflow Due Date */
    public Date dueDate;
    
    /** Workflow End Date */
    @Deprecated
    public Date endDate;

    /** Workflow Package */
    @Deprecated
    public NodeRef workflowPackage;
    
    /** Workflow Context */
    @Deprecated
    public NodeRef context;
    
    /** Workflow Definition */
    @Deprecated
    public WorkflowDefinition definition;

    public WorkflowInstance(
                String id,
                WorkflowDefinition definition,
                String description,
                NodeRef initiator,
                NodeRef workflowPackage,
                NodeRef context,
                boolean active,
                Date startDate,
                Date endDate)
    {
        this.id = id;
        this.definition = definition;
        this.description = description;
        this.initiator = initiator;
        this.workflowPackage = workflowPackage;
        this.context = context;
        this.active = active;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /**
     * @return the serialversionuid
     */
    public static long getSerialversionuid()
    {
        return serialVersionUID;
    }

    /**
     * @return the id
     */
    public String getId()
    {
        return id;
    }

    /**
     * @return the description
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * @return the active
     */
    public boolean isActive()
    {
        return active;
    }

    /**
     * @return the initiator
     */
    public NodeRef getInitiator()
    {
        return initiator;
    }

    /**
     * @return the priority, null if there is no priority set
     */
    public Integer getPriority()
    {
        return this.priority;
    }

    /**
     * @return the startDate
     */
    public Date getStartDate()
    {
        return startDate;
    }
    
    /**
     * @return the dueDate
     */
    public Date getDueDate()
    {
        return dueDate;
    }

    /**
     * @return the endDate
     */
    public Date getEndDate()
    {
        return endDate;
    }

    /**
     * @return the workflowPackage
     */
    public NodeRef getWorkflowPackage()
    {
        return workflowPackage;
    }

    /**
     * @return the context
     */
    public NodeRef getContext()
    {
        return context;
    }

    /**
     * @return the definition
     */
    public WorkflowDefinition getDefinition()
    {
        return definition;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return "WorkflowInstance[id=" + id + ",active=" + active + ",def=" + definition.toString() + "]";
    }
}
