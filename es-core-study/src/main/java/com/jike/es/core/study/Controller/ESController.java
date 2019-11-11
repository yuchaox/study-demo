package com.jike.es.core.study.Controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jike.es.core.study.utils.ESUtil;
import com.jike.es.core.study.vo.ResponseBean;
import com.jike.es.core.study.vo.UserInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.replication.ReplicationResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortMode;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * ESController
 *                 https://www.cnblogs.com/java-spring/p/11721615.html
 * @author yuchaochao
 * @version V1.0
 * @date 2019/11/8 11:56
 **/
@Api(value = "ES测试接口", tags = {"ES测试接口"})
@RestController
@RequestMapping("/es")
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE, RequestMethod.PUT})
@Slf4j
public class ESController {
    @Resource
    private RestHighLevelClient restHighLevelClient;
    @Resource
    ESUtil esUtil;

    @ApiOperation(value = "es测试创建索引接口", notes = "es测试创建索引接口")
    @RequestMapping(value = "/create/index", method = RequestMethod.POST)
    public ResponseBean createIndex(@RequestParam String indexName){
        try {
            XContentBuilder builder = XContentFactory.jsonBuilder()
                    .startObject()
                    .field("properties")
                    .startObject()
                    .field("name").startObject().field("index", "true").field("type", "keyword").endObject()
                    .field("age").startObject().field("index","true").field("type", "integer").endObject()
                    .field("money").startObject().field("index", "true").field("type", "double").endObject()
                    .field("address").startObject().field("index", "true").field("type", "text").field("analyzer", "ik_max_word").endObject()
                    .field("birthday").startObject().field("index", "true").field("type", "date").field("format", "strict_date_optional_time||epoch_millis").endObject()
                    .endObject()
                    .endObject();
            CreateIndexRequest createIndexRequest = new CreateIndexRequest(indexName);
            createIndexRequest.mapping(builder);
            CreateIndexResponse createIndexResponse = restHighLevelClient.indices().create(createIndexRequest, RequestOptions.DEFAULT);
            boolean acknowledged = createIndexResponse.isAcknowledged();
            if (acknowledged) {
                return new ResponseBean(200, "创建成功", null);
            } else {
                return new ResponseBean(1002, "创建失败", null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    @ApiOperation(value = "es测试是否存在索引接口", notes = "es测试是否存在索引接口")
    @RequestMapping(value = "/index/exists", method = RequestMethod.POST)
    public ResponseBean indexExists(@RequestParam String indexName){
        boolean isExists  = esUtil.isIndexExists(indexName);
       return new ResponseBean(200, "查询成功", isExists);
    }

    @ApiOperation(value = "es测试插入接口", notes = "es测试插入接口")
    @RequestMapping(value = "/insert/data", method = RequestMethod.POST)
    public ResponseBean insertUserInfo(@RequestBody UserInfo userInfo,@RequestParam String indexName){
        IndexRequest indexRequest = new IndexRequest(indexName);
        String userJson = JSONObject.toJSONString(userInfo);
        indexRequest.source(userJson, XContentType.JSON);
        try {
            IndexResponse indexResponse = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
            String id = indexResponse.getId();
            String index = indexResponse.getIndex();
            long version = indexResponse.getVersion();
            log.info("index:{},id:{}", index, id);
            if(indexResponse.getResult() == DocWriteResponse.Result.CREATED){
                System.out.println("新增文档成功!" + index + "-" + id + "-" + version);
                return new ResponseBean(200, "插入成功", id);
            }else if(indexResponse.getResult() == DocWriteResponse.Result.UPDATED){
                System.out.println("修改文档成功!");
                return new ResponseBean(200, "插入失败", null);
            }
            //分片处理信息
            ReplicationResponse.ShardInfo shardInfo = indexResponse.getShardInfo();
            if(shardInfo.getTotal() != shardInfo.getSuccessful()){
                System.out.println("分片处理信息.....");
            }
            // 如果有分片副本失败，可以获得失败原因信息
            if(shardInfo.getFailed()>0){
                for (ReplicationResponse.ShardInfo.Failure failure : shardInfo.getFailures()) {
                    String reason = failure.reason();
                    System.out.println("副本失败原因：" + reason);
                    log.error("副本失败原因:{}",reason);
                }
            }
        } catch (IOException e) {
            log.error("异常信息:{}",e);
        }
        return null;
    }

    @ApiOperation(value = "es测试普通查询接口", notes = "es测试普通查询接口")
    @RequestMapping(value = "/query/data", method = RequestMethod.GET)
    public ResponseBean testESQuery(){
        SearchRequest searchRequest = new SearchRequest("yc_test");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //如果用name直接查询，其实是匹配name分词过后的索引查到的记录(倒排索引)；如果用name.keyword查询则是不分词的查询，正常查询到的记录
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("birthday").from("1993-01-01").to("2019-12-12").format("yyyy-MM-dd");
        //精准查询
        //TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("name.keyword", name);
        //前缀查询
        PrefixQueryBuilder prefixQueryBuilder = QueryBuilders.prefixQuery("name.keyword", "张");
        //通配符查询
        WildcardQueryBuilder wildcardQueryBuilder = QueryBuilders.wildcardQuery("name.keyword", "*三");
        //模糊查询
        //FuzzyQueryBuilder fuzzyQueryBuilder= QueryBuilders.fuzzyQuery("name", "三");
        //按照年龄排序
        FieldSortBuilder fieldSortBuilder = SortBuilders.fieldSort("age");
        //从小到大排序
        fieldSortBuilder.sortMode(SortMode.MIN);

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //and or  查询
        boolQueryBuilder.must(rangeQueryBuilder).should(prefixQueryBuilder );

        //多条件查询
        sourceBuilder.query(boolQueryBuilder).sort(fieldSortBuilder);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        searchRequest.source(sourceBuilder);
        try {
            SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits hits = response.getHits();
            JSONArray jsonArray = new JSONArray();
            for (SearchHit hit : hits) {
                String sourceAsString = hit.getSourceAsString();
                jsonArray.add(JSONObject.parseObject(sourceAsString));
            }
            return new ResponseBean(200, "查询成功", jsonArray);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseBean(10001, "查询失败", null);
        }
    }


    @ApiOperation(value = "es测试聚合查询接口", notes = "es测试聚合查询接口")
    @RequestMapping(value = "/query/agg", method = RequestMethod.GET)
    public ResponseBean testESQueryAgg(){
        SearchRequest searchRequest = new SearchRequest("yc_test");
        SearchSourceBuilder sourceBuilder= new SearchSourceBuilder();
        TermsAggregationBuilder termsAggregationBuilder = AggregationBuilders.terms("by_age").field("age");
        sourceBuilder.aggregation(termsAggregationBuilder);
        sourceBuilder.timeout(new TimeValue(60,TimeUnit.SECONDS));
        searchRequest.source(sourceBuilder);

        try {
            SearchResponse  searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            Aggregations aggregations = searchResponse.getAggregations();
            Map<String, Aggregation> stringAggregationMap = aggregations.asMap();
            ParsedLongTerms parsedLongTerms = (ParsedLongTerms)stringAggregationMap.get("by_age");
            List<? extends Terms.Bucket> buckets = parsedLongTerms.getBuckets(); 
            Map<Integer, Long> map = new HashMap<>();
            for (Terms.Bucket bucket : buckets) {
                long docCount = bucket.getDocCount();
                Number keyAsNumber = bucket.getKeyAsNumber();
                System.err.println(keyAsNumber + "岁的有" + docCount + "个");
                map.put(keyAsNumber.intValue(),docCount);
            }
            return new ResponseBean(200, "查询成功", map);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @ApiOperation(value = "es测试更新接口", notes = "es测试更新接口")
    @PostMapping(value = "/update/data")
    public ResponseBean testESUpdate(@RequestParam String id, @RequestParam Double money){
        UpdateRequest updateRequest = new UpdateRequest("yc_test",id);
        Map<String, Object> map = new HashMap<>();
        map.put("money", money);
        updateRequest.doc(map);
        try {
            UpdateResponse updateResponse = restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
            if (updateResponse.getResult() == DocWriteResponse.Result.UPDATED) {
                return new ResponseBean(200, "更新成功", null);
            } else {
                return new ResponseBean(10002, "更新失败", null);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseBean(1003, "更新异常", null);
        }
    }


    @ApiOperation(value = "es测试删除接口", notes = "es测试删除接口")
    @RequestMapping(value = "/delete/data", method = RequestMethod.GET)
    public ResponseBean testESDelete(@RequestParam String id, @RequestParam String indexName) {
        DeleteRequest deleteRequest = new DeleteRequest(indexName);
        deleteRequest.id(id);
        try {
            DeleteResponse deleteResponse = restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
            if(deleteResponse.getResult()== DocWriteResponse.Result.NOT_FOUND){
                return new ResponseBean(1001, "删除失败", null);
            }else{
                return new ResponseBean(200, "删除成功", null);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseBean(1003, "删除异常", null);
        }
    }
}
