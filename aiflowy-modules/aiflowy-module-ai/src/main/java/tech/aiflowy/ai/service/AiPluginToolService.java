package tech.aiflowy.ai.service;

import com.mybatisflex.core.service.IService;
import tech.aiflowy.ai.entity.PluginItem;
import tech.aiflowy.common.domain.Result;

import java.math.BigInteger;
import java.util.List;

/**
 *  服务层。
 *
 * @author WangGangqiang
 * @since 2025-04-27
 */
public interface AiPluginToolService extends IService<PluginItem> {

    boolean savePluginTool(PluginItem pluginItem);

    Result searchPlugin(BigInteger aiPluginToolId);

    boolean updatePlugin(PluginItem pluginItem);

    List<PluginItem> searchPluginToolByPluginId(BigInteger pluginId, BigInteger botId);

    List<PluginItem> getPluginToolList(BigInteger botId);

    Result pluginToolTest(String inputData, BigInteger pluginToolId);

    List<PluginItem> getByPluginId(String id);
}
