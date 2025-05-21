package tech.aiflowy.job.job;

import org.quartz.JobExecutionContext;
import tech.aiflowy.job.entity.SysJob;
import tech.aiflowy.job.util.JobUtil;

/**
 * 可并发执行
 */
public class QuartzJob extends BaseQuartzJob {

    @Override
    protected Object doExecute(JobExecutionContext ctx, SysJob job) throws Exception {
        return JobUtil.execute(job);
    }
}
