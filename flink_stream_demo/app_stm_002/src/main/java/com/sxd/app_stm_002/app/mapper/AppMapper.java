package com.sxd.app_stm_002.app.mapper;

import com.sxd.app_stm_002.app.entity.App;
import com.sxd.app_stm_002.app.entity.AppCep;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AppMapper {

    App getApp(App app);

    List<AppCep> getAppCeps(App app);
}