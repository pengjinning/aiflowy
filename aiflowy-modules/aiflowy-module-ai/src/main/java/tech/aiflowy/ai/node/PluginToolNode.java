package tech.aiflowy.ai.node;

import com.agentsflex.core.chain.Chain;
import com.agentsflex.core.chain.node.BaseNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.aiflowy.ai.entity.AiPluginTool;
import tech.aiflowy.ai.service.AiPluginToolService;
import tech.aiflowy.common.util.SpringContextUtil;

import java.math.BigInteger;
import java.util.Map;

public class PluginToolNode extends BaseNode {

    private static final Logger log = LoggerFactory.getLogger(PluginToolNode.class);

    private final BigInteger pluginId;

    public PluginToolNode(BigInteger pluginId) {
        this.pluginId = pluginId;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Map<String, Object> execute(Chain chain) {
        Map<String, Object> map = chain.getParameterValues(this);
        AiPluginToolService bean = SpringContextUtil.getBean(AiPluginToolService.class);
        AiPluginTool tool = bean.getById(pluginId);
        return (Map<String, Object>) tool.toFunction().invoke(map);
    }
}
