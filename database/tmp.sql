-- MySQL Script generated by MySQL Workbench
-- 10/27/16 15:22:17
-- Model: New Model    Version: 1.0
-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema work_si_tmp
-- -----------------------------------------------------
DROP SCHEMA IF EXISTS `work_si_tmp` ;

-- -----------------------------------------------------
-- Schema work_si_tmp
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `work_si_tmp` DEFAULT CHARACTER SET utf8 ;
USE `work_si_tmp` ;

-- -----------------------------------------------------
-- Table `work_si_tmp`.`ww3`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `work_si_tmp`.`ww3` ;

CREATE TABLE IF NOT EXISTS `work_si_tmp`.`ww3` (
  `position` POINT NOT NULL,
  `time` DATETIME NOT NULL,
  `dirm` DOUBLE NULL,
  `dirp` DOUBLE NULL,
  `rtp` DOUBLE NULL,
  `tm_10` DOUBLE NULL,
  PRIMARY KEY (`position`, `time`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `work_si_tmp`.`marine`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `work_si_tmp`.`marine` ;

CREATE TABLE IF NOT EXISTS `work_si_tmp`.`marine` (
  `position` POINT NOT NULL,
  `depth` DOUBLE NOT NULL,
  `time` DATETIME NOT NULL,
  `u` INT NULL,
  `v` INT NULL,
  `salinity` INT NULL,
  PRIMARY KEY (`position`, `depth`, `time`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `work_si_tmp`.`wrf`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `work_si_tmp`.`wrf` ;

CREATE TABLE IF NOT EXISTS `work_si_tmp`.`wrf` (
  `position` POINT NOT NULL,
  `time` DATETIME NOT NULL,
  `topo` DOUBLE NULL,
  `temp` DOUBLE NULL,
  `t500` DOUBLE NULL,
  `t850` DOUBLE NULL,
  `sst` DOUBLE NULL,
  `chf` DOUBLE NULL,
  `cfm` DOUBLE NULL,
  `cfl` DOUBLE NULL,
  `visibility` DOUBLE NULL,
  `snow_level` DOUBLE NULL,
  `snow_prec` DOUBLE NULL,
  `prec` DOUBLE NULL,
  `humidity` DOUBLE NULL,
  `wind_dir` DOUBLE NULL,
  `wind_lon` DOUBLE NULL,
  `wind_lat` DOUBLE NULL,
  `wind_gust` DOUBLE NULL,
  PRIMARY KEY (`position`, `time`))
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
