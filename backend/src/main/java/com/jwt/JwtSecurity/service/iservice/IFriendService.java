package com.jwt.JwtSecurity.service.iservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jwt.JwtSecurity.dto.request.FriendRequest;
import com.jwt.JwtSecurity.dto.response.FriendResponse;

import java.util.List;

public interface IFriendService {

    public FriendResponse addFriendInfo(FriendRequest friendRequest);

    public List<FriendResponse> addFriendInfoInBulk(List<FriendRequest> friendRequest);

    public List<FriendResponse> fetchAllFriends() throws JsonProcessingException;

    public List<FriendResponse> searchFriends(String searchText) throws JsonProcessingException;

    public FriendResponse updateFriendInfo(Long friendId, FriendRequest friendRequest) throws JsonProcessingException;

    public FriendResponse deleteMyFriend(Long friendId) throws JsonProcessingException;
}
