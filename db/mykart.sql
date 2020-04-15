-- phpMyAdmin SQL Dump
-- version 4.9.0.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Apr 15, 2020 at 01:28 PM
-- Server version: 10.3.16-MariaDB
-- PHP Version: 7.3.6

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT = @@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS = @@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION = @@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `myCart`
--

-- --------------------------------------------------------

--
-- Table structure for table `admin`
--

CREATE TABLE `admin`
(
    `UID`      varchar(100) NOT NULL,
    `NAME`     varchar(50)  NOT NULL,
    `USERNAME` varchar(50)  NOT NULL,
    `PASSWORD` varchar(50)  NOT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1;

--
-- Dumping data for table `admin`
--

INSERT INTO `admin` (`UID`, `NAME`, `USERNAME`, `PASSWORD`)
VALUES ('ec289fb1-b8fb-4fab-ad24-2421f29f71e9', 'ADMIN', 'admin', 'admin');

-- --------------------------------------------------------

--
-- Table structure for table `cart`
--

CREATE TABLE `cart`
(
    `USER_ID`    varchar(100) NOT NULL,
    `PRODUCT_ID` int(11)      NOT NULL,
    `QUANTITY`   int(11)      NOT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1;

-- --------------------------------------------------------

--
-- Table structure for table `categories`
--

CREATE TABLE `categories`
(
    `ID`            int(11)     NOT NULL,
    `CATEGORY_NAME` varchar(30) NOT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1;

--
-- Dumping data for table `categories`
--

INSERT INTO `categories` (`ID`, `CATEGORY_NAME`)
VALUES (1, 'Electronics'),
       (2, 'Accessories'),
       (3, 'Clothes');

-- --------------------------------------------------------

--
-- Table structure for table `coupon`
--

CREATE TABLE `coupon`
(
    `COUPON_CODE`         char(6)        NOT NULL,
    `USAGE_TYPE`          int(11)        NOT NULL,
    `USAGE_LIMIT`         int(11)        NOT NULL,
    `START_DATE`          date           NOT NULL,
    `END_DATE`            date           NOT NULL,
    `DISCOUNT_PERCENTAGE` decimal(10, 0) NOT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1;

--
-- Dumping data for table `coupon`
--

INSERT INTO `coupon` (`COUPON_CODE`, `USAGE_TYPE`, `USAGE_LIMIT`, `START_DATE`, `END_DATE`, `DISCOUNT_PERCENTAGE`)
VALUES ('AAABBB', 1, 0, '2018-09-08', '2018-09-08', '10'),
       ('AB12PQ', 3, 0, '2020-04-14', '2020-05-01', '65'),
       ('ABC123', 3, 0, '2018-09-08', '2018-09-08', '20'),
       ('ABCDEF', 2, 25, '2020-04-12', '2020-04-19', '90'),
       ('OFF50', 1, 1, '2020-04-14', '2020-04-20', '50'),
       ('PQR123', 2, 10, '2018-09-08', '2018-09-08', '30');

-- --------------------------------------------------------

--
-- Stand-in structure for view `coupons_used`
-- (See below for the actual view)
--
CREATE TABLE `coupons_used`
(
    `APPLIED_COUPON` char(6),
    `ORDER_ID`       int(11),
    `TIMESTAMP`      timestamp,
    `USER_ID`        varchar(100)
);

-- --------------------------------------------------------

--
-- Table structure for table `invoice`
--

CREATE TABLE `invoice`
(
    `INVOICE_ID`      int(11) NOT NULL,
    `ORDER_ID`        int(11) NOT NULL,
    `BASE_AMOUNT`     double  NOT NULL,
    `DISCOUNT_AMOUNT` double  NOT NULL,
    `APPLIED_COUPON`  char(6) DEFAULT NULL,
    `TOTAL_AMOUNT`    double  NOT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1;

--
-- Dumping data for table `invoice`
--

INSERT INTO `invoice` (`INVOICE_ID`, `ORDER_ID`, `BASE_AMOUNT`, `DISCOUNT_AMOUNT`, `APPLIED_COUPON`, `TOTAL_AMOUNT`)
VALUES (11, 14, 74000, 37000, 'OFF50', 37000),
       (12, 15, 12000, 6000, 'OFF50', 6000),
       (13, 17, 56200, 500, NULL, 55700),
       (14, 18, 6500, 5850, 'ABCDEF', 650);

-- --------------------------------------------------------

--
-- Table structure for table `orders`
--

CREATE TABLE `orders`
(
    `ORDER_ID`  int(11)      NOT NULL,
    `USER_ID`   varchar(100) NOT NULL,
    `TIMESTAMP` timestamp    NOT NULL DEFAULT current_timestamp()
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1;

--
-- Dumping data for table `orders`
--

INSERT INTO `orders` (`ORDER_ID`, `USER_ID`, `TIMESTAMP`)
VALUES (14, '45b069fb-dd94-4e52-b11c-55a60ed17945', '2020-04-14 14:18:58'),
       (15, 'f34818ab-e816-4096-b7de-f10de30779be', '2020-04-14 14:28:19'),
       (17, 'f34818ab-e816-4096-b7de-f10de30779be', '2020-04-14 16:21:37'),
       (18, '45b069fb-dd94-4e52-b11c-55a60ed17945', '2020-04-14 17:47:32');

--
-- Triggers `orders`
--
DELIMITER $$
CREATE TRIGGER `ClearCart`
    AFTER INSERT
    ON `orders`
    FOR EACH ROW DELETE
                 FROM cart
                 WHERE cart.USER_ID = USER_ID
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `order_items`
--

CREATE TABLE `order_items`
(
    `ORDER_ID`   int(11) NOT NULL,
    `PRODUCT_ID` int(11) NOT NULL,
    `QUANTITY`   int(11) NOT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1;

--
-- Dumping data for table `order_items`
--

INSERT INTO `order_items` (`ORDER_ID`, `PRODUCT_ID`, `QUANTITY`)
VALUES (14, 1, 2),
       (14, 5, 1),
       (15, 4, 10),
       (17, 5, 1),
       (17, 4, 1),
       (18, 3, 10);

-- --------------------------------------------------------

--
-- Table structure for table `products`
--

CREATE TABLE `products`
(
    `ID`                  int(11)     NOT NULL,
    `CATEGORY_ID`         int(11)     NOT NULL,
    `PRODUCT_NAME`        varchar(30) NOT NULL,
    `PRODUCT_DESCRIPTION` varchar(50) NOT NULL,
    `PRICE`               double      NOT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1;

--
-- Dumping data for table `products`
--

INSERT INTO `products` (`ID`, `CATEGORY_ID`, `PRODUCT_NAME`, `PRODUCT_DESCRIPTION`, `PRICE`)
VALUES (1, 1, 'Xiaomi Phone', 'Smartphone', 9500),
       (3, 2, 'Earphone', 'With 2 layer air filter', 650),
       (4, 3, 'T-Shirt', 'Cotton T-Shirt', 1200),
       (5, 1, 'Smart TV', 'Smart TV with Android OS', 55000);

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

CREATE TABLE `user`
(
    `UID`      varchar(100) NOT NULL,
    `NAME`     varchar(50)  NOT NULL,
    `USERNAME` varchar(30)  NOT NULL,
    `PASSWORD` varchar(50)  NOT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1;

--
-- Dumping data for table `user`
--

INSERT INTO `user` (`UID`, `NAME`, `USERNAME`, `PASSWORD`)
VALUES ('45b069fb-dd94-4e52-b11c-55a60ed17945', 'Shreyas Patil', 'user1', 'user1'),
       ('f34818ab-e816-4096-b7de-f10de30779be', 'Yash Patil', 'user2', 'user2');

-- --------------------------------------------------------

--
-- Structure for view `coupons_used`
--
DROP TABLE IF EXISTS `coupons_used`;

CREATE ALGORITHM = UNDEFINED DEFINER =`` SQL SECURITY DEFINER VIEW `coupons_used` AS
select `i`.`APPLIED_COUPON` AS `APPLIED_COUPON`,
       `i`.`ORDER_ID`       AS `ORDER_ID`,
       `o`.`TIMESTAMP`      AS `TIMESTAMP`,
       `o`.`USER_ID`        AS `USER_ID`
from (`orders` `o`
         join `invoice` `i` on (`o`.`ORDER_ID` = `i`.`ORDER_ID`))
order by `o`.`TIMESTAMP` desc;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `admin`
--
ALTER TABLE `admin`
    ADD PRIMARY KEY (`UID`),
    ADD UNIQUE KEY `uid` (`UID`);

--
-- Indexes for table `cart`
--
ALTER TABLE `cart`
    ADD KEY `PRODUCT_ID` (`PRODUCT_ID`),
    ADD KEY `USER_ID` (`USER_ID`) USING BTREE;

--
-- Indexes for table `categories`
--
ALTER TABLE `categories`
    ADD PRIMARY KEY (`ID`);

--
-- Indexes for table `coupon`
--
ALTER TABLE `coupon`
    ADD PRIMARY KEY (`COUPON_CODE`);

--
-- Indexes for table `invoice`
--
ALTER TABLE `invoice`
    ADD PRIMARY KEY (`INVOICE_ID`),
    ADD KEY `APPLIED_COUPON` (`APPLIED_COUPON`),
    ADD KEY `invoice_ibfk_2` (`ORDER_ID`);

--
-- Indexes for table `orders`
--
ALTER TABLE `orders`
    ADD PRIMARY KEY (`ORDER_ID`),
    ADD KEY `orders_ibfk_1` (`USER_ID`);

--
-- Indexes for table `order_items`
--
ALTER TABLE `order_items`
    ADD KEY `ORDER_ID` (`ORDER_ID`),
    ADD KEY `PRODUCT_ID` (`PRODUCT_ID`);

--
-- Indexes for table `products`
--
ALTER TABLE `products`
    ADD PRIMARY KEY (`ID`),
    ADD KEY `CATEGORY_ID` (`CATEGORY_ID`);

--
-- Indexes for table `user`
--
ALTER TABLE `user`
    ADD PRIMARY KEY (`UID`),
    ADD UNIQUE KEY `UID` (`UID`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `categories`
--
ALTER TABLE `categories`
    MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT,
    AUTO_INCREMENT = 4;

--
-- AUTO_INCREMENT for table `invoice`
--
ALTER TABLE `invoice`
    MODIFY `INVOICE_ID` int(11) NOT NULL AUTO_INCREMENT,
    AUTO_INCREMENT = 15;

--
-- AUTO_INCREMENT for table `orders`
--
ALTER TABLE `orders`
    MODIFY `ORDER_ID` int(11) NOT NULL AUTO_INCREMENT,
    AUTO_INCREMENT = 19;

--
-- AUTO_INCREMENT for table `products`
--
ALTER TABLE `products`
    MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT,
    AUTO_INCREMENT = 6;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `cart`
--
ALTER TABLE `cart`
    ADD CONSTRAINT `cart_ibfk_1` FOREIGN KEY (`USER_ID`) REFERENCES `user` (`UID`) ON DELETE CASCADE ON UPDATE CASCADE,
    ADD CONSTRAINT `cart_ibfk_2` FOREIGN KEY (`PRODUCT_ID`) REFERENCES `products` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `invoice`
--
ALTER TABLE `invoice`
    ADD CONSTRAINT `invoice_ibfk_1` FOREIGN KEY (`APPLIED_COUPON`) REFERENCES `coupon` (`COUPON_CODE`) ON DELETE CASCADE ON UPDATE CASCADE,
    ADD CONSTRAINT `invoice_ibfk_2` FOREIGN KEY (`ORDER_ID`) REFERENCES `orders` (`ORDER_ID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `orders`
--
ALTER TABLE `orders`
    ADD CONSTRAINT `orders_ibfk_1` FOREIGN KEY (`USER_ID`) REFERENCES `user` (`UID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `order_items`
--
ALTER TABLE `order_items`
    ADD CONSTRAINT `order_items_ibfk_1` FOREIGN KEY (`ORDER_ID`) REFERENCES `orders` (`ORDER_ID`) ON DELETE CASCADE ON UPDATE CASCADE,
    ADD CONSTRAINT `order_items_ibfk_2` FOREIGN KEY (`PRODUCT_ID`) REFERENCES `products` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `products`
--
ALTER TABLE `products`
    ADD CONSTRAINT `products_ibfk_1` FOREIGN KEY (`CATEGORY_ID`) REFERENCES `categories` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT = @OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS = @OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION = @OLD_COLLATION_CONNECTION */;
