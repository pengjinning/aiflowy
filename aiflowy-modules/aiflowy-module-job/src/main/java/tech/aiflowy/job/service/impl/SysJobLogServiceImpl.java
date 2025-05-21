package tech.aiflowy.job.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import tech.aiflowy.job.entity.SysJobLog;
import tech.aiflowy.job.mapper.SysJobLogMapper;
import tech.aiflowy.job.service.SysJobLogService;

/**
 * 系统任务日志 服务层实现。
 *
 * @author xiaoma
 * @since 2025-05-20
 */
@Service
public class SysJobLogServiceImpl extends ServiceImpl<SysJobLogMapper, SysJobLog>  implements SysJobLogService {

}
