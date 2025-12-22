package tech.aiflowy.admin.controller.system;

import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.aiflowy.common.util.IdUtil;

@RestController
@RequestMapping("/api/v1/token")
public class SysGenerateTokenController {
    // 手动生成 Token 并绑定账号
    @GetMapping("/generateToken")
    public SaResult generateToken() {
        long loginId = StpUtil.getLoginIdAsLong();             // 假设这是你要绑定的账号ID
        String customToken = IdUtil.generateUUID();; // 自定义的 Token 字符串
        SaLoginModel saLoginModel = new SaLoginModel();
        saLoginModel.setToken(customToken);
        saLoginModel.setTimeout(-1);
        // 将 loginId 与 customToken 关联，并设置有效期（单位：秒）
        StpUtil.createLoginSession(loginId, saLoginModel); // 24小时有效期
        System.out.println("生成了token: " + customToken);
        return SaResult.ok("Token 已生成").setData(customToken);
    }
}
