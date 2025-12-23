package tech.aiflowy.ai.service;

import com.mybatisflex.core.service.IService;
import tech.aiflowy.ai.entity.PluginCategory;
import tech.aiflowy.ai.entity.PluginCategoryMapping;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 *  服务层。
 *
 * @author Administrator
 * @since 2025-05-21
 */
public interface PluginCategoryMappingService extends IService<PluginCategoryMapping> {

    boolean updateRelation(BigInteger pluginId, ArrayList<BigInteger> categoryIds);

    List<PluginCategory> getPluginCategories(BigInteger pluginId);
}
