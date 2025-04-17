package tech.aiflowy.ai.entity;

import com.agentsflex.core.document.Document;
import com.agentsflex.core.llm.Llm;
import com.agentsflex.core.store.DocumentStore;
import com.agentsflex.core.store.SearchWrapper;
import com.agentsflex.core.store.StoreOptions;
import dev.tinyflow.core.Tinyflow;
import dev.tinyflow.core.knowledge.Knowledge;
import dev.tinyflow.core.node.KnowledgeNode;
import dev.tinyflow.core.provider.KnowledgeProvider;
import dev.tinyflow.core.provider.LlmProvider;
import tech.aiflowy.ai.service.AiKnowledgeService;
import tech.aiflowy.ai.service.AiLlmService;
import tech.aiflowy.ai.service.AiWorkflowService;
import tech.aiflowy.common.util.SpringContextUtil;
import com.agentsflex.core.chain.Chain;
import com.agentsflex.core.chain.Parameter;
import com.agentsflex.core.llm.functions.BaseFunction;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class AiWorkflowFunction extends BaseFunction {

    private BigInteger workflowId;

    public AiWorkflowFunction() {
    }

    public AiWorkflowFunction(AiWorkflow aiWorkflow) {
        this.workflowId = aiWorkflow.getId();
        this.name = aiWorkflow.getTitle();
        this.description = aiWorkflow.getDescription();
        this.parameters = toParameters(aiWorkflow);
    }


    static com.agentsflex.core.llm.functions.Parameter[] toParameters(AiWorkflow aiWorkflow) {
        List<Parameter> parameterDefs = aiWorkflow.toTinyflow().toChain().getParameters();
        if (parameterDefs == null || parameterDefs.isEmpty()) {
            return new com.agentsflex.core.llm.functions.Parameter[0];
        }

        com.agentsflex.core.llm.functions.Parameter[] parameters = new com.agentsflex.core.llm.functions.Parameter[parameterDefs.size()];
        for (int i = 0; i < parameterDefs.size(); i++) {
            Parameter parameterDef = parameterDefs.get(i);
            com.agentsflex.core.llm.functions.Parameter parameter = new com.agentsflex.core.llm.functions.Parameter();
            parameter.setName(parameterDef.getName());
            parameter.setDescription(parameterDef.getDescription());
            parameter.setType(parameterDef.getDataType().toString());
            parameter.setRequired(parameterDef.isRequired());
            parameters[i] = parameter;
        }
        return parameters;
    }

    public BigInteger getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(BigInteger workflowId) {
        this.workflowId = workflowId;
    }

    @Override
    public Object invoke(Map<String, Object> argsMap) {
        AiWorkflowService service = SpringContextUtil.getBean(AiWorkflowService.class);
        AiWorkflow workflow = service.getById(this.workflowId);
        if (workflow != null) {
            Tinyflow tinyflow = workflow.toTinyflow();
            setLlmProvider(tinyflow);
            setKnowledgeProvider(tinyflow);
            Chain chain = tinyflow.toChain();
            return chain.executeForResult(argsMap);
        } else {
            throw new RuntimeException("can not find the workflow by id: " + this.workflowId);
        }
    }

    private void setLlmProvider(Tinyflow tinyflow) {
        AiLlmService aiLlmService = SpringContextUtil.getBean(AiLlmService.class);
        tinyflow.setLlmProvider(new LlmProvider() {
            @Override
            public Llm getLlm(Object id) {
                AiLlm aiLlm = aiLlmService.getById(new BigInteger(id.toString()));
                return aiLlm.toLlm();
            }
        });
    }

    private void setKnowledgeProvider(Tinyflow tinyflow) {
        AiLlmService aiLlmService = SpringContextUtil.getBean(AiLlmService.class);
        AiKnowledgeService aiKnowledgeService = SpringContextUtil.getBean(AiKnowledgeService.class);
        tinyflow.setKnowledgeProvider(new KnowledgeProvider() {
            @Override
            public Knowledge getKnowledge(Object o) {
                AiKnowledge aiKnowledge = aiKnowledgeService.getById(new BigInteger(o.toString()));
                return new Knowledge() {
                    @Override
                    public List<Document> search(String keyword, int limit, KnowledgeNode knowledgeNode, Chain chain) {
                        DocumentStore documentStore = aiKnowledge.toDocumentStore();
                        if (documentStore == null) {
                            return null;
                        }
                        AiLlm aiLlm = aiLlmService.getById(aiKnowledge.getVectorEmbedLlmId());
                        if (aiLlm == null) {
                            return null;
                        }
                        documentStore.setEmbeddingModel(aiLlm.toLlm());
                        SearchWrapper wrapper = new SearchWrapper();
                        wrapper.setMaxResults(Integer.valueOf(limit));
                        wrapper.setText(keyword);
                        StoreOptions options = StoreOptions.ofCollectionName(aiKnowledge.getVectorStoreCollection());

                        List<Document> results = documentStore.search(wrapper, options);
                        return results;
                    }
                };
            }
        });

    }

    @Override
    public String toString() {
        return "AiWorkflowFunction{" +
                "workflowId=" + workflowId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", parameters=" + Arrays.toString(parameters) +
                '}';
    }
}
