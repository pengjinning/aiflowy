package tech.aiflowy.ai.config;

import com.agentsflex.mcp.client.McpClientManager;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.spec.McpSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import tech.aiflowy.ai.entity.Mcp;
import tech.aiflowy.ai.service.impl.McpServiceImpl;
import tech.aiflowy.common.web.exceptions.BusinessException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

import static tech.aiflowy.ai.service.impl.McpServiceImpl.getFirstMcpServerName;

@Configuration
@DependsOn("mcpServiceImpl") // 确保 mcpService 先初始化
public class McpClientAutoConfig {

    private final McpClientManager mcpClientManager = McpClientManager.getInstance();
    private static final Logger log = LoggerFactory.getLogger(McpClientAutoConfig.class);

    @Resource
    private McpServiceImpl mcpService;

    @PostConstruct
    public void initMcpClient() {
        log.info("开始初始化 MCP 客户端...");
        try {
            List<Mcp> mcpList = mcpService.list();
            log.info("获取到 MCP 配置列表，数量：{}", mcpList.size());

            mcpList.forEach(mcp ->  {
                if (!mcp.getStatus()) {
                    return;
                }
                String configJson = mcp.getConfigJson();
                Optional<String> firstServerName = getFirstMcpServerName(configJson);
                if (firstServerName.isPresent()) {
                    String serverName = firstServerName.get();
                    mcpClientManager.registerFromJson(configJson);
                    log.info("MCP服务名称：{} 注册成功", serverName);
                } else {
                    log.error("MCP服务名称为：{} 启动失败，配置 JSON 中未找到服务名称", mcp.getTitle());
                }
            });
        } catch (Exception e) {
            log.error("初始化 MCP 客户端失败", e);
        }
    }

}
