package tech.aiflowy.ai.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import tech.aiflowy.ai.entity.BotPlugin;
import tech.aiflowy.ai.entity.Plugin;
import tech.aiflowy.ai.mapper.BotPluginMapper;
import tech.aiflowy.ai.mapper.PluginMapper;
import tech.aiflowy.ai.service.BotPluginService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 *  服务层实现。
 *
 * @author michael
 * @since 2025-04-07
 */
@Service
public class BotPluginServiceImpl extends ServiceImpl<BotPluginMapper, BotPlugin>  implements BotPluginService {

    @Resource
    private BotPluginMapper botPluginMapper;

    @Resource
    private PluginMapper pluginMapper;

    @Override
    public List<Plugin> getList(String botId) {
        QueryWrapper queryWrapper = QueryWrapper.create().select("plugin_tool_id").where("bot_id = ?", botId);
        List<BigInteger> pluginIds = botPluginMapper.selectListByQueryAs(queryWrapper, BigInteger.class);
        List<Plugin> plugins = pluginMapper.selectListByIds(pluginIds);
        return plugins;
    }

    @Override
    public boolean doRemove(String botId, String pluginToolId) {
        QueryWrapper queryWrapper = QueryWrapper.create().select("id")
                .from("tb_bot_plugin")
                .where("bot_id = ?", botId)
                .where("plugin_tool_id = ?", pluginToolId);
        BigInteger id = botPluginMapper.selectOneByQueryAs(queryWrapper, BigInteger.class);
        int delete = botPluginMapper.deleteById(id);
        return delete > 0;
    }

    @Override
    public List<BigInteger> getBotPluginToolIds(String botId) {
        QueryWrapper queryWrapper = QueryWrapper.create().select("plugin_tool_id").where("bot_id = ?", botId);
        return botPluginMapper.selectListByQueryAs(queryWrapper, BigInteger.class);
    }

    @Override
    public void saveBotAndPluginTool(BigInteger botId, BigInteger[] pluginToolIds) {
        this.remove(QueryWrapper.create().eq(BotPlugin::getBotId, botId));
        List<BotPlugin> list = new ArrayList<>(pluginToolIds.length);
        for (BigInteger pluginToolId : pluginToolIds) {
            BotPlugin aiBotPluginTool = new BotPlugin();
            aiBotPluginTool.setBotId(botId);
            aiBotPluginTool.setPluginItemId(pluginToolId);
            list.add(aiBotPluginTool);
        }
        this.saveBatch(list);
    }
}
