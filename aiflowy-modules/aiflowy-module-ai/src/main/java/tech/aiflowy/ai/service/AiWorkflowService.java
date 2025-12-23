package tech.aiflowy.ai.service;

import tech.aiflowy.ai.entity.Workflow;
import com.mybatisflex.core.service.IService;

/**
 *  服务层。
 *
 * @author michael
 * @since 2024-08-23
 */
public interface AiWorkflowService extends IService<Workflow> {

    /**
     * 根据别名或 id 查询详情
     */
    Workflow getDetail(String idOrAlias);


    Workflow getByAlias(String alias);
}
