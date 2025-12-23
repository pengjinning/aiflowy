package tech.aiflowy.ai.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import tech.aiflowy.ai.entity.BotCategory;
import tech.aiflowy.ai.mapper.AiBotCategoryMapper;
import tech.aiflowy.ai.service.AiBotCategoryService;

/**
 * bot分类 服务层实现。
 *
 * @author ArkLight
 * @since 2025-12-18
 */
@Service
public class AiBotCategoryServiceImpl extends ServiceImpl<AiBotCategoryMapper, BotCategory>  implements AiBotCategoryService {

}
