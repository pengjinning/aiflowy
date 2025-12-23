package tech.aiflowy.ai.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import tech.aiflowy.ai.entity.BotConversation;
import tech.aiflowy.ai.entity.BotMessage;
import tech.aiflowy.ai.mapper.AiBotConversationMessageMapper;
import tech.aiflowy.ai.mapper.AiBotMessageMapper;
import tech.aiflowy.ai.service.AiBotConversationMessageService;

import javax.annotation.Resource;
import java.math.BigInteger;

/**
 *  服务层实现。
 *
 * @author Administrator
 * @since 2025-04-15
 */
@Service
public class AiBotConversationMessageServiceImpl extends ServiceImpl<AiBotConversationMessageMapper, BotConversation> implements AiBotConversationMessageService {

    @Resource
    private AiBotConversationMessageMapper aiBotConversationMessageMapper;

    @Resource
    private AiBotMessageMapper aiBotMessageMapper;

    /**
     * 删除指定会话
     */
    @Override
    public void deleteConversation(String botId, String sessionId, BigInteger accountId) {
        QueryWrapper cqw = QueryWrapper.create();
        cqw.eq(BotConversation::getBotId, botId);
        cqw.eq(BotConversation::getSessionId, sessionId);
        cqw.eq(BotConversation::getAccountId, accountId);
        aiBotConversationMessageMapper.deleteByQuery(cqw);
        // 删除消息记录中的数据
        QueryWrapper mqw = QueryWrapper.create();
        mqw.eq(BotMessage::getBotId, botId);
        mqw.eq(BotMessage::getSessionId, sessionId);
        mqw.eq(BotMessage::getAccountId, accountId);
        aiBotMessageMapper.deleteByQuery(mqw);
    }

    @Override
    public void updateConversation(String botId, String sessionId, String title, BigInteger accountId) {
        QueryWrapper cqw = QueryWrapper.create();
        cqw.eq(BotConversation::getBotId, botId);
        cqw.eq(BotConversation::getSessionId, sessionId);
        cqw.eq(BotConversation::getAccountId, accountId);
        BotConversation update = new BotConversation();
        update.setTitle(title);
        aiBotConversationMessageMapper.updateByQuery(update, cqw);
    }
}
