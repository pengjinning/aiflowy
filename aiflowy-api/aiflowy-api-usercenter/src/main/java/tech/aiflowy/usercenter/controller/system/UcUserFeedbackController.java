package tech.aiflowy.usercenter.controller.system;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.aiflowy.common.web.controller.BaseCurdController;
import tech.aiflowy.system.entity.SysUserFeedback;
import tech.aiflowy.system.service.SysUserFeedbackService;


/**
 *  控制层。
 *
 * @author 12076
 * @since 2025-12-30
 */
@RestController
@RequestMapping("/userCenter/sysUserFeedback")
public class UcUserFeedbackController extends BaseCurdController<SysUserFeedbackService, SysUserFeedback> {
    public UcUserFeedbackController(SysUserFeedbackService service) {
        super(service);
    }

}