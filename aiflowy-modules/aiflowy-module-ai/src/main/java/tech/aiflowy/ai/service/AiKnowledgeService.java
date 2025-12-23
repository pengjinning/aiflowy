package tech.aiflowy.ai.service;

import com.agentsflex.core.document.Document;
import tech.aiflowy.ai.entity.DocumentCollection;
import com.mybatisflex.core.service.IService;

import java.math.BigInteger;
import java.util.List;

/**
 *  服务层。
 *
 * @author michael
 * @since 2024-08-23
 */
public interface AiKnowledgeService extends IService<DocumentCollection> {

    List<Document> search(BigInteger id, String keyword);

    DocumentCollection getDetail(String idOrAlias);

    DocumentCollection getByAlias(String idOrAlias);
}
