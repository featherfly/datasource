create database ds_write;
create database ds_read;
create database ds_read1;
create database ds_read2;

CREATE TABLE `ds_write`.`user` (
  `ID` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID，由于此表数据不用上传，所以直接使用自动递增',
  `USERNAME` varchar(20) DEFAULT NULL COMMENT '用户名，登陆系统用，单一数据库保证唯一',
  `PASSWORD` varchar(36) DEFAULT NULL COMMENT '密码，保存加密后的密码',
  `MOBILE_NO` varchar(11) DEFAULT NULL COMMENT '手机号码',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `USER_NAME_UQ` (`USERNAME`),
  UNIQUE KEY `MOBILE_NO_UQ` (`MOBILE_NO`)
) ENGINE=InnoDB AUTO_INCREMENT=1000000 DEFAULT CHARSET=utf8 COMMENT='系统用户';

CREATE TABLE `ds_read`.`user` (
  `ID` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID，由于此表数据不用上传，所以直接使用自动递增',
  `USERNAME` varchar(20) DEFAULT NULL COMMENT '用户名，登陆系统用，单一数据库保证唯一',
  `PASSWORD` varchar(36) DEFAULT NULL COMMENT '密码，保存加密后的密码',
  `MOBILE_NO` varchar(11) DEFAULT NULL COMMENT '手机号码',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `USER_NAME_UQ` (`USERNAME`),
  UNIQUE KEY `MOBILE_NO_UQ` (`MOBILE_NO`)
) ENGINE=InnoDB AUTO_INCREMENT=1000000 DEFAULT CHARSET=utf8 COMMENT='系统用户';

CREATE TABLE `ds_read1`.`user` (
  `ID` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID，由于此表数据不用上传，所以直接使用自动递增',
  `USERNAME` varchar(20) DEFAULT NULL COMMENT '用户名，登陆系统用，单一数据库保证唯一',
  `PASSWORD` varchar(36) DEFAULT NULL COMMENT '密码，保存加密后的密码',
  `MOBILE_NO` varchar(11) DEFAULT NULL COMMENT '手机号码',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `USER_NAME_UQ` (`USERNAME`),
  UNIQUE KEY `MOBILE_NO_UQ` (`MOBILE_NO`)
) ENGINE=InnoDB AUTO_INCREMENT=1000000 DEFAULT CHARSET=utf8 COMMENT='系统用户';

CREATE TABLE `ds_read2`.`user` (
  `ID` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID，由于此表数据不用上传，所以直接使用自动递增',
  `USERNAME` varchar(20) DEFAULT NULL COMMENT '用户名，登陆系统用，单一数据库保证唯一',
  `PASSWORD` varchar(36) DEFAULT NULL COMMENT '密码，保存加密后的密码',
  `MOBILE_NO` varchar(11) DEFAULT NULL COMMENT '手机号码',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `USER_NAME_UQ` (`USERNAME`),
  UNIQUE KEY `MOBILE_NO_UQ` (`MOBILE_NO`)
) ENGINE=InnoDB AUTO_INCREMENT=1000000 DEFAULT CHARSET=utf8 COMMENT='系统用户';

INSERT INTO `ds_write`.`user` (`ID`, `USERNAME`, `PASSWORD`, `MOBILE_NO`) VALUES ('1000001', "name1_w", NULL, '77395486373');
INSERT INTO `ds_write`.`user` (`ID`, `USERNAME`, `PASSWORD`, `MOBILE_NO`) VALUES ('1000002', "name2_w", NULL, '19268041238');
INSERT INTO `ds_write`.`user` (`ID`, `USERNAME`, `PASSWORD`, `MOBILE_NO`) VALUES ('1000003', "name3_w", NULL, '60293186098');

INSERT INTO `ds_read`.`user` (`ID`, `USERNAME`, `PASSWORD`, `MOBILE_NO`) VALUES ('1000001', "name1_r", NULL, '77395486373');
INSERT INTO `ds_read`.`user` (`ID`, `USERNAME`, `PASSWORD`, `MOBILE_NO`) VALUES ('1000002', "name2_r", NULL, '19268041238');
INSERT INTO `ds_read`.`user` (`ID`, `USERNAME`, `PASSWORD`, `MOBILE_NO`) VALUES ('1000003', "name3_r", NULL, '60293186098');

INSERT INTO `ds_read1`.`user` (`ID`, `USERNAME`, `PASSWORD`, `MOBILE_NO`) VALUES ('1000001', "name1_r", NULL, '77395486373');
INSERT INTO `ds_read1`.`user` (`ID`, `USERNAME`, `PASSWORD`, `MOBILE_NO`) VALUES ('1000002', "name2_r", NULL, '19268041238');
INSERT INTO `ds_read1`.`user` (`ID`, `USERNAME`, `PASSWORD`, `MOBILE_NO`) VALUES ('1000003', "name3_r", NULL, '60293186098');

INSERT INTO `ds_read2`.`user` (`ID`, `USERNAME`, `PASSWORD`, `MOBILE_NO`) VALUES ('1000001', "name1_r", NULL, '77395486373');
INSERT INTO `ds_read2`.`user` (`ID`, `USERNAME`, `PASSWORD`, `MOBILE_NO`) VALUES ('1000002', "name2_r", NULL, '19268041238');
INSERT INTO `ds_read2`.`user` (`ID`, `USERNAME`, `PASSWORD`, `MOBILE_NO`) VALUES ('1000003', "name3_r", NULL, '60293186098');