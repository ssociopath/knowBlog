create table user (
    id int primary key auto_increment,
    account varchar(20) not null unique,
    password varchar(80) not null,
    username varchar(20) not null,
    role_id int not null,
    user_face     varchar(255) null,
    date_register datetime not null
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

create table role (
    id int primary key auto_increment,
    name varchar(128) not null
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

create table permission (
    id int primary key auto_increment,
    code varchar(128) not null,
    name varchar(128) not null
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

create table role_permission (
    id int primary key auto_increment,
    permission_id int not null,
    role_id       int not null
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

create table moment (
    id int primary key auto_increment,
    title        varchar(255) not null,
    user_id      int          not null,
    md_content   text null,
    url_list     text null,
    publish_date datetime     not null,
    status       int          not null
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

create table tag (
    id int primary key auto_increment,
    name varchar(255) not null
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

create table moment_tag (
     id int primary key auto_increment,
     moment_id int not null,
     tag_id    int not null
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

insert into role values(null, 'custom');
insert into role values(null, 'admin');

insert into permission values(null, 'user:view', '查看用户');
insert into permission values(null, 'user:add', '新增用户');
insert into permission values(null, 'user:edit', '修改用户');

insert into role_permission values(null, 1, 2);
insert into role_permission values(null, 2, 2);
insert into role_permission values(null, 3, 1);
insert into role_permission values(null, 3, 2);