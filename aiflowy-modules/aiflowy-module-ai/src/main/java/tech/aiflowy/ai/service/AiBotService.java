package tech.aiflowy.ai.service;

import com.agentsflex.core.model.chat.ChatModel;
import com.agentsflex.core.model.chat.ChatOptions;
import com.agentsflex.core.prompt.MemoryPrompt;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import tech.aiflowy.ai.entity.Bot;
import com.mybatisflex.core.service.IService;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 *  服务层。
 *
 * @author michael
 * @since 2024-08-23
 */
public interface AiBotService extends IService<Bot> {

    Bot getDetail(String id);

    void updateBotLlmId(Bot aiBot);

    Bot getByAlias(String alias);

    SseEmitter startChat(BigInteger botId, ChatModel chatModel, String prompt, MemoryPrompt memoryPrompt, ChatOptions chatOptions, String sessionId, List<Map<String, String>> messages);
}
