package org.pakhama.vaadin.mvp.presenter.impl.registry;

import java.util.ArrayList;
import java.util.Collection;

import org.pakhama.vaadin.mvp.presenter.IPresenter;
import org.pakhama.vaadin.mvp.view.IView;

class PresenterRegistryNode {
	private IPresenter<? extends IView> data;
	
	private PresenterRegistryNode parent = null;
	private ArrayList<PresenterRegistryNode> children = null;
	
	PresenterRegistryNode(IPresenter<? extends IView> data) {
		this.data = data;
	}
	
	void setParent(PresenterRegistryNode parent) {
		this.parent = parent;
	}
	
	PresenterRegistryNode getParent() {
		return this.parent;
	}
	
	void setData(IPresenter<? extends IView> data) {
		this.data = data;
	}
	
	IPresenter<? extends IView> getData() {
		return this.data;
	}
	
	Collection<PresenterRegistryNode> getChildren() {
		return this.children;
	}
	
	void addChild(PresenterRegistryNode child) {
		if (this.children == null) {
			this.children = new ArrayList<PresenterRegistryNode>();
		}
		
		child.setParent(child);
		if (!this.children.contains(child)) {
			this.children.add(child);
		}
	}
	
	void removeChild(PresenterRegistryNode child) {
		if (this.children != null) {
			this.children.remove(child);
			child.setParent(null);
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof PresenterRegistryNode)) return false;
		return ((PresenterRegistryNode) obj).getData() == this.data;
	}
	
	@Override
	public int hashCode() {
		if (data == null) {
			return 29;
		} else {
			return 29 + (31 * this.data.hashCode());
		}
	}
}
