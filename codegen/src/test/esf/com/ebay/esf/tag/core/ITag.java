package com.ebay.esf.tag.core;

public interface ITag<T, M extends ITagModel> {
   /**
    * Build a component instance for this tag. The component could be an instance of resource, 
    * for example, ICssResource, IJsResource etc. It could be a DSF component, for example, 
    * DContent or DDiv etc.
    * 
    * Return null if there is no component built, that leads to render() method will be skipped.
    * 
    * @return an instance of component. Return null means no component is built.
    */
   public T build();

   /**
    * Be called when a tag ends.
    */
   public void end();

   public ITagEnv getEnv();

   /**
    * Return a data model of the tag instance.
    * 
    * @return data model of the tag instance.
    */
   public M getModel();

   public State getState();

   public void render(T component);

   /**
    * Inject an environment instance for this tag to live on.
    * 
    * @param env
    */
   public void setEnv(ITagEnv env);

   public void setState(State newState);

   /**
    * Be called when a tag starts.
    */
   public void start();

   public static enum State {
      CREATED(0, 1),

      STARTED(1, 2),

      BUILT(2, 3, 9),

      RENDERED(3, 9),

      ENDED(9, 1);

      private int m_id;

      private int[] m_nextStateIds;

      private State(int id, int... nextStates) {
         m_id = id;
         m_nextStateIds = nextStates;
      }

      public boolean canTransit(State nextState) {
         for (int id : m_nextStateIds) {
            if (id == nextState.getId()) {
               return true;
            }
         }

         return false;
      }

      public int getId() {
         return m_id;
      }

      public boolean isBuilt() {
         return this == BUILT;
      }

      public boolean isCreated() {
         return this == CREATED;
      }

      public boolean isEnded() {
         return this == ENDED;
      }

      public boolean isRendered() {
         return this == RENDERED;
      }

      public boolean isStarted() {
         return this == STARTED;
      }
   }
}
