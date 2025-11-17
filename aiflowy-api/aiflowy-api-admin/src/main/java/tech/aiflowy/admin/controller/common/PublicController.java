package tech.aiflowy.admin.controller.common;

import tech.aiflowy.common.Consts;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.tcaptcha.TCaptchaConfig;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/public/")
public class PublicController {

    @Resource
    private TCaptchaConfig tCaptchaConfig;

    @GetMapping("tcaptcha")
    public Result<?> tcaptcha() {
        Map<String, Object> map = new HashMap<>();
        map.put("enable", tCaptchaConfig.getEnable());
        map.put("appId", tCaptchaConfig.getCaptchaAppId());
        return Result.ok(map);

    }

    @GetMapping("getDataScopeState")
    public Result<?> getDataScopeState() {
        Map<String, Object> map = new HashMap<>();
        map.put("enable", Consts.ENABLE_DATA_SCOPE);
        return Result.ok(map);
    }
}
