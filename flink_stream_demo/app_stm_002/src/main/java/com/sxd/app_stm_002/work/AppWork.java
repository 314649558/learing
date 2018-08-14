package com.sxd.app_stm_002.work;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sxd.app_stm_002.app.entity.*;
import com.sxd.app_stm_002.app.service.IAppService;
import com.sxd.app_stm_002.work.cep.bean.RuleExpressionBean;
import com.sxd.app_stm_002.work.cep.function.*;
import com.sxd.app_stm_002.work.sink.ESKafkaRealTimeSink;
import com.sxd.app_stm_002.work.utils.Constants;
import com.sxd.app_stm_002.work.utils.ExpressionUtils;
import com.sxd.app_stm_002.work.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.cep.CEP;
import org.apache.flink.cep.PatternSelectFunction;
import org.apache.flink.cep.pattern.Pattern;
import org.apache.flink.cep.pattern.conditions.IterativeCondition;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.collector.selector.OutputSelector;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SplitStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.elasticsearch5.ElasticsearchSink;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.apache.flink.util.Collector;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.*;
public class AppWork implements Serializable {
    private App app;
    private IAppService appService;
    public AppWork(IAppService appService) {
        this.appService = appService;
        this.run();
    }
    public void run() {
        // 应用
        app = this.appService.getApp(new App("2"));
        final String appId=app.getAppId();
        final String appName=app.getAppName();
        // 数据源
        Source source = app.getSource();
        Properties kafkaSrcProp = new Properties();
        kafkaSrcProp.put("bootstrap.servers", source.getKafkaHosts());
        kafkaSrcProp.put("zookeeper.connect", source.getZokkeeperHost());
        kafkaSrcProp.put(Constants.MAX_REQUEST_SIZE_KEY(),Constants.MAX_REQUEST_SIZE_VALUE());
        kafkaSrcProp.put(Constants.MAX_PARTITION_FETCH_BYTES_KEY(),Constants.MAX_PARTITION_FETCH_BYTES_VALUE());
        kafkaSrcProp.put("group.id", source.getGroups());
        // CEP
        List<AppCep> appCeps = this.appService.getAppCeps(new App("2"));
        List<RuleExpressionBean> rules = new ArrayList<>(appCeps.size());
        for (int i = 0; i < appCeps.size(); i++) {
            rules.add(new RuleExpressionBean(appCeps.get(i).getFiledName(), appCeps.get(i).getExpression()));
        }
        FlinkKafkaConsumer010 kafkaConsumer = new FlinkKafkaConsumer010(source.getTopic(), new SimpleStringSchema(), kafkaSrcProp);
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(6);
        env.getConfig().disableSysoutLogging();
        env.setStreamTimeCharacteristic(TimeCharacteristic.ProcessingTime);
        env.enableCheckpointing(2000);
        DataStream<String> kafkaMessage = env.addSource(kafkaConsumer);
        /**
         * 原始数据 添加bthid 和head
         */
        SplitStream<String> splitStream=kafkaMessage.filter(x -> {
            //传递过来的数据必须是JSON数组
            try {
                return JsonUtils.strToJsonArray(x).size() > 0;
            }catch(Exception ex){
                return false;
            }
        }).map(new MapFunction<String, ArrayList<String>>() {
            @Override
            public ArrayList<String> map(String bthData) throws Exception {
                ArrayList<String> retLst=new ArrayList<String>();
                JsonObject jsonObject=new JsonObject();
                String bthId=UUID.randomUUID().toString().replaceAll("-","");
                jsonObject.addProperty("bthId",bthId);
                jsonObject.addProperty("timestamp",System.currentTimeMillis());
                jsonObject.addProperty("appId",appId);
                jsonObject.addProperty("appName",appName);
                JsonArray jsonArray=JsonUtils.strToJsonArray(bthData);
                JsonArray tmpJsonArray=new JsonArray();
                for(int i=0;i<jsonArray.size();i++){
                    JsonObject jsonObj=JsonUtils.strToJson(jsonArray.get(i).toString());
                    jsonObj.addProperty("bthId",bthId);
                    jsonObj.addProperty("id",UUID.randomUUID().toString().replaceAll("-",""));
                    tmpJsonArray.add(jsonObj);
                }
                retLst.add(JsonUtils.toJson(jsonObject));    //添加头部信息
                retLst.add(JsonUtils.toJson(tmpJsonArray));
                return retLst;
            }
        }).flatMap(new FlatMapFunction<ArrayList<String>, String>() {
            @Override
            public void flatMap(ArrayList<String> lstVal, Collector<String> out) throws Exception {
                for(String lst:lstVal){
                        out.collect(lst);
                }
            }
        }).split(new OutputSelector<String>() {
            @Override
            public Iterable<String> select(String value) {
               List<String> lst=new ArrayList<>();

               if(StringUtils.contains(value,"appName")){
                   lst.add("head");
               }else{
                   lst.add("orginData");
               }
               return lst;
            }
        });



        Map<String,String> esConfig=new HashMap<String,String>();
        esConfig.put(Constants.ES_CLUSTER_NAME_KEY(),Constants.ES_CLUSTER_NAME_VALUE());
        esConfig.put("bulk.flush.max.actions","100");
        List<InetSocketAddress> transportAddresses=new ArrayList<>();
        for(String str:Constants.ES_ADDR().split(",")){
            transportAddresses.add(new InetSocketAddress(str.split(":")[0], Integer.valueOf(str.split(":")[1])));
        }
        //头部数据发送ES
        DataStream<String> headStream=splitStream.select("head");
        Map<String,String> headConfig=new HashMap<>();
        headConfig.put(Constants.ES_INDEX_KEY(),"idx_app_data_batch_");
        headConfig.put(Constants.ES_TYPE_KEY(),"app_data_batch_");
        headStream.addSink(new ElasticsearchSink<String>(esConfig,transportAddresses,new POCElasticsearchSinkFunction(headConfig)));

        //原始数据->之后都应该基于这个数据做处理
         DataStream<String> orginStream=splitStream.select("orginData");

        //orginStream.print();

        //发送原始数据到ES
        Map<String,String> orginConfig=new HashMap<>();
        orginConfig.put(Constants.ES_INDEX_KEY(),"idx_app_stm_002_ods");
        orginConfig.put(Constants.ES_TYPE_KEY(),"app_stm_002_ods");


        orginStream.map(new RichMapFunction<String, List<String>>() {
            @Override
            public List<String> map(String value) throws Exception {
                List<String> lst=new ArrayList<String>();
                JsonArray jsonArr=JsonUtils.strToJsonArray(value);
                for(int i=0;i<jsonArr.size();i++){
                    lst.add(jsonArr.get(i).toString());
                }
                return lst;
            }
        }).flatMap(new FlatMapFunction<List<String>, String>() {
            @Override
            public void flatMap(List<String> value, Collector<String> out) throws Exception {
                for(String v:value){
                    out.collect(v);
                }
            }
        }).addSink(new ElasticsearchSink<String>(esConfig,transportAddresses,new POCElasticsearchSinkFunction(orginConfig)));

        //CEP模型规则处理
        Pattern p = Pattern.<String>begin("item_first")
                .where(new IterativeCondition<String>() {
                    @Override
                    public boolean filter(String s, Context<String> context) throws Exception {
                        for (SourceField field : source.getFields()) {
                            if (!StringUtils.contains(s, field.getName())) {
                                return false;
                            }
                        }
                        return true;
                    }
                });
        // 渠道
        Channel channel = app.getChannel();

        DataStream<String> cepPatternDS = CEP.pattern(orginStream, p).select(new PatternSelectFunction<String, String>() {
            @Override
            public String select(Map<String, List<String>> map) throws Exception {
                String jsonStr=map.get("item_first").get(0);
                JsonArray jsonArr = JsonUtils.strToJsonArray(jsonStr);
                JsonObject retJsonObj=new JsonObject();
                JsonArray jsonData = new JsonArray();
                //异常数据
                JsonArray errorJsonData=new JsonArray();
                for(int k= 0 ;k< jsonArr.size();k++){
                    String jsonTmpStr=jsonArr.get(k).toString();
                    boolean flag=true;
                    for(SourceField field : source.getFields()){
                        JsonObject tmpJsonObj1 = JsonUtils.strToJson(jsonTmpStr);
                        try {
                            if (!StringUtils.contains(jsonTmpStr, field.getName())) {
                                flag = false;
                                tmpJsonObj1.addProperty("errorInfo", " 数据中必须包含[" + field.getName() + "] 字段");
                                errorJsonData.add(tmpJsonObj1);
                                break;
                            }
                        }catch(Exception ex){
                            break;
                        }
                    }
                    //必要字段满足
                    if(flag) {
                        JsonObject jsonObj = JsonUtils.strToJson(jsonTmpStr);
                        JsonObject tmpJsonObj = new JsonObject();
                        tmpJsonObj.addProperty("bthId",jsonObj.get("bthId").getAsString());
                        tmpJsonObj.addProperty("id",jsonObj.get("id").getAsString());
                        for (ChannelField channelField : channel.getFields()) {
                            try {
                                tmpJsonObj.addProperty(channelField.getName(), jsonObj.get(channelField.getName()).getAsString());
                            } catch (Exception ex) {
                                tmpJsonObj.addProperty(channelField.getName(), 0);
                               if (rules != null && rules.size() > 0) {
                                    for (RuleExpressionBean rule : rules) {
                                        if (StringUtils.isEmpty(rule.getRuleType()) && StringUtils.equals(channelField.getName(), rule.getFiledName())) {
                                            double result=0;
                                            try {
                                                result = Double.valueOf(ExpressionUtils.expressionParse(rule.getExpression(), jsonObj));
                                            }catch(Exception e){
                                            }
                                            tmpJsonObj.addProperty(rule.getFiledName(),result);
                                        }
                                    }
                                }
                            }
                        }
                        JsonObject jsonObject=JsonUtils.strToJson(JsonUtils.toJson(tmpJsonObj));
                        jsonObject.addProperty("errorInfo","");
                        errorJsonData.add(jsonObject);
                        jsonData.add(tmpJsonObj);
                    }
                }
                retJsonObj.add("data",jsonData);
                retJsonObj.add("errorData",errorJsonData);
                return JsonUtils.toJson(retJsonObj);
            }
        });

        //对流进行切分
        SplitStream<String> dataStream=cepPatternDS.map(new CepMapFunction()).flatMap(new CEPFlatmapFunction()).split(new CepOutputSelector());
        // 3.中间数据 异常数据  写入ES
        DataStream<String> middleStream=dataStream.select("errorData").map(new CepErrorMapFunction()).flatMap(new CEPFlatmapFunction());
        Map<String,String> midConfig=new HashMap<>();
        midConfig.put(Constants.ES_INDEX_KEY(),"idx_app_stm_002_mid");
        midConfig.put(Constants.ES_TYPE_KEY(),"app_stm_002_mid");
        middleStream.addSink(new ElasticsearchSink<String>(esConfig,transportAddresses,new POCElasticsearchSinkFunction(midConfig)));
        //最终输出流
        DataStream<String> finalData=dataStream.select("finalData").map(new MapFunction<String, String>() {
            @Override
            public String map(String value) throws Exception {
                try {
                    JsonArray jsonArr = JsonUtils.strToJsonArray(value);
                    JsonObject head = new JsonObject();
                    JsonObject retJsonObj = new JsonObject();
                    String bthId = jsonArr.get(0).getAsJsonObject().get("bthId").getAsString();
                    head.addProperty("appId", appId);
                    head.addProperty("bthId", bthId);
                    head.addProperty("consume", 0);
                    head.addProperty("status", 0);
                    head.addProperty("timestamp", System.currentTimeMillis());
                    head.addProperty("produce", jsonArr.size());
                    JsonArray jsonArrData = new JsonArray();
                    for (int i = 0; i < jsonArr.size(); i++) {
                        JsonObject obj = jsonArr.get(i).getAsJsonObject();
                        jsonArrData.add(obj);
                    }
                    retJsonObj.add("data", jsonArrData);
                    retJsonObj.add("head", head);
                    return JsonUtils.toJson(retJsonObj);
                }catch(Exception ex){
                    return "";
                }

            }
        }).filter(x -> StringUtils.isNotEmpty(x));

        /*//渠道数据写入ES
        Map<String,String> chlConfig=new HashMap<>();
        chlConfig.put(Constants.ES_INDEX_KEY(),"idx_app_stm_002_cnl");
        chlConfig.put(Constants.ES_TYPE_KEY(),"app_stm_002_cnl");
        finalData.map(new RichMapFunction<String, List<String>>() {

            @Override
            public List<String> map(String value) throws Exception {
                List<String> lst=new ArrayList<>();
                try {
                    JsonArray jsonArray = JsonUtils.strToJsonArray(JsonUtils.strToJson(value).get("data").toString());
                    for (int i = 0; i < jsonArray.size(); i++) {
                        lst.add(jsonArray.get(i).toString());
                    }
                }catch(Exception ex){}
                return lst;
            }
        }).flatMap(new FlatMapFunction<List<String>, String>() {
            @Override
            public void flatMap(List<String> value, Collector<String> out) throws Exception {
                for(String str:value){
                    out.collect(str);
                }
            }
        }).addSink(new ElasticsearchSink<String>(esConfig,transportAddresses,new POCElasticsearchSinkFunction(chlConfig)));*/
        // 4.阻塞数据
        if (channel.getStatus() == 2) {
            return;
        }
        // 5.渠道数据
        Properties kafkaChlProp = new Properties();
        kafkaChlProp.put("bootstrap.servers", channel.getKafkaHosts());
        kafkaChlProp.put("zookeeper.connect", channel.getZokkeeperHost());

        HashMap<String,String> hashMap=new HashMap<>();
        hashMap.put("bootstrap.servers",channel.getKafkaHosts());
        hashMap.put("zookeeper.connect",channel.getKafkaHosts());
        hashMap.put("topic",channel.getTopic());
        hashMap.put(Constants.ES_INDEX_KEY(),"idx_app_stm_002_cnl");
        hashMap.put(Constants.ES_TYPE_KEY(),"app_stm_002_cnl");

        finalData.addSink(new ESKafkaRealTimeSink(hashMap));


        /* FlinkKafkaProducer010 kafkaProducer = new FlinkKafkaProducer010(channel.getTopic(), new SimpleStringSchema(), kafkaChlProp);
        finalData.addSink(kafkaProducer);*/
        try {
            env.execute(appName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
