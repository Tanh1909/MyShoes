# Getting Started

### Reference Documentation

For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/3.3.4/maven-plugin)
* [Create an OCI image](https://docs.spring.io/spring-boot/3.3.4/maven-plugin/build-image.html)

### Maven Parent overrides

### database ver1
create table user
(
id           bigint auto_increment
primary key,
username     varchar(50)                         not null,
password     varchar(255)                        not null,
email        varchar(100)                        null,
full_name    varchar(100)                        null,
phone_number varchar(20)                         null,
address      varchar(255)                        null,
created_at   timestamp default CURRENT_TIMESTAMP null,
updated_at   timestamp default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
constraint email
unique (email),
constraint username
unique (username)
);

create table role
(
id          bigint auto_increment primary key,
role_name   varchar(50)  not null,
description varchar(255) null,
constraint role_name
unique (role_name)
);

create table user_role
(
id      bigint auto_increment
primary key,
user_id bigint not null,
role_id bigint not null,
constraint user_id
unique (user_id, role_id),
constraint user_role_ibfk_1
foreign key (user_id) references user (id)
on delete cascade,
constraint user_role_ibfk_2
foreign key (role_id) references role (id)
on delete cascade
);

CREATE TABLE `address`
(
`id`         INT AUTO_INCREMENT PRIMARY KEY,
`user_id`    BIGINT       NOT NULL,
`content`    VARCHAR(255) NOT NULL,
`is_default` BOOLEAN  DEFAULT FALSE,
`created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
`updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
foreign key (user_id) references user (id)
) ENGINE = InnoDB;

CREATE TABLE `category`
(
`id`          INT AUTO_INCREMENT PRIMARY KEY,
`name`        VARCHAR(255) NOT NULL,
`description` TEXT,
`created_at`  DATETIME DEFAULT CURRENT_TIMESTAMP,
`updated_at`  DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE = InnoDB;


CREATE TABLE `product`
(
`id`          INT AUTO_INCREMENT PRIMARY KEY,
`name`        VARCHAR(255)   NOT NULL,
`description` TEXT,
`price`       DECIMAL(10, 2) NOT NULL,
`category_id` INT            NOT NULL,
`created_at`  DATETIME DEFAULT CURRENT_TIMESTAMP,
`updated_at`  DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
FOREIGN KEY (`category_id`) REFERENCES `category` (`id`)
) ENGINE = InnoDB;

CREATE TABLE `product_attribute`
(
`id`         INT AUTO_INCREMENT PRIMARY KEY,
`name`       VARCHAR(100) NOT NULL,
`created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
`updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE = InnoDB;

CREATE TABLE `product_attribute_option`
(
`id`                   INT AUTO_INCREMENT PRIMARY KEY,
`product_attribute_id` INT          NOT NULL,
`value`                VARCHAR(100) NOT NULL,
`created_at`           DATETIME DEFAULT CURRENT_TIMESTAMP,
`updated_at`           DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
FOREIGN KEY (`product_attribute_id`) REFERENCES `product_attribute` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB;

CREATE TABLE `product_variant`
(
`id`         INT AUTO_INCREMENT PRIMARY KEY,
`product_id` INT            NOT NULL,
`price`      DECIMAL(10, 2) NOT NULL,
`stock`      INT      DEFAULT 0,
`created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
`updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
FOREIGN KEY (`product_id`) REFERENCES `product` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB;

CREATE TABLE `product_variants_attribute_option`
(
`variant_id`                  INT NOT NULL,
`product_attribute_option_id` INT NOT NULL,
PRIMARY KEY (`variant_id`, `product_attribute_option_id`),
FOREIGN KEY (`variant_id`) REFERENCES `product_variant` (`id`) ON DELETE CASCADE,
FOREIGN KEY (`product_attribute_option_id`) REFERENCES `product_attribute_option` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB;

CREATE TABLE `cart`
(
`id`                 INT AUTO_INCREMENT PRIMARY KEY,
`user_id`            BIGINT NOT NULL,
`product_variant_id` INT    NOT NULL,
`quantity`           INT    NOT NULL DEFAULT 1,
`created_at`         DATETIME        DEFAULT CURRENT_TIMESTAMP,
`updated_at`         DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
FOREIGN KEY (`product_variant_id`) REFERENCES `product_variant` (`id`)
) ENGINE = InnoDB;


CREATE TABLE `image`
(
`id`         INT AUTO_INCREMENT PRIMARY KEY,
`url`        VARCHAR(255) NOT NULL,
`product_id` INT(255),
`created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
`updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
FOREIGN KEY (`product_id`) REFERENCES `product` (id)
) ENGINE = InnoDB;

CREATE TABLE `order`
(
`id`           INT AUTO_INCREMENT PRIMARY KEY,
`user_id`      BIGINT,
`status`       VARCHAR(100),
`total_amount` DECIMAL(10, 2) NOT NULL,
`created_at`   DATETIME DEFAULT CURRENT_TIMESTAMP,
`updated_at`   DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE SET NULL
) ENGINE = InnoDB;

CREATE TABLE `order_item`
(
`id`                 INT AUTO_INCREMENT PRIMARY KEY,
`order_id`           INT            NOT NULL,
`product_variant_id` INT            ,
`quantity`           INT            NOT NULL DEFAULT 1,
`price`              DECIMAL(10, 2) NOT NULL,
FOREIGN KEY (`order_id`) REFERENCES `order` (`id`) ON DELETE CASCADE,
FOREIGN KEY (`product_variant_id`) REFERENCES `product_variant` (`id`) ON DELETE SET NULL
) ENGINE = InnoDB;

CREATE TABLE `payment_method`
(
`id`          INT AUTO_INCREMENT PRIMARY KEY,
`name`        VARCHAR(100) NOT NULL,
`description` TEXT,
`created_at`  DATETIME DEFAULT CURRENT_TIMESTAMP,
`updated_at`  DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE = InnoDB;

CREATE TABLE `payment`
(
`id`                INT AUTO_INCREMENT PRIMARY KEY,
`order_id`          INT            NOT NULL,
`payment_method_id` INT           ,
`amount`            DECIMAL(10, 2) NOT NULL,
`payment_status`    NVARCHAR(255),
`paid_at`           DATETIME,
`created_at`        DATETIME DEFAULT CURRENT_TIMESTAMP,
`updated_at`        DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
FOREIGN KEY (`order_id`) REFERENCES `order` (`id`) ON DELETE CASCADE,
FOREIGN KEY (`payment_method_id`) REFERENCES `payment_method` (`id`) ON DELETE SET NULL
) ENGINE = InnoDB;

CREATE TABLE `review`
(
`id`                 INT AUTO_INCREMENT PRIMARY KEY,
`product_variant_id` INT    NOT NULL,
`user_id`            BIGINT NOT NULL,
`rating`             INT    NOT NULL CHECK (`rating` BETWEEN 1 AND 5),
`comment`            TEXT,
`created_at`         DATETIME DEFAULT CURRENT_TIMESTAMP,
`updated_at`         DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
FOREIGN KEY (`product_variant_id`) REFERENCES `product_variant` (`id`) ON DELETE CASCADE,
FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB;


### database ver 2

CREATE TABLE `address`
(
`id`         INT AUTO_INCREMENT PRIMARY KEY,
`user_id`    BIGINT       NOT NULL,
`content`    VARCHAR(255) NOT NULL,
`is_default` BOOLEAN  DEFAULT FALSE,
`created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
`updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE = InnoDB;

CREATE TABLE `category`
(
`id`          INT AUTO_INCREMENT PRIMARY KEY,
`name`        VARCHAR(255) NOT NULL,
`description` TEXT,
`created_at`  DATETIME DEFAULT CURRENT_TIMESTAMP,
`updated_at`  DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE = InnoDB;


CREATE TABLE `product`
(
`id`          INT AUTO_INCREMENT PRIMARY KEY,
`name`        VARCHAR(255)   NOT NULL,
`description` TEXT,
`price`       DECIMAL(10, 2) NOT NULL,
`category_id` INT            NOT NULL,
`created_at`  DATETIME DEFAULT CURRENT_TIMESTAMP,
`updated_at`  DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
FOREIGN KEY (`category_id`) REFERENCES `category` (`id`)
) ENGINE = InnoDB;

CREATE TABLE `product_attribute`
(
`id`         INT AUTO_INCREMENT PRIMARY KEY,
`name`       VARCHAR(100) NOT NULL,
`created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
`updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE = InnoDB;

CREATE TABLE `product_attribute_option`
(
`id`                   INT AUTO_INCREMENT PRIMARY KEY,
`product_attribute_id` INT          NOT NULL,
`value`                VARCHAR(100) NOT NULL,
`created_at`           DATETIME DEFAULT CURRENT_TIMESTAMP,
`updated_at`           DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
FOREIGN KEY (`product_attribute_id`) REFERENCES `product_attribute` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB;

CREATE TABLE `product_variant`
(
`id`         INT AUTO_INCREMENT PRIMARY KEY,
`product_id` INT            NOT NULL,
`price`      DECIMAL(10, 2) NOT NULL,
`stock`      INT      DEFAULT 0,
`created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
`updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
FOREIGN KEY (`product_id`) REFERENCES `product` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB;

CREATE TABLE `product_variants_attribute_option`
(
`variant_id`                  INT NOT NULL,
`product_attribute_option_id` INT NOT NULL,
PRIMARY KEY (`variant_id`, `product_attribute_option_id`),
FOREIGN KEY (`variant_id`) REFERENCES `product_variant` (`id`) ON DELETE CASCADE,
FOREIGN KEY (`product_attribute_option_id`) REFERENCES `product_attribute_option` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB;

CREATE TABLE `cart`
(
`id`                 INT AUTO_INCREMENT PRIMARY KEY,
`user_id`            BIGINT NOT NULL,
`product_variant_id` INT    NOT NULL,
`quantity`           INT    NOT NULL DEFAULT 1,
`created_at`         DATETIME        DEFAULT CURRENT_TIMESTAMP,
`updated_at`         DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
FOREIGN KEY (`product_variant_id`) REFERENCES `product_variant` (`id`)
) ENGINE = InnoDB;


CREATE TABLE `image`
(
`id`         INT AUTO_INCREMENT PRIMARY KEY,
`url`        VARCHAR(255) NOT NULL,
`target_id`  INT,
`type`       VARCHAR(100),
`created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
`updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE = InnoDB;

CREATE TABLE `order`
(
`id`           INT AUTO_INCREMENT PRIMARY KEY,
`user_id`      BIGINT,
`status`       VARCHAR(100),
`total_amount` DECIMAL(10, 2) NOT NULL,
`created_at`   DATETIME DEFAULT CURRENT_TIMESTAMP,
`updated_at`   DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE = InnoDB;

CREATE TABLE `order_item`
(
`id`                 INT AUTO_INCREMENT PRIMARY KEY,
`order_id`           INT            NOT NULL,
`product_variant_id` INT,
`quantity`           INT            NOT NULL DEFAULT 1,
`price`              DECIMAL(10, 2) NOT NULL,
FOREIGN KEY (`order_id`) REFERENCES `order` (`id`) ON DELETE CASCADE,
FOREIGN KEY (`product_variant_id`) REFERENCES `product_variant` (`id`) ON DELETE SET NULL
) ENGINE = InnoDB;

CREATE TABLE `payment_method`
(
`id`          INT AUTO_INCREMENT PRIMARY KEY,
`name`        VARCHAR(100) NOT NULL,
`description` TEXT,
`created_at`  DATETIME DEFAULT CURRENT_TIMESTAMP,
`updated_at`  DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE = InnoDB;

CREATE TABLE `payment`
(
`id`                INT AUTO_INCREMENT PRIMARY KEY,
`order_id`          INT            NOT NULL,
`payment_method_id` INT,
`amount`            DECIMAL(10, 2) NOT NULL,
`payment_status`    NVARCHAR(255),
`paid_at`           DATETIME,
`created_at`        DATETIME DEFAULT CURRENT_TIMESTAMP,
`updated_at`        DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
FOREIGN KEY (`order_id`) REFERENCES `order` (`id`) ON DELETE CASCADE,
FOREIGN KEY (`payment_method_id`) REFERENCES `payment_method` (`id`) ON DELETE SET NULL
) ENGINE = InnoDB;

CREATE TABLE `review`
(
`id`                 INT AUTO_INCREMENT PRIMARY KEY,
`product_variant_id` INT    NOT NULL,
`user_id`            BIGINT NOT NULL,
`rating`             INT    NOT NULL CHECK (`rating` BETWEEN 1 AND 5),
`comment`            TEXT,
`created_at`         DATETIME DEFAULT CURRENT_TIMESTAMP,
`updated_at`         DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
FOREIGN KEY (`product_variant_id`) REFERENCES `product_variant` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB;











