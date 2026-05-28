-- MySQL dump 10.13  Distrib 9.7.0, for Win64 (x86_64)
--
-- Host: localhost    Database: urban_report
-- ------------------------------------------------------
-- Server version	9.7.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
SET @MYSQLDUMP_TEMP_LOG_BIN = @@SESSION.SQL_LOG_BIN;
SET @@SESSION.SQL_LOG_BIN= 0;

--
-- GTID state at the beginning of the backup 
--

SET @@GLOBAL.GTID_PURGED=/*!80000 '+'*/ 'eef19f3b-4a23-11f1-aeee-0a002700000d:1-388';

--
-- Table structure for table `notifications`
--

DROP TABLE IF EXISTS `notifications`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notifications` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `lu` bit(1) NOT NULL,
  `message` varchar(255) DEFAULT NULL,
  `signalement_id` bigint DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKbqgji83tfuhtmn5sxu20wmxq3` (`signalement_id`),
  KEY `FK9y21adhxn0ayjhfocscqox7bh` (`user_id`),
  CONSTRAINT `FK9y21adhxn0ayjhfocscqox7bh` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKbqgji83tfuhtmn5sxu20wmxq3` FOREIGN KEY (`signalement_id`) REFERENCES `signalements` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notifications`
--

LOCK TABLES `notifications` WRITE;
/*!40000 ALTER TABLE `notifications` DISABLE KEYS */;
INSERT INTO `notifications` VALUES (1,'2026-05-13 23:00:19.180565',_binary '','Le statut de votre signalement \" m \" a été changé de SIGNALE à EN_COURS',8,3),(2,'2026-05-13 23:00:31.044340',_binary '','Le statut de votre signalement \"exemple2\" a été changé de EN_COURS à SIGNALE',5,3),(3,'2026-05-15 01:26:04.224172',_binary '\0','el mazouzy ismail a le même problème que \"movaise rue\"',10,3),(4,'2026-05-15 01:31:13.754972',_binary '\0','el mazouzy ismail a le même problème que \"movaise rue\"',10,3),(5,'2026-05-15 12:34:41.389813',_binary '\0','test compte a le même problème que \"Dépôts de déchets près du marché\"',15,10),(6,'2026-05-15 12:35:09.976753',_binary '\0','Le statut de votre signalement \"Route fortement dégradée près du rond-point\" a été changé de SIGNALE à EN_COURS',14,3),(7,'2026-05-15 12:35:13.527707',_binary '\0','Le statut de votre signalement \"Eclairage public défectueux\" a été changé de SIGNALE à RESOLU',2,1),(8,'2026-05-15 12:35:17.661593',_binary '\0','Le statut de votre signalement \"exemple2\" a été changé de SIGNALE à EN_COURS',5,3),(9,'2026-05-15 12:35:20.843577',_binary '\0','Le statut de votre signalement \"Égout obstrué causant des flaques d’eau importantes\" a été changé de SIGNALE à RESOLU',16,1),(10,'2026-05-15 12:35:23.520389',_binary '\0','Le statut de votre signalement \"TITRE\" a été changé de SIGNALE à EN_COURS',12,3),(11,'2026-05-15 12:35:26.787550',_binary '\0','Le statut de votre signalement \"rtreg\" a été changé de SIGNALE à RESOLU',11,3),(12,'2026-05-15 12:35:57.152608',_binary '\0','el mazouzy ismail a le même problème que \"Lampadaires hors service dans une rue résidentielle\"',13,3),(13,'2026-05-15 12:36:21.756120',_binary '\0','el mazouzy ismail a le même problème que \"exemple1\"',4,3),(14,'2026-05-15 12:36:26.717079',_binary '\0','el mazouzy ismail a le même problème que \"Banc public cassé dans un espace vert\"',17,9),(15,'2026-05-15 12:37:16.129879',_binary '\0','chef chef a le même problème que \"Banc public cassé dans un espace vert\"',17,9),(16,'2026-05-15 12:37:17.234103',_binary '\0','chef chef a le même problème que \"Route fortement dégradée près du rond-point\"',14,3),(17,'2026-05-15 12:37:22.186132',_binary '\0','chef chef a le même problème que \"movaise rue\"',10,3),(18,'2026-05-15 12:37:24.523513',_binary '\0','chef chef a le même problème que \"Lampadaires hors service dans une rue résidentielle\"',13,3),(19,'2026-05-15 14:29:06.662086',_binary '\0','Le statut de votre signalement \" m \" a été changé de EN_COURS à RESOLU',8,3);
/*!40000 ALTER TABLE `notifications` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `signalements`
--

DROP TABLE IF EXISTS `signalements`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `signalements` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `adresse` varchar(255) DEFAULT NULL,
  `categorie` enum('VOIRIE','ECLAIRAGE','DECHETS','EAU','AUTRE') DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `description` varchar(1000) DEFAULT NULL,
  `latitude` double DEFAULT NULL,
  `longitude` double DEFAULT NULL,
  `photo_url` varchar(255) DEFAULT NULL,
  `statut` enum('SIGNALE','EN_COURS','RESOLU') DEFAULT NULL,
  `titre` varchar(255) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKn5gjqwleblxbmfe3xqo33n6j1` (`user_id`),
  CONSTRAINT `FKn5gjqwleblxbmfe3xqo33n6j1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `signalements`
--

LOCK TABLES `signalements` WRITE;
/*!40000 ALTER TABLE `signalements` DISABLE KEYS */;
INSERT INTO `signalements` VALUES (2,'Rue Mohammed V, Casablanca','ECLAIRAGE','2026-05-12 07:18:06.876897','Les lampadaires de la rue sont éteints depuis 3 jours',33.5892,-7.6034,NULL,'RESOLU','Eclairage public défectueux','2026-05-15 12:35:13.517605',1),(4,'','AUTRE','2026-05-13 00:45:22.646286','ex',33.65058354097939,-7.449664513716436,NULL,'SIGNALE','exemple1',NULL,3),(5,'','ECLAIRAGE','2026-05-13 00:45:47.211327','ex2',33.65058354097939,-7.449664513716436,NULL,'EN_COURS','exemple2','2026-05-15 12:35:17.645430',3),(7,'','VOIRIE','2026-05-13 01:30:54.491524','342342',33.65050768238242,-7.450038601711707,NULL,'SIGNALE','2323',NULL,3),(8,'','VOIRIE','2026-05-13 22:41:16.686505','m,m',33.65059280124474,-7.450137868222341,'/uploads/4174bb56-8f09-43da-8b8c-caa44ebde736.jpg','RESOLU',' m ','2026-05-15 14:29:06.624010',3),(10,'jnan zenata','EAU','2026-05-13 23:10:24.427347','rrrt',33.65056956687852,-7.450112594692586,'/uploads/c83b6a13-4658-4e5a-9a04-3caa742dac7e.png','SIGNALE','movaise rue',NULL,3),(11,'','VOIRIE','2026-05-14 11:19:59.007369','fdfd',33.567313886328954,-7.540668715452807,'/uploads/011b21b7-8d7e-4bc4-8190-37d5c0cfc1d2.png','RESOLU','rtreg','2026-05-15 12:35:26.772560',3),(12,'','AUTRE','2026-05-14 14:32:01.325900','FF',33.56682141201728,-7.54133554744617,'/uploads/979882a7-0516-43c5-b6ec-c0d4c6bcdc69.avif','EN_COURS','TITRE','2026-05-15 12:35:23.508952',3),(13,'Quartier Al Qods','ECLAIRAGE','2026-05-15 12:24:39.971528','Les lampadaires de la rue ne fonctionnent plus depuis plusieurs jours, laissant la zone dans l’obscurité complète durant la nuit.',33.65057791906857,-7.449962520319352,'/uploads/e3463489-0545-451b-bc4f-d801825a2665.jpeg','SIGNALE','Lampadaires hors service dans une rue résidentielle',NULL,3),(14,'Boulevard Mohammed VI, près du rond-point central','VOIRIE','2026-05-15 12:25:40.422053','Plusieurs nids-de-poule profonds rendent la circulation dangereuse pour les voitures et les motos, surtout pendant la nuit et les jours de pluie.',33.650572323850376,-7.449987929319371,'/uploads/2710cf32-96b6-4abb-a0b3-a8e3cbacf7a8.webp','EN_COURS','Route fortement dégradée près du rond-point','2026-05-15 12:35:09.938069',3),(15,'Rue du Marché Municipal','DECHETS','2026-05-15 12:28:54.298299','Une grande quantité de déchets ménagers est accumulée sur le trottoir, provoquant de mauvaises odeurs et attirant des insectes.',33.65055690609604,-7.450180912751474,'/uploads/4951de60-c4ac-44c1-8f8b-7cf16ff26dc6.webp','SIGNALE','Dépôts de déchets près du marché',NULL,10),(16,'Avenue Hassan II','EAU','2026-05-15 12:31:30.936977','Le système d’évacuation des eaux est bouché, ce qui provoque une accumulation d’eau sur la chaussée après chaque pluie.',33.6505780137848,-7.4500052005160375,'/uploads/64e7d3cd-2aba-463d-bfda-f395bdc39c5d.webp','RESOLU','Égout obstrué causant des flaques d’eau importantes','2026-05-15 12:35:20.825526',1),(17,'Parc Al Amal','AUTRE','2026-05-15 12:34:31.218918','Un banc public est fortement endommagé et présente un danger pour les citoyens fréquentant le parc.',33.65056519914418,-7.450142151902529,'/uploads/5fe6934f-cb68-4a66-814b-0cae485f2f9b.webp','SIGNALE','Banc public cassé dans un espace vert',NULL,9);
/*!40000 ALTER TABLE `signalements` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `nom` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `prenom` varchar(255) NOT NULL,
  `role` enum('USER','ADMIN') DEFAULT NULL,
  `photo_url` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_6dotkott2kjsp8vw4d0m25fb7` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'2026-05-12 07:10:37.600579','ismail@test.com','El Mazouzy','$2a$10$SQYLyyC8KBb.l5QxdhLhAekp/bb39v1g0I.PzX3ovgzYVVXxki1da','Ismail','ADMIN',NULL),(3,'2026-05-12 15:03:53.416559','ismailelmazouzy6@gmail.com','el mazouzy','$2a$10$TFV.9kK2GupmSonnnomPVeksRXUJq5hPFFnZpmQRz1n5aztNbFA0C','ismail','ADMIN','/uploads/profile_c166c330-8b78-4bb9-a3a9-7bb411c4484e.jpg'),(4,'2026-05-12 15:12:16.948484','ismail@gmail.com','test','$2a$10$suU/QV0J6al/rLIYAGYZDeAp9U9lFiSC6m1ulmG5cnhKOTRCaB71K','test','USER',NULL),(8,'2026-05-14 19:25:30.083001','admin2@urbanreport.ma','admin','$2a$10$K2e36V3dZiHa2FNOb097zejoYKyWeUqz3Ja4/A3zNDaupUzH0FgbG','chef','ADMIN','/uploads/profile_738daa58-e72a-4aaa-8916-8b75a37266b1.jpg'),(9,'2026-05-14 20:23:50.549881','test@gmail.com','test','$2a$10$jxiwCUxQWfxZUoaxMBogEeRDnUTpo.7souq.6Sf8rVFMJpg7ezfA2','compte','USER',NULL),(10,'2026-05-15 01:13:23.740742','admin@urbanreport.ma','chef','$2a$10$vxSxZFVYb/ZdLbh7Il6kb.dp1jeUt5XoqB0csScYww7Xj8D47bpau','chef','ADMIN',NULL);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
SET @@SESSION.SQL_LOG_BIN = @MYSQLDUMP_TEMP_LOG_BIN;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-05-15 14:40:48
