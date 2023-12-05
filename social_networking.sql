-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Oct 10, 2023 at 09:01 AM
-- Server version: 10.4.28-MariaDB
-- PHP Version: 8.2.4

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `social networking`
--

DELIMITER $$
--
-- Procedures
--
CREATE DEFINER=`root`@`localhost` PROCEDURE `getStar` (OUT `outuser_id` VARCHAR(50), OUT `outpassword` VARCHAR(50), OUT `outname` VARCHAR(50), OUT `outfollowing1` INT, OUT `outfollowing2` INT, OUT `outfollower1` INT, OUT `outfollower2` INT, OUT `outfollowing_count` INT, OUT `outfollowers_count` INT, OUT `outmobileNumber` BIGINT)   SELECT * INTO OUTUSER_ID,OUTPASSWORD,OUTNAME,OUTFOLLOWING1,OUTFOLLOWING2,OUTFOLLOWER1,OUTFOLLOWER2,OUTFOLLOWING_COUNT,OUTFOLLOWERS_COUNT,OUTMOBILENUMBER FROM USERS$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getStarFromUsersByUserId` (IN `inuser_id` VARCHAR(50), OUT `outpassword` VARCHAR(50), OUT `outname` VARCHAR(50), OUT `outfollowing1` INT, OUT `outfollowing2` INT, OUT `outfollower1` INT, OUT `outfollower2` INT, OUT `outfollowing_count` INT, OUT `outfollowers_count` INT, OUT `outmobileNumber` INT)   SELECT PASSWORD,NAME,MOBILENUMBER,FOLLOWING1,FOLLOWING2,FOLLOWER1,FOLLOWER2,FOLLOWING_COUNT,FOLLOWERS_COUNT INTO OUTPASSWORD,OUTNAME,OUTMOBILENUMBER,OUTFOLLOWING1,OUTFOLLOWING2,OUTFOLLOWER1,OUTFOLLOWER2,OUTFOLLOWING_COUNT,OUTFOLLOWERS_COUNT FROM USERS WHERE USE_ID = INUSER_ID$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getUserId` (OUT `outuser_id` VARCHAR(50), IN `INUSER_ID` VARCHAR(50))   SELECT USER_ID INTO OUTUSER_ID FROM USERS WHERE USER_ID = INUSER_ID$$

DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `user_id` varchar(50) NOT NULL,
  `password` varchar(50) NOT NULL,
  `name` varchar(50) NOT NULL,
  `following1` varchar(50) NOT NULL,
  `following2` varchar(50) NOT NULL,
  `follower1` varchar(50) NOT NULL,
  `follower2` varchar(50) NOT NULL,
  `following_count` int(11) NOT NULL,
  `followers_count` int(11) NOT NULL,
  `mobile_number` bigint(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`user_id`, `password`, `name`, `following1`, `following2`, `follower1`, `follower2`, `following_count`, `followers_count`, `mobile_number`) VALUES
('anna', '123456', 'Anna', 'elsa.11', 'hetavi.1', 'elsa.11', 'hetavi.1', 2, 2, 9878765657),
('dharmesh', '123456', 'Dharmesh Shah', 'nidhi', 'moana', 'nidhi', 'moana', 2, 2, 9789876543),
('dumbo', '123456', 'Dumbo', 'null', 'kavish', 'null', 'kavish', 1, 1, 7854565489),
('elsa.11', '123456', 'Elsa', 'anna', 'null', 'anna', 'hetavi.1', 1, 2, 8978765654),
('hetavi.1', '123456', 'Hetavi', 'elsa.11', 'anna', 'anna', 'null', 2, 1, 1234567890),
('kavish', '123456', 'Kavish Patel', 'rudra', 'dumbo', 'rudra', 'dumbo', 2, 2, 7854878965),
('kavishpatel', '123456', 'kavish patel', 'null', 'null', 'null', 'null', 0, 0, 7990284997),
('moana', '123456', 'Moana', 'sonali', 'dharmesh', 'sonali', 'dharmesh', 2, 2, 7865489875),
('nidhi', '123456', 'Nidhi Shah', 'dharmesh', 'null', 'dharmesh', 'null', 1, 1, 7854565899),
('rudra', '123456', 'Rudra Shah', 'sonali', 'kavish', 'kavish', 'sonali', 2, 2, 7854987632),
('sonali', '123456', 'Sonali Shah', 'moana', 'rudra', 'rudra', 'moana', 2, 2, 7895874562);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`user_id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
