package tech.aiflowy.ai.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.agentsflex.core.model.chat.tool.Parameter;
import com.agentsflex.core.model.chat.tool.Tool;
import com.agentsflex.mcp.client.McpClientManager;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.spec.McpSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.aiflowy.ai.agentsflex.tool.McpTool;
import tech.aiflowy.ai.entity.BotMcp;
import tech.aiflowy.ai.entity.Mcp;
import tech.aiflowy.ai.mapper.McpMapper;
import tech.aiflowy.ai.service.McpService;
import org.springframework.stereotype.Service;
import tech.aiflowy.ai.utils.CommonFiledUtil;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.entity.LoginAccount;
import tech.aiflowy.common.satoken.util.SaTokenUtil;
import tech.aiflowy.common.web.exceptions.BusinessException;

import java.io.Serializable;
import java.util.*;

/**
 *  服务层实现。
 *
 * @author wangGangQiang
 * @since 2026-01-04
 */
@Service
public class McpServiceImpl extends ServiceImpl<McpMapper, Mcp>  implements McpService{
    private final McpClientManager mcpClientManager = McpClientManager.getInstance();
    protected Logger Log = LoggerFactory.getLogger(DocumentServiceImpl.class);

    @Override
    public void saveMcp(Mcp entity) {

        if (entity == null || entity.getConfigJson() == null || entity.getConfigJson().trim().isEmpty()) {
            Log.error("MCP 配置不能为空");
            throw new BusinessException("MCP 配置 JSON 不能为空");
        }
        mcpClientManager.registerFromJson(entity.getConfigJson());
        LoginAccount loginAccount = SaTokenUtil.getLoginAccount();
        CommonFiledUtil.commonFiled(entity, loginAccount.getId(), loginAccount.getTenantId(), loginAccount.getDeptId());
        this.save(entity);
    }

    @Override
    public void updateMcp(Mcp entity) {
        if (entity == null || entity.getConfigJson() == null || entity.getConfigJson().trim().isEmpty()) {
            Log.error("MCP 配置不能为空");
            throw new BusinessException("MCP 配置 JSON 不能为空");
        }
        if (entity.getStatus()) {
            mcpClientManager.registerFromJson(entity.getConfigJson());
        } else {
            McpSyncClient mcpClient = getMcpClient(entity, mcpClientManager);
            mcpClient.close();
        }
        LoginAccount loginAccount = SaTokenUtil.getLoginAccount();
        CommonFiledUtil.commonFiled(entity, loginAccount.getId(), loginAccount.getTenantId(), loginAccount.getDeptId());
        this.updateById(entity);
    }

    @Override
    public void removeMcp(Serializable id) {
        Mcp mcp = this.getById(id);
        if (mcp != null && mcp.getStatus()) {
            McpSyncClient mcpClient = getMcpClient(mcp, mcpClientManager);
            mcpClient.close();
        }
        this.removeById(id);
    }

    @Override
    public Result<Page<Mcp>> pageMcp(Result<Page<Mcp>> page) {
        return page;
    }

    @Override
    public Mcp getMcpTools(String id) {
        Mcp mcp = this.getById(id);
        if (mcp != null && mcp.getStatus()) {
            McpSyncClient mcpClient = getMcpClient(mcp, mcpClientManager);
            List<McpSchema.Tool> tools = mcpClient.listTools().tools();
            mcp.setTools(tools);
        }
        return mcp;
    }

    public static McpSyncClient getMcpClient(Mcp mcp, McpClientManager mcpClientManager) {
        String configJson = mcp.getConfigJson();
        Optional<String> mcpServerName = getFirstMcpServerName(configJson);
        return mcpServerName.map(mcpClientManager::getMcpClient).orElse(null);
    }

    @Override
    public Tool toFunction(BotMcp botMcp) {
        Mcp mcpInfo = this.getById(botMcp.getMcpId());
        String configJson = mcpInfo.getConfigJson();
        Optional<String> mcpServerName = getFirstMcpServerName(configJson);
        if (mcpServerName.isPresent()) {
            // 获取mcp 服务名称
            String serverName = mcpServerName.get();
            McpSyncClient mcpClient = mcpClientManager.getMcpClient(serverName);
            List<McpSchema.Tool> tools = mcpClient.listTools().tools();
            for (McpSchema.Tool tool : tools) {
                if (tool.name().equals(botMcp.getMcpToolName())) {
                    Map<String, Object> properties = tool.inputSchema().properties();
                    List<String> required = tool.inputSchema().required();
                    McpTool mcpTool = new McpTool();
                    mcpTool.setName(tool.name());
                    mcpTool.setDescription(tool.description());
                    List<Parameter> paramList = new ArrayList<>();
                    Set<String> keySet = properties.keySet();
                    keySet.forEach(key -> {
                        Parameter parameter = new Parameter();
                        parameter.setName(key);
                        LinkedHashMap params = (LinkedHashMap) properties.get(key);
                        Set<Object> paramsKeySet = params.keySet();
                        paramsKeySet.forEach(paramsKey -> {
                            if (paramsKey.equals("type")) {
                                parameter.setType((String) params.get(paramsKey));
                            } else if (paramsKey.equals("description")) {
                                parameter.setDescription((String) params.get(paramsKey));
                            }
                        });
                        paramList.add(parameter);
                        Parameter[] parametersArr = paramList.toArray(new Parameter[properties.size()]);
                        mcpTool.setParameters(parametersArr);
                    });
                    mcpTool.setMcpId(mcpInfo.getId());
                    return mcpTool;
                }
            }
        }
        return null;
    }

    public static Set<String> getMcpServerNames(String mcpJson) {
        JSONObject rootJson = JSON.parseObject(mcpJson);

        JSONObject mcpServersJson = rootJson.getJSONObject("mcpServers");
        if (mcpServersJson == null) {
            return Set.of();
        }

        // 提取 mcpServers 的所有键 → 即为 MCP 服务名称（如 everything）
        return mcpServersJson.keySet();
    }

    public static Optional<String> getFirstMcpServerName(String mcpJson) {
        Set<String> serverNames = getMcpServerNames(mcpJson);
        return serverNames.stream().findFirst();
    }

}
