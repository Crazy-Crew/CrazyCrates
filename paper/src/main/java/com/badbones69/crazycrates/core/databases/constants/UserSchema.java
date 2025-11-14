package com.badbones69.crazycrates.core.databases.constants;

public class UserSchema {

    public static String create_users_table = "create table if not exists users(uuid varchar(36) primary key)";

    public static String create_keys_table = "create table if not exists keys(crate varchar(8) primary key, uuid varchar(36), amount int, foreign key (uuid) references users(uuid) on delete cascade)";

}