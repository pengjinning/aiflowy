package tech.aiflowy.ai.service.impl;

import com.agentsflex.store.elasticsearch.ElasticSearchVectorStore;
import tech.aiflowy.ai.entity.AiDocumentChunk;
import tech.aiflowy.ai.entity.AiKnowledge;
import tech.aiflowy.ai.entity.AiLlm;
import tech.aiflowy.ai.mapper.AiKnowledgeMapper;
import tech.aiflowy.ai.service.AiDocumentChunkService;
import tech.aiflowy.ai.service.AiKnowledgeService;
import tech.aiflowy.ai.service.AiLlmService;
import tech.aiflowy.common.domain.Result;
import com.agentsflex.core.document.Document;
import com.agentsflex.core.store.DocumentStore;
import com.agentsflex.core.store.SearchWrapper;
import com.agentsflex.core.store.StoreOptions;
import com.agentsflex.core.util.Maps;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import tech.aiflowy.test.ElasticsearchUtil;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.*;

/**
 * 服务层实现。
 *
 * @author michael
 * @since 2024-08-23
 */
@Service
public class AiKnowledgeServiceImpl extends ServiceImpl<AiKnowledgeMapper, AiKnowledge> implements AiKnowledgeService {

    @Resource
    private AiLlmService llmService;

    @Resource
    private AiDocumentChunkService chunkService;

    @Override
    public Result search(BigInteger id, String keyword) {
        AiKnowledge knowledge = getById(id);
        if (knowledge == null) {
            return Result.fail(1, "知识库不存在");
        }

        DocumentStore documentStore = knowledge.toDocumentStore();
        if (documentStore == null) {
            return Result.fail(2, "知识库没有配置向量库");
        }

        AiLlm aiLlm = llmService.getById(knowledge.getVectorEmbedLlmId());
        if (aiLlm == null) {
            return Result.fail(3, "知识库没有配置向量模型");
        }

        documentStore.setEmbeddingModel(aiLlm.toLlm());

        SearchWrapper wrapper = new SearchWrapper();
        wrapper.setMaxResults(Integer.valueOf(5));
        wrapper.setText(keyword);

        StoreOptions options = StoreOptions.ofCollectionName(knowledge.getVectorStoreCollection());
        options.setIndexName(knowledge.getVectorStoreCollection());
        List<Document> results = documentStore.search(wrapper, options);

        if (results == null || results.isEmpty()) {
            return Result.success();
        }

        List<AiDocumentChunk> chunks = new ArrayList<>();

        for (Document result : results) {
            Object resultId = result.getId();
            Double similarityScore = result.getScore();
            // 计算相似度并保留 4 位小数
            BigDecimal similarity = BigDecimal.valueOf(similarityScore)
                    .setScale(4, RoundingMode.HALF_UP); // 四舍五入
            AiDocumentChunk documentChunk = chunkService.getMapper().selectOneWithRelationsByMap(
                    Maps.of("id", resultId));
            if (documentChunk == null){
                continue;
            }
            documentChunk.setVectorSimilarityScore(similarity.doubleValue());
            chunks.add(documentChunk);
        }

//        ElasticsearchUtil elasticsearchUtil = new ElasticsearchUtil();
//        List<AiDocumentChunk> elasticSearchChunks = null;
//        try {
//            elasticSearchChunks = elasticsearchUtil.search("knowledge-base", keyword);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        // 去重后的集合
//        List<AiDocumentChunk> aiDocumentChunks = mergeAndDeduplicate(chunks, elasticSearchChunks);
//        // 加权计算得分
//        aiDocumentChunks.forEach(aiDocumentChunk -> {
//            if (aiDocumentChunk.getVectorSimilarityScore() != null && aiDocumentChunk.getElasticSimilarityScore() != null) {
//                aiDocumentChunk.setSimilarityScore(aiDocumentChunk.getVectorSimilarityScore()*0.5 + aiDocumentChunk.getElasticSimilarityScore()*0.5);
//            } else if (aiDocumentChunk.getVectorSimilarityScore() != null && aiDocumentChunk.getElasticSimilarityScore() == null) {
//                aiDocumentChunk.setSimilarityScore(aiDocumentChunk.getVectorSimilarityScore()*0.5);
//            }else if (aiDocumentChunk.getVectorSimilarityScore() == null && aiDocumentChunk.getElasticSimilarityScore() != null) {
//                aiDocumentChunk.setSimilarityScore(aiDocumentChunk.getElasticSimilarityScore()*0.5);
//            }
//        });
//        aiDocumentChunks.sort(Comparator.comparingDouble((AiDocumentChunk chunk) -> -chunk.getSimilarityScore()));
//        return Result.success(aiDocumentChunks);
        return Result.success(chunks);
    }

    /**
     * 根据 id 去重合并两个集合
     * 默认后出现的会覆盖前面的（可改）
     */
    @SuppressWarnings("unchecked")
    public static List<AiDocumentChunk> mergeAndDeduplicate(List<AiDocumentChunk>... lists) {
        Map<String, AiDocumentChunk> map = new LinkedHashMap<>();

        for (List<AiDocumentChunk> list : lists) {
            for (AiDocumentChunk chunk : list) {
                String id = String.valueOf(chunk.getId());

                if (!map.containsKey(id)) {
                    AiDocumentChunk newChunk = new AiDocumentChunk();
                    newChunk.setId(chunk.getId());
                    newChunk.setContent(chunk.getContent());
                    newChunk.setVectorSimilarityScore(chunk.getVectorSimilarityScore());

                    map.put(id, newChunk);
                }

                map.get(id).setElasticSimilarityScore(chunk.getElasticSimilarityScore());
            }
        }

        return new ArrayList<>(map.values());
    }
}
