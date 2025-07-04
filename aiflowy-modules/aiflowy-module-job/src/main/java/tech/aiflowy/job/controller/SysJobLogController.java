package tech.aiflowy.job.controller;

import tech.aiflowy.common.annotation.UsePermission;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.satoken.util.SaTokenUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.aiflowy.common.entity.LoginAccount;
import tech.aiflowy.common.web.controller.BaseCurdController;
import tech.aiflowy.job.entity.SysJobLog;
import tech.aiflowy.job.service.SysJobLogService;

/**
 * 系统任务日志 控制层。
 *
 * @author xiaoma
 * @since 2025-05-20
 */
@RestController
@RequestMapping("/api/v1/sysJobLog")
@UsePermission(moduleName = "/api/v1/sysJob")
public class SysJobLogController extends BaseCurdController<SysJobLogService, SysJobLog> {
    public SysJobLogController(SysJobLogService service) {
        super(service);
    }

    @Override
    protected Result onSaveOrUpdateBefore(SysJobLog entity, boolean isSave) {
        LoginAccount loginUser = SaTokenUtil.getLoginAccount();
        if (isSave) {
            commonFiled(entity,loginUser.getId(),loginUser.getTenantId(), loginUser.getDeptId());
        }
        return super.onSaveOrUpdateBefore(entity, isSave);
    }
}