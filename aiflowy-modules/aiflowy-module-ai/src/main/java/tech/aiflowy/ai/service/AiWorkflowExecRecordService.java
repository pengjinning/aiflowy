package tech.aiflowy.ai.service;

import com.mybatisflex.core.service.IService;
import tech.aiflowy.ai.entity.WorkflowExecResult;

/**
 * 工作流执行记录 服务层。
 *
 * @author ArkLight
 * @since 2025-05-28
 */
public interface AiWorkflowExecRecordService extends IService<WorkflowExecResult> {

    WorkflowExecResult getByExecKey(String execKey);
}
