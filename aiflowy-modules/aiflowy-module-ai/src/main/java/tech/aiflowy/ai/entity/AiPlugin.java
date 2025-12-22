package tech.aiflowy.ai.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.RelationOneToMany;
import com.mybatisflex.annotation.Table;
import tech.aiflowy.ai.entity.base.AiPluginBase;
import tech.aiflowy.ai.mapper.AiPluginToolMapper;

import java.util.List;


/**
 *  实体类。
 *
 * @author Administrator
 * @since 2025-04-25
 */
@Table("tb_plugin")
public class AiPlugin extends AiPluginBase {

    @RelationOneToMany(selfField = "id", targetField = "pluginId", targetTable = "tb_plugin_tool")
    private List<AiPluginTool> tools;

    public String getTitle() {
        return this.getName();
    }

    public List<AiPluginTool> getTools() {
        return tools;
    }

    public void setTools(List<AiPluginTool> tools) {
        this.tools = tools;
    }
}
