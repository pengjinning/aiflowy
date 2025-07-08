package tech.aiflowy.common.constant.enums;

import tech.aiflowy.common.annotation.DictDef;

@DictDef(name = "素材类型", code = "resourceType", keyField = "code", labelField = "text")
public enum EnumResourceType {

    IMG(0, "图片"),
    VIDEO(1, "视频"),
    AUDIO(2, "音频"),
    DOC(3, "文档"),
    OTHER(99, "其他"),
    ;

    private Integer code;
    private String text;

    EnumResourceType(Integer code, String text) {
        this.code = code;
        this.text = text;
    }

    public static EnumResourceType getByCode(Integer code) {
        if (null == code) {
            return null;
        }
        for (EnumResourceType type : EnumResourceType.values()) {
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
