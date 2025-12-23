package tech.aiflowy.ai.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import tech.aiflowy.ai.entity.Plugin;
import tech.aiflowy.ai.entity.PluginCategoryMapping;
import tech.aiflowy.ai.entity.PluginItem;
import tech.aiflowy.ai.mapper.AiPluginCategoryRelationMapper;
import tech.aiflowy.ai.mapper.AiPluginMapper;
import tech.aiflowy.ai.service.AiBotPluginsService;
import tech.aiflowy.ai.service.AiPluginService;
import org.springframework.stereotype.Service;
import tech.aiflowy.ai.service.AiPluginToolService;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.web.exceptions.BusinessException;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 服务层实现。
 *
 * @author WangGangqiang
 * @since 2025-04-25
 */
@Service
public class AiPluginServiceImpl extends ServiceImpl<AiPluginMapper, Plugin> implements AiPluginService {

    private static final Logger log = LoggerFactory.getLogger(AiPluginServiceImpl.class);

    @Resource
    AiPluginMapper aiPluginMapper;

    @Resource
    AiPluginCategoryRelationMapper aiPluginCategoryRelationMapper;

    @Resource
    private AiBotPluginsService aiBotPluginsService;

    @Resource
    private AiPluginToolService aiPluginToolService;

    @Override
    public boolean savePlugin(Plugin plugin) {
        plugin.setCreated(new Date());
        int insert = aiPluginMapper.insert(plugin);
        if (insert <= 0) {
            throw new BusinessException("保存失败");
        }
        return true;
    }

    @Override
    @Transactional
    public boolean removePlugin(String id) {

        List<PluginItem> pluginItems = aiPluginToolService.getByPluginId(id);
        List<BigInteger> pluginToolIds = new ArrayList<>();

        if (pluginItems != null && !pluginItems.isEmpty()) {

            pluginToolIds = pluginItems.stream().map(PluginItem::getId).collect(Collectors.toList());

            QueryWrapper queryWrapper = QueryWrapper.create();
            queryWrapper.in("plugin_tool_id", pluginToolIds);

            boolean exists = aiBotPluginsService.exists(queryWrapper);
            if (exists){
                throw new BusinessException("插件中有工具还关联着bot，请先取消关联！");
            }

        }

        if ( !pluginToolIds.isEmpty()) {
           boolean result =  aiPluginToolService.removeByIds(pluginToolIds);
           if (!result){
               log.error("删除插件工具表结果为0");
               throw new BusinessException("删除失败，请稍后重试！");
           }
        }


        int remove = aiPluginMapper.deleteById(id);
        if (remove <= 0) {
            log.error("删除插件结果为0");
            throw new BusinessException("删除失败，请稍后重试！");
        }

        return true;

    }

    @Override
    public boolean updatePlugin(Plugin plugin) {
        QueryWrapper queryWrapper = QueryWrapper.create().select("id")
                .from("tb_plugin")
                .where("id = ?", plugin.getId());
        int update = aiPluginMapper.updateByQuery(plugin, queryWrapper);
        if (update <= 0) {
            throw new BusinessException("修改失败");
        }
        return true;
    }

    @Override
    public List<Plugin> getList() {
        QueryWrapper queryWrapper = QueryWrapper.create().select("id, name, description, icon")
                .from("tb_plugin");
        List<Plugin> plugins = aiPluginMapper.selectListByQueryAs(queryWrapper, Plugin.class);
        return plugins;
    }

    @Override
    public Result<Page<Plugin>> pageByCategory(Long pageNumber, Long pageSize, int category) {
        // 通过分类查询插件
        QueryWrapper queryWrapper = QueryWrapper.create().select("plugin_id")
                .from("tb_plugin_category_mapping")
                .where("category_id = ? ", category);
        // 分页查询该分类中的插件
        Page<BigInteger> pagePluginIds = aiPluginCategoryRelationMapper.paginateAs(new Page<>(pageNumber, pageSize), queryWrapper, BigInteger.class);
        Page<PluginCategoryMapping> paginateCategories = aiPluginCategoryRelationMapper.paginate(pageNumber, pageSize, queryWrapper);
        List<Plugin> plugins = Collections.emptyList();
        if (paginateCategories.getRecords().isEmpty()) {
            return Result.ok(new Page<>(plugins, pageNumber, pageSize, paginateCategories.getTotalRow()));
        }
        List<BigInteger> pluginIds = pagePluginIds.getRecords();
        // 查询对应的插件信息
        QueryWrapper queryPluginWrapper = QueryWrapper.create().select("*")
                .from("tb_plugin")
                .in("id", pluginIds);
        plugins = aiPluginMapper.selectListByQuery(queryPluginWrapper);
        Page<Plugin> aiPluginPage = new Page<>(plugins, pageNumber, pageSize, paginateCategories.getTotalRow());
        return Result.ok(aiPluginPage);
    }


}
