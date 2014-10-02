public interface BlockingStack <T> {

    /**
     * Add a new object to the top of the stack. If there is no free space, the
     * call will block until space becomes available.
     *
     * @param object
     */
    void push(T object);

    /**
     * Remove the topmost object from the stack. If the stack is empty, the
     * call will block until objects are pushed onto the stack.
     *
     * @return
     */
    T pop();

    /**
     * Return the number of items in the stack: 0 <= size <= capacity
     *
     * @return
     */
    int size();
}