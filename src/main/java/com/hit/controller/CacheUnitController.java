package com.hit.controller;

import java.util.Observable;

import com.hit.model.Model;
import com.hit.view.View;

public class CacheUnitController extends Object implements Controller {

	private View view;
	private Model model;

	public CacheUnitController(Model model, View view) {
		this.view = view;
		this.model = model;
	}

	@Override
	public void update(Observable obs, Object obj) {
		if (obs instanceof View) {
			model.updateModelData(obj);

		} else if (obs instanceof Model) {
			view.updateUIData(obj);

		}

	}

}
