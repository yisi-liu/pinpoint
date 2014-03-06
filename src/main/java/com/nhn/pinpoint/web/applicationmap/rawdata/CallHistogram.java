package com.nhn.pinpoint.web.applicationmap.rawdata;

import com.nhn.pinpoint.common.ServiceType;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author netspider
 * @author emeroad
 */

public class CallHistogram {
	/**
	 * UI에서 호스트를 구분하기 위한 목적으로 hostname, agentid, endpoint등 구분할 수 있는 아무거나 넣으면 됨.
	 */
	private final String id;
	private final ServiceType serviceType;

    private final Map<Long, TimeHistogram> timeHistogramMap;

    private static final ObjectMapper MAPPER = new ObjectMapper();

	public CallHistogram(String agent, ServiceType serviceType) {
        if (agent == null) {
            throw new NullPointerException("agent must not be null");
        }
        if (serviceType == null) {
            throw new NullPointerException("serviceType must not be null");
        }
        this.id = agent;
		this.serviceType = serviceType;
        this.timeHistogramMap = new HashMap<Long, TimeHistogram>();
	}

    public CallHistogram(CallHistogram copyCallHistogram) {
        if (copyCallHistogram == null) {
            throw new NullPointerException("copyCallHistogram must not be null");
        }

        this.id = copyCallHistogram.id;
        this.serviceType = copyCallHistogram.serviceType;

        this.timeHistogramMap = new HashMap<Long, TimeHistogram>();
        addTimeHistogram(copyCallHistogram.timeHistogramMap.values());
    }

    @JsonProperty("name")
    public String getId() {
		return id;
	}

    @JsonIgnore
	public ServiceType getServiceType() {
		return serviceType;
	}

    @JsonProperty("histogram")
	public Histogram getHistogram() {
        Histogram histogram = new Histogram(serviceType);
        for (TimeHistogram timeHistogram : timeHistogramMap.values()) {
            histogram.add(timeHistogram);
        }
        return histogram;
	}

    @JsonIgnore
    public Collection<TimeHistogram> getTimeHistogram() {
        return timeHistogramMap.values();
    }

    public void addTimeHistogram(TimeHistogram timeHistogram) {
        TimeHistogram find = this.timeHistogramMap.get(timeHistogram.getTimeStamp());
        if (find == null) {
            find = new TimeHistogram(serviceType, timeHistogram.getTimeStamp());
            this.timeHistogramMap.put(timeHistogram.getTimeStamp(), find);
        }
        find.add(timeHistogram);
    }

    public void addTimeHistogram(Collection<TimeHistogram> histogramList) {
        for (TimeHistogram timeHistogram : histogramList) {
            addTimeHistogram(timeHistogram);
        }
    }

    public void addTimeHistogramUncheckType(Collection<TimeHistogram> histogramList) {
        // TODO uncheck가 왜 필요한지 이유 써 놓을것. user 치환시 예외가 있었던것 같음.???
        for (TimeHistogram timeHistogram : histogramList) {
            TimeHistogram find = this.timeHistogramMap.get(timeHistogram.getTimeStamp());
            if (find == null) {
                find = new TimeHistogram(serviceType, timeHistogram.getTimeStamp());
                this.timeHistogramMap.put(timeHistogram.getTimeStamp(), find);
            }
            find.addUncheckType(timeHistogram);
        }
    }



    @JsonIgnore
	public String getJson() {
        try {
            // 추후 오브젝트를 MAPPER에 던저게 되면 해당 api를 제거하도록 하자.
            return MAPPER.writeValueAsString(this);
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CallHistogram{");
        sb.append("agent='").append(id).append('\'');
        sb.append(", serviceType=").append(serviceType);
        // 자료 구조가 변경되어 잠시 땜빵.
        sb.append(", ").append("histogram");
        sb.append('}');
        return sb.toString();
    }


}
