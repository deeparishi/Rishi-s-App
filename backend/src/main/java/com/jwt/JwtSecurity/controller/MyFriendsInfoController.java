package com.jwt.JwtSecurity.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jwt.JwtSecurity.config.annotation.RequestRateLimiter;
import com.jwt.JwtSecurity.dto.request.FriendRequest;
import com.jwt.JwtSecurity.dto.response.FriendResponse;
import com.jwt.JwtSecurity.dto.response.GenericResponse;
import com.jwt.JwtSecurity.service.iservice.IFriendService;
import com.jwt.JwtSecurity.utils.AppMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/friends-info")
public class MyFriendsInfoController {

    @Autowired
    IFriendService friendService;

    @PostMapping("/add-friends")
    public ResponseEntity<GenericResponse<FriendResponse>> addMyFriends(@RequestBody FriendRequest friendRequest) {
        FriendResponse friendResponse = friendService.addFriendInfo(friendRequest);
        return ResponseEntity.ok(GenericResponse.success(AppMessages.SUCCESS_MESSAGE, friendResponse));
    }

    @PostMapping("/add-all-friends")
    public ResponseEntity<GenericResponse<FriendResponse>> addMyFriendsInBulkMode(@RequestBody List<FriendRequest> friendRequest) {
        List<FriendResponse> friendResponse = friendService.addFriendInfoInBulk(friendRequest);
        return ResponseEntity.ok(GenericResponse.success(AppMessages.SUCCESS_MESSAGE, friendResponse));
    }

    @GetMapping("/fetch-all-friends")
    @RequestRateLimiter(endpoint = "fetch-all-friends", limit = 10)
    public ResponseEntity<GenericResponse<?>> fetchMyFriends() throws JsonProcessingException {
        List<FriendResponse> friendResponse = friendService.fetchAllFriends();
        return ResponseEntity.ok(GenericResponse.success(AppMessages.SUCCESS_MESSAGE, friendResponse));
    }

    @GetMapping("/search-friends/{friendId}")
    @RequestRateLimiter(endpoint = "fetch-all-friends")
    public ResponseEntity<GenericResponse<FriendResponse>> searchFriends(@PathVariable String friendId) throws JsonProcessingException {
        List<FriendResponse> friendResponse = friendService.searchFriends(friendId);
        return ResponseEntity.ok(GenericResponse.success(AppMessages.SUCCESS_MESSAGE, friendResponse));
    }


    @PutMapping("/update-friends-info/{friendId}")
    public ResponseEntity<GenericResponse<FriendResponse>> updateFriendInfo(@PathVariable Long friendId, @RequestBody FriendRequest friendRequest) throws JsonProcessingException {
        FriendResponse friendResponse = friendService.updateFriendInfo(friendId, friendRequest);
        return ResponseEntity.ok(GenericResponse.success(AppMessages.SUCCESS_MESSAGE, friendResponse));
    }

    @DeleteMapping("delete-my-friend/{id}")
    public ResponseEntity<GenericResponse<FriendResponse>> deleteMyFriend(@PathVariable Long id) throws JsonProcessingException {
        FriendResponse friendResponse = friendService.deleteMyFriend(id);
        return ResponseEntity.ok(GenericResponse.success(AppMessages.SUCCESS_MESSAGE, friendResponse));
    }

}