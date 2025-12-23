package tech.aiflowy.usercenter.controller.ai;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.aiflowy.ai.entity.BotMessage;
import tech.aiflowy.ai.service.AiBotMessageService;
import tech.aiflowy.ai.vo.ChatMessageVO;
import tech.aiflowy.common.annotation.UsePermission;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.satoken.util.SaTokenUtil;
import tech.aiflowy.common.web.controller.BaseCurdController;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Bot 消息记录表 控制层。
 *
 * @author michael
 * @since 2024-11-04
 */
@RestController
@RequestMapping("/userCenter/aiBotMessage")
@UsePermission(moduleName = "/api/v1/aiBot")
public class UcAiBotMessageController extends BaseCurdController<AiBotMessageService, BotMessage> {
    private final AiBotMessageService aiBotMessageService;

    public UcAiBotMessageController(AiBotMessageService service, AiBotMessageService aiBotMessageService) {
        super(service);
        this.aiBotMessageService = aiBotMessageService;
    }

    @GetMapping("/getMessages")
    public Result<List<ChatMessageVO>> getMessages(BigInteger botId, String sessionId) {
        List<ChatMessageVO> res = new ArrayList<>();
        QueryWrapper w = QueryWrapper.create();
        w.eq(BotMessage::getBotId, botId);
        w.eq(BotMessage::getSessionId, sessionId);
        w.eq(BotMessage::getAccountId, SaTokenUtil.getLoginAccount().getId());
        List<BotMessage> list = aiBotMessageService.list(w);
        if (CollectionUtil.isNotEmpty(list)) {
            for (BotMessage message : list) {
                ChatMessageVO vo = new ChatMessageVO();
                vo.setKey(message.getId().toString());
                vo.setRole(message.getRole());
                vo.setContent(JSON.parseObject(message.getContent()).getString("textContent"));
                vo.setPlacement("user".equals(message.getRole()) ? "end" : "start");
                vo.setCreated(message.getCreated());
                res.add(vo);
            }
        }
        return Result.ok(res);
    }
}
