package tech.aiflowy.ai.service;

import com.mybatisflex.core.service.IService;
import tech.aiflowy.ai.entity.Plugin;
import tech.aiflowy.common.domain.Result;

import java.util.List;

/**
 *  服务层。
 *
 * @author WangGangqiang
 * @since 2025-04-25
 */
public interface AiPluginService extends IService<Plugin> {

    boolean savePlugin(Plugin plugin);

    boolean removePlugin(String id);

    boolean updatePlugin(Plugin plugin);

    List<Plugin> getList();

    Result pageByCategory(Long pageNumber, Long pageSize, int category);
}
