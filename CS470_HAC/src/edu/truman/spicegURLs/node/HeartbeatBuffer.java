package edu.truman.spicegURLs.node;

import java.util.ArrayList;

public class HeartbeatBuffer {
	
	private ArrayList<String> changeList;
	
	public HeartbeatBuffer () {
		changeList = new ArrayList<>();
	}
	
	public String getChanges() {
		if (changeList.size() > 0) {
			return String.join(";", changeList);
			// empty array list?
		}
		return "X";
	}
	
	public void queueChange (String change) {
		changeList.add(change);
	}
}
