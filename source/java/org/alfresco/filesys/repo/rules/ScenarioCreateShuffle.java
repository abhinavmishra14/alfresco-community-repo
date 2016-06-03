package org.alfresco.filesys.repo.rules;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.alfresco.filesys.repo.rules.ScenarioInstance.Ranking;
import org.alfresco.filesys.repo.rules.operations.CreateFileOperation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A shuffle
 * 
 * a) New file created.
 * b) Existing file moved out of the way
 * c) New file moved into place.
 * d) Old file deleted.
 */
public class ScenarioCreateShuffle implements Scenario
{
    private static Log logger = LogFactory.getLog(ScenarioCreateShuffle.class);

    /**
     * The regex pattern of a create that will trigger a new instance of
     * the scenario.
     */
    private Pattern pattern;
    private String strPattern;
    
    
    private long timeout = 30000;
    
    private Ranking ranking = Ranking.HIGH;
    
    @Override
    public ScenarioInstance createInstance(final EvaluatorContext ctx, Operation operation)
    {
        /**
         * This scenario is triggered by a create of a file matching
         * the pattern
         */
        if(operation instanceof CreateFileOperation)
        {
            CreateFileOperation c = (CreateFileOperation)operation;
            
            Matcher m = pattern.matcher(c.getName());
            if(m.matches())
            {
                if(logger.isDebugEnabled())
                {
                    logger.debug("New Scenario Create Shuffle Instance pattern:" + strPattern);
                }
                
                ScenarioCreateShuffleInstance instance = new ScenarioCreateShuffleInstance() ;
                instance.setTimeout(timeout);
                instance.setRanking(ranking);
                return instance;
            }
        }
        
        // No not interested.
        return null;
   
    }

    public void setPattern(String pattern)
    {
        this.pattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
        this.strPattern = pattern;
    }
    
    public String getPattern()
    {
        return this.strPattern;
    }
    
    public void setTimeout(long timeout)
    {
        this.timeout = timeout;
    }

    public long getTimeout()
    {
        return timeout;
    }

    public void setRanking(Ranking ranking)
    {
        this.ranking = ranking;
    }

    public Ranking getRanking()
    {
        return ranking;
    }
}
