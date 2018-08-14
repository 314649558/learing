package com.sxd.test;

import com.google.gson.JsonObject;
import com.sxd.citic.drools.core.utils.JsonUtils;
import com.sxd.citic.drools.utils.KIEUtils;
import com.sxd.citic.drools.utils.ReloadDroolsRule;
import com.sxd.citic.drools.utils.RuleDefine;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

/**
 * Created by Administrator on 2018/8/4.
 */
public class DroolsTest {


    @org.junit.Test
    public void testDrools(){
        KieContainer kieContainer1= KIEUtils.getKieContainer();

        KieContainer kieContainer2=KIEUtils.getKieContainer();

        ReloadDroolsRule.reload(RuleDefine.rule());
        KieContainer kieContainer3=KIEUtils.getKieContainer();
        //观察这三个对象的地址引用可以发现 kieContainer3和前面两个不一样
        System.out.println(kieContainer1);
        System.out.println(kieContainer2);
        System.out.println(kieContainer3);
        KieSession kieSession=KIEUtils.getKieContainer().newKieSession();
        kieSession.fireAllRules();
        kieSession.dispose();
    }
    @org.junit.Test
    public void testJsonObjLog(){
        KieSession kieSession=KIEUtils.getKieContainer().newKieSession();
        String json="{\"tradeDate\":\"20180728\",\"deptNbr\":\"383362484\",\"storeNbr\":\"17\",\"itemNbr\":28.314917725682744,\"costAmt\":83.6532730047071,\"netSalesAmt\":13.97002428555566,\"retailAmt\":288.55404919181206}";
        JsonObject jsonObject= JsonUtils.strToJson(json);
        FactHandle factHandle=kieSession.insert(jsonObject);
        System.out.println(factHandle.toExternalForm());
        System.out.println(JsonUtils.toJson(jsonObject));
        kieSession.fireAllRules();
        System.out.println(JsonUtils.toJson(jsonObject));
        kieSession.dispose();
    }
}
