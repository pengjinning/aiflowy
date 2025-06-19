package tech.aiflowy.ai.utils;

import com.agentsflex.core.llm.Llm;
import com.agentsflex.llm.deepseek.DeepseekLlm;

public class AiBotChatUtil {

    /**
     * 判断当前大模型的方法名称是否需要传非中文的 tool 名称
     * @param llm
     * @return
     */
    public static boolean needEnglishName(Llm llm){
        return llm instanceof DeepseekLlm;
    }
}
