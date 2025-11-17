package tech.aiflowy.admin.controller.ai;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.aiflowy.ai.entity.AiResource;
import tech.aiflowy.ai.service.AiResourceService;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.entity.LoginAccount;
import tech.aiflowy.common.satoken.util.SaTokenUtil;
import tech.aiflowy.common.web.controller.BaseCurdController;

import java.util.Date;

/**
 * 素材库 控制层。
 *
 * @author ArkLight
 * @since 2025-06-27
 */
@RestController
@RequestMapping("/api/v1/aiResource")
public class AiResourceController extends BaseCurdController<AiResourceService, AiResource> {
    public AiResourceController(AiResourceService service) {
        super(service);
    }

    @Override
    protected Result<?> onSaveOrUpdateBefore(AiResource entity, boolean isSave) {
        LoginAccount loginUser = SaTokenUtil.getLoginAccount();
        if (isSave) {
            String resourceUrl = entity.getResourceUrl();
            entity.setSuffix(resourceUrl.substring(resourceUrl.lastIndexOf(".") + 1));
            commonFiled(entity,loginUser.getId(),loginUser.getTenantId(), loginUser.getDeptId());
        } else {
            entity.setModified(new Date());
            entity.setModifiedBy(loginUser.getId());
        }
        return super.onSaveOrUpdateBefore(entity, isSave);
    }
}