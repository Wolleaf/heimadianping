package com.hmdp.convert;

import com.hmdp.domain.doc.UserDoc;
import com.hmdp.domain.dto.UserDTO;
import com.hmdp.domain.dto.UserInfoDTO;
import com.hmdp.domain.entity.User;
import com.hmdp.domain.entity.UserInfo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserConverter {

    UserInfoDTO userInfo2UserInfoDTO(UserInfo userInfo);

    UserDTO user2UserDTO(User user);

    UserDoc user2UserDoc(User user);
}
