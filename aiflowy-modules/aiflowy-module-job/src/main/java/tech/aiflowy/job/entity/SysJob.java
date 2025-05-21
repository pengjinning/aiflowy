package tech.aiflowy.job.entity;

import com.mybatisflex.annotation.Table;
import tech.aiflowy.job.entity.base.SysJobBase;


/**
 * 系统任务表 实体类。
 *
 * @author xiaoma
 * @since 2025-05-20
 */
@Table(value = "tb_sys_job", comment = "系统任务表")
public class SysJob extends SysJobBase {
}
