# ************************************************************
# Sequel Pro SQL dump
# Version (null)
#
# https://www.sequelpro.com/
# https://github.com/sequelpro/sequelpro
#
# Host: 127.0.0.1 (MySQL 5.7.22-log)
# Database: groupchat
# Generation Time: 2020-03-21 12:23:14 +0000
# ************************************************************


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
SET NAMES utf8mb4;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


# Dump of table config
# ------------------------------------------------------------

DROP TABLE IF EXISTS `config`;

CREATE TABLE `config` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `keyword` varchar(120) DEFAULT NULL,
  `value` varchar(500) DEFAULT NULL,
  `remark` varchar(120) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `key_index` (`keyword`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='键值对配置表';

LOCK TABLES `config` WRITE;
/*!40000 ALTER TABLE `config` DISABLE KEYS */;

INSERT INTO `config` (`id`, `keyword`, `value`, `remark`)
VALUES
	(0,'ip_plan','1','0-无 1-白名单 2-黑名单'),
	(1,'chat_ws_host','ws://127.0.0.1','Netty服务主机'),
	(2,'chat_ws_port','9089','Netty服务端口'),
	(3,'chat_ws_uri','/groupchat','Websocket请求uri'),
	(4,'read_idle','300','读空闲-秒'),
	(5,'write_idle','600','写空闲-秒'),
	(6,'all_idle','7200','读写空闲-秒'),
	(7,'access_rate_limit','20','接口限流速率'),
	(8,'msg_delay_hour','24','消息最久延迟发送时间');

/*!40000 ALTER TABLE `config` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table group_relation
# ------------------------------------------------------------

DROP TABLE IF EXISTS `group_relation`;

CREATE TABLE `group_relation` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `gid` int(11) NOT NULL COMMENT '群标识',
  `uid` varchar(32) NOT NULL DEFAULT '' COMMENT '成员标识',
  `valid` tinyint(1) NOT NULL DEFAULT '1' COMMENT '关系0-解除1-建立',
  `updated` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '离群时间',
  `created` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '进群时间',
  PRIMARY KEY (`id`),
  KEY `gidx` (`gid`),
  KEY `uidx` (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='群与成员';

LOCK TABLES `group_relation` WRITE;
/*!40000 ALTER TABLE `group_relation` DISABLE KEYS */;

INSERT INTO `group_relation` (`id`, `gid`, `uid`, `valid`, `updated`, `created`)
VALUES
	(1,1,'0FB8E4CFBFAF4022C8996DE15ED2B5E8',1,NULL,'2020-03-11 18:21:14'),
	(2,1,'21232F297A57A5A743894A0E4A801FC3',0,'2020-03-15 20:28:43','2020-03-11 18:21:14'),
	(3,1,'248A9B3369371F8395399D6D3A686EE4',1,'2020-03-11 18:21:39','2020-03-11 18:21:14'),
	(4,1,'5996C4C3CECA2B93F9C0902547C245DB',1,'2020-03-11 18:21:44','2020-03-11 18:21:14'),
	(5,1,'7986E4397EF4A527121FF590646E0071',1,'2020-03-11 18:21:49','2020-03-11 18:21:14'),
	(6,2,'248A9B3369371F8395399D6D3A686EE4',1,NULL,'2020-03-15 19:42:15'),
	(7,2,'248A9B3369371F8395399D6D3A686E==',1,NULL,'2020-03-15 19:44:12');

/*!40000 ALTER TABLE `group_relation` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table groups
# ------------------------------------------------------------

DROP TABLE IF EXISTS `groups`;

CREATE TABLE `groups` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(120) NOT NULL DEFAULT '群聊' COMMENT '群名称',
  `avatar` varchar(256) DEFAULT 'img/avatar/group.jpg' COMMENT '群头像',
  `owner` varchar(32) DEFAULT NULL COMMENT '群主',
  `status` tinyint(2) NOT NULL DEFAULT '1' COMMENT '群状态-1废弃0禁用1正常',
  `updated` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `created` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `oidx` (`owner`),
  KEY `nidx` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='群聊室';

LOCK TABLES `groups` WRITE;
/*!40000 ALTER TABLE `groups` DISABLE KEYS */;

INSERT INTO `groups` (`id`, `name`, `avatar`, `owner`, `status`, `updated`, `created`)
VALUES
	(1,'群聊','img/avatar/group.jpg','248A9B3369371F8395399D6D3A686EE4',1,'2020-03-14 18:00:20','2020-03-11 18:19:15'),
	(2,'哈哈','img/avatar/group.jpg','248A9B3369371F8395399D6D3A686EE4',1,'2020-03-15 20:03:17','2020-03-15 19:40:47');

/*!40000 ALTER TABLE `groups` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table ip_black_list
# ------------------------------------------------------------

CREATE TABLE `ip_black_list` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `ipAddr` varchar(100) DEFAULT '',
  `valid` tinyint(1) NOT NULL DEFAULT '1',
  `updated` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `created` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ip_idx` (`ipAddr`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='IP黑名单';



# Dump of table ip_white_list
# ------------------------------------------------------------

CREATE TABLE `ip_white_list` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `ipAddr` varchar(100) DEFAULT '',
  `valid` tinyint(1) NOT NULL DEFAULT '1',
  `updated` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `created` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ip_idx` (`ipAddr`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='IP白名单';



# Dump of table msg_log_0
# ------------------------------------------------------------

CREATE TABLE `msg_log_0` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `mid` varchar(32) NOT NULL DEFAULT '' COMMENT '消息ID',
  `type` int(11) NOT NULL COMMENT '消息类型',
  `sourceUid` varchar(32) NOT NULL DEFAULT '' COMMENT '发送者',
  `targetUid` varchar(32) DEFAULT '' COMMENT '用户',
  `gid` int(11) DEFAULT NULL COMMENT '群标识',
  `content` text COMMENT '消息',
  `fileName` varchar(200) DEFAULT NULL COMMENT '文件名',
  `fileSize` varchar(20) DEFAULT NULL COMMENT '文件大小',
  `fileURL` varchar(256) DEFAULT NULL COMMENT '文件地址',
  `ipAddr` varchar(100) NOT NULL DEFAULT '' COMMENT 'IP地址',
  `time` bigint(20) NOT NULL COMMENT '消息的时间毫秒',
  `created` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `mid` (`mid`),
  KEY `uidx` (`targetUid`),
  KEY `gidx` (`gid`),
  KEY `ug_idx` (`targetUid`,`gid`),
  KEY `sourceUid` (`sourceUid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息记录表';



# Dump of table msg_log_1
# ------------------------------------------------------------

CREATE TABLE `msg_log_1` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `mid` varchar(32) NOT NULL DEFAULT '' COMMENT '消息ID',
  `type` int(11) NOT NULL COMMENT '消息类型',
  `sourceUid` varchar(32) NOT NULL DEFAULT '' COMMENT '发送者',
  `targetUid` varchar(32) DEFAULT '' COMMENT '用户',
  `gid` int(11) DEFAULT NULL COMMENT '群标识',
  `content` text COMMENT '消息',
  `fileName` varchar(200) DEFAULT NULL COMMENT '文件名',
  `fileSize` varchar(20) DEFAULT NULL COMMENT '文件大小',
  `fileURL` varchar(256) DEFAULT NULL COMMENT '文件地址',
  `ipAddr` varchar(100) NOT NULL DEFAULT '' COMMENT 'IP地址',
  `time` bigint(20) NOT NULL COMMENT '消息的时间毫秒',
  `created` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `mid` (`mid`),
  KEY `uidx` (`targetUid`),
  KEY `gidx` (`gid`),
  KEY `ug_idx` (`targetUid`,`gid`),
  KEY `sourceUid` (`sourceUid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息记录表';



# Dump of table msg_log_2
# ------------------------------------------------------------

CREATE TABLE `msg_log_2` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `mid` varchar(32) NOT NULL DEFAULT '' COMMENT '消息ID',
  `type` int(11) NOT NULL COMMENT '消息类型',
  `sourceUid` varchar(32) NOT NULL DEFAULT '' COMMENT '发送者',
  `targetUid` varchar(32) DEFAULT '' COMMENT '用户',
  `gid` int(11) DEFAULT NULL COMMENT '群标识',
  `content` text COMMENT '消息',
  `fileName` varchar(200) DEFAULT NULL COMMENT '文件名',
  `fileSize` varchar(20) DEFAULT NULL COMMENT '文件大小',
  `fileURL` varchar(256) DEFAULT NULL COMMENT '文件地址',
  `ipAddr` varchar(100) NOT NULL DEFAULT '' COMMENT 'IP地址',
  `time` bigint(20) NOT NULL COMMENT '消息的时间毫秒',
  `created` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `mid` (`mid`),
  KEY `uidx` (`targetUid`),
  KEY `gidx` (`gid`),
  KEY `ug_idx` (`targetUid`,`gid`),
  KEY `sourceUid` (`sourceUid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息记录表';



# Dump of table msg_log_3
# ------------------------------------------------------------

CREATE TABLE `msg_log_3` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `mid` varchar(32) NOT NULL DEFAULT '' COMMENT '消息ID',
  `type` int(11) NOT NULL COMMENT '消息类型',
  `sourceUid` varchar(32) NOT NULL DEFAULT '' COMMENT '发送者',
  `targetUid` varchar(32) DEFAULT '' COMMENT '用户',
  `gid` int(11) DEFAULT NULL COMMENT '群标识',
  `content` text COMMENT '消息',
  `fileName` varchar(200) DEFAULT NULL COMMENT '文件名',
  `fileSize` varchar(20) DEFAULT NULL COMMENT '文件大小',
  `fileURL` varchar(256) DEFAULT NULL COMMENT '文件地址',
  `ipAddr` varchar(100) NOT NULL DEFAULT '' COMMENT 'IP地址',
  `time` bigint(20) NOT NULL COMMENT '消息的时间毫秒',
  `created` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `mid` (`mid`),
  KEY `uidx` (`targetUid`),
  KEY `gidx` (`gid`),
  KEY `ug_idx` (`targetUid`,`gid`),
  KEY `sourceUid` (`sourceUid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息记录表';



# Dump of table user
# ------------------------------------------------------------

DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `uid` varchar(32) NOT NULL DEFAULT '' COMMENT '用户唯一标识',
  `username` varchar(64) NOT NULL DEFAULT '' COMMENT '用户名称（登录）',
  `password` varchar(128) NOT NULL DEFAULT '' COMMENT '用户密码',
  `nickname` varchar(64) NOT NULL DEFAULT '' COMMENT '用户昵称',
  `avatar` varchar(256) DEFAULT 'img/avatar/m.jpg' COMMENT '头像地址',
  `age` int(4) unsigned DEFAULT '0' COMMENT '用户年龄',
  `sex` tinyint(1) DEFAULT '1' COMMENT '0:F 1:M',
  `mobile` varchar(18) DEFAULT NULL COMMENT '手机',
  `mailbox` varchar(32) DEFAULT NULL COMMENT '邮箱',
  `address` varchar(150) DEFAULT NULL COMMENT '地址',
  `level` tinyint(2) unsigned NOT NULL DEFAULT '0' COMMENT '等级',
  `status` tinyint(2) NOT NULL DEFAULT '1' COMMENT '用户状态-1废弃0-冻结1-正常',
  `updated` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `created` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uid_idx` (`uid`),
  UNIQUE KEY `uname_idx` (`username`),
  KEY `nname_idx` (`nickname`),
  KEY `u_idx` (`nickname`,`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;

INSERT INTO `user` (`id`, `uid`, `username`, `password`, `nickname`, `avatar`, `age`, `sex`, `mobile`, `mailbox`, `address`, `level`, `status`, `updated`, `created`)
VALUES
	(1,'21232F297A57A5A743894A0E4A801FC3','admin','7a57a5a7101df96c21232f2943894a0e4a801fc3','Admin','img/avatar/m.jpg',18,1,NULL,NULL,NULL,0,1,'2020-03-19 12:58:36','2020-03-11 15:58:50'),
	(2,'248A9B3369371F8395399D6D3A686EE4','xixincan','69371f836c3962b9248a9b3395399d6d3a686ee4','xixincan','img/avatar/m.jpg',18,1,NULL,NULL,NULL,0,1,'2020-03-19 12:58:43','2020-03-11 15:59:13'),
	(3,'5996C4C3CECA2B93F9C0902547C245DB','wangfeng','ceca2b9329aedf4a5996c4c3f9c0902547c245db','wangfeng','img/avatar/m.jpg',18,1,NULL,NULL,NULL,0,1,'2020-03-19 12:58:49','2020-03-11 15:59:34'),
	(4,'7986E4397EF4A527121FF590646E0071','vincent','7ef4a52779c58e937986e439121ff590646e0071','vincent','img/avatar/m.jpg',18,1,NULL,NULL,NULL,0,1,'2020-03-19 12:58:56','2020-03-11 15:59:56'),
	(5,'0FB8E4CFBFAF4022C8996DE15ED2B5E8','wangwu','bfaf4022fb0c1cd00fb8e4cfc8996de15ed2b5e8','wangwu','img/avatar/m.jpg',18,1,NULL,NULL,NULL,0,1,'2020-03-19 12:59:01','2020-03-11 16:00:17'),
	(6,'248A9B3369371F8395399D6D3A686E==','zhangsan','e6ea326d82d1b5c178d7c3259c1a5c5b1172a59d','zhangsan','img/avatar/f.jpg',18,0,NULL,NULL,NULL,0,1,'2020-03-19 12:59:24','2020-03-15 19:43:12'),
	(7,'dGVzdA==','test','4621d373679e9d9b098f6bcdcade4e832627b4f6','test','img/avatar/m.jpg',0,1,'12345678910','test@test.com',NULL,0,1,NULL,'2020-03-21 18:22:21');

/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table user_log_0
# ------------------------------------------------------------

CREATE TABLE `user_log_0` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `uid` varchar(32) NOT NULL DEFAULT '' COMMENT 'UID',
  `event` tinyint(2) NOT NULL DEFAULT '0' COMMENT '0:注册 1:上线 2:下线 -1:注销',
  `ipAddr` varchar(50) NOT NULL DEFAULT '' COMMENT 'IP地址',
  `created` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `uidx` (`uid`),
  KEY `ueidx` (`uid`,`event`),
  KEY `ugidx` (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户事件记录';



# Dump of table user_log_1
# ------------------------------------------------------------

CREATE TABLE `user_log_1` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `uid` varchar(32) NOT NULL DEFAULT '' COMMENT 'UID',
  `event` tinyint(2) NOT NULL DEFAULT '0' COMMENT '0:注册 1:上线 2:下线 -1:注销',
  `ipAddr` varchar(50) NOT NULL DEFAULT '' COMMENT 'IP地址',
  `created` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `uidx` (`uid`),
  KEY `ueidx` (`uid`,`event`),
  KEY `ugidx` (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户事件记录';



# Dump of table user_log_2
# ------------------------------------------------------------

CREATE TABLE `user_log_2` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `uid` varchar(32) NOT NULL DEFAULT '' COMMENT 'UID',
  `event` tinyint(2) NOT NULL DEFAULT '0' COMMENT '0:注册 1:上线 2:下线 -1:注销',
  `ipAddr` varchar(50) NOT NULL DEFAULT '' COMMENT 'IP地址',
  `created` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `uidx` (`uid`),
  KEY `ueidx` (`uid`,`event`),
  KEY `ugidx` (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户事件记录';



# Dump of table user_log_3
# ------------------------------------------------------------

CREATE TABLE `user_log_3` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `uid` varchar(32) NOT NULL DEFAULT '' COMMENT 'UID',
  `event` tinyint(2) NOT NULL DEFAULT '0' COMMENT '0:注册 1:上线 2:下线 -1:注销',
  `ipAddr` varchar(50) NOT NULL DEFAULT '' COMMENT 'IP地址',
  `created` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `uidx` (`uid`),
  KEY `ueidx` (`uid`,`event`),
  KEY `ugidx` (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户事件记录';



# Dump of table user_msg_0
# ------------------------------------------------------------

CREATE TABLE `user_msg_0` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `uid` varchar(32) NOT NULL DEFAULT '' COMMENT 'uid',
  `mid` varchar(32) NOT NULL DEFAULT '' COMMENT 'msg id',
  `sent` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否已发送',
  `created` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `midx` (`mid`),
  KEY `ums_idx` (`uid`,`mid`,`sent`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;



# Dump of table user_msg_1
# ------------------------------------------------------------

CREATE TABLE `user_msg_1` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `uid` varchar(32) NOT NULL DEFAULT '' COMMENT 'uid',
  `mid` varchar(32) NOT NULL DEFAULT '' COMMENT 'msg id',
  `sent` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否已发送',
  `created` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `midx` (`mid`),
  KEY `ums_idx` (`uid`,`mid`,`sent`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;



# Dump of table user_msg_2
# ------------------------------------------------------------

CREATE TABLE `user_msg_2` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `uid` varchar(32) NOT NULL DEFAULT '' COMMENT 'uid',
  `mid` varchar(32) NOT NULL DEFAULT '' COMMENT 'msg id',
  `sent` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否已发送',
  `created` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `midx` (`mid`),
  KEY `ums_idx` (`uid`,`mid`,`sent`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;



# Dump of table user_msg_3
# ------------------------------------------------------------

CREATE TABLE `user_msg_3` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `uid` varchar(32) NOT NULL DEFAULT '' COMMENT 'uid',
  `mid` varchar(32) NOT NULL DEFAULT '' COMMENT 'msg id',
  `sent` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否已发送',
  `created` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `midx` (`mid`),
  KEY `ums_idx` (`uid`,`mid`,`sent`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;



# Dump of table user_relation
# ------------------------------------------------------------

DROP TABLE IF EXISTS `user_relation`;

CREATE TABLE `user_relation` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `uid` varchar(32) NOT NULL DEFAULT '' COMMENT '用户',
  `fuid` varchar(32) NOT NULL DEFAULT '' COMMENT '好友',
  `valid` tinyint(1) NOT NULL DEFAULT '1' COMMENT '有效',
  `updated` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `created` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `uidx` (`uid`),
  KEY `fidx` (`fuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='好友关系';

LOCK TABLES `user_relation` WRITE;
/*!40000 ALTER TABLE `user_relation` DISABLE KEYS */;

INSERT INTO `user_relation` (`id`, `uid`, `fuid`, `valid`, `updated`, `created`)
VALUES
	(1,'0FB8E4CFBFAF4022C8996DE15ED2B5E8','21232F297A57A5A743894A0E4A801FC3',0,'2020-03-15 20:29:47','2020-03-11 18:22:34'),
	(2,'21232F297A57A5A743894A0E4A801FC3','248A9B3369371F8395399D6D3A686EE4',1,'2020-03-11 18:24:26','2020-03-11 18:22:34'),
	(3,'248A9B3369371F8395399D6D3A686EE4','7986E4397EF4A527121FF590646E0071',1,'2020-03-11 18:24:37','2020-03-11 18:22:34'),
	(4,'248A9B3369371F8395399D6D3A686EE4','5996C4C3CECA2B93F9C0902547C245DB',1,'2020-03-11 18:30:48','2020-03-11 18:22:34'),
	(5,'7986E4397EF4A527121FF590646E0071','21232F297A57A5A743894A0E4A801FC3',0,'2020-03-15 20:29:50','2020-03-11 18:22:34'),
	(6,'0FB8E4CFBFAF4022C8996DE15ED2B5E8','248A9B3369371F8395399D6D3A686EE4',1,'2020-03-11 18:23:46','2020-03-11 18:22:34'),
	(7,'0FB8E4CFBFAF4022C8996DE15ED2B5E8','5996C4C3CECA2B93F9C0902547C245DB',1,'2020-03-11 18:23:46','2020-03-11 18:22:34'),
	(8,'0FB8E4CFBFAF4022C8996DE15ED2B5E8','7986E4397EF4A527121FF590646E0071',1,'2020-03-11 18:23:46','2020-03-11 18:22:34'),
	(9,'21232F297A57A5A743894A0E4A801FC3','5996C4C3CECA2B93F9C0902547C245DB',0,'2020-03-15 20:29:35','2020-03-11 18:22:34'),
	(10,'5996C4C3CECA2B93F9C0902547C245DB','7986E4397EF4A527121FF590646E0071',1,'2020-03-11 18:27:52','2020-03-11 18:22:34'),
	(11,'248A9B3369371F8395399D6D3A686E==','248A9B3369371F8395399D6D3A686EE4',1,NULL,'2020-03-15 19:43:58');

/*!40000 ALTER TABLE `user_relation` ENABLE KEYS */;
UNLOCK TABLES;



/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
