package tech.aiflowy.common.constant.enums;

import tech.aiflowy.common.annotation.DictDef;

@DictDef(name = "定时任务状态", code = "jobStatus", keyField = "code", labelField = "text")
public enum EnumJobExecStatus {

    SUCCESS(1,"成功"),
    FAIL(0,"失败"),
    ;

    private final int code;
    private final String text;

    EnumJobExecStatus(int code, String text) {
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
