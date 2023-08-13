package jarvey.streams.turn;

import java.time.Duration;
import java.time.Instant;

import com.google.gson.annotations.SerializedName;

import jarvey.streams.zone.ZoneEvent;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public final class ZoneTravel {
	@SerializedName("zone") private final String m_zoneId;
	@SerializedName("first_ts") private final long m_enterTs;
	@SerializedName("last_ts") private long m_leaveTs;
	
	public static ZoneTravel open(ZoneEvent ev) {
		return new ZoneTravel(ev.getZoneId(), ev.getTimestamp(), -1);
	}
	
	private ZoneTravel(String zoneId, long enterTs, long leaveTs) {
		m_zoneId = zoneId;
		m_enterTs = enterTs;
		m_leaveTs = leaveTs;
	}
	
	public boolean isOpen() {
		return m_leaveTs <= 0;
	}
	
	public boolean isClosed() {
		return m_leaveTs > 0;
	}
	
	public String getZoneId() {
		return m_zoneId;
	}
	
	public long getEnterTimestamp() {
		return m_enterTs;
	}
	
	public long getLeaveTimestamp() {
		return m_leaveTs;
	}
	
	public Duration getDuration() {
		if ( m_leaveTs > 0 ) {
			return Duration.between(getEnterTime(), getLeaveTime());
		}
		else {
			return null;
		}
	}
	
	public Instant getEnterTime() {
		return Instant.ofEpochMilli(m_enterTs);
	}
	
	public Instant getLeaveTime() {
		return Instant.ofEpochMilli(m_leaveTs);
	}
	
	public ZoneTravel close(long ts) {
		m_leaveTs = ts;
		
		return this;
	}
	
	public ZoneTravel duplicate() {
		return new ZoneTravel(m_zoneId, m_enterTs, m_leaveTs);
	}
	
	@Override
	public String toString() {
		Duration stay = getDuration();
		String stayStr = (stay != null) ? String.format("%.1fs", stay.toMillis() / 1000.0) : "?";
		
		return String.format("%s[%d:%s]", m_zoneId, m_enterTs, stayStr);
	}
}
