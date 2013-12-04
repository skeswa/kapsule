package com.epsilonlabs.kapsule;

import java.lang.ref.SoftReference;
import java.util.HashMap;

public class KapsuleCache {
	private HashMap<String, SoftReference<Kapsule>> cache;

	public KapsuleCache() {
		this.cache = new HashMap<String, SoftReference<Kapsule>>();
	}

	public Kapsule get(String key) {
		// Parameter checking
		if (key == null)
			return null;

		SoftReference<Kapsule> ref = this.cache.get(key);
		if (ref == null)
			return null;
		if (ref.get() == null) {
			// The kapsule got garbage collected - we must remove it
			this.cache.remove(key);
			return null;
		}
		return ref.get();
	}

	public void put(String key, Kapsule kapsule) {
		// Parameter checking
		if (key == null)
			throw new IllegalArgumentException("The key parameter was null.");
		if (kapsule == null)
			throw new IllegalArgumentException("The kapsule parameter was null.");

		SoftReference<Kapsule> ref = this.cache.get(key);
		// Kill whatever is already here by this key
		if (ref.get() != null) {
			ref.enqueue();
			ref.clear();
		}
		// Put the new kapsule here
		ref = new SoftReference<Kapsule>(kapsule);
		this.cache.put(key, ref);
	}
}
