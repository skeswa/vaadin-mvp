package org.pakhama.vaadin.mvp.view;

import org.pakhama.vaadin.mvp.event.BlueColorChangeEvent;
import org.pakhama.vaadin.mvp.event.RedColorChangeEvent;

public class ColorSamplerView extends View implements IColorSamplerView {
	private static final long serialVersionUID = 3292630133544612822L;

	public void init() {
	}

	public void trigger() {
		fire(new RedColorChangeEvent());
		fire(new BlueColorChangeEvent());
	}
}
