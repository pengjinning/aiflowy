package tech.aiflowy.ai.service;

import tech.aiflowy.ai.entity.DocumentChunk;
import com.mybatisflex.core.service.IService;
import tech.aiflowy.ai.entity.DocumentCollection;

import java.math.BigInteger;

/**
 *  服务层。
 *
 * @author michael
 * @since 2024-08-23
 */
public interface AiDocumentChunkService extends IService<DocumentChunk> {

    boolean removeChunk(DocumentCollection knowledge, BigInteger chunkId);
}
