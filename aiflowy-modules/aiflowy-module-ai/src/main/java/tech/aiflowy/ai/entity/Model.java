
package tech.aiflowy.ai.entity;

import cn.hutool.core.util.StrUtil;
import com.agentsflex.core.model.chat.ChatModel;
import com.agentsflex.core.model.embedding.EmbeddingModel;
import com.agentsflex.core.model.rerank.RerankModel;
import com.agentsflex.embedding.ollama.OllamaEmbeddingConfig;
import com.agentsflex.embedding.ollama.OllamaEmbeddingModel;
import com.agentsflex.embedding.openai.OpenAIEmbeddingConfig;
import com.agentsflex.embedding.openai.OpenAIEmbeddingModel;
import com.agentsflex.llm.deepseek.DeepseekChatModel;
import com.agentsflex.llm.deepseek.DeepseekConfig;
import com.agentsflex.llm.ollama.OllamaChatConfig;
import com.agentsflex.llm.ollama.OllamaChatModel;
import com.agentsflex.llm.openai.OpenAIChatConfig;
import com.agentsflex.llm.openai.OpenAIChatModel;
import com.agentsflex.rerank.DefaultRerankModel;
import com.agentsflex.rerank.DefaultRerankModelConfig;
import com.agentsflex.rerank.gitee.GiteeRerankModel;
import com.agentsflex.rerank.gitee.GiteeRerankModelConfig;
import com.mybatisflex.annotation.RelationManyToOne;
import com.mybatisflex.annotation.Table;
import org.springframework.util.StringUtils;
import tech.aiflowy.ai.entity.base.ModelBase;
import tech.aiflowy.common.util.StringUtil;
import tech.aiflowy.common.web.exceptions.BusinessException;

import java.util.Map;

/**
 * 实体类。
 *
 * @author michael
 * @since 2024-08-23
 */

@Table("tb_model")
public class Model extends ModelBase {

    @RelationManyToOne(selfField = "providerId", targetField = "id")
    private ModelProvider modelProvider;

    /**
     * model 请求地址
     */
    public final static String LLM_ENDPOINT = "llmEndpoint";
    public final static String CHAT_PATH = "chatPath";
    public final static String EMBED_PATH = "embedPath";
    public final static String RERANK_PATH = "rerankPath";

    /**
     * 模型类型
     */
    public final static String[] MODEL_TYPES = {"chatModel", "embeddingModel", "rerankModel"};


    public ModelProvider getModelProvider() {
        return modelProvider;
    }

    public void setModelProvider(ModelProvider modelProvider) {
        this.modelProvider = modelProvider;
    }

    public ChatModel toChatModel() {
        String providerType = modelProvider.getProviderType();
        if (StringUtil.noText(providerType)) {
            return null;
        }
        switch (providerType.toLowerCase()) {
            case "ollama":
                return ollamaLlm();
            case "deepseek":
                return deepSeekLLm();
            default:
                return openaiLLm();
        }
    }

    private ChatModel ollamaLlm() {
        OllamaChatConfig ollamaChatConfig = new OllamaChatConfig();
        ollamaChatConfig.setEndpoint(getEndpointNotNull());
        ollamaChatConfig.setApiKey(getApiKey());
        ollamaChatConfig.setModel(getModelNameNotNull());
        return new OllamaChatModel(ollamaChatConfig);
    }

    private ChatModel deepSeekLLm() {
        DeepseekConfig deepseekConfig = new DeepseekConfig();
        deepseekConfig.setProvider(getModelProvider().getProviderType());
        deepseekConfig.setEndpoint(getEndpoint());
        deepseekConfig.setApiKey(getApiKey());
        deepseekConfig.setModel(getModelName());
        deepseekConfig.setRequestPath(getRequestPath());
        return new DeepseekChatModel(deepseekConfig);
    }

    private ChatModel openaiLLm() {
        OpenAIChatConfig openAIChatConfig = new OpenAIChatConfig();
        openAIChatConfig.setProvider(getModelProvider().getProviderType());
        openAIChatConfig.setEndpoint(getEndpointNotNull());
        openAIChatConfig.setApiKey(getApiKeyNotNull());
        openAIChatConfig.setModel(getModelNameNotNull());
        openAIChatConfig.setRequestPath(getRequestPathNotNull());
        return new OpenAIChatModel(openAIChatConfig);
    }

    public RerankModel toRerankModel() {
        switch (modelProvider.getProviderType().toLowerCase()) {
            case "gitee":
                GiteeRerankModelConfig giteeRerankModelConfig = new GiteeRerankModelConfig();
                giteeRerankModelConfig.setApiKey(getApiKeyNotNull());
                giteeRerankModelConfig.setEndpoint(getEndpointNotNull());
                giteeRerankModelConfig.setModel(getModelNameNotNull());
                giteeRerankModelConfig.setRequestPath(getRequestPathNotNull());
                return new GiteeRerankModel(giteeRerankModelConfig);
            default:
                DefaultRerankModelConfig defaultRerankModelConfig = new DefaultRerankModelConfig();
                defaultRerankModelConfig.setApiKey(getApiKeyNotNull());
                defaultRerankModelConfig.setEndpoint(getEndpointNotNull());
                defaultRerankModelConfig.setRequestPath(getRequestPathNotNull());
                defaultRerankModelConfig.setModel(getModelNameNotNull());
                return new DefaultRerankModel(defaultRerankModelConfig);
        }
    }

    public EmbeddingModel toEmbeddingModel() {
        String providerType = modelProvider.getProviderType();
        if (StringUtil.noText(providerType)) {
            return null;
        }
        switch (providerType.toLowerCase()) {
            case "ollama":
                OllamaEmbeddingConfig ollamaEmbeddingConfig = new OllamaEmbeddingConfig();
                ollamaEmbeddingConfig.setEndpoint(getEndpointNotNull());
                ollamaEmbeddingConfig.setApiKey(getApiKey());
                ollamaEmbeddingConfig.setModel(getModelNameNotNull());
                ollamaEmbeddingConfig.setRequestPath(getRequestPathNotNull());
                return new OllamaEmbeddingModel(ollamaEmbeddingConfig);
            default:
                OpenAIEmbeddingConfig openAIEmbeddingConfig = new OpenAIEmbeddingConfig();
                openAIEmbeddingConfig.setEndpoint(getEndpointNotNull());
                openAIEmbeddingConfig.setApiKey(getApiKeyNotNull());
                openAIEmbeddingConfig.setModel(getModelNameNotNull());
                openAIEmbeddingConfig.setRequestPath(getRequestPathNotNull());
                return new OpenAIEmbeddingModel(openAIEmbeddingConfig);
        }
    }

    public String getRequestPathNotNull() {
        if (StrUtil.isEmpty(getRequestPath())){
            throw new BusinessException("请求地址不能为空");
        }
        return getRequestPath();
    }

    public String getApiKeyNotNull() {
        if (StrUtil.isEmpty(getApiKey())) {
            throw new BusinessException("API 密钥不能为空");
        }
        return getApiKey();
    }

    public String getEndpointNotNull() {
        if (StrUtil.isEmpty(getEndpoint())){
            throw new BusinessException("API 地址不能为空");
        }
        return getEndpoint();
    }

    public String getModelNameNotNull() {
        if (StrUtil.isEmpty(getModelName())){
            throw new BusinessException("模型名称不能为空");
        }
        return getModelName();
    }

}
