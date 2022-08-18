package jarvey.streams.turn;

import java.time.Duration;
import java.time.Instant;

import com.google.gson.annotations.SerializedName;

import jarvey.streams.zone.ZoneLineRelationEvent;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public final class ZoneTravel {
	@SerializedName("zone") private final String m_zoneId;
	@SerializedName("first_frame_index") private final long m_enterFrameIndex;
	@SerializedName("first_ts") private final long m_enterTs;
	@SerializedName("last_frame_index") private long m_leaveFrameIndex;
	@SerializedName("last_ts") private long m_leaveTs;
	
	public static ZoneTravel open(ZoneLineRelationEvent ev) {
		return new ZoneTravel(ev.getZone(), ev.getFrameIndex(), ev.getTimestamp(), -1, -1);
	}
	
	private ZoneTravel(String zoneId, long enterFrameIndex, long enterTs, long leaveFrameIndex, long leaveTs) {
		m_zoneId = zoneId;
		m_enterFrameIndex = enterFrameIndex;
		m_enterTs = enterTs;
		m_leaveFrameIndex = leaveFrameIndex;
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
	
	public long getEnterFrameIndex() {
		return m_enterFrameIndex;
	}
	
	public long getEnterTimestamp() {
		return m_enterTs;
	}
	
	public long getLeaveFrameIndex() {
		return m_leaveFrameIndex;
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
	
	public ZoneTravel close(long index, long ts) {
		m_leaveFrameIndex = index;
		m_leaveTs = ts;
		
		return this;
	}
	
	public ZoneTravel duplicate() {
		return new ZoneTravel(m_zoneId, m_enterFrameIndex, m_enterTs, m_leaveFrameIndex, m_leaveTs);
	}
	
	@Override
	public String toString() {
		Duration stay = getDuration();
		String stayStr = (stay != null) ? String.format("%.1fs", stay.toMillis() / 1000.0) : "?";
		String leaveIdxStr = m_leaveFrameIndex > 0 ? "" + m_leaveFrameIndex : "?";
		
		return String.format("%s[%d-%s:%s]", m_zoneId, m_enterFrameIndex, leaveIdxStr, stayStr);
	}
}
