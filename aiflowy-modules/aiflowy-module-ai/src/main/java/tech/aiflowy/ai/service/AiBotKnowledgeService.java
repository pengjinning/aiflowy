package tech.aiflowy.ai.service;

import tech.aiflowy.ai.entity.BotDocumentCollection;
import com.mybatisflex.core.service.IService;

import java.math.BigInteger;
import java.util.List;

/**
 *  服务层。
 *
 * @author michael
 * @since 2024-08-28
 */
public interface AiBotKnowledgeService extends IService<BotDocumentCollection> {

    List<BotDocumentCollection> listByBotId(BigInteger botId);

    void saveBotAndKnowledge(BigInteger botId, BigInteger[] knowledgeIds);
}
