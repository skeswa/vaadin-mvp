package org.pakhama.vaadin.mvp;

public class ColorSampleTestManager {
	private boolean isOtherColorFlagged = false;
	private boolean isOtherRedFlagged = false;
	private boolean isColorFlagged = false;
	private boolean isRedFlagged = false;
	private boolean isBlueFlagged = false;
	private boolean isGreenFlagged = false;

	public void flagRed() {
		this.isRedFlagged = true;
	}

	public void flagBlue() {
		this.isBlueFlagged = true;
	}

	public void flagGreen() {
		this.isGreenFlagged = true;
	}

	public void flagColor() {
		this.isColorFlagged = true;
	}

	public void unflag() {
		this.isRedFlagged = false;
		this.isBlueFlagged = false;
		this.isGreenFlagged = false;
		this.isColorFlagged = false;
		this.isOtherColorFlagged = false;
		this.isOtherRedFlagged = false;
	}

	public boolean isColorFlagged() {
		return isColorFlagged;
	}

	public boolean isRedFlagged() {
		return isRedFlagged;
	}

	public boolean isBlueFlagged() {
		return isBlueFlagged;
	}

	public boolean isGreenFlagged() {
		return isGreenFlagged;
	}

	public boolean isOtherColorFlagged() {
		return isOtherColorFlagged;
	}

	public void flagOtherColor() {
		this.isOtherColorFlagged = true;
	}

	public boolean isOtherRedFlagged() {
		return isOtherRedFlagged;
	}

	public void flagOtherRed() {
		this.isOtherRedFlagged = true;
	}
}
