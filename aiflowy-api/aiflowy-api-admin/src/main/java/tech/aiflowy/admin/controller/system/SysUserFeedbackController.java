package tech.aiflowy.admin.controller.system;

import cn.dev33.satoken.stp.StpUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.web.controller.BaseCurdController;
import tech.aiflowy.system.entity.SysUserFeedback;
import tech.aiflowy.system.service.SysUserFeedbackService;

import java.math.BigInteger;
import java.util.Date;

/**
 *  控制层。
 *
 * @author 12076
 * @since 2025-12-30
 */
@RestController
@RequestMapping("/api/v1/sysUserFeedback")
public class SysUserFeedbackController extends BaseCurdController<SysUserFeedbackService, SysUserFeedback> {
    public SysUserFeedbackController(SysUserFeedbackService service) {
        super(service);
    }

    @Override
    protected Result<?> onSaveOrUpdateBefore(SysUserFeedback entity, boolean isSave) {
        if (!isSave) {
            entity.setHandlerId(new BigInteger(StpUtil.getLoginIdAsString()));
            entity.setModified(new Date());
            entity.setHandleTime(new Date());
        }
        return super.onSaveOrUpdateBefore(entity, isSave);
    }
}