package tech.aiflowy.ai.config;

import com.agentsflex.rerank.DefaultRerankModel;
import com.agentsflex.rerank.DefaultRerankModelConfig;
import tech.aiflowy.ai.entity.Model;
import tech.aiflowy.common.util.PropertiesUtil;

import java.util.Properties;
import java.util.Map;
import org.springframework.util.StringUtils;

public class RagRerankModelUtil {

    public static DefaultRerankModel getRerankModel(Model modelRerank) {
        if (modelRerank == null){
            return null;
        }
        DefaultRerankModelConfig defaultRerankModelConfig = new DefaultRerankModelConfig();
        if (modelRerank.getLlmModel() != null && !modelRerank.getLlmModel().isEmpty()){
            defaultRerankModelConfig.setModel(modelRerank.getLlmModel());
        }
        if (modelRerank.getLlmApiKey() != null && !modelRerank.getLlmApiKey().isEmpty()){
            defaultRerankModelConfig.setApiKey(modelRerank.getLlmApiKey());
        }
        if (modelRerank.getLlmEndpoint() != null && !modelRerank.getLlmEndpoint().isEmpty()){
            defaultRerankModelConfig.setEndpoint(modelRerank.getLlmEndpoint());
        }
        String llmExtraConfig = modelRerank.getLlmExtraConfig();
        if (llmExtraConfig != null && !llmExtraConfig.isEmpty()){
            Properties prop = PropertiesUtil.textToProperties(llmExtraConfig);
            String basePath = prop.getProperty("basePath");
            defaultRerankModelConfig.setEndpoint(basePath);
        }else{
            Map<String, Object> options = modelRerank.getOptions();
            if (options != null) {
                String rerankPath = (String)options.get("rerankPath");
                if (StringUtils.hasLength(rerankPath)){
                    defaultRerankModelConfig.setRequestPath(rerankPath);
                }
            }
        }

        return new DefaultRerankModel(defaultRerankModelConfig);
    }
}
