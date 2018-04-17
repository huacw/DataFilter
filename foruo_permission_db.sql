/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 50711
 Source Host           : localhost
 Source Database       : sc_permission

 Target Server Type    : MySQL
 Target Server Version : 50711
 File Encoding         : utf-8

 Date: 04/17/2018 14:45:43 PM
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
--  Table structure for `sys_exam`
-- ----------------------------
DROP TABLE IF EXISTS `sys_exam`;
CREATE TABLE `sys_exam` (
  `id` int(10) DEFAULT NULL,
  `username` varchar(200) DEFAULT NULL,
  `region_cd` varchar(6) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Records of `sys_exam`
-- ----------------------------
BEGIN;
INSERT INTO `sys_exam` VALUES ('1', '高元', '320101'), ('2', '李豪山', '320202');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
