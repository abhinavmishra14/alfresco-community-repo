/*
 * Copyright (C) 2006 Alfresco, Inc.
 *
 * Licensed under the Mozilla Public License version 1.1 
 * with a permitted attribution clause. You may obtain a
 * copy of the License at
 *
 *   http://www.alfresco.org/legal/license.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */

package org.alfresco.repo.avm;

import java.io.Serializable;

import org.hibernate.Query;
import org.hibernate.Session;

/**
 * Base class for all repository file system like objects.
 * @author britt
 */
abstract class AVMNodeImpl implements AVMNode, Serializable
{
    /**
     * The Object ID.
     */
    private long fID;
    
    /**
     * The Version ID.
     */
    private int fVersionID;
    
    /**
     * The Repository that owns this.
     */
    private Repository fRepository;
    
    /**
     * The basic attributes of this.  Owner, creator, mod time, etc.
     */
    private BasicAttributes fBasicAttributes;
    
    /**
     * Whether this node is new (and should therefore not be COWed).
     */
    private boolean fIsNew;
    
    /**
     * The version number (for concurrency control).
     */
    private long fVers;
    
    /**
     * The rootness of this node.
     */
    private boolean fIsRoot;
    
    /**
     * Default constructor.
     */
    protected AVMNodeImpl()
    {
    }

    /**
     * Constructor used when creating a new concrete subclass instance.
     * @param id The object id.
     * @param repo The Repository that owns this.
     */
    protected AVMNodeImpl(long id,
                          Repository repo)
    {
        fID = id;
        fVersionID = -1;
        fRepository = repo;
        fIsRoot = false;
        long time = System.currentTimeMillis();
        fBasicAttributes = new BasicAttributesImpl("britt",
                                                       "britt",
                                                       "britt",
                                                       time,
                                                       time,
                                                       time);
        fIsNew = true;
    }
    
    /**
     * Set the ancestor of this node.
     * @param ancestor The ancestor to set.
     */
    public void setAncestor(AVMNode ancestor)
    {
        if (ancestor == null)
        {
            return;
        }
        HistoryLinkImpl link = new HistoryLinkImpl();
        link.setAncestor(ancestor);
        link.setDescendent(this);
        SuperRepository.GetInstance().getSession().save(link);
    }

    /**
     * Get the ancestor of this node.
     * @return The ancestor of this node.
     */
    public AVMNode getAncestor()
    {
        Session sess = SuperRepository.GetInstance().getSession();
        Query query = sess.createQuery("select hl.ancestor from HistoryLinkImpl hl where hl.descendent = :desc");
        query.setEntity("desc", this);
        return (AVMNode)query.uniqueResult();
    }
    
    /**
     * Set the node that was merged into this.
     * @param mergedFrom The node that was merged into this.
     */
    public void setMergedFrom(AVMNode mergedFrom)
    {
        if (mergedFrom == null)
        {
            return;
        }
        MergeLinkImpl link = new MergeLinkImpl();
        link.setMfrom(mergedFrom);
        link.setMto(this);
        SuperRepository.GetInstance().getSession().save(link);
    }
    
    /**
     * Get the node that was merged into this.
     * @return The node that was merged into this.
     */
    public AVMNode getMergedFrom()
    {
        Session sess = SuperRepository.GetInstance().getSession();
        Query query = sess.createQuery("select ml.mfrom from MergeLinkImpl ml where ml.mto = :to");
        query.setEntity("to", this);
        return (AVMNode)query.uniqueResult();
    }
    
    /**
     * Set the owning repository for this.
     * @param repo The owning repository.
     */
    public void setRepository(Repository repo)
    {
        fRepository = repo;
    }

    /**
     * Get the repository that owns this.
     * @return The repository.
     */
    public Repository getRepository()
    {
        return fRepository;
    }
    
    /**
     * Equality based on object ids.
     * @param obj The thing to compare against.
     * @return Equality.
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (!(obj instanceof AVMNode))
        {
            System.err.println("Failing AVMNodeImpl.equals");
            return false;
        }
        return fID == ((AVMNode)obj).getId();
    }

    /**
     * Get a reasonable hash value.
     * @return The hash code.
     */
    @Override
    public int hashCode()
    {
        return (int)fID;
    }
    
    /**
     * Set the object id.  For Hibernate.
     * @param id The id to set.
     */
    protected void setId(long id)
    {
        fID = id;
    }
    
    /**
     * Get the id of this node.
     * @return The object id.
     */
    public long getId()
    {
        return fID;
    }
 
    /**
     * Set the versionID for this node.  
     * @param versionID The id to set.
     */
    public void setVersionID(int versionID)
    {
        fVersionID = versionID;
    }
    
    /**
     * Get the version id of this node.
     * @return The version id.
     */
    public int getVersionID()
    {
        return fVersionID;
    }
    
    /**
     * Set the basic attributes. For Hibernate.
     * @param attrs
     */
    protected void setBasicAttributes(BasicAttributes attrs)
    {
        fBasicAttributes = attrs;
    }
    
    /**
     * Get the basic attributes.
     * @return The basic attributes.
     */
    public BasicAttributes getBasicAttributes()
    {
        return fBasicAttributes;
    }
    
    /**
     * Set whether this is new.
     * @param isNew Whether this is new.
     */
    public void setIsNew(boolean isNew)
    {
        fIsNew = isNew;
    }

    /**
     * Get whether this is a new node.
     * @return Whether this is new.
     */
    public boolean getIsNew()
    {
        return fIsNew;
    }
 
    /**
     * Set the version for concurrency control
     * @param vers
     */
    protected void setVers(long vers)
    {
        fVers = vers;
    }
    
    /**
     * Get the version for concurrency control.
     * @return The version for optimistic locks.
     */
    protected long getVers()
    {
        return fVers;
    }

    /**
     * Get whether this is a root node.
     * @return Whether this is a root node.
     */
    public boolean getIsRoot()
    {
        return fIsRoot;
    }

    /**
     * @param isRoot
     */
    public void setIsRoot(boolean isRoot)
    {
        fIsRoot = isRoot;
    }
}
