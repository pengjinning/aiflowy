package tech.aiflowy.common.constant.enums;

import tech.aiflowy.common.annotation.DictDef;

@DictDef(name = "定时任务类型", code = "jobType", keyField = "code", labelField = "text")
public enum EnumJobType {

    TINY_FLOW(1,"工作流"),
    SPRING_BEAN(2,"SpringBean"),
    JAVA_CLASS(3,"Java类");
    ;

    private final int code;
    private final String text;

    EnumJobType(int code, String text) {
        this.code = code;
        this.text = text;
    }

    public int getCode() {
        return code;
    }

    public String getText() {
        return text;
    }
}
