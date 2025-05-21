package tech.aiflowy.common.constant.enums;

import tech.aiflowy.common.annotation.DictDef;

@DictDef(name = "任务执行结果", code = "jobResult", keyField = "code", labelField = "text")
public enum EnumJobResult {


    SUCCESS(1,"成功"),
    FAIL(0,"失败"),
    ;

    private final int code;
    private final String text;

    EnumJobResult(int code, String text) {
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
