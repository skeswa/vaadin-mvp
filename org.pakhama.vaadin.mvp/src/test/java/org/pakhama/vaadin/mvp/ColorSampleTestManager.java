package org.pakhama.vaadin.mvp;

public class ColorSampleTestManager {
	private boolean isOtherColorFlagged = false;
	private boolean isOtherRedFlagged = false;
	private boolean isColorFlagged = false;
	private boolean isRedFlagged = false;
	private boolean isBlueFlagged = false;
	private boolean isGreenFlagged = false;
	private boolean isFirstChildFlagged = false;
	private boolean isSecondChildFlagged = false;
	private boolean isThirdChildFlagged = false;

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
		this.isFirstChildFlagged = false;
		this.isSecondChildFlagged = false;
		this.isThirdChildFlagged = false;
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

	public boolean isFirstChildFlagged() {
		return isFirstChildFlagged;
	}

	public void flagFirstChild() {
		this.isFirstChildFlagged = true;
	}

	public boolean isSecondChildFlagged() {
		return isSecondChildFlagged;
	}

	public void flagSecondChild() {
		this.isSecondChildFlagged = true;
	}

	public boolean isThirdChildFlagged() {
		return isThirdChildFlagged;
	}

	public void flagThirdChild() {
		this.isThirdChildFlagged = true;
	}
}
