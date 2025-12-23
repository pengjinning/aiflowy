package tech.aiflowy.ai.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import tech.aiflowy.ai.entity.PluginCategory;
import tech.aiflowy.ai.entity.PluginCategoryMapping;
import tech.aiflowy.ai.mapper.AiPluginCategoriesMapper;
import tech.aiflowy.ai.mapper.AiPluginCategoryRelationMapper;
import tech.aiflowy.ai.service.AiPluginCategoryRelationService;
import org.springframework.stereotype.Service;
import tech.aiflowy.common.web.exceptions.BusinessException;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 *  服务层实现。
 *
 * @author Administrator
 * @since 2025-05-21
 */
@Service
public class AiPluginCategoryRelationServiceImpl extends ServiceImpl<AiPluginCategoryRelationMapper, PluginCategoryMapping>  implements AiPluginCategoryRelationService{

    @Resource
    private AiPluginCategoryRelationMapper relationMapper;

    @Resource
    private AiPluginCategoriesMapper aiPluginCategoriesMapper;

    @Override
    public boolean updateRelation(long pluginId, ArrayList<Integer> categoryIds) {
        if (categoryIds == null){
            QueryWrapper queryWrapper = QueryWrapper.create().select("*")
                    .from("tb_plugin_category_mapping")
                    .where("plugin_id  = ?", pluginId);
            int delete = relationMapper.deleteByQuery(queryWrapper);
            if (delete <= 0){
                throw new BusinessException("删除失败");
            }
            return true;
        }
        for (Integer categoryId : categoryIds) {
            QueryWrapper queryWrapper = QueryWrapper.create().select("*")
                    .from("tb_plugin_category_mapping")
                    .where("plugin_id  = ?", pluginId)
                    .where("category_id  = ?", categoryId);
            PluginCategoryMapping selectedOneByQuery = relationMapper.selectOneByQuery(queryWrapper);
            PluginCategoryMapping pluginCategoryMapping = new PluginCategoryMapping();
            pluginCategoryMapping.setCategoryId(categoryId);
            pluginCategoryMapping.setPluginId(pluginId);
            if (selectedOneByQuery == null) {
                int insert = relationMapper.insert(pluginCategoryMapping);
                if (insert <= 0) {
                    throw new BusinessException("新增失败");
                }
            } else {
                QueryWrapper queryWrapperUpdate = QueryWrapper.create().select("*")
                        .from("tb_plugin_category_mapping")
                        .where("plugin_id  = ?", pluginId);
                PluginCategoryMapping selectedOne = relationMapper.selectOneByQuery(queryWrapper);
                if (selectedOne != null){
                    continue;
                }
                int update = relationMapper.updateByQuery(pluginCategoryMapping, queryWrapperUpdate);
                if (update <= 0){
                    throw new BusinessException("更新失败");
                }
            }

        }
        return true;
    }

    @Override
    public List<PluginCategory>  getPluginCategories(long pluginId) {
        QueryWrapper categoryQueryWrapper =   QueryWrapper.create().select("category_id")
                .from("tb_plugin_category_mapping")
                .where("plugin_id  = ?", pluginId);
        List<BigInteger> categoryIdList =  relationMapper.selectListByQueryAs(categoryQueryWrapper, BigInteger.class);
        List<PluginCategory> pluginCategories = new ArrayList<PluginCategory>();
        if (categoryIdList.isEmpty()){
            return pluginCategories;
        }
        QueryWrapper categoryQuery =  QueryWrapper.create().select("id, name")
                .from("tb_plugin_category")
                .in("id", categoryIdList);
        pluginCategories = aiPluginCategoriesMapper.selectListByQuery(categoryQuery);
        return pluginCategories;
    }
}
