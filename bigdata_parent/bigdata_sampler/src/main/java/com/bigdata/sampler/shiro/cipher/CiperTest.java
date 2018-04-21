package com.bigdata.sampler.shiro.cipher;

import org.apache.shiro.codec.Base64;
import org.apache.shiro.codec.Hex;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.DefaultHashService;
import org.apache.shiro.crypto.hash.HashRequest;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.util.ByteSource;
import org.apache.shiro.util.SimpleByteSource;

public class CiperTest {


    public static void main(String[] args) {
        CiperTest ciper=new CiperTest();
        String password="hailong";
        ciper.base64(password);
        ciper.str2Hex(password);


        ciper.hashService(password);
    }

    public void base64(String password){
        String base64Encoded = Base64.encodeToString(password.getBytes());
        String str2 = Base64.decodeToString(base64Encoded);
        System.out.println("base64Encoded："+base64Encoded);
        System.out.println("str2："+str2);
    }


    public void str2Hex(String password){
        String base64Encoded = Hex.encodeToString(password.getBytes());
        String str2 = new String(Hex.decode(base64Encoded.getBytes()));
        System.out.println("base64Encoded："+base64Encoded);
        System.out.println("str2："+str2);
    }
    public void hash(String password){
        String salt="123";
        String md5=new Md5Hash(password,salt).toString();
        System.out.println(md5);
        String sha1 = new Sha256Hash(password, salt).toString();
        System.out.println(sha1);
    }


    public void hashService(String password){
        DefaultHashService hashService=new DefaultHashService();
        hashService.setHashAlgorithmName("SHA-512");
        hashService.setPrivateSalt(new SimpleByteSource("123")); //私盐，默认无
        hashService.setGeneratePublicSalt(true);//是否生成公盐，默认false
        hashService.setRandomNumberGenerator(new SecureRandomNumberGenerator());//用于生成公盐。默认就这个
        hashService.setHashIterations(1); //生成Hash值的迭代次数

        HashRequest request = new HashRequest.Builder()
                .setAlgorithmName("MD5").setSource(ByteSource.Util.bytes(password))
                .setSalt(ByteSource.Util.bytes("123")).setIterations(2).build();
        String hex = hashService.computeHash(request).toHex();
        System.out.println("hex:"+hex);
    }

}
