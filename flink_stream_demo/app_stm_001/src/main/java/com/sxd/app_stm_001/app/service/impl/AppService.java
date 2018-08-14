package com.sxd.app_stm_001.app.service.impl;

import com.sxd.app_stm_001.app.entity.App;
import com.sxd.app_stm_001.app.mapper.AppMapper;
import com.sxd.app_stm_001.app.service.IAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;

@Service
public class AppService implements IAppService, Serializable {

    @Autowired
    private AppMapper appMapper;

    @Override
    public App getApp(App app) {
        return this.appMapper.getApp(app);
    }

}
