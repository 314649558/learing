package com.sxd.app_stm_002.work.cep.wartermarker;

import com.google.gson.JsonObject;
import com.sxd.app_stm_002.work.utils.Constants;
import com.sxd.app_stm_002.work.utils.JsonUtils;
import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks;
import org.apache.flink.streaming.api.watermark.Watermark;

import javax.annotation.Nullable;

/**
 * Created by Administrator on 2018/8/2.
 */
public class BoundedLatenessWatermarkAssigner implements AssignerWithPeriodicWatermarks<String> {

    private Integer allowLatenss;

    private Long maxTimestamp = -1L;

    public BoundedLatenessWatermarkAssigner() {
        new BoundedLatenessWatermarkAssigner(0);
    }


    public BoundedLatenessWatermarkAssigner(Integer allowLatenss) {
        this.allowLatenss = allowLatenss;
    }

    @Nullable
    @Override
    public Watermark getCurrentWatermark() {
        return new Watermark(maxTimestamp - allowLatenss * 1000L);
    }

    @Override
    public long extractTimestamp(String element, long previousElementTimestamp) {

        long timestamp = System.currentTimeMillis();
        /*try {
            JsonObject jsonObj = JsonUtils.strToJson(element);

            if (jsonObj.get(Constants.TIMESTAMP()) != null) {
                timestamp = jsonObj.get(Constants.TIMESTAMP()).getAsLong();
            }
            if (timestamp > maxTimestamp) {

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }*/
        maxTimestamp = timestamp;
        return timestamp;
    }
}
