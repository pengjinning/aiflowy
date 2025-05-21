package tech.aiflowy.common.constant.enums;

import tech.aiflowy.common.annotation.DictDef;

@DictDef(name = "定时任务状态", code = "jobStatus", keyField = "code", labelField = "text")
public enum EnumJobStatus {

    STOP(0,"停止"),
    RUNNING(1,"运行中"),
    ;

    private final int code;
    private final String text;

    EnumJobStatus(int code, String text) {
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
