package tech.aiflowy.job.job;

import cn.hutool.core.exceptions.ExceptionUtil;
import com.alibaba.fastjson.JSON;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.aiflowy.common.constant.enums.EnumJobExecStatus;
import tech.aiflowy.common.util.SpringContextUtil;
import tech.aiflowy.job.entity.SysJob;
import tech.aiflowy.job.entity.SysJobLog;
import tech.aiflowy.job.service.SysJobLogService;

import java.util.Date;

public abstract class BaseQuartzJob implements Job {

    protected Logger log = LoggerFactory.getLogger(getClass());

    private final static ThreadLocal<Date> TIME_RECORD = new ThreadLocal<>();

    @Override
    public void execute(JobExecutionContext ctx) throws JobExecutionException {

        SysJob job = (SysJob) ctx.getMergedJobDataMap().get(JobConstant.JOB_MAP_BEAN_NAME);

        try {
            beforeExecute(ctx, job);
            Object result = doExecute(ctx, job);
            afterExecute(ctx, job, result, null);
        } catch (Exception e) {
            log.error("quartz 任务执行报错：", e);
            afterExecute(ctx, job, null, e);
        }
    }

    protected void beforeExecute(JobExecutionContext ctx, SysJob job) {
        TIME_RECORD.set(new Date());
    }

    protected void afterExecute(JobExecutionContext ctx, SysJob job, Object result, Exception e) {
        Date startTime = TIME_RECORD.get();
        TIME_RECORD.remove();
        Date endTime = new Date();

        SysJobLog sysJobLog = new SysJobLog();
        sysJobLog.setJobId(job.getId());
        sysJobLog.setJobName(job.getJobName());
        sysJobLog.setStatus(EnumJobExecStatus.SUCCESS.getCode());
        sysJobLog.setJobParams(job.getJobParams());
        if (result != null) {
            sysJobLog.setJobResult(JSON.toJSONString(result));
        }
        if (e != null) {
            String message = ExceptionUtil.getRootCauseMessage(e);
            if (message.length() > 1000) {
                message = message.substring(0, 1000);
            }
            sysJobLog.setErrorInfo(message);
            sysJobLog.setStatus(EnumJobExecStatus.FAIL.getCode());
        }
        sysJobLog.setStartTime(startTime);
        sysJobLog.setEndTime(endTime);
        sysJobLog.setCreated(new Date());
        SysJobLogService service = SpringContextUtil.getBean(SysJobLogService.class);
        service.save(sysJobLog);
    }

    protected abstract Object doExecute(JobExecutionContext ctx, SysJob job) throws Exception;
}
