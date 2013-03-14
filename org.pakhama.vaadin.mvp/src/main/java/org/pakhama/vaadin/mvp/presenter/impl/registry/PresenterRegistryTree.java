package org.pakhama.vaadin.mvp.presenter.impl.registry;

import java.util.Iterator;

import org.pakhama.vaadin.mvp.presenter.IPresenter;
import org.pakhama.vaadin.mvp.view.IView;

class PresenterRegistryTree implements Iterable<PresenterRegistryNode> {
	private PresenterRegistryNode head;

	PresenterRegistryTree() {
		this.head = null;
	}

	void add(final IPresenter<? extends IView> presenter) {
		if (head == null) {
			PresenterRegistryNode node = new PresenterRegistryNode(presenter);
			this.head = node;
		} else {
			PresenterRegistryNode match = search(this.head, new ISearchListener() {
				
				@Override
				public boolean isMatch(PresenterRegistryNode node) {
					return presenter.getClass().isAssignableFrom(node.getData().getClass());
				}
			});
		}
	}

	PresenterRegistryNode search(PresenterRegistryNode node, ISearchListener listener) {
		if (node != null) {
			if (listener.isMatch(node)) {
				return node;
			}
			if (node.getChildren() != null) {
				for (PresenterRegistryNode childNode : node.getChildren()) {
					PresenterRegistryNode returnNode = search(childNode, listener);
					if (returnNode != null) {
						return returnNode;
					}
				}
			}
		}
		return null;
	}
	
	private interface ISearchListener {
		boolean isMatch(PresenterRegistryNode node);
	}
}
