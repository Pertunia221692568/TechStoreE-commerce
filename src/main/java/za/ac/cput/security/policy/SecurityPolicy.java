package za.ac.cput.security.policy;

/**
 * Base contract for all security policies in TechStore.
 *
 * A policy answers a single question: "Is this action allowed for this user?"
 * This is the Policy design pattern applied to authorisation.
 *
 * Concrete policies (OrderOwnershipPolicy, AdminOnlyPolicy, etc.) implement
 * the actual business rules so that security logic stays out of controllers.
 */
public interface SecurityPolicy {

    /**
     * @return true if the current authenticated principal is allowed to perform the action
     */
    boolean isAllowed();
}
