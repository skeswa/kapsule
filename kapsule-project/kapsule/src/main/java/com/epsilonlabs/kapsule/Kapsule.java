package com.epsilonlabs.kapsule;

import android.content.Context;

import com.epsilonlabs.kapsule.Db.Fetch;
import com.epsilonlabs.kapsule.Db.Insert;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class Kapsule {
    /*************************************************************************/
    /**************************** STATIC SECTION *****************************/
    /*************************************************************************/

    // The id of the default kapsule
    private static final String DEFAULT_KAPSULE_ID = "thisisthedefaultkapsule";

    // For serialization and deserialization of objects
    private static final Gson gson = new Gson();
    // The last specified android context for queries
    private static Context context;

    // EXTERNAL STATIC METHODS

    public static void context(Context context) {
        // Parameter checking
        if (context == null)
            throw new IllegalArgumentException("The context parameter was null.");

        Kapsule.context = context;
    }

    public static Put put(final String key, Object value) {
        // Parameter checking
        if (key == null)
            throw new IllegalArgumentException("The key parameter was null.");
        if (value == null)
            throw new IllegalArgumentException("The value parameter was null.");

        // Internal state checking
        if (context == null)
            throw new UndefinedContextException();

        // Create the db artifact, stringify it
        DbArtifact artifact = DbArtifact.create(value, gson);
        final String artifactJson = gson.toJson(artifact);
        // Create the query objects
        final Put query = new Put();
        query.ready(new Ready() {
            @Override
            public void ready() {
                String kapsuleId = query.kapsule();
                if (kapsuleId == null)
                    kapsuleId = DEFAULT_KAPSULE_ID;

                // Execute the db query
                new Insert(context, toTableName(kapsuleId)) {

                    @Override
                    void callback(Throwable problem) {
                        if (problem == null)
                            query.doSuccess();
                        else
                            query.doFailure(problem);
                    }
                }.execute(key, artifactJson);
            }
        });
        // Give the query callback object back
        return query;
    }

    public static <T> Get<T> get(final String key, final Class<T> type) {
        // Parameter checking
        if (key == null)
            throw new IllegalArgumentException("The key parameter was null.");
        if (type == null)
            throw new IllegalArgumentException("The type parameter was null.");

        // Internal state checking
        if (context == null)
            throw new UndefinedContextException();

        // Create the query objects
        final Get<T> query = new Get<T>();
        query.ready(new Ready() {
            @Override
            public void ready() {
                String kapsuleId = query.kapsule();
                if (kapsuleId == null)
                    kapsuleId = DEFAULT_KAPSULE_ID;
                // Execute the db query
                new Fetch(context, toTableName(kapsuleId)) {

                    @Override
                    void callback(Throwable problem, String result) {
                        if (problem != null)
                            query.doFailure(problem);
                        else {
                            try {
                                if (query.overridden()) {
                                    if (result == null) {
                                        query.doOverriddenSuccess(null);
                                        return;
                                    }
                                    // Interpret as a collection
                                    DbArtifact artifact = gson.fromJson(result, DbArtifact.class);
                                    List<T> payload = gson.fromJson(artifact.payload, new TypeToken<List<T>>() {}.getType());
                                    query.doOverriddenSuccess(payload);
                                } else {
                                    if (result == null) {
                                        query.doSuccess(null);
                                        return;
                                    }
                                    // Interpret as element
                                    DbArtifact artifact = gson.fromJson(result, DbArtifact.class);
                                    T payload = gson.fromJson(artifact.payload, type);
                                    query.doSuccess(payload);
                                }
                            } catch (JsonSyntaxException e) {
                                // TODO this needs an exception wrapper
                                query.doFailure(e);
                            } catch (Throwable e) {
                                query.doFailure(e);
                            }
                        }
                    }
                }.execute(key);
            }
        });
        // Give the query callback object back
        return query;
    }

    // INTERNAL STATIC METHODS

    private static String toTableName(String kapsuleId) {
        // TODO make this more sophisticated
        return kapsuleId.hashCode() + "";
    }

}
