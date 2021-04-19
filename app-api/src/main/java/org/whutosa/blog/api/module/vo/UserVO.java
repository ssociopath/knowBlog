package org.whutosa.blog.api.module.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.whutosa.blog.data.entity.User;

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
