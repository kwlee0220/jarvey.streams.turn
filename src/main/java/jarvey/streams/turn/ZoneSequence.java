package jarvey.streams.turn;

import java.time.Duration;
import java.util.List;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.gson.annotations.SerializedName;

import utils.stream.FStream;


/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public final class ZoneSequence {
	@SerializedName("track_id") private String m_trackId;
	@SerializedName("visits") private List<ZoneTravel> m_visits;
	@SerializedName("closed") private boolean m_closed;
	
	public static ZoneSequence from(String trackId, ZoneTravel first) {
		return new ZoneSequence(trackId, Lists.newArrayList(first));
	}
	
	public static ZoneSequence empty(String trackId) {
		return new ZoneSequence(trackId, Lists.newArrayList());
	}
	
	private ZoneSequence(String trackId, List<ZoneTravel> travels) {
		m_trackId = trackId;
		m_visits = travels;
		m_closed = false;
	}
	
	public String getTrackId() {
		return m_trackId;
	}
	
	public int getVisitCount() {
		return m_visits.size();
	}
	
	public ZoneTravel getVisit(int index) {
		return m_visits.get(index);
	}
	
	public List<ZoneTravel> getVisitAll() {
		return m_visits;
	}
	
	public ZoneTravel getLastZoneTravel() {
		return Iterables.getLast(m_visits, null);
	}
	
	public List<String> getZoneIdSequence() {
		return FStream.from(m_visits).map(ZoneTravel::getZoneId).toList();
	}
	
	public void append(ZoneTravel travel) {
		m_visits.add(travel);
	}
	
	public void close() {
		m_closed = true;
	}
	
	public Duration getInterTravelDuration(int fromIdx, int toIdx) {
		if ( fromIdx >= toIdx || fromIdx < 0 || toIdx >= m_visits.size() ) {
			throw new IllegalArgumentException(String.format("invalid travel index: %d, %d", fromIdx, toIdx));
		}
		
		return Duration.between(m_visits.get(fromIdx).getLeaveTime(), m_visits.get(toIdx).getEnterTime());
	}
	
	public Duration getTravelDuration(int trvIdx) {
		if ( trvIdx < 0 || trvIdx >= m_visits.size() ) {
			throw new IllegalArgumentException(String.format("invalid travel index: %d", trvIdx));
		}
		
		return m_visits.get(trvIdx).getDuration();
	}
	
	public void collapseToPrevious(int idx) {
		ZoneTravel travel = m_visits.get(idx);
		ZoneTravel prev = m_visits.get(idx-1);
		prev.close(travel.getLeaveTimestamp());
		m_visits.remove(idx);
	}
	
	public ZoneSequence duplicate() {
		List<ZoneTravel> visits = FStream.from(m_visits)
										.map(ZoneTravel::duplicate)
										.toList();
		return new ZoneSequence(m_trackId, visits);
	}
	
	@Override
	public String toString() {
		String seqStr =  FStream.from(m_visits)
								.map(ZoneTravel::toString)
								.join('-');
		String endStr = (m_closed) ? "-END" : "";
		return String.format("%s/%d: %s%s", m_trackId, seqStr, endStr);
	}
}