package org.unidal.codegen.generator.model.dobby;

public enum TicketState {
   CREATED(1, 2, 9),

   ASSIGNED(2, 3, 4, 9),

   ACCEPTED(3, 2, 4, 9),

   RESOLVED(4, 2, 4, 9),

   IGNORED(9);

   public static TicketState getByName(String name, TicketState defaultState) {
      for (TicketState state : values()) {
         if (state.name().equals(name)) {
            return state;
         }
      }

      return defaultState;
   }

   private int m_id;

   private int[] m_nextIds;

   private TicketState(int id, int... nextIds) {
      m_id = id;
      m_nextIds = nextIds;
   }

   public int getId() {
      return m_id;
   }

   public boolean canMoveTo(TicketState nextState) {
      int id = nextState.getId();

      for (int nextId : m_nextIds) {
         if (id == nextId) {
            return true;
         }
      }

      return false;
   }
}
