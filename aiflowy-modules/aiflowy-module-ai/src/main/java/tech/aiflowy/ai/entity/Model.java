
package tech.aiflowy.ai.entity;

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

    public final static String LLM_ENDPOINT = "llmEndpoint";
    public final static String CHAT_PATH = "chatPath";
    public final static String EMBED_PATH = "embedPath";
    public final static String RERANK_PATH = "rerankPath";

    public ModelProvider getModelProvider() {
        return modelProvider;
    }

    public void setModelProvider(ModelProvider modelProvider) {
        this.modelProvider = modelProvider;
    }

    public ChatModel toChatModel() {
        String provider = modelProvider.getProviderName();
        if (StringUtil.noText(provider)) {
            return null;
        }
        switch (provider.toLowerCase()) {
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
        ollamaChatConfig.setEndpoint(getPath(LLM_ENDPOINT));
        ollamaChatConfig.setApiKey(getApiKey());
        ollamaChatConfig.setModel(getModelName());
        return new OllamaChatModel(ollamaChatConfig);
    }

    private ChatModel deepSeekLLm() {
        DeepseekConfig deepseekConfig = new DeepseekConfig();
        deepseekConfig.setProvider(getModelProvider().getProviderType());
        deepseekConfig.setEndpoint(getPath(LLM_ENDPOINT));
        deepseekConfig.setApiKey(getApiKey());
        deepseekConfig.setModel(getModelName());
        deepseekConfig.setRequestPath(getPath(CHAT_PATH));
        return new DeepseekChatModel(deepseekConfig);
    }

    private ChatModel openaiLLm() {
        OpenAIChatConfig openAIChatConfig = new OpenAIChatConfig();
        openAIChatConfig.setProvider(getModelProvider().getProviderType());
        openAIChatConfig.setEndpoint(getPath(LLM_ENDPOINT));
        openAIChatConfig.setApiKey(getApiKey());
        openAIChatConfig.setModel(getModelName());
        openAIChatConfig.setLogEnabled(true);
        openAIChatConfig.setRequestPath(getPath(CHAT_PATH));
        return new OpenAIChatModel(openAIChatConfig);
    }

    public RerankModel toRerankModel() {
        String rerankPath = getPath(RERANK_PATH);
        String endpoint = getPath(LLM_ENDPOINT);
        String apiKey = getApiKey();
        switch (modelProvider.getProviderName().toLowerCase()) {
            case "gitee":
                GiteeRerankModelConfig giteeRerankModelConfig = new GiteeRerankModelConfig();
                giteeRerankModelConfig.setApiKey(apiKey);
                giteeRerankModelConfig.setEndpoint(endpoint);
                giteeRerankModelConfig.setRequestPath(rerankPath);
                return new GiteeRerankModel(giteeRerankModelConfig);
            default:
                DefaultRerankModelConfig defaultRerankModelConfig = new DefaultRerankModelConfig();
                defaultRerankModelConfig.setApiKey(apiKey);
                defaultRerankModelConfig.setEndpoint(endpoint);
                defaultRerankModelConfig.setRequestPath(rerankPath);
                defaultRerankModelConfig.setModel(getModelName());
                return new DefaultRerankModel(defaultRerankModelConfig);
        }
    }

    public EmbeddingModel toEmbeddingModel() {
        String embedPath = getPath(EMBED_PATH);;
        String endpoint = getPath(LLM_ENDPOINT);
        String providerName = modelProvider.getProviderName();
        if (StringUtil.noText(providerName)) {
            return null;
        }
        switch (providerName.toLowerCase()) {
            case "ollama":
                OllamaEmbeddingConfig ollamaEmbeddingConfig = new OllamaEmbeddingConfig();
                ollamaEmbeddingConfig.setEndpoint(endpoint);
                ollamaEmbeddingConfig.setApiKey(getApiKey());
                ollamaEmbeddingConfig.setModel(getModelName());
                if (StringUtils.hasLength(embedPath)) {
                    ollamaEmbeddingConfig.setRequestPath(embedPath);
                }
                return new OllamaEmbeddingModel(ollamaEmbeddingConfig);
            default:
                OpenAIEmbeddingConfig openAIEmbeddingConfig = new OpenAIEmbeddingConfig();
                openAIEmbeddingConfig.setEndpoint(endpoint);
                openAIEmbeddingConfig.setApiKey(getApiKey());
                openAIEmbeddingConfig.setModel(getModelName());
                if (StringUtils.hasLength(embedPath)) {
                    openAIEmbeddingConfig.setRequestPath(embedPath);
                }
                return new OpenAIEmbeddingModel(openAIEmbeddingConfig);
        }
    }

    /**
     * 获取模型路径
     * @param key
     * @return
     */
    public String getPath (String key) {
        Map<String, Object> options = getOptions();
        String path = "";
        if (LLM_ENDPOINT.equals(key)) {
            path = getEndpoint();
        }

        if (options != null) {
            String pathFromOptions = (String) options.get(key);
            if (path == null && StringUtils.hasLength(pathFromOptions)) {
                path = pathFromOptions;
            } else {
                if (LLM_ENDPOINT.equals(key)) {
                    path = this.modelProvider.getEndpoint();
                } else if (CHAT_PATH.equals(key)){
                    path = this.modelProvider.getChatPath();
                } else if (EMBED_PATH.equals(key)){
                    path = this.modelProvider.getEmbedPath();
                } else if (RERANK_PATH.equals(key)){
                    path = this.modelProvider.getRerankPath();
                }
            }
        }
        if (StringUtil.noText(path)) {
            throw new BusinessException("请设置模型" + key);
        }
        return path;
    }
}
