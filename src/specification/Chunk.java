package specification;

import specification.core.DefaultValuesBundle;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 *  Created by Tomas Hanus on 2/20/2015.
 */

@XmlAccessorType(XmlAccessType.NONE)
/*@XmlRootElement (name = "chunk", namespace="ch")*/
public class Chunk {
    private static final String ID_PROP = "ID";

    private static final String ITEM_READER_PROP = "ItemReader";
    private static final String ITEM_PROCESSOR_PROP = "ItemProcessor";
    private static final String ITEM_WRITER_PROP = "ItemWriter";


    ////////////////////////////////////
    // Elements
    @XmlElement
    private ItemReader reader;
    @XmlElement
    private ItemProcessor processor;
    @XmlElement
    private ItemWriter writer;

    ////////////////////////////////////
    // Attributes
    @XmlAttribute(name = "checkpoint-policy")
    private String checkpoint_policy;
    @XmlElement(name = "checkpoint-algorithm")
    private CheckpointAlgorithm checkpointAlgorithm;
    @XmlAttribute(name = "item-count")
    private Integer item_count;
    @XmlAttribute(name = "time-limit")
    private Integer time_limit; // Commit Interval
    //    @XmlAttribute (name = "buffer-items") // default is true
//    private boolean buffer_items;
    @XmlAttribute(name = "skip-limit")
    private Integer skip_limit;
    @XmlAttribute(name = "retry-limit")
    private Integer retry_limit;
    private String id;

    @XmlElement(name = "skippable-exception-classes")
    private SkippableExceptions skippableExceptions;

    @XmlElement(name = "retryable-exception-classes")
    private RetryableExceptions retryableExceptions;

    @XmlElement(name = "no-rollback-exception-classes")
    private NoRollbackExceptions noRollbackExceptions;

    public Chunk() {

    }

    public Chunk(String id) {
        this.id = id;
        this.reader = new ItemReader(this.id);
        this.processor = new ItemProcessor(this.id);
        this.writer = new ItemWriter(this.id);
    }


    public ItemReader getReader() {
        return reader;
    }

    public void setReader(ItemReader reader) {
        this.reader = reader;
    }

    public ItemProcessor getProcessor() {
        if (processor == null) return new ItemProcessor(null);
        return processor;
    }

    public void setProcessor(ItemProcessor processor) {
        this.processor = processor;
    }

    public void setProcessorRef(String ref) {
        if (ref.equals("")) this.processor = null;
        else {
            if (processor == null) processor = new ItemProcessor(this.id);
            processor.setRef(ref);
        }
    }

    public ItemWriter getWriter() {
        return writer;
    }

    public void setWriter(ItemWriter writer) {
        this.writer = writer;
    }


    public String getCheckpoint_policy() {
        if (checkpoint_policy == null) return DefaultValuesBundle.value("step.checkpointPolicy");
        return checkpoint_policy;
    }

    public void setCheckpoint_policy(String checkpoint_policy) {
        if (checkpoint_policy.equals(DefaultValuesBundle.value("step.checkpointPolicy"))) this.checkpoint_policy = null;
        else this.checkpoint_policy = checkpoint_policy;
    }

    public CheckpointAlgorithm getCheckpointAlgorithm() {
        return checkpointAlgorithm;
    }

    public void setCheckpointAlgorithm(CheckpointAlgorithm checkpointAlgorithm) {
        this.checkpointAlgorithm = checkpointAlgorithm;
        if (this.checkpointAlgorithm != null && this.checkpointAlgorithm.getRef() == null)
            this.checkpointAlgorithm.setRef(new String(""));
    }

    public int getItem_count() {
        if (item_count == null) return Integer.valueOf(DefaultValuesBundle.value("step.itemCount"));
        return item_count;
    }

    public void setItem_count(int item_count) {
        if (item_count == Integer.valueOf(DefaultValuesBundle.value("step.itemCount"))) {
            this.item_count = null;
        } else this.item_count = item_count;
    }

    public int getTime_limit() {
        if (this.time_limit == null) return Integer.valueOf(DefaultValuesBundle.value("step.timeLimit"));
        return time_limit;
    }

    public void setTime_limit(int time_limit) {
        if (time_limit == Integer.valueOf(DefaultValuesBundle.value("step.timeLimit"))) {
            this.time_limit = null;
        } else this.time_limit = time_limit;
    }

    public Integer getSkip_limit() {
        return skip_limit;
    }

    public void setSkip_limit(Integer skip_limit) {
        this.skip_limit = skip_limit;
    }

    public Integer getRetry_limit() {
        return retry_limit;
    }

    public void setRetry_limit(Integer retry_limit) {
        this.retry_limit = retry_limit;
    }

    public SkippableExceptions getSkippableExceptions() {
        return skippableExceptions;
    }

    public void setSkippableExceptions(SkippableExceptions skippableExceptions) {
        this.skippableExceptions = skippableExceptions;
    }

    public void setSkippableExceptions(ExceptionClasses exceptionClasses) {
        if (exceptionClasses == null) {
            this.skippableExceptions = null;
            return;
        }
        this.skippableExceptions = new SkippableExceptions();
        this.skippableExceptions.setExcludeClasses(exceptionClasses.getExcludeClasses());
        this.skippableExceptions.setIncludeClasses(exceptionClasses.getIncludeClasses());
    }

    public RetryableExceptions getRetryableExceptions() {
        return retryableExceptions;
    }

    public void setRetryableExceptions(RetryableExceptions retryableExceptions) {
        this.retryableExceptions = retryableExceptions;
    }

    public void setRetryableExceptions(ExceptionClasses exceptionClasses) {
        if (exceptionClasses == null) {
            this.retryableExceptions = null;
            return;
        }
        this.retryableExceptions = new RetryableExceptions();
        this.retryableExceptions.setExcludeClasses(exceptionClasses.getExcludeClasses());
        this.retryableExceptions.setIncludeClasses(exceptionClasses.getIncludeClasses());
    }

    public NoRollbackExceptions getNoRollbackExceptions() {
        return noRollbackExceptions;
    }

    public void setNoRollbackExceptions(NoRollbackExceptions noRollbackExceptions) {
        this.noRollbackExceptions = noRollbackExceptions;
    }

    public void setNoRollbackExceptions(ExceptionClasses exceptionClasses) {
        if (exceptionClasses == null) {
            this.noRollbackExceptions = null;
            return;
        }
        this.noRollbackExceptions = new NoRollbackExceptions();
        this.noRollbackExceptions.setExcludeClasses(exceptionClasses.getExcludeClasses());
        this.noRollbackExceptions.setIncludeClasses(exceptionClasses.getIncludeClasses());
    }

    public void setId(String id) {
        this.id = id;

    }
}