package tech.aiflowy.admin.controller.ai;

import tech.aiflowy.ai.entity.BotModel;
import tech.aiflowy.ai.service.AiBotLlmService;
import tech.aiflowy.common.annotation.UsePermission;
import tech.aiflowy.common.web.controller.BaseCurdController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *  控制层。
 *
 * @author michael
 * @since 2024-08-28
 */
@RestController
@RequestMapping("/api/v1/aiBotLlm")
@UsePermission(moduleName = "/api/v1/aiBot")
public class AiBotLlmController extends BaseCurdController<AiBotLlmService, BotModel> {
    public AiBotLlmController(AiBotLlmService service) {
        super(service);
    }
}