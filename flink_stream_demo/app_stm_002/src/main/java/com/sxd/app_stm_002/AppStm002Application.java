package com.sxd.app_stm_002;

import com.sxd.app_stm_002.app.service.IAppService;
import com.sxd.app_stm_002.work.AppWork;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.io.Serializable;

/**
 * Created by Administrator on 2018/8/2.
 */

@MapperScan(value = "com.sxd.app_stm_002.app.mapper")
@SpringBootApplication
public class AppStm002Application implements Serializable {

    @Autowired
    private IAppService appService;

    public static void main(String[] args) {
        SpringApplication.run(AppStm002Application.class, args);
    }

    @PostConstruct
    public void init() {
        new AppWork(this.appService);
    }
}
