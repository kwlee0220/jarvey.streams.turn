package jarvey.streams.turn;

import com.google.gson.annotations.SerializedName;


/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public final class ObjectTurn {
	@SerializedName("node") private String m_nodeId;
	@SerializedName("track_id") private String m_trackId;
	@SerializedName("turn") private String m_turn;
	
	public ObjectTurn(String nodeId, String trackId, String turn) {
		m_nodeId = nodeId;
		m_trackId = trackId;
		m_turn = turn;
	}
	
	@Override
	public String toString() {
		return String.format("Turn[%s/%s: %s]", m_nodeId, m_trackId, m_turn);
	}
}