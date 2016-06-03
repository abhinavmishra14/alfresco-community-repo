package org.alfresco.repo.importer.system;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.alfresco.service.cmr.dictionary.DictionaryException;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;


/**
 * Root data holder of Repository system information to be exported and imported
 * 
 * @author davidc
 */
public class SystemInfo
{
    public List<PatchInfo> patches = new ArrayList<PatchInfo>();

    /**
     * Create System Info from XML representation
     *  
     * @param xml  xml representation of system info
     * @return  the System Info
     */
    public static SystemInfo createSystemInfo(InputStream xml)
    {
        try
        {
            IBindingFactory factory = BindingDirectory.getFactory(SystemInfo.class);
            IUnmarshallingContext context = factory.createUnmarshallingContext();
            Object obj = context.unmarshalDocument(xml, null);
            return (SystemInfo)obj;
        }
        catch(JiBXException e)
        {
            throw new DictionaryException("Failed to parse System Info", e);
        }
    }

    /**
     * Create XML representation of System Info
     * 
     * @param xml  xml representation of system info
     */
    public void toXML(OutputStream xml)
    {
        try
        {
            IBindingFactory factory = BindingDirectory.getFactory(SystemInfo.class);
            IMarshallingContext context = factory.createMarshallingContext();
            context.setIndent(4);
            context.marshalDocument(this, "UTF-8", null, xml);    
        }
        catch(JiBXException e)
        {
            throw new DictionaryException("Failed to create System Info", e);
        }
    }
}
