package com.lbu.lbuauth.services;

import com.lbu.lbuauth.dtos.JWTTokenDto;
import com.lbu.lbuauth.models.User;

public interface UserService {

    /**
     * This method serves as the entry point for creating a new user within the system.
     * <p>
     * It expects a user object containing all the necessary details required for user creation,
     * such as username, password, email, etc.
     * <p>
     * The method first validates the provided user object to ensure that all mandatory fields are present
     * and that they meet the required criteria (e.g., username is unique, password meets complexity requirements).
     * If the validation fails, the method throws an exception indicating the validation error,
     * and the user creation process is aborted.
     * <p>
     * Assuming the validation passes, the method proceeds to create a new user record in the database.
     * This involves persisting the user details provided in the user object into the underlying data store,
     * typically a relational database or some other persistent storage mechanism.
     * <p>
     * Upon successful creation of the user record, the method constructs a new User object representing the
     * newly created user and returns it to the caller. This object contains attributes such as
     * user ID, username, email, creation timestamp, etc.
     * <p>
     * Additionally, the method may also handle other aspects related to user creation,
     * such as logging relevant information triggering notifications to other parts of the system.
     * <p>
     * Overall, this method encapsulates the logic for creating a new user in the system
     * and provides a clear interface for external components to interact with the user creation functionality.
     * It ensures that the user creation process is executed reliably and consistently,
     * following the specified business rules and validation criteria.
     *
     * @param user This object includes attributes such as username, password, email, etc.
     * @return This object includes attributes such as user ID, username, email, creation timestamp, etc.
     */
    User createUser(User user);

    /**
     * The function first verifies the user credentials provided (e.g., username and password)
     * against the stored user data in the system.
     * <p>
     * If the credentials are valid, it proceeds to create a JWT token using a secure algorithm for signing
     * with using a secret key known only to the server, ensuring its authenticity and integrity.
     * <p>
     * Once the JWT token is generated, it includes the user ID and any additional claims or metadata required
     * for authorization purposes.
     * <p>
     * Finally, the function returns the JWT token along with the user ID, allowing the client application
     * to securely authenticate the user for subsequent API calls.
     * It's important to note that JWT tokens have an expiration time, after which they are no longer valid,
     * adding a layer of security to the authentication process.
     *
     * @param user An object containing user details such as username, password, and any other relevant
     *             information needed for authentication.
     * @return An object containing the generated JWT token and the user ID. The JWT token is used for
     * authenticating the user in subsequent requests, while the user ID provides a reference to the authenticated user.
     */
    JWTTokenDto generateLoginToken(User user, boolean isCheckPassword);

    /**
     * Getting user details for the given user id
     *
     * @param userId    user id
     * @param authToken
     * @return user detail object
     */
    User getUserByUserId(String userId, String authToken);

    void activateAccount(String token);

    void reSendActivateToken(String userId);

    void validateToken(String token);

    User updateUserRole(String userId, String authToken);
}
