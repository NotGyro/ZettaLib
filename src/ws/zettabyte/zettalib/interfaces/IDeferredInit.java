package ws.zettabyte.zettalib.interfaces;

import ws.zettabyte.zettalib.ContentRegistry;

/**
 * Doesn't appear to work. Do not use.
 */
public interface IDeferredInit {
	//To be called after every configgable has been configured.
	void DeferredInit(ContentRegistry cr);
}
