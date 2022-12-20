package com.ol4juwon.blog.domain.mapper;

import java.util.List;

import org.bson.types.ObjectId;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import com.ol4juwon.blog.domain.dto.UserView;
import com.ol4juwon.blog.domain.model.User;
import com.ol4juwon.blog.repository.UserRepo;

@Mapper(componentModel = "spring", uses = {ObjectIdMapper.class})
public abstract class UserViewMapper {
    @Autowired
    private UserRepo userRepo;
  
    public abstract UserView toUserView(User user);
  
    public abstract List<UserView> toUserView(List<User> users);
  
    public UserView toUserViewById(ObjectId id) {
      if (id == null) {
        return null;
      }
      return toUserView(userRepo.getById(id));
    }
}
