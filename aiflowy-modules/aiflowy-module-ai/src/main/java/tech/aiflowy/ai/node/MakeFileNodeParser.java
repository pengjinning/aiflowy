package tech.aiflowy.ai.node;

import cn.hutool.core.util.StrUtil;
import com.agentsflex.core.chain.ChainNode;
import com.alibaba.fastjson.JSONObject;
import dev.tinyflow.core.Tinyflow;
import dev.tinyflow.core.parser.BaseNodeParser;
import tech.aiflowy.common.filestorage.FileStorageService;

public class MakeFileNodeParser extends BaseNodeParser {

    private final FileStorageService fileStorageService;

    public MakeFileNodeParser(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @Override
    public ChainNode parse(JSONObject jsonObject, Tinyflow tinyflow) {
        JSONObject data = getData(jsonObject);
        String suffix = data.getString("suffix");
        if (StrUtil.isEmpty(suffix)) {
            suffix = "docx";
        }
        MakeFileNode node = new MakeFileNode(fileStorageService, suffix);
        addParameters(node, data);
        addOutputDefs(node, data);
        return node;
    }

    public String getNodeName() {
        return "make-file";
    }
}
