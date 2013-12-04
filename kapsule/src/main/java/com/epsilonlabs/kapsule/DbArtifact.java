package com.epsilonlabs.kapsule;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

import com.google.gson.Gson;

public class DbArtifact implements Serializable {
	private static final long serialVersionUID = 347502369977300975L;

	public enum ArtifactType {
		BASIC, COLLECTION, MAP
	}

	ArtifactType type;
	String clazz;
	String payload;

	public static DbArtifact create(Object value, Gson gson) {
		// Parameter checking
		if (value == null)
			throw new IllegalArgumentException("The value parameter was null.");
		if (gson == null)
			throw new IllegalArgumentException("The gson parameter was null.");

		DbArtifact artifact = new DbArtifact();
		// Check if we have a collection or a map
		if (Collection.class.isAssignableFrom(value.getClass()))
			artifact.type = ArtifactType.COLLECTION;
		else if (Map.class.isAssignableFrom(value.getClass()))
			artifact.type = ArtifactType.MAP;
		else
			artifact.type = ArtifactType.BASIC;
		// Get the inner-most class name
		Object firstElement;
		switch (artifact.type) {
		case COLLECTION:
			firstElement = ((Collection<?>) value).iterator().next();
			if (firstElement == null)
				artifact.clazz = Object.class.getName();
			else
				artifact.clazz = firstElement.getClass().getName();
			break;
		case MAP:
			firstElement = ((Map<?, ?>) value).entrySet().iterator().next().getValue();
			if (firstElement == null)
				artifact.clazz = Object.class.getName();
			else
				artifact.clazz = firstElement.getClass().getName();
			break;
		default:
			artifact.clazz = value.getClass().getName();
		}
		// JSON serialization time
		// TODO we need a special deserialization strategy for maps
		artifact.payload = gson.toJson(value);
		
		// We're done here
		return artifact;
	}
}
