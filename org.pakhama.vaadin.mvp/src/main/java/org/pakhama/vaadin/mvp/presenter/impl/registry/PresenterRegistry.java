package org.pakhama.vaadin.mvp.presenter.impl.registry;

import java.util.Collection;
import java.util.HashMap;

import org.pakhama.vaadin.mvp.presenter.IPresenter;
import org.pakhama.vaadin.mvp.presenter.IPresenterRegistry;
import org.pakhama.vaadin.mvp.view.IView;

public class PresenterRegistry implements IPresenterRegistry {
	private static final long serialVersionUID = 3399515352322336940L;

	private PresenterRegistryTree presenterCache = new PresenterRegistryTree();
	private HashMap<IView, IPresenter<? extends IView>> viewPresenterMap = new HashMap<IView, IPresenter<? extends IView>>();

	@SuppressWarnings("unchecked")
	@Override
	public void register(IPresenter<? extends IView> presenter, IPresenter<? extends IView> parent, IView view) {
		if (presenter == null) {
			throw new IllegalArgumentException("The presenter parameter was null.");
		}
		if (view == null) {
			throw new IllegalArgumentException("The view parameter was null.");
		}

		this.presenterCache.add(presenter, parent);
		this.viewPresenterMap.put(view, presenter);

		// Welcome the new presenter with open arms, then whisper into its ear
		// that its days are numbered >:-D
		((IPresenter<IView>) presenter).onBind(view);
	}

	@Override
	public void unregister(IPresenter<? extends IView> presenter) {
		if (presenter == null) {
			throw new IllegalArgumentException("The presenter parameter was null.");
		}
		// Erase any evidence that the presenter was referenced here
		this.presenterCache.remove(presenter);
		if (presenter.getView() != null) {
			this.viewPresenterMap.remove(presenter.getView());
		}
		// Let the presenter in on its own death,
		// Don't hold it a funeral
		presenter.onUnbind();
	}

	@Override
	public IPresenter<? extends IView> find(IView view) {
		if (view == null) {
			throw new IllegalArgumentException("The view parameter was null.");
		}

		return this.viewPresenterMap.get(view);
	}

	@Override
	public IPresenter<? extends IView> parentOf(IPresenter<? extends IView> presenter) {
		if (presenter == null) {
			throw new IllegalArgumentException("The presenter parameter was null.");
		}

		return this.presenterCache.parentOf(presenter);
	}

	@Override
	public Collection<IPresenter<? extends IView>> siblingsOf(IPresenter<? extends IView> presenter) {
		if (presenter == null) {
			throw new IllegalArgumentException("The presenter parameter was null.");
		}

		return this.presenterCache.siblingsOf(presenter);
	}

	@Override
	public Collection<IPresenter<? extends IView>> childrenOf(IPresenter<? extends IView> presenter) {
		if (presenter == null) {
			throw new IllegalArgumentException("The presenter parameter was null.");
		}

		return this.presenterCache.childrenOf(presenter);
	}

	@Override
	public int size() {
		return this.viewPresenterMap.entrySet().size();
	}

}
