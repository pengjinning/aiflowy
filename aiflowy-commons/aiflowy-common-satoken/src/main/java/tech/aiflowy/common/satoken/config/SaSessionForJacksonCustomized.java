package tech.aiflowy.common.satoken.config;

import cn.dev33.satoken.session.SaSession;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({"timeout"})
public class SaSessionForJacksonCustomized extends SaSession {

    /**
     *
     */
    private static final long serialVersionUID = -7600983549653130681L;

    public SaSessionForJacksonCustomized() {
        super();
    }

    /**
     * 构建一个Session对象
     * @param id Session的id
     */
    public SaSessionForJacksonCustomized(String id) {
        super(id);
    }

}
