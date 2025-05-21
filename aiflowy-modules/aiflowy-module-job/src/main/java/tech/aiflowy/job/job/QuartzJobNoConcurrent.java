package tech.aiflowy.job.job;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import tech.aiflowy.job.entity.SysJob;
import tech.aiflowy.job.util.JobUtil;

/**
 * 禁止并发执行
 */
@DisallowConcurrentExecution
public class QuartzJobNoConcurrent extends BaseQuartzJob {

    @Override
    protected Object doExecute(JobExecutionContext ctx, SysJob job) throws Exception {
        return JobUtil.execute(job);
    }
}
