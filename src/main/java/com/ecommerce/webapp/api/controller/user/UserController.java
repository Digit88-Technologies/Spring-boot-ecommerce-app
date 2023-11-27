package com.ecommerce.webapp.api.controller.user;

import com.ecommerce.webapp.api.model.MobileOTPRequestDto;
import com.ecommerce.webapp.model.Address;
import com.ecommerce.webapp.model.LocalUser;
import com.ecommerce.webapp.model.dao.AddressDAO;
import com.ecommerce.webapp.model.dao.LocalUserDAO;
import com.ecommerce.webapp.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {

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
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
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
    @PutMapping("/{userId}/address")
    public ResponseEntity<Address> putAddress(
            @AuthenticationPrincipal LocalUser user, @PathVariable Long userId,
            @RequestBody Address address) {
        if (!userService.userHasPermissionToUser(user, userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        address.setId(null);
        LocalUser refUser = new LocalUser();
        refUser.setId(userId);
        address.setUser(refUser);
        Address savedAddress = addressDAO.save(address);

        return ResponseEntity.ok(savedAddress);
    }

    @PutMapping("/update/contact")
    public ResponseEntity<LocalUser> putContactDetails(
            @AuthenticationPrincipal LocalUser user,
            @RequestBody MobileOTPRequestDto requestDto) {
        if (!userService.userHasPermissionToUserByUserName(user, requestDto.getUserName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        user.setPhoneNumber(requestDto.getPhoneNumber());
        LocalUser savedUser = localUserDAO.save(user);

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
    @PatchMapping("/{userId}/address/{addressId}")
    public ResponseEntity<Address> patchAddress(
            @AuthenticationPrincipal LocalUser user, @PathVariable Long userId,
            @PathVariable Long addressId, @RequestBody Address address) {
        if (!userService.userHasPermissionToUser(user, userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        if (address.getId() == addressId) {
            Optional<Address> opOriginalAddress = addressDAO.findById(addressId);
            if (opOriginalAddress.isPresent()) {
                LocalUser originalUser = opOriginalAddress.get().getUser();
                if (originalUser.getId() == userId) {
                    address.setUser(originalUser);
                    Address savedAddress = addressDAO.save(address);
                    return ResponseEntity.ok(savedAddress);
                }
            }
        }
        return ResponseEntity.badRequest().build();
    }

}
