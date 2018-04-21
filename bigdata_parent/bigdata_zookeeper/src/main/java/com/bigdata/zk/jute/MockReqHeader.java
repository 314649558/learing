package com.bigdata.zk.jute;

import org.apache.jute.InputArchive;
import org.apache.jute.OutputArchive;
import org.apache.jute.Record;

import java.io.IOException;

/**
 * Created by Administrator on 2017/11/27 0027.
 */
public class MockReqHeader implements Record {

    private long sessionId;

    private String type;


    public long getSessionId() {
        return sessionId;
    }

    public void setSessionId(long sessionId) {
        this.sessionId = sessionId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public void serialize(OutputArchive archive, String tag) throws IOException {
        archive.startRecord(this,tag);
        archive.writeLong(sessionId,"sessionId");
        archive.writeString(type,"type");
        archive.endRecord(this,tag);
    }

    @Override
    public void deserialize(InputArchive archive, String tag) throws IOException {

        archive.startRecord(tag);
        sessionId=archive.readLong("sessionId");
        type=archive.readString("type");
        archive.endRecord(tag);
    }
}
