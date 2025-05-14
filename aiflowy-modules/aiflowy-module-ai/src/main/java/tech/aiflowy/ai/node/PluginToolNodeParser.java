package tech.aiflowy.ai.node;

import com.agentsflex.core.chain.ChainNode;
import com.alibaba.fastjson.JSONObject;
import dev.tinyflow.core.Tinyflow;
import dev.tinyflow.core.parser.BaseNodeParser;

import java.math.BigInteger;

public class PluginToolNodeParser extends BaseNodeParser {

    @Override
    public ChainNode parse(JSONObject jsonObject, Tinyflow tinyflow) {
        JSONObject data = getData(jsonObject);
        BigInteger pluginId = data.getBigInteger("pluginId");
        PluginToolNode node = new PluginToolNode(pluginId);
        addParameters(node, data);
        addOutputDefs(node, data);
        return node;
    }

    public String getNodeName() {
        return "plugin-node";
    }
}
