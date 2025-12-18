package tech.aiflowy.admin.controller.ai;

import tech.aiflowy.ai.entity.AiLlmProvider;
import tech.aiflowy.ai.service.AiLlmProviderService;
import tech.aiflowy.common.annotation.UsePermission;
import tech.aiflowy.common.web.controller.BaseCurdController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *  控制层。
 *
 * @author 12076
 * @since 2025-12-16
 */
@RestController
@RequestMapping("/api/v1/aiLlmProvider")
@UsePermission(moduleName = "/api/v1/aiLlm")
public class AiLlmProviderController extends BaseCurdController<AiLlmProviderService, AiLlmProvider> {
    public AiLlmProviderController(AiLlmProviderService service) {
        super(service);
    }
}