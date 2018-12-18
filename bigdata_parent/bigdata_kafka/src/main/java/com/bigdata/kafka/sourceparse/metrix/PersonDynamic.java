package com.bigdata.kafka.sourceparse.metrix;

import javax.management.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Administrator on 2018/12/16.
 */
public class PersonDynamic implements DynamicMBean {


    private Person person;

    //描述属性信息
    private List<MBeanAttributeInfo> attributeInfos=new ArrayList<>();

    //描述构造器信息
    private List<MBeanConstructorInfo> constructorInfos=new ArrayList<>();

    //描述方法信息
    private List<MBeanOperationInfo> operationInfo=new ArrayList<>();

    //描述通知信息
    private List<MBeanNotificationInfo> notificationInfos=new ArrayList<>();


    //MBeanInfo 用于管理以上信息
    private MBeanInfo mBeanInfo;


    public PersonDynamic(Person person) {
        this.person = person;
        try{
            init();
        }catch (Exception e){

        }
    }

    //初始化方法
    public void init() throws Exception{
        //构建Person的属性，方法，构造器信息
        constructorInfos.add(new MBeanConstructorInfo("PersonDynamic(String,Integer)构造器",
                this.person.getClass().getConstructors()[0]));

        attributeInfos.add(new MBeanAttributeInfo("name","java.lang.String","姓名",true,false,false));
        attributeInfos.add(new MBeanAttributeInfo("age","java.lang.Integer","年龄",true,false,false));

        operationInfo.add(new MBeanOperationInfo("sayHello（） 方法",this.person.getClass().getMethod("sayHello",new Class[]{String.class})));


        //创建一个MBeanInfo对象
        this.mBeanInfo=new MBeanInfo(this.getClass().getName(),
                "PersonDynamic",
                attributeInfos.toArray(new MBeanAttributeInfo[attributeInfos.size()]),
                constructorInfos.toArray(new MBeanConstructorInfo[constructorInfos.size()]),
                operationInfo.toArray(new MBeanOperationInfo[operationInfo.size()]),
                notificationInfos.toArray(new MBeanNotificationInfo[notificationInfos.size()]));
    }

    @Override
    public Object getAttribute(String attribute) throws AttributeNotFoundException, MBeanException, ReflectionException {

        if("name".equals(attribute)){
            return this.person.getName();
        }else if ("age".equals(attribute)){
            return this.person.getAge();
        }

        return null;
    }

    @Override
    public void setAttribute(Attribute attribute) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {

    }

    @Override
    public AttributeList getAttributes(String[] attributes) {
        if(attributes==null || attributes.length==0){
            return null;
        }

        try{
            AttributeList attributeList=new AttributeList();
            for(String attrName:attributes){
                Object obj=this.getAttribute(attrName);
                Attribute attribute=new Attribute(attrName,obj);
                attributeList.add(attribute);
            }
            return attributeList;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public AttributeList setAttributes(AttributeList attributes) {
        return null;
    }

    @Override
    public Object invoke(String actionName, Object[] params, String[] signature) throws MBeanException, ReflectionException {

        if(actionName.equals("sayHello")){
            return this.person.sayHello(params[0].toString());
        }

        return null;
    }

    @Override
    public MBeanInfo getMBeanInfo() {
        return this.mBeanInfo;
    }
}
