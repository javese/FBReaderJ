package org.geometerplus.android.fbreader.tips;

import java.util.HashMap;

public class State {
	private static HashMap<String, Object> myState = new HashMap<String, Object>();
	
	public static synchronized void putToState(String key, Object object){
		myState.put(key, object);
	}

	public static synchronized Object getFromState(String key){
		return myState.get(key);
	}

	public static synchronized Object popFromState(String key){
		Object object = myState.get(key);
		myState.remove(key);
		return object;
	}

	public static synchronized void clearState(String key){
		myState.clear();
	}
}
