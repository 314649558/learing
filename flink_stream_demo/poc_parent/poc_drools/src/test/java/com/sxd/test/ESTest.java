package com.sxd.test;

import com.sxd.citic.drools.utils.elasticsearch.ESUtils;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/8/7.
 *
 * ES 测试类
 */
public class ESTest {

    private TransportClient client;



    @Before
    public void setup(){
        this.client=ESUtils.getTransportClient();
    }

    @After
    public void destory(){
        ESUtils.close(this.client);
    }

    /**
     * ES查询
     */
    @Test
    public void testESSyntax(){
        SearchRequestBuilder requestBuilder=client.prepareSearch("bank")
                .setTypes("account")
                .setQuery(QueryBuilders.termQuery("firstname.keyword","Virginia"));
        System.out.println("###################################");
        System.out.println(requestBuilder.toString());
        System.out.println("###################################");

        SearchResponse  response= requestBuilder.execute().actionGet();

        long count=response.getHits().getTotalHits();

        System.out.println("-----------------------total count:"+count);
        SearchHit[] searchHits=response.getHits().getHits();

        for (SearchHit searchHit:searchHits){
            System.out.println(searchHit.getSourceAsString());
        }
    }


    /**
     * 测试删除索引
     */
    @Test
    public void testDeleteIndex(){
        List<String> indexLst=new ArrayList<String>();
        indexLst.add("idx_app_data_batch_");
        indexLst.add("idx_app_stm_001_cnl");
        indexLst.add("idx_app_stm_001_cnl_tmp");
        indexLst.add("idx_app_stm_001_mid");
        indexLst.add("idx_app_stm_001_ods");
        indexLst.add("idx_app_stm_002_ods");
        indexLst.add("idx_app_stm_002_mid");
        indexLst.add("idx_app_stm_002_cnl");
        indexLst.add("idx_im_app_stm_001_ods");
        indexLst.add("idx_im_app_data_batch_");
        for(String indexName:indexLst){
            try {
                if(ESUtils.indexExists(client,indexName)) {
                    client.admin().indices().prepareDelete(indexName).execute().actionGet();
                    System.out.println("删除索引名称[" + indexName + "] 成功");
                }
            }catch(Exception ex){
                System.err.println("删除索引名称["+indexName+"] 失败");
            }
        }
    }


    @Test
    public void createIndex(){
        List<String> indexLst=new ArrayList<String>();
        indexLst.add("idx_app_data_batch_");
        indexLst.add("idx_app_stm_001_cnl");
        indexLst.add("idx_app_stm_001_cnl_tmp");
        indexLst.add("idx_app_stm_001_mid");
        indexLst.add("idx_app_stm_001_ods");
        indexLst.add("idx_app_stm_002_ods");
        indexLst.add("idx_app_stm_002_mid");
        indexLst.add("idx_app_stm_002_cnl");
        for(String indexName:indexLst){
            try {
                client.admin().indices().prepareCreate(indexName)
                        .setSettings(Settings.builder().put("index.number_of_shards", 3)
                                                        .put("index.number_of_replicas", 1)).get();
                System.out.println("创建索引名称[" + indexName + "] 成功");
            }catch(Exception ex){
                System.err.println("创建索引名称["+indexName+"] 失败");
            }
        }
    }

    @Test
    public void insertTestData(){
        String indexName="idx_app_stm_001_cnl_tmp";
        String typeName="app_stm_001_cnl";
        String json="{\"tradeDate\":\"20180801\",\"deptNbr\":\"721830690\",\"storeNbr\":\"32\",\"itemNbr\":\"2\",\"costAmt\":84.6529730335166,\"netSalesAmt\":34.1087082481774,\"retailAmt\":89.1799682820915,\"bthId\":\"9bf4ce6637834d1781cf1e2fcaba71bb\",\"id\":\"edc6f39cbf15441487dc1cd89bf6040f\",\"imPct\":0.0507624675785401,\"toSalesPct\":0.94923753242146}";
        client.prepareIndex().setIndex(indexName).setType(typeName).setId("testiddd").setSource(json).execute().actionGet();
    }
    @Test
    public void testDelete(){
        String indexName="idx_app_stm_001_cnl_tmp";
        String typeName="app_stm_001_cnl";
        String id="testiddd";
        client.prepareDelete(indexName,typeName,id).execute().actionGet();
    }


    @Test
    public void updateMapping() throws IOException {
        String indexName="idx_app_stm_001_cnl_tmp";
        String typeName="app_stm_001_cnl";

        XContentBuilder mapping = XContentFactory.jsonBuilder()
                .startObject()
                .startObject(indexName)
                .startObject("properties")
                .startObject("id").field("type", "string").field("store", "yes").endObject()
                .startObject("bthId").field("type", "string").field("fielddata",true).field("store", "yes").endObject()
                .startObject("tradeDate").field("type", "string").field("fielddata",true).field("store", "yes").endObject()
                .startObject("deptNbr").field("type", "string").field("fielddata",true).field("index", "not_analyzed").endObject()
                .startObject("storeNbr").field("type", "string").field("fielddata",true).field("index", "not_analyzed").endObject()
                .startObject("itemNbr").field("type", "string").field("fielddata",true).field("index", "not_analyzed").endObject()
                .startObject("costAmt").field("type", "double").field("index", "not_analyzed").endObject()
                .startObject("netSalesAmt").field("type", "double").field("index", "not_analyzed").endObject()
                .startObject("retailAmt").field("type", "double").field("index", "not_analyzed").endObject()
                .startObject("imPct").field("type", "double").field("index", "not_analyzed").endObject()
                .startObject("toSalesPct").field("type", "double").field("index", "not_analyzed").endObject()
                .endObject()
                .endObject();
        PutMappingRequest  request= Requests.putMappingRequest(indexName).type(typeName).source(mapping);
        client.admin().indices().putMapping(request).actionGet();


    }




    @Test
    public void createIndexAndPutMapping(){
        String indexName="idx_app_stm_001_cnl_tmp";
        String typeName="app_stm_001_cnl";

        String mappingJson="{\n" +
                "    \"app_stm_001_cnl\": {\n" +
                "        \"properties\": {\n" +
                "            \"bthId\": {\n" +
                "                \"type\": \"text\",\n" +
                "                \"fields\": {\n" +
                "                    \"keyword\": {\n" +
                "                        \"type\": \"keyword\",\n" +
                "                        \"ignore_above\": 256\n" +
                "                    }\n" +
                "                },\n" +
                "                \"fielddata\": true\n" +
                "            },\n" +
                "            \"costAmt\": {\n" +
                "                \"type\": \"float\"\n" +
                "            },\n" +
                "            \"deptNbr\": {\n" +
                "                \"type\": \"text\",\n" +
                "                \"fields\": {\n" +
                "                    \"keyword\": {\n" +
                "                        \"type\": \"keyword\",\n" +
                "                        \"ignore_above\": 256\n" +
                "                    }\n" +
                "                },\n" +
                "                \"fielddata\": true\n" +
                "            },\n" +
                "            \"id\": {\n" +
                "                \"type\": \"text\",\n" +
                "                \"fields\": {\n" +
                "                    \"keyword\": {\n" +
                "                        \"type\": \"keyword\",\n" +
                "                        \"ignore_above\": 256\n" +
                "                    }\n" +
                "                }\n" +
                "            },\n" +
                "            \"imPct\": {\n" +
                "                \"type\": \"float\"\n" +
                "            },\n" +
                "            \"itemNbr\": {\n" +
                "                \"type\": \"text\",\n" +
                "                \"fields\": {\n" +
                "                    \"keyword\": {\n" +
                "                        \"type\": \"keyword\",\n" +
                "                        \"ignore_above\": 256\n" +
                "                    }\n" +
                "                },\n" +
                "                \"fielddata\": true\n" +
                "            },\n" +
                "            \"netSalesAmt\": {\n" +
                "                \"type\": \"float\"\n" +
                "            },\n" +
                "            \"retailAmt\": {\n" +
                "                \"type\": \"float\"\n" +
                "            },\n" +
                "            \"storeNbr\": {\n" +
                "                \"type\": \"text\",\n" +
                "                \"fields\": {\n" +
                "                    \"keyword\": {\n" +
                "                        \"type\": \"keyword\",\n" +
                "                        \"ignore_above\": 256\n" +
                "                    }\n" +
                "                },\n" +
                "                \"fielddata\": true\n" +
                "            },\n" +
                "            \"toSalesPct\": {\n" +
                "                \"type\": \"float\"\n" +
                "            },\n" +
                "            \"tradeDate\": {\n" +
                "                \"type\": \"text\",\n" +
                "                \"fields\": {\n" +
                "                    \"keyword\": {\n" +
                "                        \"type\": \"keyword\",\n" +
                "                        \"ignore_above\": 256\n" +
                "                    }\n" +
                "                },\n" +
                "                \"fielddata\": true\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "}";
        client.admin().indices().preparePutMapping(indexName)
                .setSource(mappingJson, XContentType.JSON).get();
    }



}
