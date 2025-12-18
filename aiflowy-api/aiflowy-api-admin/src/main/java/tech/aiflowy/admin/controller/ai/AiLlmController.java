package tech.aiflowy.admin.controller.ai;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.aiflowy.ai.entity.AiLlm;
import tech.aiflowy.ai.mapper.AiLlmMapper;
import tech.aiflowy.ai.service.AiLlmService;
import tech.aiflowy.common.annotation.UsePermission;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.entity.LoginAccount;
import tech.aiflowy.common.tree.Tree;
import tech.aiflowy.common.web.controller.BaseCurdController;
import tech.aiflowy.common.satoken.util.SaTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import tech.aiflowy.common.util.StringUtil;
import org.springframework.util.StringUtils;
import tech.aiflowy.common.web.exceptions.BusinessException;
import tech.aiflowy.common.web.jsonbody.JsonBody;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 控制层。
 *
 * @author michael
 * @since 2024-08-23
 */
@RestController
@RequestMapping("/api/v1/aiLlm")
public class AiLlmController extends BaseCurdController<AiLlmService, AiLlm> {

    public AiLlmController(AiLlmService service) {
        super(service);
    }

    @Autowired
    AiLlmService aiLlmService;

    @Resource
    AiLlmMapper aiLlmMapper;

    @GetMapping("list")
    @SaCheckPermission("/api/v1/aiLlm/query")
    public Result<List<AiLlm>> list(AiLlm entity, Boolean asTree, String sortKey, String sortType) {
        QueryWrapper queryWrapper = QueryWrapper.create(entity, buildOperators(entity));
        queryWrapper.orderBy(buildOrderBy(sortKey, sortType, getDefaultOrderBy()));
        List<AiLlm> list = Tree.tryToTree(aiLlmMapper.selectListWithRelationsByQuery(queryWrapper), asTree);
        return Result.ok(list);
    }

    @GetMapping("getList")
    @SaCheckPermission("/api/v1/aiLlm/query")
    public Result<Map<String, Map<String, List<AiLlm>>>> getList(AiLlm entity) {
        return Result.ok(aiLlmService.getList(entity));
    }

    @PostMapping("/addAiLlm")
    @SaCheckPermission("/api/v1/aiLlm/save")
    public Result<Boolean> addAiLlm(AiLlm entity) {
        LoginAccount account = SaTokenUtil.getLoginAccount();
        commonFiled(entity, account.getId(), account.getTenantId(), account.getDeptId());
        return Result.ok(aiLlmService.addAiLlm(entity));
    }


    @GetMapping("verifyLlmConfig")
    @SaCheckPermission("/api/v1/aiLlm/save")
    public Result<Void> verifyLlmConfig(@RequestParam BigInteger id) {
        AiLlm llm = service.getLlmInstance(id);
        service.verifyLlmConfig(llm);

        return Result.ok();
    }

    @PostMapping("quickAdd")
    @SaCheckPermission("/api/v1/aiLlm/save")
    public Result<Void> quickAdd(@JsonBody(value = "brand",required = true) String brand,@JsonBody(value = "apiKey",required = true) String apiKey){


        if (!StringUtils.hasLength(brand)){
            throw new BusinessException("请选择供应商！");
        }


        aiLlmService.quickAdd(brand,apiKey);

        return Result.ok();
    }

    @PostMapping("/removeByEntity")
    @SaCheckPermission("/api/v1/aiLlm/remove")
    public Result<?> removeByEntity(@RequestBody AiLlm entity) {
        aiLlmService.removeByEntity(entity);
        return Result.ok();
    }

    @GetMapping("/selectLlmByProviderCategory")
    @SaCheckPermission("/api/v1/aiLlm/query")
    public Result<Map<String, List<AiLlm>>> selectLlmByProviderCategory(AiLlm entity, String sortKey, String sortType) {
        QueryWrapper queryWrapper = QueryWrapper.create(entity, buildOperators(entity));
        queryWrapper.orderBy(buildOrderBy(sortKey, sortType, getDefaultOrderBy()));
        BaseMapper<AiLlm> mapper = service.getMapper();
        List<AiLlm> totalList = mapper.selectListWithRelationsByQuery(queryWrapper);
//        List<AiLlm> totalList = Tree.tryToTree(service.list(queryWrapper), asTree);
        Map<String, List<AiLlm>> groupList = totalList.stream().collect(Collectors.groupingBy(AiLlm::getGroupName));
        return Result.ok(groupList);
    }

    @GetMapping("/selectLlmByProviderAndModelType")
    @SaCheckPermission("/api/v1/aiLlm/query")
    public Result<Map<String, List<AiLlm>>> selectLlmByProviderAndModelType(
            @RequestParam String modelType,
            @RequestParam BigInteger providerId,
            @RequestParam(required = false) String selectText
    ) {
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.eq(AiLlm::getProviderId, providerId);
        queryWrapper.eq(AiLlm::getModelType, modelType);
        if (StringUtils.hasLength(selectText)) {
            queryWrapper.and("title like " + "'%" + selectText + "%' " + "or llm_model like " + "'%" + selectText + "%' ");
        }
        List<AiLlm> totalList = service.getMapper().selectListWithRelationsByQuery(queryWrapper);
        Map<String, List<AiLlm>> groupList = totalList.stream().collect(Collectors.groupingBy(AiLlm::getGroupName));
        return Result.ok(groupList);
    }


    /**
     * 添加所有模型
     * @param entity
     * @return
     */
    @PostMapping("addAllLlm")
    public Result<?> addAllLlm(@JsonBody AiLlm entity) {
        QueryWrapper queryWrapper = QueryWrapper.create().eq(AiLlm::getProviderId, entity.getProviderId());
        service.update(entity, queryWrapper);
        return Result.ok();
    }

    @GetMapping("/selectLlmList")
    @SaCheckPermission("/api/v1/aiLlm/query")
    public Result<List<AiLlm>> selectLlmList(AiLlm entity, Boolean asTree, String sortKey, String sortType) {
        QueryWrapper queryWrapper = QueryWrapper.create(entity, buildOperators(entity));
        queryWrapper.orderBy(buildOrderBy(sortKey, sortType, getDefaultOrderBy()));
        List<AiLlm> totalList = Tree.tryToTree(aiLlmMapper.selectListWithRelationsByQuery(queryWrapper), asTree);
        totalList.forEach(aiLlm -> {
            aiLlm.setTitle(aiLlm.getAiLlmProvider().getProviderName() + "/" + aiLlm.getTitle());
        });
        return Result.ok(totalList);
    }

    @Override
    protected Result<?> onSaveOrUpdateBefore(AiLlm entity, boolean isSave) {
        if (isSave) {
            entity.setAdded(true);
        }
        return super.onSaveOrUpdateBefore(entity, isSave);
    }


    @PostMapping("removeLlmByIds")
    @Transactional
    public Result<?> removeLlm(@JsonBody(value = "id", required = true) Serializable id) {
        List<Serializable> ids = Collections.singletonList(id);
        QueryWrapper queryWrapper = QueryWrapper.create().in(AiLlm::getId, ids);
        service.remove(queryWrapper);
        return Result.ok();
    }
}