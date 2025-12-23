package tech.aiflowy.ai.service.impl;

import tech.aiflowy.ai.entity.BotDocumentCollection;
import tech.aiflowy.ai.mapper.BotDocumentCollectionMapper;
import tech.aiflowy.ai.service.BotDocumentCollectionService;
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
public class BotDocumentCollectionServiceImpl extends ServiceImpl<BotDocumentCollectionMapper, BotDocumentCollection> implements BotDocumentCollectionService {

    @Override
    public List<BotDocumentCollection> listByBotId(BigInteger botId) {

        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.eq(BotDocumentCollection::getBotId,botId);

        return list(queryWrapper);
    }

    @Override
    public void saveBotAndKnowledge(BigInteger botId, BigInteger[] knowledgeIds) {
        this.remove(QueryWrapper.create().eq(BotDocumentCollection::getBotId, botId));
        List<BotDocumentCollection> list = new ArrayList<>(knowledgeIds.length);
        for (BigInteger knowledgeId : knowledgeIds) {
            BotDocumentCollection botDocumentCollection = new BotDocumentCollection();
            botDocumentCollection.setBotId(botId);
            botDocumentCollection.setDocumentCollectionId(knowledgeId);
            list.add(botDocumentCollection);
        }
        this.saveBatch(list);
    }
}
