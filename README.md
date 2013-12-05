kapsule
====================
kapsule is a simple android storage utility that makes unsophisticated local datastore use a breeze. It brings the feel of a map to database interaction on Android that seems more intuitive to beginners. Its pretty darned simple.

Say you have a session object for your Android app that stores some basic information:

  ```java
  public class MySesh {
      private String userName;
      private long sessionId;
      
      public MySesh() {}
      public MySesh(String userName, long sessionId) {
          this.sessionId = sessionId;
          this.userName = userName;
      }
      
      // Some getters...
      // Some setters...
  }
  ```
    
Storing a session object in a kapsule is as easy as:

  ```java
  // We have to establish the context first
  Kapsule.context(getContext());
  // Now we can execute the query
  Kapsule.put("da session", new MySesh("jdoe", 1337)).then(new Put.Callback() {
      @Override
      public void success() {
          Log.d("test", "car1 put() successfully");
      }
  });
  ```
    
or if you want to do it synchronously:

  ```java
  Kapsule.put("da session", new MySesh("jdoe", 1337)).synchronously();
  ```
    
As you can tell, the inline simplicity of the query makes database interaction trivial. You can even have different kapsules for different groups of information. For instance, instead of having a session class, I could have a kapsule called "session" and put the same fields into it - observe:

  ```java
  Kapsule.put("userName", "jdoe").into("session").synchronously();
  Kapsule.put("sessionId", 1337).into("session").synchronously();
  ```
    
Getting your data back is equally trivial:

  ```java
  String userName = Kapsule.get("userName", String.class).from("session").synchronously();
  // Lets do it asynchronously this time
  Kapsule.get("sessionId", Long.class).from("session").then(new Get.Callback<Long>() {
      @Override
      public void success(Long sessionId) {
          // I got the sessionId back!
      }
  
      @Override
      public void failure(Throwable e) {
          // In the unlikely case that there was a problem, it goes here
          e.printStackTrace();
      }
  });
  // Same goes for our custom MySesh session
  MySesh sesh = Kapsule.get("da session", MySesh.class).synchronously();
  ```
    

