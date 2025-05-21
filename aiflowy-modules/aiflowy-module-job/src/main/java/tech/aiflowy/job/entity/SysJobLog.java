package tech.aiflowy.job.entity;

import com.mybatisflex.annotation.Table;
import tech.aiflowy.job.entity.base.SysJobLogBase;


/**
 * 系统任务日志 实体类。
 *
 * @author xiaoma
 * @since 2025-05-20
 */
@Table(value = "tb_sys_job_log", comment = "系统任务日志")
public class SysJobLog extends SysJobLogBase {
}
