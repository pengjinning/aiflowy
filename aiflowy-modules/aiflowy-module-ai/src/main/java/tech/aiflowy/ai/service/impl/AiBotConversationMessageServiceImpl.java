package tech.aiflowy.ai.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import tech.aiflowy.ai.entity.AiBotConversationMessage;
import tech.aiflowy.ai.entity.AiBotMessage;
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
public class AiBotConversationMessageServiceImpl extends ServiceImpl<AiBotConversationMessageMapper, AiBotConversationMessage> implements AiBotConversationMessageService {

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
        cqw.eq(AiBotConversationMessage::getBotId, botId);
        cqw.eq(AiBotConversationMessage::getSessionId, sessionId);
        cqw.eq(AiBotConversationMessage::getAccountId, accountId);
        aiBotConversationMessageMapper.deleteByQuery(cqw);
        // 删除消息记录中的数据
        QueryWrapper mqw = QueryWrapper.create();
        mqw.eq(AiBotMessage::getBotId, botId);
        mqw.eq(AiBotMessage::getSessionId, sessionId);
        mqw.eq(AiBotMessage::getAccountId, accountId);
        aiBotMessageMapper.deleteByQuery(mqw);
    }

    @Override
    public void updateConversation(String botId, String sessionId, String title, BigInteger accountId) {
        QueryWrapper cqw = QueryWrapper.create();
        cqw.eq(AiBotConversationMessage::getBotId, botId);
        cqw.eq(AiBotConversationMessage::getSessionId, sessionId);
        cqw.eq(AiBotConversationMessage::getAccountId, accountId);
        AiBotConversationMessage update = new AiBotConversationMessage();
        update.setTitle(title);
        aiBotConversationMessageMapper.updateByQuery(update, cqw);
    }
}
