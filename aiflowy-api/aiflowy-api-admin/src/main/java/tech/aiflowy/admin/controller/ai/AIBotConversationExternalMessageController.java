package tech.aiflowy.admin.controller.ai;

import cn.dev33.satoken.annotation.SaIgnore;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.aiflowy.ai.entity.AiBotConversationMessage;
import tech.aiflowy.ai.service.AiBotConversationMessageService;
import tech.aiflowy.common.web.controller.BaseCurdController;

@RestController
@RequestMapping("/api/v1/conversation")
@SaIgnore
public class AIBotConversationExternalMessageController extends BaseCurdController<AiBotConversationMessageService, AiBotConversationMessage> {

    public AIBotConversationExternalMessageController(AiBotConversationMessageService service) {
        super(service);
    }
}
