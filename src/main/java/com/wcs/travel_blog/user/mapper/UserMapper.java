package com.wcs.travel_blog.user.mapper;

import com.wcs.travel_blog.user.dto.UpsertUserDTO;
import com.wcs.travel_blog.user.dto.UserDTO;
import com.wcs.travel_blog.user.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDTO converToDto(User user){
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());
        userDTO.setAvatar(user.getAvatar());
        userDTO.setBiography(user.getBiography());
        // Récupérer le contenu de l'énum
        userDTO.setStatus(user.getStatus().getLabel());

        return userDTO;
    }

    public User converToEntity(UpsertUserDTO userDTO){
        User user = new User();
        user.setId(userDTO.getId());
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setAvatar(userDTO.getAvatar());
        user.setBiography(userDTO.getBiography());
        // A voir si on garde le status provenant du front ?
        // user.setStatus(UserStatus.valueOf(userDTO.getStatus()));

        return user;
    }

}
