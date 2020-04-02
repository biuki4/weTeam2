DROP TABLE IF EXISTS `user`;
DROP TABLE IF EXISTS `game`;
DROP TABLE IF EXISTS `award`;
DROP TABLE IF EXISTS `business_card`;
DROP TABLE IF EXISTS `user_tag`;
DROP TABLE IF EXISTS `user_tag_record`;
DROP TABLE IF EXISTS `game_tag`;
DROP TABLE IF EXISTS `game_tag_record`;
DROP TABLE IF EXISTS `game_notice`;
DROP TABLE IF EXISTS `game_attachment`;
DROP TABLE IF EXISTS `game_join`;
DROP TABLE IF EXISTS `message`;
DROP TABLE IF EXISTS `education`;
DROP TABLE IF EXISTS `user_attention`;
DROP TABLE IF EXISTS `game_attention`;
DROP TABLE IF EXISTS `game_source`;
DROP TABLE IF EXISTS `game_category`;
DROP TABLE IF EXISTS `game_category_record`;

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
	`id` int(20) NOT NULL AUTO_INCREMENT,
	`nickname` varchar(255) DEFAULT NULL COMMENT '昵称',
	`username` varchar(255) DEFAULT NULL COMMENT '用户名',
	`password` varchar(255) DEFAULT NULL COMMENT '密码',
	`open_id` varchar(255) DEFAULT NULL COMMENT '小程序openId',
	`gender` tinyint(2) DEFAULT '-1' COMMENT '昵称 1-男 0-女 -1-保密',
	`user_type` tinyint(2) DEFAULT '3' COMMENT '用户类型 0-超管 1-一级管理员 2-二级管理员 3-WeTeam用户',
	`show_me` tinyint(2) DEFAULT '0' COMMENT '0-不展示 1展示',
	`grade` varchar(255) DEFAULT NULL COMMENT '年级',
	`academy_id` int(20) DEFAULT NULL COMMENT '学院',
	`phone` varchar(255) DEFAULT NULL COMMENT '电话',
	`email` varchar(255) DEFAULT NULL COMMENT '邮箱',
	`person_info` varchar(255) DEFAULT NULL COMMENT '个人简介',
	`avatar_url` varchar(255) DEFAULT NULL COMMENT '头像',
	`create_time` date DEFAULT NULL COMMENT '创建时间',
	`login_last_time` date DEFAULT NULL COMMENT '最后登录时间',
	`login_enable` tinyint(2) DEFAULT '0' COMMENT '是否可登录 0-否 1-是',
  	`user_views` bigint(20) DEFAULT '0' COMMENT '访问量统计',
	PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin` (
	`id` int(20) NOT NULL AUTO_INCREMENT,
	`user_id` int(20) NOT NULL COMMENT '用户id',
	`set_id` int(20) NOT NULL COMMENT '用户id',
	PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;



DROP TABLE IF EXISTS `advantage`;
CREATE TABLE `advantage` (
	`id` int(20) NOT NULL AUTO_INCREMENT,
	`user_id` int(20) NOT NULL COMMENT '用户id',
	`name` varchar(255) DEFAULT NULL COMMENT '标签名称',
	`brief` varchar(255) DEFAULT NULL COMMENT '简介',
	PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;



-- ----------------------------
-- 获奖经历
-- ----------------------------
DROP TABLE IF EXISTS `award`;
CREATE TABLE `award` (
	`id` int(20) NOT NULL AUTO_INCREMENT,
	`user_id` int(20) DEFAULT NULL COMMENT '对应user',
	`name` varchar(255) DEFAULT NULL COMMENT '获奖名称',
	`type` tinyint(2) DEFAULT '5' COMMENT '比赛类型 0-院级 1-校级 2-市级 3-省级 4-国家级 5-国际级 6-其他',
	`brief` varchar(255) DEFAULT NULL COMMENT '奖项',
	`paper_url` varchar(255) DEFAULT NULL COMMENT '证书url',
	PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;


-- ----------------------------
-- 教育经历
-- ----------------------------
-- DROP TABLE IF EXISTS `education`;
-- CREATE TABLE `education` (
-- 	`id` int(20) NOT NULL AUTO_INCREMENT,
-- 	`user_id` int(20) DEFAULT NULL COMMENT '对应user',
-- 	`school` varchar(255) DEFAULT NULL COMMENT '学校名称',
-- 	`major` varchar(255) DEFAULT NULL COMMENT '专业',
-- 	`enter_time` date DEFAULT NULL COMMENT '入学年份',
-- 	`graduate_time` date DEFAULT NULL COMMENT '毕业年份',
-- 	PRIMARY KEY (`id`)
-- ) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;


-- ----------------------------
-- 关注用户列表
-- ----------------------------
DROP TABLE IF EXISTS `user_attention`;
CREATE TABLE `user_attention` (
	`user_id` int(20) NOT NULL COMMENT 'userId',
	`attention_id` int(20) NOT NULL COMMENT '关注id'
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;














-- ----------------------------
-- Table structure for game
-- ----------------------------
DROP TABLE IF EXISTS `game`;
CREATE TABLE `game` (
	`id` int(20) NOT NULL AUTO_INCREMENT,
	`post_id` int(20) DEFAULT NULL COMMENT '发布者',
	`game_name` varchar(255) DEFAULT NULL COMMENT '比赛名称',
	`poster_url` varchar(255) DEFAULT NULL COMMENT '海报',
	`game_content` longtext DEFAULT NULL COMMENT '比赛介绍',
	`post_time` date DEFAULT NULL COMMENT '发布时间',
	`register_start_time` date DEFAULT NULL COMMENT '比赛报名时间段',
	`register_end_time` date DEFAULT NULL COMMENT '比赛报名时间段',
	`game_start_time` date DEFAULT NULL COMMENT '比赛时间持续段',
	`game_end_time` date DEFAULT NULL COMMENT '比赛时间持续段',
	`game_source` varchar(255) DEFAULT NULL COMMENT '主办方',
	`game_type` tinyint(2) DEFAULT '5' COMMENT '比赛类型 0-不限 1-院级 2-校级 3-市级 4-省级 5-国家级 6-国际',
	`category_id` int(20) DEFAULT NULL COMMENT '分类',
	`game_url` varchar(255) DEFAULT NULL COMMENT '比赛链接',
	`team_size` varchar(255) DEFAULT '1' COMMENT '队伍规模',
	`game_views` bigint(20) DEFAULT '0' COMMENT '访问量统计',
	PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;


-- ----------------------------
-- Table structure for tag
-- ----------------------------
DROP TABLE IF EXISTS `game_tag`;
CREATE TABLE `game_tag` (
	`id` int(20) NOT NULL AUTO_INCREMENT,
	`tag_name` varchar(255) DEFAULT NULL COMMENT '标签名称',
	PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for game tag
-- ----------------------------
DROP TABLE IF EXISTS `game_tag_record`;
CREATE TABLE `game_tag_record` (
  `game_id` int(20) NOT NULL COMMENT '比赛id',
  `tag_id` int(20) NOT NULL COMMENT '标签id',
  KEY `ARTILE_ID` (`game_id`) USING BTREE,
  KEY `TAG_ID` (`tag_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;

-- ----------------------------
-- 比赛公告
-- ----------------------------
-- DROP TABLE IF EXISTS `game_notice`;
-- CREATE TABLE `game_notice` (
--   `id` int(20) NOT NULL AUTO_INCREMENT,
--   `game_id` int(20) NOT NULL COMMENT '比赛id',
--   `post_id` int(20) DEFAULT NULL COMMENT '上发表用户',
--   `notice_info` longtext DEFAULT NULL COMMENT '公告内容',
--   PRIMARY KEY (`id`)
-- ) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;

-- -- ----------------------------
-- -- 附件
-- -- ----------------------------
-- DROP TABLE IF EXISTS `game_attachment`;
-- CREATE TABLE `game_attachment` (
--   `id` int(20) NOT NULL AUTO_INCREMENT,
--   `game_id` int(20) NOT NULL COMMENT '比赛id',
--   `post_id` int(20) DEFAULT NULL COMMENT '上传用户',
--   `attachment_url` varchar(255) DEFAULT NULL COMMENT '附件url',
--   `upload_time` date DEFAULT NULL COMMENT '创建时间',
--   PRIMARY KEY (`id`)
-- ) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;


-- ----------------------------
-- 关注比赛列表
-- ----------------------------
DROP TABLE IF EXISTS `game_attention`;
CREATE TABLE `game_attention` (
	`user_id` int(20) NOT NULL COMMENT '关注id',
	`game_id` int(20) NOT NULL COMMENT 'gameId',
	KEY `USER_ID` (`user_id`) USING BTREE,
  	KEY `GAME_ID` (`game_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;


-- ----------------------------
-- 分类
-- ----------------------------
DROP TABLE IF EXISTS `game_category`;
CREATE TABLE `game_category` (
	`id` int(20) NOT NULL AUTO_INCREMENT,
	`category` varchar(255) DEFAULT NULL COMMENT '分类',
	PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;

-- ----------------------------
-- 竞赛分类
-- ----------------------------
DROP TABLE IF EXISTS `game_category_record`;
CREATE TABLE `game_category_record` (
  `game_id` int(20) NOT NULL,
  `category_id` bigint(20) NOT NULL,
  KEY `GAME_ID` (`game_id`) USING BTREE,
  KEY `CATEGORY_ID` (`category_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- ----------------------------
-- ----------------------------
-- 组队
-- ----------------------------
-- ----------------------------

-- ----------------------------
-- 队伍
-- ----------------------------
DROP TABLE IF EXISTS `team`;
CREATE TABLE `team` (
  `id` int(20) NOT NULL,
  `user_id` int(20) DEFAULT NULL COMMENT '队长',
  `game_id` int(20) DEFAULT NULL COMMENT '对应比赛',
  `create_time` date DEFAULT NULL COMMENT '创建时间',
  `team_no`varchar(255) DEFAULT NULL COMMENT '编号',
  `name`varchar(255) DEFAULT NULL COMMENT '编号',
  `brief` varchar(255) DEFAULT NULL COMMENT '介绍',
  `size` int(5) DEFAULT NULL COMMENT '人数',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- 队员
-- ----------------------------
DROP TABLE IF EXISTS `team_user`;
CREATE TABLE `team_user` (
  `team_id` int(20) NOT NULL COMMENT '队伍',
  `user_id` int(20) NOT NULL COMMENT '成员',
  `type` tinyint(2) DEFAULT '0' COMMENT '0-申请中  1-同意',
  KEY `TEAM_ID` (`team_id`) USING BTREE,
  KEY `USER_ID` (`user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- ----------------------------
-- Table structure for join 参与比赛列表
-- ----------------------------
-- DROP TABLE IF EXISTS `game_join`;
-- CREATE TABLE `game_join` (
-- 	`game_id` int(20) DEFAULT NULL COMMENT '比赛id',
-- 	`user_id` int(20) DEFAULT NULL COMMENT '用户id',
-- 	`game_team_id` int(20) DEFAULT NULL COMMENT '队伍id'
-- ) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;







-- ----------------------------
-- Table structure 学院
-- ----------------------------
DROP TABLE IF EXISTS `academy`;
CREATE TABLE `academy` (
	`id` int(20) NOT NULL AUTO_INCREMENT,
	`name` varchar(255) DEFAULT NULL COMMENT '学院',
	`icon_url` varchar(255) DEFAULT NULL COMMENT 'icon',
	PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8;


-- ----------------------------
-- 消息
-- ----------------------------
DROP TABLE IF EXISTS `message`;
CREATE TABLE `message` (
	`id` int(20) NOT NULL AUTO_INCREMENT,
	`create_time` date DEFAULT NULL COMMENT '发送日期',
	`title` varchar(255) DEFAULT NULL COMMENT '标题',
	`img_url` varchar(255) DEFAULT NULL COMMENT '图片',
	`msg_content` longtext DEFAULT NULL COMMENT '消息内容',
	`sender_id` int(20) DEFAULT NULL COMMENT '发送人',
	`receiver_id` int(20) DEFAULT NULL COMMENT '接收人',
	PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;

