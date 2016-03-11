-- MySQL dump 10.13  Distrib 5.7.9, for Win64 (x86_64)
--
-- Host: localhost    Database: construction
-- ------------------------------------------------------
-- Server version	5.7.9-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `customers`
--

DROP TABLE IF EXISTS `customers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `customers` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `notes` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customers`
--

LOCK TABLES `customers` WRITE;
/*!40000 ALTER TABLE `customers` DISABLE KEYS */;
INSERT INTO `customers` VALUES (1,'пп Клепаєм місто ІФ',NULL),(2,'Файнобуд','Троха якихось нотаток');
/*!40000 ALTER TABLE `customers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `employees`
--

DROP TABLE IF EXISTS `employees`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `employees` (
  `id` int(11) NOT NULL,
  `name` varchar(45) NOT NULL,
  `surname` varchar(45) NOT NULL,
  `middleName` varchar(45) NOT NULL,
  `birthDate` date DEFAULT NULL,
  `firstDay` date NOT NULL,
  `lastDay` date DEFAULT NULL,
  `notes` varchar(100) DEFAULT NULL,
  `workingHours` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `employees`
--

LOCK TABLES `employees` WRITE;
/*!40000 ALTER TABLE `employees` DISABLE KEYS */;
INSERT INTO `employees` VALUES (1,'Юрій','Працьовитий','Іванович','1971-02-05','2012-07-23',NULL,NULL,9),(2,'Богдан','Кукурудза','Петрович','1980-05-12','2013-03-27',NULL,NULL,9),(3,'Іван','Сусанін','Осипович','1981-09-11','2012-11-01','2014-12-25',NULL,9),(4,'Назарій','Яремчук','Назарович','1967-10-17','2011-01-14',NULL,NULL,9),(5,'Аліна','Савченко','Богданівна','1992-09-13','2013-04-10',NULL,'Файно співає',8),(6,'Петро','Карась','Олексійович','1983-07-19','2011-07-25',NULL,NULL,12),(7,'Євген','Кошовий','Вікторович','1973-08-27','2011-02-01',NULL,NULL,24),(8,'Михайло','Бурак','Романович','1990-03-16','2012-06-29',NULL,NULL,7),(9,'Остап','Бульба','Тарасович','1987-01-12','2013-10-18',NULL,'Баран, нічо не вміє',9),(10,'Ксюха','Петрівна','Альбертівна','1993-08-13','2012-08-30',NULL,NULL,9),(11,'Лілія','Поліщук','Миколаївна','1096-08-16','2014-10-28',NULL,NULL,9),(12,'Яна','Кайдашева','Гаврилівна','1993-05-26','2012-04-12',NULL,'Вічно спізнюється',9),(13,'Ярина','Білодід','Артемівна','1987-10-09','2013-06-19','2015-01-17',NULL,8),(14,'Галина','Афанасенко','Олегівна','1995-03-21','2013-12-30',NULL,NULL,9),(15,'Вікторія','Воєвідка','Василівна','1990-01-01','2012-08-24',NULL,NULL,9),(16,'Мар\'яна','Мишка','Богданівна','1997-06-04','2015-02-03',NULL,NULL,10),(17,'Ігор','Бублик','Андрійович','1969-11-27','2012-05-18','2016-02-25',NULL,9),(18,'Фіона','Шрек','Драконівна','1994-07-10','2013-04-20',NULL,NULL,5),(19,'Буратіно','Бос','Карлович','1989-03-16','2014-08-18',NULL,NULL,2);
/*!40000 ALTER TABLE `employees` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `emptelephone`
--

DROP TABLE IF EXISTS `emptelephone`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `emptelephone` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `employees_id` int(11) NOT NULL,
  `telephoneNumber` varchar(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `fk_E_Telephone_employees1_idx` (`employees_id`),
  CONSTRAINT `fk_E_Telephone_employees1` FOREIGN KEY (`employees_id`) REFERENCES `employees` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `emptelephone`
--

LOCK TABLES `emptelephone` WRITE;
/*!40000 ALTER TABLE `emptelephone` DISABLE KEYS */;
INSERT INTO `emptelephone` VALUES (1,1,'0998934176'),(2,2,'0502307643'),(3,3,'0571928476'),(4,4,'0939342134'),(5,5,'0501010452'),(6,6,'0679428521'),(7,7,'0963257673'),(8,8,'0573494524'),(9,9,'0653510019'),(10,10,'0635484253'),(11,11,'0660345673'),(12,12,'0557842652'),(13,13,'0934574533'),(14,14,'0672012390'),(15,15,'0684354678'),(16,16,'0562312012'),(17,17,'0953126393'),(18,18,'0952312805'),(19,19,'0635456563'),(20,4,'0665640960'),(21,16,'0703053054'),(22,7,'0935675870'),(23,9,'0985467567'),(24,2,'0674675832');
/*!40000 ALTER TABLE `emptelephone` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `object`
--

DROP TABLE IF EXISTS `object`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `object` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `address` varchar(50) NOT NULL,
  `startDate` date NOT NULL,
  `finishDate` date DEFAULT NULL,
  `customers_id` int(11) NOT NULL,
  `estimatedCost` decimal(10,2) DEFAULT NULL,
  `notes` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `fk_object_customers1_idx` (`customers_id`),
  CONSTRAINT `fk_object_customers1` FOREIGN KEY (`customers_id`) REFERENCES `customers` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `object`
--

LOCK TABLES `object` WRITE;
/*!40000 ALTER TABLE `object` DISABLE KEYS */;
INSERT INTO `object` VALUES (1,'м. Івано-Франківськ, вул. Мельника, 24','2015-09-07','2015-10-21',1,70000.00,'теестовий текст нотататки про об\'єкт'),(2,'м. Івано-Франківськ, вул. Сахарова, 247','2015-12-19',NULL,2,100000.00,NULL),(3,'м. Івано-Франківськ, вул. Цалевича, 45','2016-01-18',NULL,1,35000.00,'нема що написати, але мушу щось написати, тому щось написав');
/*!40000 ALTER TABLE `object` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `object_employees`
--

DROP TABLE IF EXISTS `object_employees`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `object_employees` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `object_id` int(11) NOT NULL,
  `employees_id` int(11) NOT NULL,
  `startDate` date NOT NULL,
  `finishDate` date DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `fk_object_employees_employees1_idx` (`employees_id`),
  KEY `fk_object_employees_object1_idx` (`object_id`),
  CONSTRAINT `fk_object_employees_employees1` FOREIGN KEY (`employees_id`) REFERENCES `employees` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_object_employees_object1` FOREIGN KEY (`object_id`) REFERENCES `object` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `object_employees`
--

LOCK TABLES `object_employees` WRITE;
/*!40000 ALTER TABLE `object_employees` DISABLE KEYS */;
INSERT INTO `object_employees` VALUES (1,1,1,'2015-09-07','2015-10-21'),(2,1,2,'2015-09-07','2015-10-10'),(3,1,4,'2015-09-24','2015-10-07'),(4,1,7,'2015-09-13','2015-09-23'),(5,1,9,'2015-09-07','2015-10-21'),(6,1,7,'2015-10-10','2015-10-19'),(7,1,11,'2015-09-07','2015-10-15'),(8,1,8,'2015-09-07','2015-10-21'),(9,1,10,'2015-09-07','2015-10-21'),(10,2,1,'2015-11-19',NULL),(11,2,2,'2015-12-24','2016-01-06'),(12,2,4,'2015-12-20','2015-12-28'),(13,2,5,'2015-12-21','2016-01-06'),(14,2,12,'2016-01-09',NULL),(15,2,14,'2015-12-19','2015-12-29'),(16,2,15,'2015-12-25','2016-01-17'),(17,2,16,'2015-11-19','2016-01-17'),(18,2,18,'2016-01-04',NULL),(19,2,19,'2015-12-19',NULL),(20,2,5,'2016-01-20',NULL),(21,3,6,'2016-01-18','2016-01-19'),(22,3,7,'2016-01-18','2016-01-20'),(23,3,5,'2016-01-19','2016-01-19'),(24,3,15,'2016-01-18',NULL),(25,3,9,'2016-01-18',NULL),(26,3,14,'2016-01-18',NULL),(27,3,19,'2016-01-18',NULL),(28,3,11,'2016-01-18',NULL),(29,2,4,'2015-12-29','2016-01-06');
/*!40000 ALTER TABLE `object_employees` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `repository`
--

DROP TABLE IF EXISTS `repository`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `repository` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idrepository_UNIQUE` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `repository`
--

LOCK TABLES `repository` WRITE;
/*!40000 ALTER TABLE `repository` DISABLE KEYS */;
INSERT INTO `repository` VALUES (1,'Загальний'),(2,'Renault');
/*!40000 ALTER TABLE `repository` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `skills`
--

DROP TABLE IF EXISTS `skills`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `skills` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `skill` varchar(45) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `skills`
--

LOCK TABLES `skills` WRITE;
/*!40000 ALTER TABLE `skills` DISABLE KEYS */;
INSERT INTO `skills` VALUES (1,'Маляр'),(2,'Штукатур'),(3,'Зварщик'),(4,'Бос'),(5,'Просто чьоткий тіп'),(6,'Бухгалтер'),(7,'Підривник'),(8,'Плиточник');
/*!40000 ALTER TABLE `skills` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `skills_employees`
--

DROP TABLE IF EXISTS `skills_employees`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `skills_employees` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `employees_id` int(11) NOT NULL,
  `skills_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `fk_skills_employees1_idx` (`employees_id`),
  KEY `fk_skills_skills1_idx` (`skills_id`),
  CONSTRAINT `fk_skills_employees1` FOREIGN KEY (`employees_id`) REFERENCES `employees` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_skills_skills1` FOREIGN KEY (`skills_id`) REFERENCES `skills` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `skills_employees`
--

LOCK TABLES `skills_employees` WRITE;
/*!40000 ALTER TABLE `skills_employees` DISABLE KEYS */;
INSERT INTO `skills_employees` VALUES (1,1,3),(2,1,2),(3,2,8),(4,3,1),(5,4,2),(6,5,7),(7,6,6),(8,7,1),(9,7,2),(10,8,3),(11,9,5),(12,10,1),(13,11,6),(14,12,7),(15,13,5),(16,14,8),(17,15,1),(18,16,2),(19,17,8),(20,18,3),(21,19,4),(22,5,1),(23,8,2),(24,11,3),(25,2,2),(26,18,5),(27,7,8);
/*!40000 ALTER TABLE `skills_employees` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `stock`
--

DROP TABLE IF EXISTS `stock`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `stock` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) DEFAULT NULL,
  `stockType_id` int(11) NOT NULL,
  `price` decimal(10,2) DEFAULT NULL,
  `status` enum('доступно','списано','в ремонті') DEFAULT 'доступно',
  `notes` varchar(100) DEFAULT NULL,
  `repository_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `fk_stock_stockType2_idx` (`stockType_id`),
  KEY `fk_stock_repository1_idx` (`repository_id`),
  CONSTRAINT `fk_stock_repository1` FOREIGN KEY (`repository_id`) REFERENCES `repository` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_stock_stockType2` FOREIGN KEY (`stockType_id`) REFERENCES `stocktype` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `stock`
--

LOCK TABLES `stock` WRITE;
/*!40000 ALTER TABLE `stock` DISABLE KEYS */;
INSERT INTO `stock` VALUES (1,NULL,1,5.00,'доступно',NULL,2),(2,NULL,1,5.00,'доступно',NULL,2),(3,NULL,1,5.00,'доступно',NULL,2),(4,NULL,1,5.00,'доступно',NULL,2),(5,NULL,1,5.00,'доступно',NULL,2),(6,NULL,1,5.00,'доступно',NULL,2),(7,NULL,1,5.00,'доступно',NULL,2),(8,NULL,1,5.00,'доступно',NULL,2),(9,NULL,1,5.00,'доступно',NULL,2),(10,NULL,1,5.00,'доступно',NULL,2),(11,'Kende ММА-250',2,23400.00,'доступно',NULL,1),(12,'Патон ВДІ-200E DC MMA',2,4199.00,'в ремонті','щось зломано',NULL),(13,'Bosch Professional GBH 2-24DF',3,3999.00,'доступно','Якісь нотатки',1),(14,'Forte PLRH 3216 RV',3,2085.00,'доступно',NULL,1),(15,'Tylon 30м',4,35.50,'доступно',NULL,2),(16,'Tylon 30м',4,35.50,'списано',NULL,1),(17,'10м',4,24.00,'доступно',NULL,2),(18,'15м',4,31.00,'доступно',NULL,2),(19,'InterTool HT-0022',5,36.00,'доступно','Рамоподібний',1),(20,'FAVORIT 12-009',5,58.00,'доступно',NULL,1),(21,'Bosch GWS 850 CE',6,1758.00,'списано',NULL,1),(22,'Makita GA9020',6,3095.00,'доступно',NULL,1),(23,'500м',7,30.00,'списано',NULL,1),(24,'500м',7,30.00,'списано',NULL,2),(25,'500м',7,30.00,'списано',NULL,2),(26,'500м',7,30.00,'списано',NULL,2),(27,'500м',7,30.00,'доступно',NULL,2),(28,'INTERTOOL MT-1270',8,161.00,'доступно',NULL,1),(29,'INTERTOOL MT-1270',8,161.00,'доступно',NULL,1),(30,'Monolith РЦ 2,0 мм 1 кг',9,61.00,'списано',NULL,1),(31,'Monolith РЦ 2,0 мм 1 кг',9,61.00,'списано',NULL,1),(32,'Monolith РЦ 2,0 мм 1 кг',9,61.00,'доступно',NULL,2);
/*!40000 ALTER TABLE `stock` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `stocktracking`
--

DROP TABLE IF EXISTS `stocktracking`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `stocktracking` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `stock_id` int(11) NOT NULL,
  `employees_id` int(11) DEFAULT NULL,
  `object_id` int(11) DEFAULT NULL,
  `givingDate` date DEFAULT NULL,
  `returnDate` date DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `fk_stockAccountability_stock1_idx` (`stock_id`),
  KEY `fk_stockAccountability_employees1_idx` (`employees_id`),
  KEY `fk_stockAccountability_object1_idx` (`object_id`),
  CONSTRAINT `fk_stockAccountability_employees1` FOREIGN KEY (`employees_id`) REFERENCES `employees` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_stockAccountability_object1` FOREIGN KEY (`object_id`) REFERENCES `object` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_stockAccountability_stock1` FOREIGN KEY (`stock_id`) REFERENCES `stock` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `stocktracking`
--

LOCK TABLES `stocktracking` WRITE;
/*!40000 ALTER TABLE `stocktracking` DISABLE KEYS */;
INSERT INTO `stocktracking` VALUES (1,11,1,NULL,'2015-10-12','2016-01-19'),(2,11,8,NULL,'2016-01-20',NULL),(3,12,11,NULL,'2016-01-20',NULL),(4,14,9,NULL,'2016-01-20',NULL),(5,22,5,NULL,'2016-01-20',NULL),(6,1,1,1,'2015-09-07',NULL),(7,2,2,1,'2015-09-07',NULL),(8,3,4,1,'2015-09-24',NULL),(9,21,7,1,'2015-09-16','2015-09-20'),(10,30,8,1,'2015-09-07',NULL),(11,4,4,2,'2015-12-20',NULL),(12,5,5,2,'2015-12-21',NULL),(13,6,12,2,'2016-01-09',NULL),(14,30,1,2,'2015-12-27',NULL),(15,29,15,2,'2016-01-05','2016-01-17'),(16,23,18,2,'2016-01-19',NULL),(17,14,2,2,'2015-12-24','2016-01-03'),(18,15,9,3,'2016-01-18',NULL),(19,15,14,3,'2016-01-18',NULL),(20,19,5,3,'2016-01-19','2016-01-19'),(21,22,11,3,'2016-01-18','2016-01-20');
/*!40000 ALTER TABLE `stocktracking` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `stocktype`
--

DROP TABLE IF EXISTS `stocktype`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `stocktype` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `stocktype`
--

LOCK TABLES `stocktype` WRITE;
/*!40000 ALTER TABLE `stocktype` DISABLE KEYS */;
INSERT INTO `stocktype` VALUES (1,'Рукавиці'),(2,'Зварювальні апарати'),(3,'Перфератори'),(4,'Вимірювальні рулетки'),(5,'Пістолети для силікону'),(6,'Шліфувальні машинки'),(7,'Скотч'),(8,'Ватерпаси'),(9,'Електроди');
/*!40000 ALTER TABLE `stocktype` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `techinspection`
--

DROP TABLE IF EXISTS `techinspection`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `techinspection` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `customers_id` int(11) NOT NULL,
  `name` varchar(45) NOT NULL,
  `surname` varchar(45) NOT NULL,
  `middleName` varchar(45) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `fk_techInspection_customers1_idx` (`customers_id`),
  CONSTRAINT `fk_techInspection_customers1` FOREIGN KEY (`customers_id`) REFERENCES `customers` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `techinspection`
--

LOCK TABLES `techinspection` WRITE;
/*!40000 ALTER TABLE `techinspection` DISABLE KEYS */;
INSERT INTO `techinspection` VALUES (1,1,'Андрій','Кузьменко','Вікторович'),(2,2,'Богдан','Семченко','Семенович'),(3,2,'Віктор','Бринюк','Михайлович');
/*!40000 ALTER TABLE `techinspection` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `techtelephone`
--

DROP TABLE IF EXISTS `techtelephone`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `techtelephone` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `techInspection_id` int(11) NOT NULL,
  `telephoneNumber` varchar(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `fk_techTelephone_techInspection1_idx` (`techInspection_id`),
  CONSTRAINT `fk_techTelephone_techInspection1` FOREIGN KEY (`techInspection_id`) REFERENCES `techinspection` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `techtelephone`
--

LOCK TABLES `techtelephone` WRITE;
/*!40000 ALTER TABLE `techtelephone` DISABLE KEYS */;
INSERT INTO `techtelephone` VALUES (1,1,'0993565371'),(2,2,'0683456908'),(3,2,'0932478931'),(4,3,'0502869329'),(5,3,'0672425354');
/*!40000 ALTER TABLE `techtelephone` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `worktracking`
--

DROP TABLE IF EXISTS `worktracking`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `worktracking` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `object_employees_id` int(11) NOT NULL,
  `date` date NOT NULL,
  `workingHours` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `fk_attendance_object_employees1_idx` (`object_employees_id`),
  CONSTRAINT `fk_attendance_object_employees1` FOREIGN KEY (`object_employees_id`) REFERENCES `object_employees` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `worktracking`
--

LOCK TABLES `worktracking` WRITE;
/*!40000 ALTER TABLE `worktracking` DISABLE KEYS */;
INSERT INTO `worktracking` VALUES (1,23,'2016-01-19',8),(2,24,'2016-01-18',9),(3,24,'2016-01-20',10),(4,26,'2016-01-18',9),(5,26,'2016-01-19',8),(6,26,'2016-01-20',9),(7,27,'2016-01-20',9);
/*!40000 ALTER TABLE `worktracking` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-02-17 20:22:06
