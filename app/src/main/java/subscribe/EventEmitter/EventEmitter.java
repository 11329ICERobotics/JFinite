package subscribe.EventEmitter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class EventEmitter<T extends Enum<T>> {

    protected T currentState;
    protected HashMap<T, List<EventHandler>> events = new HashMap<>();
    protected HashMap<T, HashMap<T, EventTransition>> transitions = new HashMap<>();

    /*
     * Allows a subsystem to register a method to be called when the <pre>EventEmitter</pre> transitions to state.
     * 
     * @param initialState The initial state for the <pre>EventEmitter</pre> to start in
     */
    public EventEmitter(T initialState) {
        currentState = initialState;
    }

    /*
     * Allows a subsystem to register a method to be called when the <pre>EventEmitter</pre> transitions to state.
     * 
     * @param  state The target state to wait for
     * @param callee The method to call on transition
     */
    public void register(T state, EventHandler callee) {
        if (events.get(state) == null)
            events.put(state, new ArrayList<>());

        events.get(state).add(callee);
    }

    /*
     * Allows the code to add a condition to describe a transition between two states.
     * 
     * @param activeState The state to wait for before checking <pre>condition</pre>
     * @param targetState The state to transition to when <pre>condition</pre> goes true
     * @param   condition The condition to check before transitioning to <pre>targetState</pre>
     */
    public void setTransition(
            T activeState,
            T targetState,
            EventTransition condition) {
        if (transitions.get(activeState) == null)
            transitions.put(
                    activeState,
                    new HashMap<>());

        transitions.get(activeState).put(targetState, condition);
    }

    /*
     * The function to periodically call in order to allow the <pre>EventEmitter</pre> to make transitions.
     */
    public void checkTransition() {
        HashMap<T, EventTransition> activeTransitions = transitions.get(currentState);

        Set<T> conditions = activeTransitions.keySet();

        for (T event : conditions) {
            if (activeTransitions.get(event).check()) {
                transitionTo(event);
                break;
            }
        }
    }

    private void transitionTo(T state) {
        currentState = state;

        if (events.get(state) != null) {
            for (EventHandler method : events.get(state)) {
                try {
                    method.call();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /*
     * Gets the current state of the <pre>EventEmitter</pre>
     * 
     * @returns The current state of the <pre>EventEmitter</pre>
     */
    public T getState() {
        return currentState;
    }
}
