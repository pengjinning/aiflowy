package tech.aiflowy.ai.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import tech.aiflowy.ai.entity.AiPluginCategories;
import tech.aiflowy.ai.entity.AiPluginCategoryRelation;
import tech.aiflowy.ai.mapper.AiPluginCategoriesMapper;
import tech.aiflowy.ai.mapper.AiPluginCategoryRelationMapper;
import tech.aiflowy.ai.service.AiPluginCategoryRelationService;
import org.springframework.stereotype.Service;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.web.exceptions.BusinessException;

import javax.annotation.Resource;
import java.lang.reflect.Array;
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
public class AiPluginCategoryRelationServiceImpl extends ServiceImpl<AiPluginCategoryRelationMapper, AiPluginCategoryRelation>  implements AiPluginCategoryRelationService{

    @Resource
    private AiPluginCategoryRelationMapper relationMapper;

    @Resource
    private AiPluginCategoriesMapper aiPluginCategoriesMapper;

    @Override
    public boolean updateRelation(long pluginId, ArrayList<Integer> categoryIds) {
        if (categoryIds == null){
            QueryWrapper queryWrapper = QueryWrapper.create().select("*")
                    .from("tb_plugin_category_relation")
                    .where("plugin_id  = ?", pluginId);
            int delete = relationMapper.deleteByQuery(queryWrapper);
            if (delete <= 0){
                throw new BusinessException("删除失败");
            }
            return true;
        }
        for (Integer categoryId : categoryIds) {
            QueryWrapper queryWrapper = QueryWrapper.create().select("*")
                    .from("tb_plugin_category_relation")
                    .where("plugin_id  = ?", pluginId)
                    .where("category_id  = ?", categoryId);
            AiPluginCategoryRelation selectedOneByQuery = relationMapper.selectOneByQuery(queryWrapper);
            AiPluginCategoryRelation aiPluginCategoryRelation = new AiPluginCategoryRelation();
            aiPluginCategoryRelation.setCategoryId(categoryId);
            aiPluginCategoryRelation.setPluginId(pluginId);
            if (selectedOneByQuery == null) {
                int insert = relationMapper.insert(aiPluginCategoryRelation);
                if (insert <= 0) {
                    throw new BusinessException("新增失败");
                }
            } else {
                QueryWrapper queryWrapperUpdate = QueryWrapper.create().select("*")
                        .from("tb_plugin_category_relation")
                        .where("plugin_id  = ?", pluginId);
                AiPluginCategoryRelation selectedOne = relationMapper.selectOneByQuery(queryWrapper);
                if (selectedOne != null){
                    continue;
                }
                int update = relationMapper.updateByQuery(aiPluginCategoryRelation, queryWrapperUpdate);
                if (update <= 0){
                    throw new BusinessException("更新失败");
                }
            }

        }
        return true;
    }

    @Override
    public List<AiPluginCategories>  getPluginCategories(long pluginId) {
        QueryWrapper categoryQueryWrapper =   QueryWrapper.create().select("category_id")
                .from("tb_plugin_category_relation")
                .where("plugin_id  = ?", pluginId);
        List<BigInteger> categoryIdList =  relationMapper.selectListByQueryAs(categoryQueryWrapper, BigInteger.class);
        List<AiPluginCategories> aiPluginCategories = new ArrayList<AiPluginCategories>();
        if (categoryIdList.isEmpty()){
            return aiPluginCategories;
        }
        QueryWrapper categoryQuery =  QueryWrapper.create().select("id, name")
                .from("tb_plugin_categories")
                .in("id", categoryIdList);
        aiPluginCategories = aiPluginCategoriesMapper.selectListByQuery(categoryQuery);
        return aiPluginCategories;
    }
}
