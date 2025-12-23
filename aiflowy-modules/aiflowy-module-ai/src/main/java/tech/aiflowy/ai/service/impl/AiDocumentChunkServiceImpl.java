package tech.aiflowy.ai.service.impl;

import com.agentsflex.search.engine.service.DocumentSearcher;
import org.springframework.beans.factory.annotation.Autowired;
import tech.aiflowy.ai.config.SearcherFactory;
import tech.aiflowy.ai.entity.DocumentChunk;
import tech.aiflowy.ai.entity.DocumentCollection;
import tech.aiflowy.ai.mapper.AiDocumentChunkMapper;
import tech.aiflowy.ai.service.AiDocumentChunkService;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.math.BigInteger;

/**
 *  服务层实现。
 *
 * @author michael
 * @since 2024-08-23
 */
@Service
public class AiDocumentChunkServiceImpl extends ServiceImpl<AiDocumentChunkMapper, DocumentChunk> implements AiDocumentChunkService {

    @Autowired
    private SearcherFactory searcherFactory;

    @Override
    public boolean removeChunk(DocumentCollection knowledge, BigInteger chunkId) {
        DocumentSearcher searcher = searcherFactory.getSearcher();
        // 删除搜索引擎中的数据
        if (searcherFactory.getSearcher() == null){
            return true;
        }
        return  searcher.deleteDocument(chunkId);
    }
}
