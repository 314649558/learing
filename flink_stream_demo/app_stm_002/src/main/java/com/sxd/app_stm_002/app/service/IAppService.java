package com.sxd.app_stm_002.app.service;

import com.sxd.app_stm_002.app.entity.App;
import com.sxd.app_stm_002.app.entity.AppCep;

import java.util.List;

public interface IAppService {

    App getApp(App app);

    List<AppCep> getAppCeps(App app);
}
