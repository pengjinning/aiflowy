package tech.aiflowy.ai.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import tech.aiflowy.ai.entity.PluginCategory;
import tech.aiflowy.ai.mapper.AiPluginCategoriesMapper;
import tech.aiflowy.ai.mapper.AiPluginCategoryRelationMapper;
import tech.aiflowy.ai.service.AiPluginCategoriesService;
import org.springframework.stereotype.Service;
import tech.aiflowy.common.web.exceptions.BusinessException;

import javax.annotation.Resource;

/**
 *  服务层实现。
 *
 * @author Administrator
 * @since 2025-05-21
 */
@Service
public class AiPluginCategoriesServiceImpl extends ServiceImpl<AiPluginCategoriesMapper, PluginCategory>  implements AiPluginCategoriesService{

    @Resource
    private AiPluginCategoryRelationMapper relationMapper;

    @Resource
    private AiPluginCategoriesMapper aiPluginCategoriesMapper;

    @Override
    public boolean doRemoveCategory(Integer id) {
        QueryWrapper queryWrapper = QueryWrapper.create().select("plugin_id")
                .where("category_id = ? ", id);
        long relationCount = relationMapper.selectCountByQuery(queryWrapper);
        if (relationCount > 0){
            int deletePluginRelation = relationMapper.deleteByQuery(queryWrapper);
            if (deletePluginRelation <= 0){
                throw new BusinessException("删除失败");
            }
        }

        int deleteCategory = aiPluginCategoriesMapper.deleteById(id);
        if (deleteCategory <= 0){
            throw new BusinessException("删除失败");
        }
        return true;
    }
}
