package com.epsilonlabs.kapsule;

import android.content.Context;
import android.os.AsyncTask;

import java.util.HashMap;

public class Db {
    private static HashMap<String, HashMap<String, String>> fakeDb = new HashMap<String, HashMap<String, String>>();

    abstract static class Insert extends AsyncTask<String, Void, Void> {
        private Context context;
        private String tableName;
        private boolean calledBack = false;

        Insert(Context context, String tableName) {
            if (context == null)
                throw new IllegalArgumentException("The context parameter was null.");
            if (tableName == null)
                throw new IllegalArgumentException("The table name parameter was null.");

            this.context = context;
            this.tableName = tableName;
        }

        @Override
        protected Void doInBackground(String... params) {
            try {
                // Parameter checking
                if (params.length != 2)
                    throw new IllegalArgumentException(
                            "Two parameters are required for insertions - the key and the value.");
                String key = params[0];
                String value = params[1];
                if (key == null)
                    throw new IllegalArgumentException("The key parameter was null.");
                if (value == null)
                    throw new IllegalArgumentException("The value parameter was null.");

                // TODO make this work for the actual database
                this.context.getClass(); // FIXME To make eclipse shut up
                // Ensure the table exists
                HashMap<String, String> table = fakeDb.get(this.tableName);
                if (table == null) {
                    fakeDb.put(this.tableName, table = new HashMap<String, String>());
                }
                // Insert the entry
                table.put(key, value);

                // Record the transaction
                // TODO add logging here
            } catch (Throwable e) {
                callback(e);
                this.calledBack = true;
            }

            // We don't actually need to return anything
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (!this.calledBack) {
                callback(null);
            }
        }

        abstract void callback(Throwable problem);
    }

    abstract static class Fetch extends AsyncTask<String, Void, String> {
        private Context context;
        private String tableName;
        private boolean calledBack = false;

        Fetch(Context context, String tableName) {
            if (context == null)
                throw new IllegalArgumentException("The context parameter was null.");
            if (tableName == null)
                throw new IllegalArgumentException("The table name parameter was null.");

            this.context = context;
            this.tableName = tableName;
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                // Parameter checking
                if (params.length != 1)
                    throw new IllegalArgumentException("One parameter is required for fetches, the key.");
                String key = params[0];
                if (key == null)
                    throw new IllegalArgumentException("The key parameter was null.");

                // TODO make this work for the actual database
                this.context.getClass(); // FIXME To make eclipse shut up
                // Ensure the table exists
                HashMap<String, String> table = fakeDb.get(this.tableName);
                if (table == null) {
                    fakeDb.put(this.tableName, table = new HashMap<String, String>());
                }
                // Insert the entry
                return table.get(key);
            } catch (Throwable e) {
                // TODO add logging here
                callback(e, null);
                this.calledBack = true;
            }
            // We don't actually need to return anything
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (!this.calledBack) {
                callback(null, result);
            }
        }

        abstract void callback(Throwable problem, String result);
    }
}
