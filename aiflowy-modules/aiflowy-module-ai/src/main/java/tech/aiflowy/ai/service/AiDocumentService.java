package tech.aiflowy.ai.service;

import tech.aiflowy.ai.entity.Document;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import tech.aiflowy.ai.entity.DocumentChunk;
import tech.aiflowy.common.domain.Result;

import java.math.BigInteger;
import java.util.List;

/**
 *  服务层。
 *
 * @author michael
 * @since 2024-08-23
 */
public interface AiDocumentService extends IService<Document> {

    Page<Document> getDocumentList(String knowledgeId , int pageSize, int pageNum, String fileName);

    boolean removeDoc(String id);

    Result textSplit(Integer pageNumber, Integer pageSize, String operation, BigInteger knowledgeIdm, String filePath, String originFileName, String splitterName, Integer chunkSize, Integer overlapSize, String regex, Integer rowsPerChunk);

    Result saveTextResult(List<DocumentChunk> documentChunks, Document document);
}
