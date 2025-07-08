package tech.aiflowy.common.constant.enums;

import tech.aiflowy.common.annotation.DictDef;

@DictDef(name = "素材来源", code = "resourceOriginType", keyField = "code", labelField = "text")
public enum EnumResourceOriginType {

    SYSTEM(0, "系统上传"),
    GENERATE(1, "工作流生成"),

    ;

    private Integer code;
    private String text;

    EnumResourceOriginType(Integer code, String text) {
        this.code = code;
        this.text = text;
    }

    public static EnumResourceOriginType getByCode(Integer code) {
        if (null == code) {
            return null;
        }
        for (EnumResourceOriginType type : EnumResourceOriginType.values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new RuntimeException("内容类型非法");
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }}
