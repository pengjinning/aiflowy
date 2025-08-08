
package tech.aiflowy.ai.service.impl;

import tech.aiflowy.ai.entity.AiWorkflow;
import tech.aiflowy.ai.mapper.AiWorkflowMapper;
import tech.aiflowy.ai.service.AiWorkflowService;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.ai.utils.RegexUtils;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.beans.BeanUtils;
import tech.aiflowy.common.web.exceptions.BusinessException;
import java.math.BigInteger;
import tech.aiflowy.ai.utils.CustomBeanUtils;

/**
 * 服务层实现。
 *
 * @author michael
 * @since 2024-08-23
 */
@Service
public class AiWorkflowServiceImpl extends ServiceImpl<AiWorkflowMapper, AiWorkflow> implements AiWorkflowService {

    /**
     * 根据别名或 id 查询详情
     */
    @Override
    public Result getDetail(String idOrAlias) {

        AiWorkflow workflow = null;

        if (idOrAlias.matches(RegexUtils.ALL_NUMBER)) {
            workflow = getById(idOrAlias);
            if (workflow == null) {
                workflow = getByAlias(idOrAlias);
            }
        }

        if (workflow == null) {
            workflow = getByAlias(idOrAlias);
        }

        return Result.success(workflow);
    }

    @Override
    public AiWorkflow getByAlias(String idOrAlias) {

        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.eq("alias",idOrAlias);

        return getOne(queryWrapper);

    }


    @Override
    public boolean updateById(AiWorkflow entity, boolean ignoreNulls) {
        AiWorkflow workFlow = getById(entity.getId());
        if (workFlow == null) {
            throw new BusinessException("工作流不存在");
        }

        CustomBeanUtils.copyPropertiesIgnoreNull(entity,workFlow);

        if ("".equals(workFlow.getAlias())){
            workFlow.setAlias(null);
        }


        return super.updateById(workFlow,false);
    }


}
