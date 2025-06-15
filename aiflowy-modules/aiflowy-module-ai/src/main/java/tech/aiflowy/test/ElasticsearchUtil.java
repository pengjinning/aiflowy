package tech.aiflowy.test;

import com.agentsflex.core.document.Document;
import com.agentsflex.core.document.DocumentParser;
import com.agentsflex.core.document.DocumentSplitter;
import com.mybatisflex.core.keygen.impl.FlexIDKeyGenerator;
import org.apache.http.HttpHost;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import tech.aiflowy.ai.entity.AiDocument;
import tech.aiflowy.ai.entity.AiDocumentChunk;
import tech.aiflowy.ai.service.impl.AiDocumentServiceImpl;
import tech.aiflowy.common.ai.DocumentParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ElasticsearchUtil {

    private static final String HOST = "localhost";
    private static final int PORT = 9200;
    private static final String SCHEME = "http";

    private RestHighLevelClient client;

    public ElasticsearchUtil() {
        this.client = new RestHighLevelClient(
                RestClient.builder(new HttpHost(HOST, PORT, SCHEME)));
    }

    /**
     * 关闭客户端
     */
    public void close() throws IOException {
        if (client != null) {
            client.close();
        }
    }

    /**
     * 插入文档到指定索引
     *
     * @param index 索引名（如 knowledge-base）
     * @param id    文档ID（可为空，由ES自动生成）
     * @param json  JSON格式的文档内容
     */
    public boolean indexDocument(String index, String id, String json) throws IOException {
        IndexRequest request = new IndexRequest(index);
        if (id != null && !id.isEmpty()) {
            request.id(id);
        }
        request.source(json, XContentType.JSON);

        IndexResponse response = client.index(request, RequestOptions.DEFAULT);
        return response.status() == RestStatus.CREATED || response.status() == RestStatus.OK;
    }

    /**
     * 全文搜索
     *
     * @param index   索引名
     * @param keyword 查询关键词
     * @return 搜索结果
     */
    public List<AiDocumentChunk> search(String index, String keyword) throws IOException {
        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        // 多字段匹配
        MultiMatchQueryBuilder queryBuilder = QueryBuilders.multiMatchQuery(keyword, "title", "content");
        sourceBuilder.query(queryBuilder);

        searchRequest.source(sourceBuilder);
        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
        System.out.println("找到结果数: " + Objects.requireNonNull(response.getHits().getTotalHits()).value);
        SearchHit[] hits = response.getHits().getHits();
        List<AiDocumentChunk> documentChunks = new ArrayList<>();
        double maxElasticScore = hits[0].getScore(); // 所有文档中最大的 _score
        double minElasticScore = hits[hits.length-1].getScore(); // 最小值

        for (SearchHit hit : hits) {
            double rawScore = hit.getScore();
            double normalizedElasticScore = maxElasticScore > 0 ? rawScore / maxElasticScore : 0;
            AiDocumentChunk aiDocumentChunk = new AiDocumentChunk();
            aiDocumentChunk.setId(BigInteger.valueOf(Long.parseLong(hit.getId())));
            aiDocumentChunk.setElasticSimilarityScore(normalizedElasticScore);
            aiDocumentChunk.setContent(hit.getSourceAsMap().get("content").toString());
            documentChunks.add(aiDocumentChunk);
        }

        return documentChunks;
    }

    public static void main(String[] args) {
        ElasticsearchUtil esUtil = new ElasticsearchUtil();

        try {
            // 示例数据
            String json = "{ \"title\": \"如何使用Elasticsearch\", \"content\": \"Elasticsearch 是一个分布式搜索引擎。\", \"source\": \"wiki.md\" }";
//
//            // 插入文档
//            boolean success = esUtil.indexDocument("knowledge-base", null, json);
//            System.out.println("插入成功: " + success);
            // 文件路径
            String filePath = "";

            // 创建 File 对象
            File file = new File(filePath);

            // 判断文件是否存在
            if (!file.exists()) {
                System.out.println("文件不存在！");
                return;
            }

            // 使用 FileInputStream 读取文件流
            try (InputStream inputStream = new FileInputStream(file)) {
                // 在这里处理 inputStream（比如传给 DocumentParser）
                System.out.println("成功打开文件输入流...");

                AiDocument aiDocument = new AiDocument();
                List<AiDocumentChunk> previewList = new ArrayList<>();
                AiDocumentServiceImpl aiDocumentService = new AiDocumentServiceImpl();

                DocumentSplitter documentSplitter = aiDocumentService.getDocumentSplitter("SimpleDocumentSplitter", 200, 100, "regex", 0);
                Document document = null;
                DocumentParser documentParser = DocumentParserFactory.getDocumentParser(file.getName());
                if (documentParser != null) {
                    document = documentParser.parse(inputStream);
                }
                List<Document> documents = documentSplitter.split(document);
                FlexIDKeyGenerator flexIDKeyGenerator = new FlexIDKeyGenerator();
                int sort = 1;
                for (Document value : documents) {
                    AiDocumentChunk chunk = new AiDocumentChunk();
                    chunk.setId(new BigInteger(String.valueOf(flexIDKeyGenerator.generate(chunk, null))));
                    chunk.setContent(value.getContent());
                    chunk.setSorting(sort);
                    sort++;
                    previewList.add(chunk);
                }

                // 搜索
                List<AiDocumentChunk> result = esUtil.search("knowledge-base", "恶劣影响的物品");

                // 关闭连接
                esUtil.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
