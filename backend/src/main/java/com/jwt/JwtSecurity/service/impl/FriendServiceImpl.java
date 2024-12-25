package com.jwt.JwtSecurity.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jwt.JwtSecurity.dto.request.FriendRequest;
import com.jwt.JwtSecurity.dto.response.FriendResponse;
import com.jwt.JwtSecurity.exception.NotFoundException;
import com.jwt.JwtSecurity.model.MyFriendsInfo;
import com.jwt.JwtSecurity.repository.MyFriendsRepo;
import com.jwt.JwtSecurity.service.iservice.IFriendService;
import com.jwt.JwtSecurity.utils.CacheConstants;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
public class FriendServiceImpl implements IFriendService {

    @Autowired
    MyFriendsRepo friendsRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    CacheService<MyFriendsInfo> cacheService;

    @Override
    public FriendResponse addFriendInfo(FriendRequest friendRequest) {

        modelMapper.typeMap(FriendRequest.class, MyFriendsInfo.class)
                .addMappings(mapper -> mapper.skip(MyFriendsInfo::setId))
                .addMappings(mapper -> mapper.skip(MyFriendsInfo::setDeleteStatus));

        MyFriendsInfo friendsInfo = modelMapper.map(friendRequest, MyFriendsInfo.class);
        friendsInfo.setConnectedFrom(LocalDate.now());
        friendsInfo.setDeleteStatus(false);
        MyFriendsInfo friendsInfoFromDb = friendsRepo.save(friendsInfo);
        cacheService.save(CacheConstants.FRIEND_KEY, friendsInfoFromDb, MyFriendsInfo.class);
        return modelMapper.map(friendsInfo, FriendResponse.class);

    }

    @Override
    public List<FriendResponse> addFriendInfoInBulk(List<FriendRequest> friendRequests) {

        modelMapper.typeMap(FriendRequest.class, MyFriendsInfo.class)
                .addMappings(mapper -> mapper.skip(MyFriendsInfo::setId));

        List<MyFriendsInfo> friendsInfos = friendRequests.stream()
                .map(friendRequest -> modelMapper.map(friendRequest, MyFriendsInfo.class))
                .toList();

        List<MyFriendsInfo> friendsInfoFromDb = friendsRepo.saveAll(friendsInfos);
        cacheService.saveInBulk(CacheConstants.FRIEND_KEY, friendsInfoFromDb, MyFriendsInfo.class);

        List<FriendResponse> response = friendsInfoFromDb.stream()
                .map(friendsInfo -> modelMapper.map(friendsInfo, FriendResponse.class))
                .toList();


        return response;
    }

    @Override
    public List<FriendResponse> fetchAllFriends() throws JsonProcessingException {
        List<MyFriendsInfo> allFriends = cacheService.getAll(CacheConstants.FRIEND_KEY, MyFriendsInfo.class);
        if (allFriends == null)
            return friendsRepo.findAll().stream().map(friendsInfo -> modelMapper.map(friendsInfo, FriendResponse.class)).toList();
        return allFriends.stream().map(friendsInfo -> modelMapper.map(friendsInfo, FriendResponse.class)).toList();
    }

    @Override
    public List<FriendResponse> searchFriends(String searchText) throws JsonProcessingException {

        List<MyFriendsInfo> allFriends = cacheService.getAll(CacheConstants.FRIEND_KEY, MyFriendsInfo.class);
        if (allFriends == null)
            return null;

        return allFriends.stream()
                .filter(friendsInfo -> matchesSearch(friendsInfo, searchText))
                .map(friendsInfo -> modelMapper.map(friendsInfo, FriendResponse.class))
                .toList();
    }

    private boolean matchesSearch(MyFriendsInfo friendsInfo, String key) {
        return friendsInfo.getFriendName().equalsIgnoreCase(key) ||
                friendsInfo.getFriendsFrom().name().equalsIgnoreCase(key) ||
                friendsInfo.getCity().equalsIgnoreCase(key) ||
                friendsInfo.getEmailId().equalsIgnoreCase(key) ||
                friendsInfo.getMobileNumber().equalsIgnoreCase(key);
    }

    @Override
    public FriendResponse updateFriendInfo(Long friendId, FriendRequest friendRequest) throws JsonProcessingException {

        MyFriendsInfo friendsInfo = friendsRepo.findById(friendId).orElseThrow(() -> new NotFoundException("Friend Not Found!"));
        friendsInfo.setFriendName(friendRequest.getFriendName());
        friendsInfo.setCity(friendsInfo.getCity());
        friendsInfo.setEmailId(friendsInfo.getEmailId());
        friendsInfo.setFriendsFrom(friendsInfo.getFriendsFrom());
        friendsInfo.setMobileNumber(friendsInfo.getMobileNumber());

        List<MyFriendsInfo> friendsInfos = cacheService.getAll(CacheConstants.FRIEND_KEY, MyFriendsInfo.class);
        List<MyFriendsInfo> updatedFriendsInfos = friendsInfos.stream()
                .filter(Objects::nonNull)
                .peek(info -> {
                    if (info.getId() != null && info.getId().equals(friendId)) {
                        info.setFriendName(friendRequest.getFriendName());
                        info.setCity(friendRequest.getCity());
                        info.setEmailId(friendRequest.getEmailId());
                        info.setFriendsFrom(friendRequest.getFriendsFrom());
                        info.setMobileNumber(friendRequest.getMobileNumber());
                    }
                })
                .toList();
        cacheService.delete(CacheConstants.FRIEND_KEY);
        cacheService.saveInBulk(CacheConstants.FRIEND_KEY, updatedFriendsInfos, MyFriendsInfo.class);

        return modelMapper.map(friendsInfo, FriendResponse.class);
    }

    @Override
    public FriendResponse deleteMyFriend(Long friendId) throws JsonProcessingException {

        MyFriendsInfo friendsInfo = friendsRepo.findById(friendId).orElseThrow(() -> new NotFoundException("Friend Not Found!"));
        friendsInfo.setDeleteStatus(true);

        List<MyFriendsInfo> friendsInfos = cacheService.getAll(CacheConstants.FRIEND_KEY, MyFriendsInfo.class);
        List<MyFriendsInfo> updatedFriendsInfos = friendsInfos.stream()
                .filter(Objects::nonNull)
                .filter(friend -> friend.getId() != null && !friend.getId().equals(friendId))
                .toList();

        friendsRepo.save(friendsInfo);
        cacheService.delete(CacheConstants.FRIEND_KEY);
        cacheService.saveInBulk(CacheConstants.FRIEND_KEY, updatedFriendsInfos, MyFriendsInfo.class);

        return modelMapper.map(friendsInfo, FriendResponse.class);
    }


}