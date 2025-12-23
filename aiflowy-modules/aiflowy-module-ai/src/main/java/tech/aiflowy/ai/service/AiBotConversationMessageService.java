package tech.aiflowy.ai.service;

import com.mybatisflex.core.service.IService;
import tech.aiflowy.ai.entity.BotConversation;

import java.math.BigInteger;

/**
 *  服务层。
 *
 * @author Administrator
 * @since 2025-04-15
 */
public interface AiBotConversationMessageService extends IService<BotConversation> {

    void deleteConversation(String botId, String sessionId, BigInteger accountId);

    void updateConversation(String botId, String sessionId, String title, BigInteger accountId);
}
