package com.ecommerce.webapp.controller.user;

import com.ecommerce.webapp.dto.MobileOTPRequestDto;
import com.ecommerce.webapp.model.Address;
import com.ecommerce.webapp.model.LocalUser;
import com.ecommerce.webapp.repository.AddressDAO;
import com.ecommerce.webapp.repository.LocalUserDAO;
import com.ecommerce.webapp.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    public static final String PATCH_ADDRESS = "/{userId}/address/{addressId}";
    public static final String UPDATE_CONTACT = "/update/contact";
    public static final String USER_ID_ADDRESS = "/{userId}/address";
    private AddressDAO addressDAO;

    private LocalUserDAO localUserDAO;
    private UserService userService;

    public UserController(AddressDAO addressDAO, LocalUserDAO localUserDAO, UserService userService) {
        this.addressDAO = addressDAO;
        this.localUserDAO = localUserDAO;
        this.userService = userService;
    }

    /**
     * Gets all addresses for the given user and presents them.
     *
     * @param user   The authenticated user account.
     * @param userId The user ID to get the addresses of.
     * @return The list of addresses.
     */
    @GetMapping("/{userId}/address")
    public ResponseEntity<List<Address>> getAddress(
            @AuthenticationPrincipal LocalUser user, @PathVariable Long userId) {

        if (!userService.userHasPermissionToUser(user, userId)) {
            log.info("User does not have permission!");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        log.info("Fetching addresses for current user");
        return ResponseEntity.ok(addressDAO.findByUser_Id(userId));
    }

    /**
     * Allows the user to add a new address.
     *
     * @param user    The authenticated user.
     * @param userId  The user id for the new address.
     * @param address The Address to be added.
     * @return The saved address.
     */
    @PutMapping(USER_ID_ADDRESS)
    public ResponseEntity<Address> putAddress(
            @AuthenticationPrincipal LocalUser user, @PathVariable Long userId,
            @RequestBody Address address) {
        if (!userService.userHasPermissionToUser(user, userId)) {
            log.info("User does not have permission!");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        address.setId(null);
        LocalUser refUser = new LocalUser();
        refUser.setId(userId);
        address.setUser(refUser);
        Address savedAddress = addressDAO.save(address);

        log.info("Address saved successfully for user " + user.getUsername());
        return ResponseEntity.ok(savedAddress);
    }

    @PutMapping(UPDATE_CONTACT)
    public ResponseEntity<LocalUser> putContactDetails(
            @AuthenticationPrincipal LocalUser user,
            @RequestBody MobileOTPRequestDto requestDto) {
        if (!userService.userHasPermissionToUserByUserName(user, requestDto.getUserName())) {
            log.info("User does not have permission!");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        user.setPhoneNumber(requestDto.getPhoneNumber());
        LocalUser savedUser = localUserDAO.save(user);

        log.info("Contact saved successfully for user " + user.getUsername());
        return ResponseEntity.ok(savedUser);
    }

    /**
     * Updates the given address.
     *
     * @param user      The authenticated user.
     * @param userId    The user ID the address belongs to.
     * @param addressId The address ID to alter.
     * @param address   The updated address object.
     * @return The saved address object.
     */
    @PatchMapping(PATCH_ADDRESS)
    public ResponseEntity<Address> patchAddress(
            @AuthenticationPrincipal LocalUser user, @PathVariable Long userId,
            @PathVariable Long addressId, @RequestBody Address address) {
        if (!userService.userHasPermissionToUser(user, userId)) {
            log.info("User does not have permission!");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        if (address.getId() == addressId) {
            Optional<Address> opOriginalAddress = addressDAO.findById(addressId);
            if (opOriginalAddress.isPresent()) {
                LocalUser originalUser = opOriginalAddress.get().getUser();
                if (originalUser.getId() == userId) {
                    address.setUser(originalUser);
                    Address savedAddress = addressDAO.save(address);
                    log.info("Address updated successfully : " + savedAddress);
                    return ResponseEntity.ok(savedAddress);
                }
            }
        }
        return ResponseEntity.badRequest().build();
    }

}
