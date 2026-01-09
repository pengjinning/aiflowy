package tech.aiflowy.admin.controller.ai;

import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.aiflowy.ai.entity.Model;
import tech.aiflowy.ai.entity.ModelProvider;
import tech.aiflowy.ai.service.ModelProviderService;
import tech.aiflowy.ai.service.ModelService;
import tech.aiflowy.common.annotation.UsePermission;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.web.controller.BaseCurdController;
import tech.aiflowy.common.web.exceptions.BusinessException;
import tech.aiflowy.common.web.jsonbody.JsonBody;

import java.io.Serializable;

/**
 * 控制层。
 *
 * @author 12076
 * @since 2025-12-16
 */
@RestController
@RequestMapping("/api/v1/modelProvider")
@UsePermission(moduleName = "/api/v1/model")
public class ModelProviderController extends BaseCurdController<ModelProviderService, ModelProvider> {
    private final ModelService modelService;

    public ModelProviderController(ModelProviderService service, ModelService modelService) {
        super(service);
        this.modelService = modelService;
    }

    @Override
    @PostMapping("remove")
    @Transactional
    public Result<?> remove(@JsonBody(value = "id", required = true) Serializable id) {
        QueryWrapper modelQueryWrapper = QueryWrapper.create()
                .in(Model::getProviderId, id)
                .eq(Model::getWithUsed, true);
        if (modelService.count(modelQueryWrapper) > 0) {
            throw new BusinessException("Delete all child models before removing this Model Provider");
        }
        return Result.ok(service.removeById(id));
    }
}