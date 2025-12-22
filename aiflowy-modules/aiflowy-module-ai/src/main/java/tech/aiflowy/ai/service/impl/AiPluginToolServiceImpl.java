package tech.aiflowy.ai.service.impl;

import cn.hutool.json.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import tech.aiflowy.ai.entity.AiPlugin;
import tech.aiflowy.ai.entity.AiPluginFunction;
import tech.aiflowy.ai.entity.AiPluginTool;
import tech.aiflowy.ai.mapper.AiBotPluginsMapper;
import tech.aiflowy.ai.mapper.AiPluginMapper;
import tech.aiflowy.ai.mapper.AiPluginToolMapper;
import tech.aiflowy.ai.service.AiPluginToolService;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.web.exceptions.BusinessException;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 服务层实现。
 *
 * @author WangGangqiang
 * @since 2025-04-27
 */
@Service
public class AiPluginToolServiceImpl extends ServiceImpl<AiPluginToolMapper, AiPluginTool> implements AiPluginToolService {

    @Resource
    private AiPluginToolMapper aiPluginToolMapper;

    @Resource
    private AiPluginMapper aiPluginMapper;

    @Resource
    private AiBotPluginsMapper aiBotPluginsMapper;

    @Override
    public boolean savePluginTool(AiPluginTool aiPluginTool) {
        aiPluginTool.setCreated(new Date());
        aiPluginTool.setRequestMethod("Post");
        int insert = aiPluginToolMapper.insert(aiPluginTool);
        if (insert <= 0) {
            throw new BusinessException("保存失败");
        }
        return true;
    }

    @Override
    public Result<?> searchPlugin(BigInteger aiPluginToolId) {
        //查询当前插件工具
        QueryWrapper queryAiPluginToolWrapper = QueryWrapper.create()
                .select("*")
                .from("tb_plugin_tool")
                .where("id = ? ", aiPluginToolId);
        AiPluginTool aiPluginTool = aiPluginToolMapper.selectOneByQuery(queryAiPluginToolWrapper);
        // 查询当前的插件信息
        QueryWrapper queryAiPluginWrapper = QueryWrapper.create()
                .select("*")
                .from("tb_plugin")
                .where("id = ?", aiPluginTool.getPluginId());
        AiPlugin aiPlugin = aiPluginMapper.selectOneByQuery(queryAiPluginWrapper);
        Map<String, Object> result = new HashMap<>();
        result.put("data", aiPluginTool);
        result.put("aiPlugin", aiPlugin);
        return Result.ok(result);
    }

    @Override
    public boolean updatePlugin(AiPluginTool aiPluginTool) {
        int update = aiPluginToolMapper.update(aiPluginTool);
        if (update <= 0) {
            throw new BusinessException("修改失败");
        }
        return true;
    }

    @Override
    public List<AiPluginTool> searchPluginToolByPluginId(BigInteger pluginId, BigInteger botId) {
        QueryWrapper queryAiPluginToolWrapper = QueryWrapper.create()
                .select("*")
                .from("tb_plugin_tool")
                .where("plugin_id = ? ", pluginId);
        List<AiPluginTool> aiPluginTools = aiPluginToolMapper.selectListByQueryAs(queryAiPluginToolWrapper, AiPluginTool.class);
        // 查询当前bot有哪些插件工具方法
        QueryWrapper queryBotPluginTools = QueryWrapper.create()
                .select("plugin_tool_id")
                .from("tb_bot_plugins")
                .where("bot_id = ? ", botId);
        List<BigInteger> aiBotPluginToolIds = aiBotPluginsMapper.selectListWithRelationsByQueryAs(queryBotPluginTools, BigInteger.class);
        aiBotPluginToolIds.forEach(botPluginTooId -> {
            aiPluginTools.forEach(item -> {
                if (Objects.equals(botPluginTooId, item.getId())) {
                    item.setJoinBot(true);
                }
            });
        });
        return aiPluginTools;
    }

    @Override
    public List<AiPluginTool> getPluginToolList(BigInteger botId) {
        QueryWrapper queryAiPluginToolWrapper = QueryWrapper.create()
                .select("plugin_tool_id")
                .from("tb_bot_plugins")
                .where("bot_id = ? ", botId);
        List<BigInteger> pluginToolIds = aiBotPluginsMapper.selectListByQueryAs(queryAiPluginToolWrapper, BigInteger.class);
        if (pluginToolIds == null || pluginToolIds.isEmpty()) {
            return Collections.emptyList();
        }
        // 查询当前bots对应的有哪些pluginTool
        List<AiPluginTool> aiPluginTools = aiPluginToolMapper.selectListByIds(pluginToolIds);
        return aiPluginTools;
    }

    @Override
    public Result<?> pluginToolTest(String inputData, BigInteger pluginToolId) {
        AiPluginTool aiPluginTool = new AiPluginTool();
        aiPluginTool.setId(pluginToolId);
        aiPluginTool.setInputData(inputData);
        AiPluginFunction aiPluginFunction = new AiPluginFunction(aiPluginTool);
        return Result.ok(aiPluginFunction.runPluginTool(null, inputData, pluginToolId));
    }

    @Override
    public List<AiPluginTool> getByPluginId(String id) {

        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.eq("plugin_id", id);

        return list(queryWrapper);
    }

}
