package org.alfresco.opencmis;

import java.util.Map;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionInterceptor;
import org.alfresco.service.cmr.security.AuthorityService;
import org.apache.chemistry.opencmis.commons.impl.server.AbstractServiceFactory;
import org.apache.chemistry.opencmis.commons.server.CallContext;
import org.apache.chemistry.opencmis.commons.server.CmisService;
import org.apache.chemistry.opencmis.server.support.CmisServiceWrapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.framework.ProxyFactory;

/**
 * Factory for OpenCMIS service objects.
 * 
 * @author florian.mueller
 * @author Derek Hulley
 */
public class AlfrescoCmisServiceFactory extends AbstractServiceFactory
{
    private static final Log logger = LogFactory.getLog(AlfrescoCmisServiceFactory.class);
    
    private CMISConnector connector;
    private RetryingTransactionInterceptor cmisTransactions;
    private AlfrescoCmisExceptionInterceptor cmisExceptions;
    private AlfrescoCmisServiceInterceptor cmisControl;
    private AlfrescoCmisStreamInterceptor cmisStreams;
    private AuthorityService authorityService;

    /**
     * Sets the Authority Service.
     */
    public void setAuthorityService(AuthorityService authorityService)
    {
        this.authorityService = authorityService;
    }

    /**
     * Sets the CMIS connector.
     */
    public void setCmisConnector(CMISConnector connector)
    {
        this.connector = connector;
    }

    /**
     * @param cmisTransactions                      the interceptor that applies appropriate transactions
     */
    public void setCmisTransactions(RetryingTransactionInterceptor cmisTransactions)
    {
        this.cmisTransactions = cmisTransactions;
    }

    /**
     * @param cmisExceptions                        interceptor to translate exceptions
     */
    public void setCmisExceptions(AlfrescoCmisExceptionInterceptor cmisExceptions)
    {
        this.cmisExceptions = cmisExceptions;
    }

    /**
     * @param cmisControl                           interceptor that provides logging and authentication checks
     */
    public void setCmisControl(AlfrescoCmisServiceInterceptor cmisControl)
    {
        this.cmisControl = cmisControl;
    }

    /**
     * @param cmisStreams                   interceptor to create reusable ContentStreams
     */
    public void setCmisStreams(AlfrescoCmisStreamInterceptor cmisStreams)
    {
        this.cmisStreams = cmisStreams;
    }

    @Override
    public void init(Map<String, String> parameters)
    {
    }
    
    public void init()
    {
//        this.service = getCmisServiceTarget(connector);
//        
//        // Wrap it
//        ProxyFactory proxyFactory = new ProxyFactory(service);
//        proxyFactory.addInterface(AlfrescoCmisService.class);
//        proxyFactory.addAdvice(cmisExceptions);
//        proxyFactory.addAdvice(cmisControl);
//        proxyFactory.addAdvice(cmisStreams);
//        proxyFactory.addAdvice(cmisTransactions);
//        AlfrescoCmisService cmisService = (AlfrescoCmisService) proxyFactory.getProxy();
//
//        this.serviceWrapper = new CmisServiceWrapper<CmisService>(
//                cmisService,
//                connector.getTypesDefaultMaxItems(), connector.getTypesDefaultDepth(),
//                connector.getObjectsDefaultMaxItems(), connector.getObjectsDefaultDepth());
    }

    @Override
    public void destroy()
    {
    }
    
    /**
     * TODO:
     *      We are producing new instances each time.   
     */
    @Override
    public CmisService getService(final CallContext context)
    {
        if (logger.isDebugEnabled())
        {
            StringBuilder sb = new StringBuilder();
            sb.append("getService: ").append(AuthenticationUtil.getFullyAuthenticatedUser())
                    .append(" [runAsUser=").append(AuthenticationUtil.getRunAsUser())
                    .append(",ctxUserName=").append(context.getUsername())
                    .append(",ctxRepoId=").append(context.getRepositoryId()).append("]");

            logger.debug(sb.toString());
        }

        // Avoid using guest user if the user is provided in the context
        if(AuthenticationUtil.getFullyAuthenticatedUser() != null && authorityService.isGuestAuthority(AuthenticationUtil.getFullyAuthenticatedUser()))
        {
            AuthenticationUtil.clearCurrentSecurityContext();
        }

        AlfrescoCmisService service = getCmisServiceTarget(connector);
        
        // Wrap it
        ProxyFactory proxyFactory = new ProxyFactory(service);
        proxyFactory.addInterface(AlfrescoCmisService.class);
        proxyFactory.addAdvice(cmisExceptions);
        proxyFactory.addAdvice(cmisControl);
        proxyFactory.addAdvice(cmisStreams);
        proxyFactory.addAdvice(cmisTransactions);
        AlfrescoCmisService cmisService = (AlfrescoCmisService) proxyFactory.getProxy();

        CmisServiceWrapper<CmisService> wrapperService = new CmisServiceWrapper<CmisService>(
                cmisService,
                connector.getTypesDefaultMaxItems(), connector.getTypesDefaultDepth(),
                connector.getObjectsDefaultMaxItems(), connector.getObjectsDefaultDepth());

        // We use our specific open method here because only we know about it
        cmisService.open(context);

        return wrapperService;
    }
    
    protected AlfrescoCmisService getCmisServiceTarget(CMISConnector connector)
    {
        return new AlfrescoCmisServiceImpl(connector);
    }
}
