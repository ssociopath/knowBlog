package org.whutosa.blog.api.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.whutosa.blog.data.dto.valid.UserEditValidGroup;
import org.whutosa.blog.data.dto.valid.UserLoginValidGroup;
import org.whutosa.blog.data.entity.User;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

/**
 * @author bobo
 * @date 2021/4/9
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserVO {
    private Integer id;
    private String account;
    private String username;
    private String userFace;
    private String dateRegister;

    public static UserVO fromUser(User user) {
        return UserVO.builder()
                .id(user.getId())
                .account(user.getAccount())
                .username(user.getUsername())
                .userFace(user.getUserFace())
                .dateRegister(user.getDateRegister())
                .build();
    }
}
