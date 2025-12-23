package tech.aiflowy.ai.service;

import com.mybatisflex.core.service.IService;
import tech.aiflowy.ai.entity.PluginCategory;

/**
 *  服务层。
 *
 * @author Administrator
 * @since 2025-05-21
 */
public interface AiPluginCategoriesService extends IService<PluginCategory> {

    boolean doRemoveCategory(Integer id);
}
