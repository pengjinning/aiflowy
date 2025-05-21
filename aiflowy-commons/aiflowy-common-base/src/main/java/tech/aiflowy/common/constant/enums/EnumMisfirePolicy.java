package tech.aiflowy.common.constant.enums;

import tech.aiflowy.common.annotation.DictDef;

@DictDef(name = "错过策略", code = "misfirePolicy", keyField = "code", labelField = "text")
public enum EnumMisfirePolicy {

    DEFAULT(0,"默认"),
    MISFIRE_IGNORE_MISFIRES(1,"立即触发"),
    MISFIRE_FIRE_AND_PROCEED(2,"立即触发一次"),
    MISFIRE_DO_NOTHING(3,"忽略");
    ;

    private final int code;
    private final String text;

    EnumMisfirePolicy(int code, String text) {
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
