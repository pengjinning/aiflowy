package tech.aiflowy.admin.controller.ai;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.aiflowy.ai.entity.Document;
import tech.aiflowy.ai.entity.DocumentCollection;
import tech.aiflowy.ai.service.DocumentChunkService;
import tech.aiflowy.ai.service.DocumentCollectionService;
import tech.aiflowy.ai.service.DocumentService;
import tech.aiflowy.ai.service.ModelService;
import tech.aiflowy.common.annotation.UsePermission;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.tree.Tree;
import tech.aiflowy.common.util.RequestUtil;
import tech.aiflowy.common.util.StringUtil;
import tech.aiflowy.common.web.controller.BaseCurdController;
import tech.aiflowy.common.web.exceptions.BusinessException;
import tech.aiflowy.common.web.jsonbody.JsonBody;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 控制层。
 *
 * @author michael
 * @since 2024-08-23
 */
@RestController
@RequestMapping("/api/v1/document")
@UsePermission(moduleName = "/api/v1/documentCollection")
public class DocumentController extends BaseCurdController<DocumentService, Document> {

    private final DocumentCollectionService knowledgeService;


    @Autowired
    private DocumentService documentService;


    @Value("${aiflowy.storage.local.root}")
    private  String fileUploadPath;


    public DocumentController(DocumentService service,
                              DocumentCollectionService knowledgeService,
                              DocumentChunkService documentChunkService, ModelService modelService) {
        super(service);
        this.knowledgeService = knowledgeService;
    }
    @PostMapping("removeDoc")
    @Transactional
    @SaCheckPermission("/api/v1/documentCollection/remove")
    public Result<?> remove(@JsonBody(value = "id", required = true) String id) {
        List<Serializable> ids = Collections.singletonList(id);
        Result<?> result = onRemoveBefore(ids);
        if (result != null) return result;
        boolean isSuccess = documentService.removeDoc(id);
        if (!isSuccess){
            return Result.ok(false);
        }
        boolean success = service.removeById(id);
        onRemoveAfter(ids);
        return Result.ok(success);
    }


    /**
     * 查询所有所有数据
     *
     * @param entity
     * @param asTree
     * @param sortKey
     * @param sortType
     * @return 所有数据
     */
    @GetMapping("list")
    @Override
    @SaCheckPermission("/api/v1/documentCollection/query")
    public Result<List<Document>> list(Document entity, Boolean asTree, String sortKey, String sortType) {
        String kbSlug = RequestUtil.getParamAsString("id");
        if (StringUtil.noText(kbSlug)) {
            throw new BusinessException("知识库id不能为空");
        }

        DocumentCollection knowledge = StringUtil.isNumeric(kbSlug)
                ? knowledgeService.getById(kbSlug)
                : knowledgeService.getOne(QueryWrapper.create().eq(DocumentCollection::getSlug, kbSlug));

        if (knowledge == null) {
            throw new BusinessException("知识库不存在");
        }

        QueryWrapper queryWrapper = QueryWrapper.create()
                .eq(Document::getCollectionId, knowledge.getId());
        queryWrapper.orderBy(buildOrderBy(sortKey, sortType, getDefaultOrderBy()));
        List<Document> documents = service.list(queryWrapper);
        List<Document> list = Tree.tryToTree(documents, asTree);
        return Result.ok(list);
    }

    @GetMapping("documentList")
    @SaCheckPermission("/api/v1/documentCollection/query")
    public Result<Page<Document>> documentList(@RequestParam(name="title", required = false) String fileName, @RequestParam(name="pageSize") int pageSize, @RequestParam(name = "pageNumber") int pageNumber) {
        String kbSlug = RequestUtil.getParamAsString("id");
        if (StringUtil.noText(kbSlug)) {
            throw new BusinessException("知识库id不能为空");
        }
        Page<Document> documentList = documentService.getDocumentList(kbSlug, pageSize, pageNumber,fileName);
        return Result.ok(documentList);
    }


    @Override
    protected String getDefaultOrderBy() {
        return "order_no asc";
    }


    @PostMapping("update")
    @Override
    @SaCheckPermission("/api/v1/documentCollection/save")
    public Result<Boolean> update(@JsonBody Document entity) {
        super.update(entity);
        return Result.ok(updatePosition(entity));
    }

    /**
     * 文本拆分
     * @param filePath 文件路径
     * @param operation textSplit 拆分预览/  saveText 保存
     * @param splitterName 拆分器名称
     * @param chunkSize 分段大小
     * @param overlapSize 重叠大小
     * @param regex 正则表达式
     * @param rowsPerChunk excel 分割段数
     */
    @PostMapping(value = {"textSplit", "/saveText"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @SaCheckPermission("/api/v1/documentCollection/save")
    public Result<?> textSplit(
                              @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                              @RequestParam(value = "pageSize", required = false) Integer pageSize,
                              @RequestParam("operation") String operation,
                              @RequestParam("filePath") String filePath,
                              @RequestParam("fileOriginName") String fileOriginName,
                              @RequestParam(name="knowledgeId") BigInteger knowledgeId,
                              @RequestParam(name="splitterName", required = false) String splitterName,
                              @RequestParam(name="chunkSize", required = false) Integer chunkSize,
                              @RequestParam(name="overlapSize", required = false) Integer overlapSize,
                              @RequestParam(name="regex", required = false) String regex,
                              @RequestParam(name="rowsPerChunk", required = false) Integer rowsPerChunk
                              ) {
        if (pageNumber == null || pageNumber == 0){
            pageNumber = 1;
        }
        if (pageSize == null || pageSize == 0){
            pageSize = 10;
        }
        if (chunkSize == null){
            chunkSize = 512;
        }
        if (overlapSize == null){
            overlapSize = 128;
        }
        if (rowsPerChunk == null){
            rowsPerChunk = 1;
        }
        return documentService.textSplit(pageNumber, pageSize, operation, knowledgeId, filePath, fileOriginName,  splitterName, chunkSize, overlapSize, regex, rowsPerChunk);

    }

    /**
     * 更新 entity
     *
     * @param entity
     * @return Result
     */
    private boolean updatePosition(Document entity) {
        Integer orderNo = entity.getOrderNo();
        if (orderNo != null) {
            if (orderNo <= 0) orderNo = 0;
            BigInteger knowledgeId = service.getById(entity.getId()).getCollectionId();
            List<Document> list = service.list(QueryWrapper.create()
                    .eq(Document::getCollectionId, knowledgeId)
                    .orderBy(getDefaultOrderBy())
            );

            list.removeIf(item -> item.getId().equals(entity.getId()));
            if (orderNo >= list.size()) {
                list.add(entity);
            } else {
                list.add(orderNo, entity);
            }

            List<Document> updateList = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                Document updateItem = new Document();
                updateItem.setId(list.get(i).getId());
                updateItem.setOrderNo(i);
                updateList.add(updateItem);
            }

            service.updateBatch(updateList);
        }

        return true;
    }


    public String getRootPath() {
        if (StringUtil.hasText(this.fileUploadPath)) {
            return this.fileUploadPath;
        }
        ClassPathResource fileResource = new ClassPathResource("/");
        try {
            return new File(fileResource.getFile(), "/public").getAbsolutePath();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
