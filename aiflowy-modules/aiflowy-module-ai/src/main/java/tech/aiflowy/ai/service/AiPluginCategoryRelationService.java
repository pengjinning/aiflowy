package tech.aiflowy.ai.service;

import com.mybatisflex.core.service.IService;
import tech.aiflowy.ai.entity.PluginCategory;
import tech.aiflowy.ai.entity.PluginCategoryMapping;

import java.util.ArrayList;
import java.util.List;

/**
 *  服务层。
 *
 * @author Administrator
 * @since 2025-05-21
 */
public interface AiPluginCategoryRelationService extends IService<PluginCategoryMapping> {

    boolean updateRelation(long pluginId, ArrayList<Integer> categoryIds);

    List<PluginCategory> getPluginCategories(long pluginId);
}
