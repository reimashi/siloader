-- MySQL Script generated by MySQL Workbench
-- 12/21/16 20:50:21
-- Model: New Model    Version: 1.0
-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema si-database
-- -----------------------------------------------------
DROP SCHEMA IF EXISTS `si-database` ;

-- -----------------------------------------------------
-- Schema si-database
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `si-database` DEFAULT CHARACTER SET utf8 ;
USE `si-database` ;

-- -----------------------------------------------------
-- Table `si-database`.`time`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `si-database`.`time` ;

CREATE TABLE IF NOT EXISTS `si-database`.`time` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `year` INT NOT NULL,
  `month` INT NOT NULL,
  `day` INT NOT NULL,
  `hour` INT NOT NULL,
  `minute` INT NOT NULL,
  `second` INT NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `si-database`.`location`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `si-database`.`location` ;

CREATE TABLE IF NOT EXISTS `si-database`.`location` (
  `id` INT NOT NULL,
  `latitude` DOUBLE NOT NULL,
  `longitude` DOUBLE NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `si-database`.`alerts`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `si-database`.`alerts` ;

CREATE TABLE IF NOT EXISTS `si-database`.`alerts` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `temperature_high` TINYINT(1) NOT NULL DEFAULT 0,
  `temperature_half` TINYINT(1) NOT NULL DEFAULT 0,
  `temperature_low` TINYINT(1) NOT NULL DEFAULT 0,
  `rain_high` TINYINT(1) NOT NULL DEFAULT 0,
  `rain_half` TINYINT(1) NOT NULL DEFAULT 0,
  `rain_low` TINYINT(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `si-database`.`measurement`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `si-database`.`measurement` ;

CREATE TABLE IF NOT EXISTS `si-database`.`measurement` (
  `idAlert` INT NOT NULL,
  `idTime` INT NOT NULL,
  `idLocation` INT NOT NULL,
  `elevation` DOUBLE NULL,
  `temperature_surface` DOUBLE NULL,
  `temperature_500mb` DOUBLE NULL,
  `temperature_850mb` DOUBLE NULL,
  `temperature_sea_level` DOUBLE NULL,
  `cloud_cover_high` DOUBLE NULL,
  `cloud_cover_half` DOUBLE NULL,
  `cloud_cover_low` DOUBLE NULL,
  `visibility` DOUBLE NULL,
  `salinity` DOUBLE NULL,
  `water_speed_eastward` DOUBLE NULL,
  `water_speed_northward` DOUBLE NULL,
  `wave_direction_mean` DOUBLE NULL COMMENT 'Radianes',
  `wave_direction_peak` DOUBLE NULL COMMENT 'Olas pico - radianes',
  `wave_period_absolute` DOUBLE NULL COMMENT 'segundos',
  `wave_period_peak` DOUBLE NULL COMMENT 'segundos',
  `snow_level` DOUBLE NULL,
  `snow_precipitation` DOUBLE NULL,
  `rain_precipitation` DOUBLE NULL,
  `humidity` DOUBLE NULL,
  `wind_direction` DOUBLE NULL,
  `wind_lon` DOUBLE NULL,
  `wind_lat` DOUBLE NULL,
  `wind_gust` DOUBLE NULL COMMENT 'Rafagas',
  PRIMARY KEY (`idAlert`, `idTime`, `idLocation`),
  INDEX `fk_Medicion_Time_idx` (`idTime` ASC),
  INDEX `fk_Medicion_Localizacion1_idx` (`idLocation` ASC),
  CONSTRAINT `fk_Medicion_Time`
    FOREIGN KEY (`idTime`)
    REFERENCES `si-database`.`time` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT `fk_Medicion_Localizacion`
    FOREIGN KEY (`idLocation`)
    REFERENCES `si-database`.`location` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT `fk_medicion_alerta`
    FOREIGN KEY (`idAlert`)
    REFERENCES `si-database`.`alerts` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `si-database`.`user`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `si-database`.`user` ;

CREATE TABLE IF NOT EXISTS `si-database`.`user` (
  `username` VARCHAR(64) NOT NULL,
  `name` VARCHAR(128) NULL,
  `password` VARCHAR(64) NOT NULL,
  UNIQUE INDEX `username_UNIQUE` (`username` ASC),
  PRIMARY KEY (`username`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `si-database`.`log-record`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `si-database`.`log-record` ;

CREATE TABLE IF NOT EXISTS `si-database`.`log-record` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `table` VARCHAR(64) NOT NULL,
  `user` VARCHAR(64) NOT NULL,
  `action` VARCHAR(64) NOT NULL,
  `raw` VARCHAR(512) NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_user_log_idx` (`user` ASC),
  CONSTRAINT `fk_user_log`
    FOREIGN KEY (`user`)
    REFERENCES `si-database`.`user` (`username`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;

SET SQL_MODE = '';
GRANT USAGE ON *.* TO esei;
 DROP USER esei;
SET SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';
CREATE USER 'esei' IDENTIFIED BY 'eseipass';

GRANT ALL ON `si-database`.* TO 'esei';
GRANT SELECT ON TABLE `si-database`.* TO 'esei';
GRANT SELECT, INSERT, TRIGGER ON TABLE `si-database`.* TO 'esei';
GRANT SELECT, INSERT, TRIGGER, UPDATE, DELETE ON TABLE `si-database`.* TO 'esei';

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

-- -----------------------------------------------------
-- Data for table `si-database`.`user`
-- -----------------------------------------------------
START TRANSACTION;
USE `si-database`;
INSERT INTO `si-database`.`user` (`username`, `name`, `password`) VALUES ('admin', 'Administrador', 'd033e22ae348aeb5660fc2140aec35850c4da997');

COMMIT;
