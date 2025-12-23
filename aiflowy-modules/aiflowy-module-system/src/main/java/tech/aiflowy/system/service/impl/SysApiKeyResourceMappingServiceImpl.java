package tech.aiflowy.system.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import tech.aiflowy.system.entity.SysApiKey;
import tech.aiflowy.system.entity.SysApiKeyResourceMapping;
import tech.aiflowy.system.mapper.SysApiKeyResourceMappingMapper;
import tech.aiflowy.system.service.SysApiKeyResourceMappingService;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * apikey-请求接口表 服务层实现。
 *
 * @author 12076
 * @since 2025-12-01
 */
@Service
public class SysApiKeyResourceMappingServiceImpl extends ServiceImpl<SysApiKeyResourceMappingMapper, SysApiKeyResourceMapping>  implements SysApiKeyResourceMappingService {

    /**
     * 批量授权apiKey接口
     * @param entity
     */
    @Override
    public void authInterface(SysApiKey entity) {
        this.remove(QueryWrapper.create().eq(SysApiKeyResourceMapping::getApiKeyId, entity.getId()));
        List<SysApiKeyResourceMapping> rows = new ArrayList<>(entity.getPermissionIds().size());
        BigInteger apiKeyId = entity.getId();
        for (BigInteger resourceId : entity.getPermissionIds()) {
            SysApiKeyResourceMapping sysApiKeyResourcePermissionRelationship = new SysApiKeyResourceMapping();
            sysApiKeyResourcePermissionRelationship.setApiKeyId(apiKeyId);
            sysApiKeyResourcePermissionRelationship.setApiKeyResourceId(resourceId);
            rows.add(sysApiKeyResourcePermissionRelationship);
        }
        this.saveBatch(rows);
    }
}
