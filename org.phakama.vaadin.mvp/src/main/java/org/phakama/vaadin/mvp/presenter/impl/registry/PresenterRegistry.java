package org.phakama.vaadin.mvp.presenter.impl.registry;

import java.util.Collection;
import java.util.HashMap;

import org.phakama.vaadin.mvp.presenter.IPresenter;
import org.phakama.vaadin.mvp.presenter.IPresenterRegistry;
import org.phakama.vaadin.mvp.view.IView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PresenterRegistry implements IPresenterRegistry {
	private static final long serialVersionUID = 3399515352322336940L;
	
	private static final Logger logger = LoggerFactory.getLogger(PresenterRegistry.class);

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
		
		if (parent == null) {
			logger.debug("Presenter instance of type %s successfully added to the presenter registry with no parent.", presenter.getClass());
		} else {
			logger.debug("Presenter instance of type %s successfully added to the presenter registry under a parent of type %s.", presenter.getClass(), parent.getClass());
		}
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
		
		logger.debug("Presenter %s was successfully removed from the presenter registry.", presenter);
	}

	@Override
	public IPresenter<? extends IView> find(IView view) {
		if (view == null) {
			throw new IllegalArgumentException("The view parameter was null.");
		}
		
		IPresenter<? extends IView> owner = this.viewPresenterMap.get(view);
		if (owner == null) {
			logger.debug("Could not find the presenter that owns view %s.", view);
		} else {
			logger.debug("Presenter %s was found to be the owner of view %s.", owner, view);
		}
		
		return owner;
	}

	@Override
	public IPresenter<? extends IView> parentOf(IPresenter<? extends IView> presenter) {
		if (presenter == null) {
			throw new IllegalArgumentException("The presenter parameter was null.");
		}

		IPresenter<? extends IView> parent = this.presenterCache.parentOf(presenter);
		if (parent == null) {
			logger.debug("Could not find a parent of presenter %s.", presenter);
		} else {
			logger.debug("%s was found to be the parent of %s.", parent, presenter);
		}
		
		return parent;
	}

	@Override
	public Collection<IPresenter<? extends IView>> siblingsOf(IPresenter<? extends IView> presenter) {
		if (presenter == null) {
			throw new IllegalArgumentException("The presenter parameter was null.");
		}

		Collection<IPresenter<? extends IView>> siblings = this.presenterCache.siblingsOf(presenter);
		if (siblings == null) {
			logger.debug("Could not find any siblings of presenter %s.", presenter);
		} else {
			logger.debug("%d siblings of presenter %s were found.", siblings.size(), presenter);
		}
		
		return siblings;
	}

	@Override
	public Collection<IPresenter<? extends IView>> childrenOf(IPresenter<? extends IView> presenter) {
		if (presenter == null) {
			throw new IllegalArgumentException("The presenter parameter was null.");
		}

		Collection<IPresenter<? extends IView>> children = this.presenterCache.childrenOf(presenter);
		if (children == null) {
			logger.debug("Could not find any children of presenter %s.", presenter);
		} else {
			logger.debug("%d children of presenter %s were found.", children.size(), presenter);
		}
		
		return children;
	}

	@Override
	public int size() {
		return this.viewPresenterMap.entrySet().size();
	}

}
