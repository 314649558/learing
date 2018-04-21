package com.bigdata.sampler.test;

import java.net.URI;

public class UriApiDemo {

    public static void main(String[] args) throws Exception{

        //URI uri=new URI ("http://localhost:8080/index?login_name=hailong");
        URI uri=new URI ("file:///etc/data/dong");


        System.out.println ("Authority:"+uri.getAuthority());

        System.out.println (uri.getScheme());

        System.out.println (uri.getPath());

        System.out.println (uri.getHost());

        System.out.println (uri.getFragment());

        System.out.println (uri.getPort());

        System.out.println (uri.getQuery());
        System.out.println (uri.getRawQuery());


    }
}
