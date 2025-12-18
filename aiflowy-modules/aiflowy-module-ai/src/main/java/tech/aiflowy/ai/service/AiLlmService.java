package tech.aiflowy.ai.service;

import tech.aiflowy.ai.entity.AiLlm;
import tech.aiflowy.common.domain.Result;
import com.mybatisflex.core.service.IService;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 *  服务层。
 *
 * @author michael
 * @since 2024-08-23
 */
public interface AiLlmService extends IService<AiLlm> {

    boolean addAiLlm(AiLlm entity);

    void verifyLlmConfig(AiLlm llm);

    void quickAdd(String brand, String apiKey);

    Map<String, Map<String, List<AiLlm>>> getList(AiLlm  entity);

    void removeByEntity(AiLlm entity);

    AiLlm getLlmInstance(BigInteger llmId);
}
