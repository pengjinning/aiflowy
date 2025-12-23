package tech.aiflowy.ai.service.impl;

import tech.aiflowy.ai.entity.BotWorkflow;
import tech.aiflowy.ai.mapper.AiBotWorkflowMapper;
import tech.aiflowy.ai.service.AiBotWorkflowService;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import com.mybatisflex.core.query.QueryWrapper;

/**
 *  服务层实现。
 *
 * @author michael
 * @since 2024-08-28
 */
@Service
public class AiBotWorkflowServiceImpl extends ServiceImpl<AiBotWorkflowMapper, BotWorkflow> implements AiBotWorkflowService {

    @Override
    public List<BotWorkflow> listByBotId(BigInteger botId) {

        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.eq("bot_id",botId);

        return list(queryWrapper);
    }

    @Override
    public void saveBotAndWorkflowTool(BigInteger botId, BigInteger[] workflowIds) {
        this.remove(QueryWrapper.create().eq(BotWorkflow::getBotId, botId));
        List<BotWorkflow> list = new ArrayList<>(workflowIds.length);
        for (BigInteger workflowId : workflowIds) {
            BotWorkflow botWorkflow = new BotWorkflow();
            botWorkflow.setBotId(botId);
            botWorkflow.setWorkflowId(workflowId);
            list.add(botWorkflow);
        }
        this.saveBatch(list);
    }
}
