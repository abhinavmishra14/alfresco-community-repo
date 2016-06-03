package org.alfresco.repo.search.impl.querymodel.impl.db;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.repo.domain.node.NodeDAO;
import org.alfresco.repo.domain.qname.QNameDAO;
import org.alfresco.repo.search.impl.querymodel.Argument;
import org.alfresco.repo.search.impl.querymodel.FunctionEvaluationContext;
import org.alfresco.repo.search.impl.querymodel.impl.BaseSelector;
import org.alfresco.repo.tenant.TenantService;
import org.alfresco.service.cmr.dictionary.AspectDefinition;
import org.alfresco.service.cmr.dictionary.ClassDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.TypeDefinition;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.Pair;

/**
 * @author Andy
 */
public class DBSelector extends BaseSelector implements DBQueryBuilderComponent
{

    DBQueryBuilderComponent builderSupport;

    /**
     * @param type QName
     * @param alias String
     */
    public DBSelector(QName type, String alias)
    {
        super(type, alias);
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.repo.search.impl.querymodel.impl.db.DBQueryBuilderComponent#isSupported()
     */
    @Override
    public boolean isSupported()
    {
        return true;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.alfresco.repo.search.impl.querymodel.impl.db.DBQueryBuilderComponent#prepare(org.alfresco.service.namespace
     * .NamespaceService, org.alfresco.service.cmr.dictionary.DictionaryService,
     * org.alfresco.repo.domain.qname.QNameDAO, org.alfresco.repo.domain.node.NodeDAO, java.util.Set, java.util.Map,
     * org.alfresco.repo.search.impl.querymodel.FunctionEvaluationContext)
     */
    @Override
    public void prepare(NamespaceService namespaceService, DictionaryService dictionaryService, QNameDAO qnameDAO, NodeDAO nodeDAO, TenantService tenantService, Set<String> selectors,
            Map<String, Argument> functionArgs, FunctionEvaluationContext functionContext, boolean supportBooleanFloatAndDouble)
    {
        ClassDefinition classDef = dictionaryService.getClass(getType());
        List<Long> qnameIds = new ArrayList<Long>();
        if (classDef.isAspect())
        {
            Collection<QName> subaspects = dictionaryService.getSubAspects(classDef.getName(), true);
            for (QName qname : subaspects)
            {
                AspectDefinition current = dictionaryService.getAspect(qname);
                if (classDef.getName().equals(current.getName()) || current.getIncludedInSuperTypeQuery())
                {
                    Pair<Long, QName> pair = qnameDAO.getQName(qname);
                    if (pair != null)
                    {
                        Long qnameId = pair.getFirst();
                        qnameIds.add(qnameId);
                    }
                }
            }
            AspectSupport aspectSupport = new AspectSupport();
            aspectSupport.setQnameIds(qnameIds);
            builderSupport = aspectSupport;
        }
        else
        {
            Collection<QName> subclasses = dictionaryService.getSubTypes(classDef.getName(), true);
            for (QName qname : subclasses)
            {
                TypeDefinition current = dictionaryService.getType(qname);
                if (classDef.getName().equals(current.getName()) || current.getIncludedInSuperTypeQuery())
                {
                    Pair<Long, QName> pair = qnameDAO.getQName(qname);
                    if (pair != null)
                    {
                        Long qnameId = pair.getFirst();
                        qnameIds.add(qnameId);
                    }
                }
            }
            TypeSupport typeSupport = new TypeSupport();
            typeSupport.setQnameIds(qnameIds);
            typeSupport.setCommandType(DBQueryBuilderPredicatePartCommandType.IN);
            builderSupport = typeSupport;
        }

    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.repo.search.impl.querymodel.impl.db.DBQueryBuilderComponent#buildJoins(java.util.Map,
     * java.util.List)
     */
    @Override
    public void buildJoins(Map<QName, DBQueryBuilderJoinCommand> singleJoins, List<DBQueryBuilderJoinCommand> multiJoins)
    {
        builderSupport.buildJoins(singleJoins, multiJoins);
    }

    /*
     * (non-Javadoc)
     * @see
     * org.alfresco.repo.search.impl.querymodel.impl.db.DBQueryBuilderComponent#buildPredicateCommands(java.util.List)
     */
    @Override
    public void buildPredicateCommands(List<DBQueryBuilderPredicatePartCommand> predicatePartCommands)
    {
        builderSupport.buildPredicateCommands(predicatePartCommands);
    }

}
