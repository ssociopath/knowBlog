package org.whutosa.blog.data.entity;

import lombok.*;
import org.whutosa.blog.data.bo.valid.UserEditValidGroup;
import org.whutosa.blog.data.bo.valid.UserLoginValidGroup;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * @author bobo
 * @date 2021/3/31
 */

@Data
@Entity
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NotNull(groups = {UserLoginValidGroup.class, UserEditValidGroup.class})
    private String account;
    @NotNull(groups = {UserLoginValidGroup.class, UserEditValidGroup.class})
    private String password;
    @NotNull(groups = {UserEditValidGroup.class})
    private String username;
    @NotNull(groups = {UserEditValidGroup.class})
    private Integer roleId;
    private String userFace;
    private Timestamp dateRegister;

    public User(String account, String password, String username, String dateRegister) {
        this.account = account;
        this.password = password;
        this.username = username;
        try {
            this.dateRegister = new Timestamp(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateRegister).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public String getDateRegister() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dateRegister);
    }
}
