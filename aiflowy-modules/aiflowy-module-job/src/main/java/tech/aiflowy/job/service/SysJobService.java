package tech.aiflowy.job.service;

import com.mybatisflex.core.service.IService;
import tech.aiflowy.job.entity.SysJob;

import java.io.Serializable;
import java.util.Collection;

/**
 * 系统任务表 服务层。
 *
 * @author xiaoma
 * @since 2025-05-20
 */
public interface SysJobService extends IService<SysJob> {

    void test();

    void testParam(String a, Boolean b, Integer c);

    void addJob(SysJob job);

    void deleteJob(Collection<Serializable> ids);
}
