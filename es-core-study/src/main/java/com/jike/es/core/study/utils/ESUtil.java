package com.jike.es.core.study.utils;

import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * ESUtil
 *
 * @author yuchaochao
 * @version V1.0
 * @date 2019/11/8 11:57
 **/
@Component
public class ESUtil {
    @Resource
    RestHighLevelClient restHighLevelClient;

    /**
     * 判断索引是否存在
     * @param indexName
     * @return boolean
     * @author yuchaochao
     * @date 2019/11/8 12:02
     */
    public boolean isIndexExists(String indexName){
        boolean exists = false;
        try {
            GetIndexRequest getIndexRequest = new GetIndexRequest(indexName);
            //可读性
            getIndexRequest.humanReadable(true);
            exists = restHighLevelClient.indices().exists(getIndexRequest, RequestOptions.DEFAULT);
        }catch (Exception e){
            e.printStackTrace();
        }
    return exists;
    }

    /**
     * 删除索引
     * @param indexName
     * @return boolean
     * @author yuchaochao
     * @date 2019/11/8 13:40
     */
    public boolean deleteIndex(String indexName) {
        boolean acknowledged = false;
        try {
            DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(indexName);
            deleteIndexRequest.indicesOptions(IndicesOptions.LENIENT_EXPAND_OPEN);
            AcknowledgedResponse delete = restHighLevelClient.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);
            acknowledged = delete.isAcknowledged();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return acknowledged;
    }


}
