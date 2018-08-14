package com.sxd.app_stm_002.app.service.impl;

import com.sxd.app_stm_002.app.entity.App;
import com.sxd.app_stm_002.app.entity.AppCep;
import com.sxd.app_stm_002.app.mapper.AppMapper;
import com.sxd.app_stm_002.app.service.IAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

@Service
public class AppService implements IAppService, Serializable {

    @Autowired
    private AppMapper appMapper;

    @Override
    public App getApp(App app) {
        return this.appMapper.getApp(app);
    }

    @Override
    public List<AppCep> getAppCeps(App app) {
        return this.appMapper.getAppCeps(app);
    }
}
