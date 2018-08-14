package com.sxd.app_stm_001.app.mapper;

import com.sxd.app_stm_001.app.entity.App;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AppMapper {
    App getApp(App app);
}