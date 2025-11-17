package tech.aiflowy.admin.controller.ai;

import org.springframework.web.bind.annotation.*;
import tech.aiflowy.ai.entity.AiPluginCategories;
import tech.aiflowy.ai.entity.AiPluginCategoryRelation;
import tech.aiflowy.ai.service.AiPluginCategoryRelationService;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.web.controller.BaseCurdController;
import tech.aiflowy.common.web.jsonbody.JsonBody;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 *  控制层。
 *
 * @author wangGangQiang
 * @since 2025-05-21
 */
@RestController
@RequestMapping("/api/v1/aiPluginCategoryRelation")
public class AiPluginCategoryRelationController extends BaseCurdController<AiPluginCategoryRelationService, AiPluginCategoryRelation> {
    public AiPluginCategoryRelationController(AiPluginCategoryRelationService service) {
        super(service);
    }

    @Resource
    private AiPluginCategoryRelationService relationService;

    @PostMapping("/updateRelation")
    public Result<Boolean> updateRelation(
            @JsonBody(value="pluginId") long pluginId,
            @JsonBody(value="categoryIds") ArrayList<Integer> categoryIds
    ){
        return Result.ok(relationService.updateRelation(pluginId, categoryIds));
    }

    @GetMapping("/getPluginCategories")
    public Result<List<AiPluginCategories>> getPluginCategories(@RequestParam(value="pluginId") long pluginId
    ){
        return Result.ok(relationService.getPluginCategories(pluginId));
    }
}