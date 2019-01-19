package com.hit.model;

import java.util.Observable;

public class CacheUnitModel extends Observable implements Model {
	private CacheUnitClient clientUnit;

	public CacheUnitModel() {
		clientUnit = new CacheUnitClient();
	}

	@Override
	public <T> void updateModelData(T t) {
		setChanged(); // for notifyObserver
		notifyObservers(clientUnit.send((String) t));// update all the observers
	}

}
