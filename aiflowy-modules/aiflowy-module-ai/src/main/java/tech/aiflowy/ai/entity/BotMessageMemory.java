package tech.aiflowy.ai.entity;

import com.agentsflex.core.memory.ChatMemory;
import com.agentsflex.core.message.Message;
import com.mybatisflex.core.query.QueryWrapper;
import tech.aiflowy.ai.service.AiBotMessageService;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BotMessageMemory implements ChatMemory {
    private final BigInteger botId;
    private final BigInteger accountId;
    private final String sessionId;
    private final AiBotMessageService messageService;
    
    public BotMessageMemory(BigInteger botId, BigInteger accountId, String sessionId,
                            AiBotMessageService messageService) {
        this.botId = botId;
        this.accountId = accountId;
        this.sessionId = sessionId;
        this.messageService = messageService;
    }

    @Override
    public List<Message> getMessages(int count) {
        List<BotMessage> sysAiMessages = messageService.list(QueryWrapper.create()
                .eq(BotMessage::getBotId, botId, true)
                .eq(BotMessage::getAccountId, accountId, true)
                .eq(BotMessage::getSessionId, sessionId, true)
                .orderBy(BotMessage::getCreated, true)
                .limit(count)
        );

        if (sysAiMessages == null || sysAiMessages.isEmpty()) {
            return null;
        }

        List<Message> messages = new ArrayList<>(sysAiMessages.size());
        for (BotMessage botMessage : sysAiMessages) {
            Message message = botMessage.getContentAsMessage();
            if (message != null) messages.add(message);
        }
        return messages;
    }


    @Override
    public void addMessage(Message message) {

        BotMessage dbMessage = new BotMessage();
        dbMessage.setCreated(new Date());
        dbMessage.setBotId(botId);
        dbMessage.setAccountId(accountId);
        dbMessage.setSessionId(sessionId);
        dbMessage.setContentAndRole(message);
        messageService.save(dbMessage);
    }

    @Override
    public void clear() {

    }

    @Override
    public Object id() {
        return botId;
    }

}
