/*
Navicat MySQL Data Transfer

Source Server         : local
Source Server Version : 50626
Source Host           : localhost:3306
Source Database       : findchildren

Target Server Type    : MYSQL
Target Server Version : 50626
File Encoding         : 65001

Date: 2018-02-14 17:51:56
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `points`
-- ----------------------------
DROP TABLE IF EXISTS `points`;
CREATE TABLE `points` (
  `pid` int(11) NOT NULL AUTO_INCREMENT,
  `privince` varchar(255) DEFAULT NULL,
  `latitude` double DEFAULT NULL,
  `longitude` double DEFAULT NULL,
  `city` varchar(255) DEFAULT NULL,
  `district` varchar(255) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `time` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`pid`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of points
-- ----------------------------
INSERT INTO `points` VALUES ('1', '3', '1', '2', '2', '2', '2', '2', '123@qq.com', '1123');
INSERT INTO `points` VALUES ('2', '3', '1', '2', '2', '2', '2', '2', '123@qq.com', '1123');
INSERT INTO `points` VALUES ('3', 'å¹¿ä¸œçœ', '23.125818', '113.255061', 'å¹¿å·žå¸‚', 'è¶Šç§€åŒº', 'å¹¿ä¸œçœå¹¿å·žå¸‚è¶Šç§€åŒºäººæ°‘åŒ—è·¯244å·é è¿‘ä¸­å›½é“¶è¡Œ(å¹¿å·žè¥¿é—¨å£æ”¯è¡Œ)', '2018-02-14 17:40:54', '123@qq.com', '1123');
INSERT INTO `points` VALUES ('4', 'å¹¿ä¸œçœ', '23.125825', '113.254984', 'å¹¿å·žå¸‚', 'è¶Šç§€åŒº', 'å¹¿ä¸œçœå¹¿å·žå¸‚è¶Šç§€åŒºäººæ°‘åŒ—è·¯238å·é è¿‘ä¸­å›½é“¶è¡Œ(å¹¿å·žè¥¿é—¨å£æ”¯è¡Œ)', '2018-02-14 17:41:01', '123@qq.com', '1123');
INSERT INTO `points` VALUES ('5', 'å¹¿ä¸œçœ', '23.125825', '113.254984', 'å¹¿å·žå¸‚', 'è¶Šç§€åŒº', 'å¹¿ä¸œçœå¹¿å·žå¸‚è¶Šç§€åŒºäººæ°‘åŒ—è·¯238å·é è¿‘ä¸­å›½é“¶è¡Œ(å¹¿å·žè¥¿é—¨å£æ”¯è¡Œ)', '2018-02-14 17:41:01', '123@qq.com', '1123');
INSERT INTO `points` VALUES ('6', 'å¹¿ä¸œçœ', '23.125825', '113.254984', 'å¹¿å·žå¸‚', 'è¶Šç§€åŒº', 'å¹¿ä¸œçœå¹¿å·žå¸‚è¶Šç§€åŒºäººæ°‘åŒ—è·¯238å·é è¿‘ä¸­å›½é“¶è¡Œ(å¹¿å·žè¥¿é—¨å£æ”¯è¡Œ)', '2018-02-14 17:41:01', '123@qq.com', '1123');
INSERT INTO `points` VALUES ('7', 'å¹¿ä¸œçœ', '23.125825', '113.254984', 'å¹¿å·žå¸‚', 'è¶Šç§€åŒº', 'å¹¿ä¸œçœå¹¿å·žå¸‚è¶Šç§€åŒºäººæ°‘åŒ—è·¯238å·é è¿‘ä¸­å›½é“¶è¡Œ(å¹¿å·žè¥¿é—¨å£æ”¯è¡Œ)', '2018-02-14 17:41:01', '123@qq.com', '1123');
INSERT INTO `points` VALUES ('8', 'å¹¿ä¸œçœ', '23.125825', '113.254984', 'å¹¿å·žå¸‚', 'è¶Šç§€åŒº', 'å¹¿ä¸œçœå¹¿å·žå¸‚è¶Šç§€åŒºäººæ°‘åŒ—è·¯238å·é è¿‘ä¸­å›½é“¶è¡Œ(å¹¿å·žè¥¿é—¨å£æ”¯è¡Œ)', '2018-02-14 17:41:01', '123@qq.com', '1123');
INSERT INTO `points` VALUES ('9', 'å¹¿ä¸œçœ', '23.125825', '113.254984', 'å¹¿å·žå¸‚', 'è¶Šç§€åŒº', 'å¹¿ä¸œçœå¹¿å·žå¸‚è¶Šç§€åŒºäººæ°‘åŒ—è·¯238å·é è¿‘ä¸­å›½é“¶è¡Œ(å¹¿å·žè¥¿é—¨å£æ”¯è¡Œ)', '2018-02-14 17:41:01', '123@qq.com', '1123');
INSERT INTO `points` VALUES ('10', 'å¹¿ä¸œçœ', '23.125825', '113.254984', 'å¹¿å·žå¸‚', 'è¶Šç§€åŒº', 'å¹¿ä¸œçœå¹¿å·žå¸‚è¶Šç§€åŒºäººæ°‘åŒ—è·¯238å·é è¿‘ä¸­å›½é“¶è¡Œ(å¹¿å·žè¥¿é—¨å£æ”¯è¡Œ)', '2018-02-14 17:41:01', '123@qq.com', '1123');
INSERT INTO `points` VALUES ('11', 'å¹¿ä¸œçœ', '23.125825', '113.254984', 'å¹¿å·žå¸‚', 'è¶Šç§€åŒº', 'å¹¿ä¸œçœå¹¿å·žå¸‚è¶Šç§€åŒºäººæ°‘åŒ—è·¯238å·é è¿‘ä¸­å›½é“¶è¡Œ(å¹¿å·žè¥¿é—¨å£æ”¯è¡Œ)', '2018-02-14 17:41:01', '123@qq.com', '1123');
INSERT INTO `points` VALUES ('12', 'å¹¿ä¸œçœ', '23.125825', '113.254984', 'å¹¿å·žå¸‚', 'è¶Šç§€åŒº', 'å¹¿ä¸œçœå¹¿å·žå¸‚è¶Šç§€åŒºäººæ°‘åŒ—è·¯238å·é è¿‘ä¸­å›½é“¶è¡Œ(å¹¿å·žè¥¿é—¨å£æ”¯è¡Œ)', '2018-02-14 17:41:01', '123@qq.com', '1123');
INSERT INTO `points` VALUES ('13', 'å¹¿ä¸œçœ', '23.125825', '113.254984', 'å¹¿å·žå¸‚', 'è¶Šç§€åŒº', 'å¹¿ä¸œçœå¹¿å·žå¸‚è¶Šç§€åŒºäººæ°‘åŒ—è·¯238å·é è¿‘ä¸­å›½é“¶è¡Œ(å¹¿å·žè¥¿é—¨å£æ”¯è¡Œ)', '2018-02-14 17:41:01', '123@qq.com', '1123');
INSERT INTO `points` VALUES ('14', 'å¹¿ä¸œçœ', '23.125825', '113.254984', 'å¹¿å·žå¸‚', 'è¶Šç§€åŒº', 'å¹¿ä¸œçœå¹¿å·žå¸‚è¶Šç§€åŒºäººæ°‘åŒ—è·¯238å·é è¿‘ä¸­å›½é“¶è¡Œ(å¹¿å·žè¥¿é—¨å£æ”¯è¡Œ)', '2018-02-14 17:41:01', '123@qq.com', '1123');
INSERT INTO `points` VALUES ('15', 'å¹¿ä¸œçœ', '23.125825', '113.254984', 'å¹¿å·žå¸‚', 'è¶Šç§€åŒº', 'å¹¿ä¸œçœå¹¿å·žå¸‚è¶Šç§€åŒºäººæ°‘åŒ—è·¯238å·é è¿‘ä¸­å›½é“¶è¡Œ(å¹¿å·žè¥¿é—¨å£æ”¯è¡Œ)', '2018-02-14 17:41:01', '123@qq.com', '1123');
INSERT INTO `points` VALUES ('16', 'å¹¿ä¸œçœ', '23.125825', '113.254984', 'å¹¿å·žå¸‚', 'è¶Šç§€åŒº', 'å¹¿ä¸œçœå¹¿å·žå¸‚è¶Šç§€åŒºäººæ°‘åŒ—è·¯238å·é è¿‘ä¸­å›½é“¶è¡Œ(å¹¿å·žè¥¿é—¨å£æ”¯è¡Œ)', '2018-02-14 17:41:01', '123@qq.com', '1123');
INSERT INTO `points` VALUES ('17', '3', '1', '2', '2', '2', '2', '2', '123@qq.com', '1123');
INSERT INTO `points` VALUES ('18', '3', '1', '2', 'æ‰‘è¡—', '2', '2', '2', '123@qq.com', '1123');
INSERT INTO `points` VALUES ('19', '3', '1', '2', 'æ‰‘è¡—', '2', '2', '2', '123@qq.com', '1123');
INSERT INTO `points` VALUES ('20', '3', '1', '2', 'æ‰‘è¡—', '2', '2', '2', '123@qq.com', '1123');
INSERT INTO `points` VALUES ('21', '3', '1', '2', 'æ‰‘è¡—', '2', '2', '2', '123@qq.com', '1123');

-- ----------------------------
-- Table structure for `user`
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `son` varchar(255) DEFAULT NULL,
  `phone` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('1', '1', '2', 'parent', '1', '', '110');
INSERT INTO `user` VALUES ('2', '12', '2', 'children', '1@111.com', '', '110');
INSERT INTO `user` VALUES ('3', '1123', '123456', 'children', '123@qq.com', '', '110');