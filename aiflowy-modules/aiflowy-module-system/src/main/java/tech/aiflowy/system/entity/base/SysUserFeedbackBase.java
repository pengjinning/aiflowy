package tech.aiflowy.system.entity.base;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import tech.aiflowy.common.entity.DateEntity;


public class SysUserFeedbackBase extends DateEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @Id(keyType = KeyType.Generator, value = "snowFlakeId", comment = "主键id")
    private BigInteger id;

    /**
     * 问题摘要
     */
    @Column(comment = "问题摘要")
    private String feedbackContent;

    /**
     * 问题类型（1-功能故障 2-优化建议 3-账号问题 4-其他）
     */
    @Column(comment = "问题类型（1-功能故障 2-优化建议 3-账号问题 4-其他）")
    private Integer feedbackType;

    /**
     * 联系方式【手机号/邮箱】
     */
    @Column(comment = "联系方式【手机号/邮箱】")
    private String contactInfo;

    /**
     * 附件url
     */
    @Column(comment = "附件url")
    private String attachmentUrl;

    /**
     * 反馈处理状态（0-未查看 1-已查看 2-已处理）
     */
    @Column(comment = "反馈处理状态（0-未查看 1-已查看 2-已处理）")
    private Integer status;

    /**
     * 处理人id
     */
    @Column(comment = "处理人id")
    private BigInteger handlerId;

    /**
     * 处理时间
     */
    @Column(comment = "处理时间")
    private Date handleTime;

    /**
     * 部门ID
     */
    @Column(comment = "部门ID")
    private BigInteger deptId;

    /**
     * 租户ID
     */
    @Column(tenantId = true, comment = "租户ID")
    private BigInteger tenantId;

    /**
     * 创建时间
     */
    @Column(comment = "创建时间")
    private Date created;

    /**
     * 创建人
     */
    @Column(comment = "创建人")
    private BigInteger createdBy;

    /**
     * 最后修改时间
     */
    @Column(comment = "最后修改时间")
    private Date modified;

    /**
     * 最后修改的人
     */
    @Column(comment = "最后修改的人")
    private BigInteger modifiedBy;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public String getFeedbackContent() {
        return feedbackContent;
    }

    public void setFeedbackContent(String feedbackContent) {
        this.feedbackContent = feedbackContent;
    }

    public Integer getFeedbackType() {
        return feedbackType;
    }

    public void setFeedbackType(Integer feedbackType) {
        this.feedbackType = feedbackType;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public String getAttachmentUrl() {
        return attachmentUrl;
    }

    public void setAttachmentUrl(String attachmentUrl) {
        this.attachmentUrl = attachmentUrl;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public BigInteger getHandlerId() {
        return handlerId;
    }

    public void setHandlerId(BigInteger handlerId) {
        this.handlerId = handlerId;
    }

    public Date getHandleTime() {
        return handleTime;
    }

    public void setHandleTime(Date handleTime) {
        this.handleTime = handleTime;
    }

    public BigInteger getDeptId() {
        return deptId;
    }

    public void setDeptId(BigInteger deptId) {
        this.deptId = deptId;
    }

    public BigInteger getTenantId() {
        return tenantId;
    }

    public void setTenantId(BigInteger tenantId) {
        this.tenantId = tenantId;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public BigInteger getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(BigInteger createdBy) {
        this.createdBy = createdBy;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public BigInteger getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(BigInteger modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

}
