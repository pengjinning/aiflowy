package tech.aiflowy.ai.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import tech.aiflowy.ai.entity.Resource;
import tech.aiflowy.ai.mapper.AiResourceMapper;
import tech.aiflowy.ai.service.AiResourceService;

/**
 * 素材库 服务层实现。
 *
 * @author ArkLight
 * @since 2025-06-27
 */
@Service
public class AiResourceServiceImpl extends ServiceImpl<AiResourceMapper, Resource>  implements AiResourceService{

}
